package com.chanin.lincc.exdisplay;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.chanin.lincc.exdisplay.adapter.MainAdapter;
import com.chanin.lincc.exdisplay.connect.IMessageCallBack;
import com.chanin.lincc.exdisplay.model.MessageDemoEvent;
import com.chanin.lincc.exdisplay.service.ConnectService;
import com.chanin.lincc.exdisplay.utils.ActivityUtil;
import com.chanin.lincc.exdisplay.utils.DateUtil;
import com.chanin.lincc.exdisplay.utils.RequestUtil;
import com.chanin.lincc.exdisplay.utils.SystemUtil;
import com.chanin.lincc.exdisplay.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private RadioButton bt_day;
    private RadioButton bt_week;
    private RadioButton bt_month;
    private RecyclerView rl_content;
    private LinearLayout ll_title;
    private MainAdapter adapter;

    private boolean isBind;
    private ConnectService service;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            ConnectService.ServiceBinder myBinder = (ConnectService.ServiceBinder) binder;
            service = myBinder.getService();
            String today = DateUtil.getToday();
            bt_day.setText(String.format(getResources().getString(R.string.day), today.substring(today.indexOf("-") + 1)));
            sendTodayCmd(today);
            bt_day.setSelected(true);
            bt_week.setSelected(false);
            bt_month.setSelected(false);
            service.startSchedule();
            Log.d(TAG, " - onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.i(TAG, "  onServiceDisconnected");
        }
    };
    private ProgressDialog progressDialog;
    private DatePickerDialog datePickerDialog;
    private Toolbar viewToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ConnectService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        initView();
        setToolBar(viewToolbar, getResources().getString(R.string.app_name), false);
        if (savedInstanceState == null) {

        }
        //initData(3);
    }



//    private void initData(int len) {
//        ArrayList<ExClass> exClasses = createTestData(len);
//        adapter.setDatas(exClasses);
//    }

