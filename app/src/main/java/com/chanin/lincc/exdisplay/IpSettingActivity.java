package com.chanin.lincc.exdisplay;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chanin.lincc.exdisplay.utils.Constants;
import com.chanin.lincc.exdisplay.utils.PfUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpSettingActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar viewToolbar;
    private AppBarLayout viewAppbar;
    private TextInputEditText etIp;
    private Button btSave;
    private TextInputEditText etPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_setting);
        initView();
        setToolBar(viewToolbar, "IP设置", true);
    }

    private void initView() {
        viewToolbar = (Toolbar) findViewById(R.id.view_toolbar);
        viewAppbar = (AppBarLayout) findViewById(R.id.view_appbar);
        etIp = (TextInputEditText) findViewById(R.id.et_ip);
        btSave = (Button) findViewById(R.id.bt_save);
        etIp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    validateIP();
                }
            }
        });

        btSave.setOnClickListener(this);
        etPort = (TextInputEditText) findViewById(R.id.et_port);
        etPort.setOnClickListener(this);
        etIp.setText(PfUtils.getIP());
        etPort.setText(PfUtils.getPort()+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                submit();
                break;
        }
    }

    private boolean submit() {
        // validate
        String ip = etIp.getText().toString().trim();
        String port = etPort.getText().toString().trim();
        if (TextUtils.isEmpty(ip)||!isIP(ip)) {
            etIp.setError("请输入正确的IP地址");
            return false;
        }

        if(TextUtils.isEmpty(port)||!isPort(port)){
            etIp.setError("请输入正确的端口号");
            return false;
        }
        PfUtils.saveIP(ip);
        PfUtils.savePort(Integer.parseInt(port));
        this.finish();
        return true;

        // TODO validate success, do something


    }


    private boolean validateIP() {
        // validate
        String ip = etIp.getText().toString().trim();
        if (TextUtils.isEmpty(ip)||!isIP(ip)) {
            etIp.setError("请输入正确的IP地址");
            return false;
        }

        return true;
    }

    public static boolean isIP(String str){
        Pattern p = Pattern.compile(Constants.REGEX_IP);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isPort(String str){
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
