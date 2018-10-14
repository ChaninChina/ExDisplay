package com.chanin.lincc.exdisplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;
import com.chanin.lincc.exdisplay.connect.IMessageCallBack;
import com.chanin.lincc.exdisplay.service.ConnectService;
import com.chanin.lincc.exdisplay.utils.ActivityUtil;
import com.chanin.lincc.exdisplay.utils.PfUtils;
import com.chanin.lincc.exdisplay.utils.RequestUtil;
import com.chanin.lincc.exdisplay.utils.ResultUtil;
import com.chanin.lincc.exdisplay.utils.SystemUtil;
import com.chanin.lincc.exdisplay.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";
    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private UserLoginTask mAuthTask;
    private CheckBox cbSave;
    private ImageView ivLogo;
    private int times;
    private long last_time;
    private long durtion = 3000;

    private boolean isBind;
    private ConnectService service;
    private BlockingQueue<String> blockingDeque = new ArrayBlockingQueue<>(1);

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            ConnectService.ServiceBinder myBinder = (ConnectService.ServiceBinder) binder;
            service = myBinder.getService();
            Log.d(TAG, " - onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.i(TAG, "  onServiceDisconnected");
        }
    };
    private Toolbar view_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        view_toolbar.setTitle(getResources().getString(R.string.title_activity_login));
        setSupportActionBar(view_toolbar);
        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.tv_username);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - last_time <= durtion) {
                    times--;
                    if (times == 0) {
                        times = 3;
                        Intent intent = new Intent(LoginActivity.this, IpSettingActivity.class);
                        ActivityUtil.startActivity(LoginActivity.this, intent);
                    }
                } else {
                    last_time = System.currentTimeMillis();
                    times = 3;
                }
            }
        });
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.tv_password);
        cbSave = findViewById(R.id.cb_save);
        cbSave.setChecked(PfUtils.getSaveSate());
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                // App.noticeMessage("This is Error");
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        if (!TextUtils.isEmpty(PfUtils.getUsername())) {
            mUserNameView.setText(PfUtils.getUsername());
        }

        if (!TextUtils.isEmpty(PfUtils.getPassword())) {
            mPasswordView.setText(PfUtils.getPassword());
        }

        Intent intent = new Intent(this, ConnectService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        if (Connection.getInstance().isLogin()) {
            MainActivity.startMainActivity(this);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!Connection.getInstance().isLogin()) {
            service.stopConnection();
            unbindService(conn);
            Intent intent = new Intent(this, ConnectService.class);
            stopService(intent);
        } else {
            unbindService(conn);
        }


    }

    private void populateAutoComplete() {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("zhangsan");
        strings.add("lisi");
        strings.add("wangwu");
        addUsersToAutoComplete(strings);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (!SystemUtil.isNetworkConnected()) {
            ToastUtil.shortShow(getString(R.string.network_error));
            return;
        }


        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUserNameView.setError(getString(R.string.error_invalid_username));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
            // login(username,password);
        }
    }


    private boolean isEmailValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void addUsersToAutoComplete(List<String> userNames) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, userNames);
        mUserNameView.setAdapter(adapter);
    }

    private void initView() {
        view_toolbar = (Toolbar) findViewById(R.id.view_toolbar);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUserName;
        private final String mPassword;


        UserLoginTask(String userName, String password) {
            mUserName = userName;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            Integer integer = service.startConnection(mUserName, mPassword);
            if (Connection.LOGIN_SUCCESS == integer) {
                service.setIsLogin(true);
            }
            return integer;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            mAuthTask = null;
            showProgress(false);
            if (Connection.LOGIN_SUCCESS == result) {
                MainActivity.startMainActivity(LoginActivity.this);
                LoginActivity.this.finish();
                if (cbSave.isChecked()) {
                    PfUtils.saveUser(mUserName, mPassword);
                    PfUtils.saveSaveSate(true);
                    PfUtils.saveLogin(true);
                } else {
                    PfUtils.saveLogin(false);
                    PfUtils.clearUser();
                    PfUtils.saveSaveSate(false);

                }
            } else if (Connection.LOGIN_ERROR == result) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else {
                ToastUtil.shortShow(getString(R.string.request_failed));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

