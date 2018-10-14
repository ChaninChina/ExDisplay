package com.chanin.lincc.exdisplay;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;
import com.chanin.lincc.exdisplay.utils.ActivityUtil;
import com.chanin.lincc.exdisplay.utils.DBHelper;
import com.chanin.lincc.exdisplay.utils.PfUtils;
import com.chanin.lincc.exdisplay.utils.SystemUtil;
import com.chanin.lincc.exdisplay.utils.ToastUtil;


public class SettingActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "SettingActivity";
    private TextInputEditText edInterval;
    private TextInputEditText edTimeOut;
    private TextInputEditText edRetryTime;
    private LinearLayout llClear;
    private LinearLayout llExit;
    private Toolbar view_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_setting);
        initView();
        setToolBar(view_toolbar, "设置", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        edInterval = (TextInputEditText) findViewById(R.id.ed_interval);
        edTimeOut = (TextInputEditText) findViewById(R.id.ed_time_out);
        edRetryTime = (TextInputEditText) findViewById(R.id.ed_retry_time);

        edInterval.setText(PfUtils.getInterval() + "");

        edInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    int value = getStringToInt(charSequence);
                    if (value > 3600 || value < 30) {
                        edInterval.setError(getString(R.string.tip_interval));
                    }
                } else {
                    edInterval.setError(getString(R.string.tip_interval));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edTimeOut.setText(PfUtils.getTimeout() + "");
        edTimeOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    int value = getStringToInt(charSequence);
                    if (value > 20 || value < 10) {
                        edTimeOut.setError(getString(R.string.tip_timeout));
                    }
                } else {
                    edTimeOut.setError(getString(R.string.tip_timeout));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edRetryTime.setText(PfUtils.getRetrytime() + "");
        edRetryTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    int value = getStringToInt(charSequence);
                    if (value > 5 || value < 0) {
                        edRetryTime.setError(getString(R.string.tip_retry_time));
                    }
                } else {
                    edRetryTime.setError(getString(R.string.tip_retry_time));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        llClear = (LinearLayout) findViewById(R.id.ll_clear);
        llClear.setOnClickListener(this);
        llExit = (LinearLayout) findViewById(R.id.ll_exit);
        llExit.setOnClickListener(this);
        view_toolbar = (Toolbar) findViewById(R.id.view_toolbar);
    }

    private int getStringToInt(String charSequence) {
        try {
            return Integer.parseInt(charSequence);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getStringToInt(CharSequence charSequence) {
        try {
            return Integer.parseInt(charSequence.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private boolean submit() {
        // validate
        String interval = edInterval.getText().toString().trim();
        if (TextUtils.isEmpty(interval) || getStringToInt(interval) > 3600 || getStringToInt(interval) < 30) {
            edRetryTime.setError(getString(R.string.tip_interval));
            return false;
        }

        String out = edTimeOut.getText().toString().trim();
        if (TextUtils.isEmpty(out) || getStringToInt(out) > 20 || getStringToInt(out) < 3) {
            edRetryTime.setError(getString(R.string.tip_timeout));
            return false;
        }

        String time = edRetryTime.getText().toString().trim();
        if (TextUtils.isEmpty(time) || getStringToInt(time) > 5 || getStringToInt(time) < 0) {
            edRetryTime.setError(getString(R.string.tip_retry_time));
            return false;
        }

        // TODO validate success, do something


        PfUtils.saveInterval(getStringToInt(interval));
        PfUtils.saveTimeout(getStringToInt(out));
        PfUtils.saveRetryTime(getStringToInt(time));
        return true;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_clear:
                showClearDialog();
                break;
            case R.id.ll_exit:
                showExitUserDialog();
                break;
        }

    }


    private void showExitUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("您确定退出当前用户？");
        builder.setNegativeButton("否", null);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PfUtils.saveLogin(false);
                Connection.getInstance().setIsLogin(false);
                Connection.getInstance().stopSchedule();
                Connection.getInstance().stopConnection();
                SystemUtil.clearNotification(SettingActivity.this);
                Intent intent1 = new Intent(SettingActivity.this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityUtil.startActivity(SettingActivity.this, intent1);
                SettingActivity.this.finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));

    }


    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("您确定清除所有数据？");
        builder.setNegativeButton("否", null);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtil.shortShow("数据已清除");
                if(App.getInstance().dbHelper!=null){
                    App.getInstance().dbHelper.clear();
                }

                SystemUtil.clearNotification(SettingActivity.this);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if (submit()) {
                    ToastUtil.shortShow("保存成功！");
                    finish();
                } else {
                    ToastUtil.shortShow("保存失败！");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
