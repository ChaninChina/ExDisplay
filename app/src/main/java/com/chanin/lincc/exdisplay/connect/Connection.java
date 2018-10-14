package com.chanin.lincc.exdisplay.connect;

import android.text.TextUtils;
import android.util.Log;


import com.chanin.lincc.exdisplay.SettingActivity;
import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.ex.LoginErrorException;
import com.chanin.lincc.exdisplay.ex.RequestException;
import com.chanin.lincc.exdisplay.model.ExClass;
import com.chanin.lincc.exdisplay.model.ExGroup;
import com.chanin.lincc.exdisplay.model.LoginErrorEvent;
import com.chanin.lincc.exdisplay.model.MessageDemoEvent;
import com.chanin.lincc.exdisplay.model.NewMessageEvent;
import com.chanin.lincc.exdisplay.model.PushMessage;
import com.chanin.lincc.exdisplay.utils.Constants;
import com.chanin.lincc.exdisplay.utils.DBHelper;
import com.chanin.lincc.exdisplay.utils.IOUtils;
import com.chanin.lincc.exdisplay.utils.PfUtils;
import com.chanin.lincc.exdisplay.utils.RequestUtil;
import com.chanin.lincc.exdisplay.utils.ResultUtil;
import com.chanin.lincc.exdisplay.utils.SchedulerHelper;
import com.chanin.lincc.exdisplay.utils.SystemUtil;
import com.chanin.lincc.exdisplay.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;


/**
 * 1.用户第一次登陆：传递用户名和密码，开始连接socket，新启线程然后发送用户名和密码， 然后阻塞等待返回，若登陆成功，修改登录状态标识为true，开启定时请求线程；若登陆失败，给提示，修改登录标识为false。
 * 2.发送请求：若socket已连接，直接发送请求，若未连接，若保存了用户名和密码，新建socket登陆，若成功发起请求；
 * 3.当APP退出：若用户已登录，定时任务运行；若未登录，后台服务以及定时请求关闭；
 * 4.连接中断重连：若用户保存了用户名和密码，重连，并发送用户名和密码，登陆成功，开启定时任务；
 */
public class Connection {


    private static final String TAG = "Connection";
    public static final int CONNECTION_ERROR = -1;
    public static final int CONNECTION_SUCCESS = 1;
    public static final int LOGIN_SUCCESS = 2;
    public static final int LOGIN_FAIL = 3;
    public static final int LOGIN_ERROR = 4;
    public static final int REQUEST_FAIL = 5;
    public static final int REQUEST_SUCCESS = 6;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader reader;
    private Lock lock = new ReentrantLock();
    private Lock hashMap = new ReentrantLock();
    private Lock executorLock = new ReentrantLock();
    private Lock loginLock = new ReentrantLock();
    private ExecutorService executorService;
    public volatile boolean isSchedule;
    public volatile boolean isLogin;
    public CompositeDisposable mCompositeDisposable;
    public ScheduledExecutorService scheduledExecutorService;
    public ConcurrentHashMap<String, BlockingQueue<String>> concurrentHashMap = new ConcurrentHashMap<>();

    public void setIsLogin(boolean b) {
        loginLock.lock();
        try {
            isLogin = b;
        } finally {
            loginLock.unlock();
        }
    }

    public boolean isLogin() {
        loginLock.lock();
        try {
            return isLogin;
        } finally {
            loginLock.unlock();
        }
    }


    public static class Holder {
        static Connection instance = new Connection();
    }

    public static Connection getInstance() {
        return Holder.instance;
    }