//    @NonNull
//    private ArrayList<ExClass> createTestData(int len) {
//        ArrayList<ExClass> exClasses = new ArrayList<>();
//        String[] str = {"A","B","C","D","E","F","G","H","I","J","K"};
//        String[] types = {"超时","提前","正常"};
//        for (int i = len; i > 0; i--) {
//            ArrayList<ExGroup> exGroups = new ArrayList<>();
//            for (int j = 0; j < 6; j++) {
//                exGroups.add(new ExGroup("名称"+str[j],j%3,types[j%3],"当前处理状态"));
//            }
//            exClasses.add(new ExClass("异常类型"+str[i],i,exGroups));
//        }
//        return exClasses;
//    }

    public void initProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.load));

    }

    public void showProgressBar() {
        if (progressDialog == null) {
            initProgressBar();
        }
        progressDialog.show();
    }

    public void dissmissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        if (isBind) {
            unbindService(conn);
        }
        super.onDestroy();
        dissmissProgressBar();
        Log.d(TAG, "onDestroy");

    }

    private void initView() {
        bt_day = (RadioButton) findViewById(R.id.bt_day);
        bt_week = (RadioButton) findViewById(R.id.bt_week);
        bt_month = (RadioButton) findViewById(R.id.bt_month);
        rl_content = (RecyclerView) findViewById(R.id.rl_content);

        bt_day.setOnClickListener(this);
        bt_week.setOnClickListener(this);
        bt_month.setOnClickListener(this);
        rl_content.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this);
        rl_content.setAdapter(adapter);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        //ll_title.setOnClickListener(this);
        viewToolbar = (Toolbar) findViewById(R.id.view_toolbar);
    }

    @Override
    public void onClick(View v) {
        if (!SystemUtil.isNetworkConnect()) {
            Log.d(TAG, "isNetworkConnect : false");
            return;
        }
        switch (v.getId()) {
            case R.id.bt_day:
                selectDate();
                break;
            case R.id.bt_week:
                sendWeekCmd();
                break;
            case R.id.bt_month:
                sendMonthCmd();
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    public void selectDate() {
        if (datePickerDialog == null) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    int i = month + 1;
                    String m = null;
                    if (i < 10) {
                        m = "0" + i;
                    } else {
                        m = "" + i;
                    }
                    String day = year + "-" + m + "-" + dayOfMonth;
                    Log.d(TAG, day);
                    sendTodayCmd(day);
                }
            }, year, month, day);
            DatePicker datePicker = datePickerDialog.getDatePicker();
            calendar.set(year,month,day+1,0,0,0);
            datePicker.setMaxDate(calendar.getTime().getTime());
            datePickerDialog.create();
            datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
            datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        datePickerDialog.show();

    }

    public void sendTodayCmd(final String day) {
        showProgressBar();
        service.sendCallBackMessage("60", RequestUtil.createDayCmd(day), new IMessageCallBack() {
            @Override
            public void call(String str) {
                dissmissProgressBar();
                if (DateUtil.getToday().equalsIgnoreCase(day)) {
                    service.startSchedule();
                } else {
                    service.stopSchedule();
                }
                String text = "一天";
                try {
                    text = String.format(getResources().getString(R.string.day), day.substring(day.indexOf("-") + 1));
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                bt_day.setText(text);
                bt_day.setSelected(true);
                bt_week.setSelected(false);
                bt_month.setSelected(false);

            }

            @Override
            public void error() {
                ToastUtil.shortShow(getString(R.string.request_failed));
                dissmissProgressBar();
            }

            @Override
            public void onLogin() {
                ToastUtil.shortShow(getString(R.string.login_error));
                ActivityUtil.startLoginAgain(MainActivity.this);
            }
        });


    }

    public void sendWeekCmd() {
        showProgressBar();
        service.sendCallBackMessage("70", RequestUtil.createWeekCmd(), new IMessageCallBack() {
            @Override
            public void call(String str) {
                dissmissProgressBar();
                service.stopSchedule();
                bt_day.setSelected(false);
                bt_week.setSelected(true);
                bt_month.setSelected(false);
            }

            @Override
            public void error() {
                ToastUtil.shortShow(getString(R.string.request_failed));
                dissmissProgressBar();
            }

            @Override
            public void onLogin() {
                ToastUtil.shortShow(getString(R.string.login_error));
                ActivityUtil.startLoginAgain(MainActivity.this);
            }
        });
    }

    public void sendMonthCmd() {
        showProgressBar();
        service.sendCallBackMessage("80", RequestUtil.createMonthCmd(), new IMessageCallBack() {
            @Override
            public void call(String str) {
                dissmissProgressBar();
                service.stopSchedule();
                bt_day.setSelected(false);
                bt_week.setSelected(false);
                bt_month.setSelected(true);
            }

            @Override
            public void error() {
                ToastUtil.shortShow(getString(R.string.request_failed));
                dissmissProgressBar();
            }

            @Override
            public void onLogin() {
                ToastUtil.shortShow(getString(R.string.login_error));
                ActivityUtil.startLoginAgain(MainActivity.this);
            }
        });
    }


    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        ActivityUtil.startActivity(context,intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_message) {
            Intent intent = new Intent(this, MessageDetailActivity.class);
            ActivityUtil.startActivity(this, intent);
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            ActivityUtil.startActivity(this, intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageRecveivedEvent(MessageDemoEvent event) {
        Log.d(TAG, "onMessageRecveivedEvent");
        if (event.getDatas() != null && event.getDatas().size() == 0) {
            ll_title.setVisibility(View.VISIBLE);
            rl_content.setVisibility(View.INVISIBLE);
            adapter.setDatas(event.getDatas());
        } else {
            ll_title.setVisibility(View.INVISIBLE);
            rl_content.setVisibility(View.VISIBLE);
            adapter.setDatas(event.getDatas());
        }
        EventBus.getDefault().removeStickyEvent(MessageDemoEvent.class);
    }
}