    private void initExecutors() {
        if (executorService == null) {
            executorLock.lock();
            try {
                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();
                }
            } finally {
                executorLock.unlock();
            }
        }
    }


    public int connection(String userName, String password) {
        lock.lock();
        try {
            int retry = PfUtils.getRetrytime();
            while (!isConnected()) {
                if (retry < 0) {
                    Log.d(TAG, "connect error :");
                    return CONNECTION_ERROR;
                }
                retry--;
                socket = new Socket();
                String ip = PfUtils.getIP();
                int port = PfUtils.getPort();
                socket.connect(new InetSocketAddress(ip, port), PfUtils.getTimeout() * 1000);
            }
            socket.setTcpNoDelay(true);
            Log.d(TAG, "CONNECTION!!!");
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), "GBK"));
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), "UTF-8")), true);
            String loginCmd = RequestUtil.createLoginCmd(userName, password);
            printWriter.println(loginCmd);
            printWriter.flush();
            Log.d(TAG, "loginCmd" + loginCmd);
            long start = System.currentTimeMillis();
            socket.setSoTimeout(3000);
            String readLine = null;
            while (System.currentTimeMillis() - start < 3000 && (readLine = reader.readLine()) != null) {
                Log.d(TAG, "start wait");
                Log.d(TAG, "readLine" + readLine);
                if (ResultUtil.verifyResult(readLine) && "00".equalsIgnoreCase(ResultUtil.getNo(readLine))) {
                    if (ResultUtil.isNoData(readLine)) {
                        isLogin = false;
                        PfUtils.saveLogin(false);
                        stopSchedule();
                        stopConnection();
                        App.getInstance().disconnect();
                        SystemUtil.clearNotification(App.getInstance());
                        return LOGIN_ERROR;
                    } else {
                        socket.setSoTimeout(0);
                        socket.setKeepAlive(true);
                        App.getInstance().initDBHelper(userName);
                        startDeal();
                        return LOGIN_SUCCESS;
                    }
                }
            }
            stopConnection();
            return LOGIN_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            stopConnection();
            return CONNECTION_ERROR;
        } finally {
            lock.unlock();
        }
    }


    private void startDeal() {
        initExecutors();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = null;
                    while (isConnected() && (message = reader.readLine()) != null) {
                        Log.d(TAG, "receive message :" + message);
                        if (!TextUtils.isEmpty(message) && ResultUtil.verifyResult(message)) {
                            String no = ResultUtil.getNo(message);
                            if (TextUtils.isEmpty(no)) {
                                continue;
                            }
                            BlockingQueue<String> queue = getQueue(no);
                            Log.d(TAG, "no : " + no);
                            Log.d(TAG, "concurrentHashMap" + concurrentHashMap.keySet().size());
                            if (queue != null) {
                                queue.add(message);
                            }
                            if (message.startsWith("Start00>>>")) {
                                dealLoginResult(message);
                            } else if (message.startsWith("Start20>>>")) {
                                dealMessage(message);
                            } else if (message.startsWith("Start")) {
                                dealDataMessage(message);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    stopConnection();
                }
            }
        });

    }


    private void dealLoginResult(String message) {

    }

    public ArrayList<ExClass> dealDataMessage(String message) {
        String content = ResultUtil.getContent(message);
        Log.d(TAG, "content :" + content);
        if (content != null && content.length() > 0) {
            ArrayList<ExClass> exClasses = new ArrayList<>();
            HashMap<String, ArrayList<ExGroup>> map = new HashMap<>();
            String[] result = content.substring(2, content.length() - 2).split(Constants.REGEX);
            Log.d(TAG, "result size :" + result.length);
            for (String s : result) {
                try {
                    String[] bean = s.split(Constants.REGEX_SEPARATOR);
                    if (bean.length == 8) {
                        ExGroup exGroup = new ExGroup(bean[0], bean[1], bean[2], bean[3], bean[4], bean[5], bean[6], bean[7]);
                        ArrayList<ExGroup> groups = map.get(exGroup.getType());
                        if (groups == null) {
                            groups = new ArrayList<>();
                            groups.add(exGroup);
                            map.put(exGroup.getType(), groups);
                        } else {
                            groups.add(exGroup);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, ArrayList<ExGroup>> entry : map.entrySet()) {
                ArrayList<ExGroup> exGroups = entry.getValue();
                int unCount = 0;
                for (ExGroup exGroup : exGroups) {
                    if (!TextUtils.isEmpty(exGroup.getDealState()) && exGroup.getDealState().startsWith("0")) {
                        unCount++;
                    }
                }
                exClasses.add(new ExClass(entry.getKey(), exGroups.size(), unCount, exGroups));
            }
            Log.d(TAG, "exClasses Size :" + exClasses.size());
            Collections.sort(exClasses, new Comparator<ExClass>() {
                @Override
                public int compare(ExClass o1, ExClass o2) {
                    return o2.getCount() - o1.getCount();
                }
            });
            EventBus.getDefault().removeStickyEvent(MessageDemoEvent.class);
            EventBus.getDefault().postSticky(new MessageDemoEvent(exClasses));
            return exClasses;
        }
        return null;
    }


    private void dealMessage(String message) {
        String content = ResultUtil.getContent(message);
        final String no = ResultUtil.getNo(message);
        initExecutors();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sendMessage(RequestUtil.createRevPush(no));
            }
        });
        if (App.getInstance().dbHelper != null) {
            App.getInstance().dbHelper.saveMessage(new PushMessage(null, content, System.currentTimeMillis()));
            App.noticeMessage(content);
            EventBus.getDefault().post(new NewMessageEvent(message));
        }

    }

    public void stopConnection() {
        lock.lock();
        try {
            if (socket != null) {
                Log.d(TAG, "stopConnection");
                // unSubscribe();
                if (executorService != null) {
                    executorService.shutdown();
                    executorService.shutdownNow();
                }
                executorService = null;
                IOUtils.close(printWriter);
                IOUtils.close(reader);
                printWriter = null;
                reader = null;
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(TextUtils.isEmpty(PfUtils.getUsername())||TextUtils.isEmpty(PfUtils.getPassword())){
                isLogin = false;
            }

        } finally {
            lock.unlock();
        }
    }


    public void sendAsyncMessage(final String message) {
        initExecutors();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sendMessage(message);
            }
        });
    }


    public int sendMessage(String message) {
        lock.lock();
        try {
            if (isConnected()) {
                printMessage(message);
                return REQUEST_SUCCESS;
            } else {
                String username = PfUtils.getUsername();
                String password = PfUtils.getPassword();
                if ((isLogin || PfUtils.isLogin()) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    int login = connection(username, password);
                    if (login == LOGIN_SUCCESS) {
                        printMessage(message);
                        return REQUEST_SUCCESS;
                    } else if (login == LOGIN_ERROR) {
                        return LOGIN_ERROR;
                    } else {
                        return REQUEST_FAIL;
                    }
                }
                return LOGIN_ERROR;
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendCallBackMessage(final String no, final String message, final IMessageCallBack messageCallBack) {

        Log.d(TAG, "sendCallBackMessage");
        Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                Log.d(TAG, "ThreadName : " + Thread.currentThread().getName());
                ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
                String result = null;
                try {
                    putHashMap(queue, no);
                    int sendMessage = sendMessage(message);
                    if (REQUEST_SUCCESS == sendMessage) {
                        result = queue.poll(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
                    } else if (LOGIN_ERROR == sendMessage) {
                        e.onError(new LoginErrorException(""));
                        return;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    removeHashMap(no);
                }

                Log.d(TAG, "RESULT : " + result);
                if (!TextUtils.isEmpty(result)) {
                    e.onNext(result);
                    e.onComplete();
                } else {
                    e.onError(new RequestException("Result is TimeOut!"));
                }

            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        messageCallBack.call(s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t instanceof LoginErrorException) {
                            messageCallBack.onLogin();
                        }
                        messageCallBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addSubscribe(disposable);
    }


    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

    private void removeHashMap(String no) {
        hashMap.lock();
        try {
            concurrentHashMap.remove(no);
        } finally {
            hashMap.unlock();
        }
    }

    private void putHashMap(ArrayBlockingQueue<String> queue, String no) {
        hashMap.lock();
        try {
            concurrentHashMap.put(no, queue);
        } finally {
            hashMap.unlock();
        }
    }

    private BlockingQueue<String> getQueue(String no) {
        hashMap.lock();
        try {
            return concurrentHashMap.get(no);
        } finally {
            hashMap.unlock();
        }
    }

    private void clearHashMap() {
        hashMap.lock();
        try {
            concurrentHashMap.clear();
        } finally {
            hashMap.unlock();
        }
    }

    private void printMessage(String message) {
        if (printWriter != null) {
            lock.lock();
            try {
                printWriter.println(message);
                Log.d(TAG, "send Message : " + message);
                printWriter.flush();
            } catch (Exception e){
                stopConnection();
            }finally {
                lock.unlock();
            }
        }
    }


    public boolean isConnected() {
        return SystemUtil.isNetworkConnected() && socket != null && socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }


    public boolean isInputConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown();
    }


    public boolean isOutputConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && !socket.isOutputShutdown();
    }


    public void startSchedule() {
        if (isLogin || PfUtils.isLogin()) {
            if (!isSchedule) {
                Log.d(TAG, "startSchedule");
                SchedulerHelper.schedule(App.getInstance());
                scheduledExecutorService = Executors.newScheduledThreadPool(1);
                scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(RequestUtil.createTodayCmd());
                    }
                }, TimeUnit.SECONDS.toSeconds(PfUtils.getInterval()), TimeUnit.SECONDS.toSeconds(PfUtils.getInterval()), TimeUnit.SECONDS);
                isSchedule = true;
            }
        } else {
            SchedulerHelper.cancelJob(App.getInstance());
        }

    }

    public boolean isSchedule() {
        return isSchedule;
    }

    public void stopSchedule() {
        isSchedule = false;
        Log.d(TAG, "stopSchedule");
        SchedulerHelper.cancelJob(App.getInstance());
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                scheduledExecutorService.awaitTermination(0, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                scheduledExecutorService.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            scheduledExecutorService = null;
        }


    }


    public void restartScheduler() {
        if (isSchedule) {
            stopSchedule();
            startSchedule();
        }
    }


    public void sendLogin(final String mUserName, final String mPassword) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String cmd = RequestUtil.createLoginCmd(mUserName, mPassword);
                printWriter.println(cmd);
            }
        });

    }


}
