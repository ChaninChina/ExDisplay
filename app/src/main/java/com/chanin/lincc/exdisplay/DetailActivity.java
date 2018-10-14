package com.chanin.lincc.exdisplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chanin.lincc.exdisplay.model.ExGroup;


public class DetailActivity extends BaseActivity implements View.OnClickListener {


    private static final String DATA = "DATA";
    private TextView tvContent;
    private Button btBack;
    private TextView tvDealState;
    private TextView tvRecord;
    private TextView tvRemark;
    private TextView tvDealTime;
    private Toolbar viewToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("异常详情");
        initView();
        setToolBar(viewToolbar,"异常详情",true);
        ExGroup exGroup = getIntent().getParcelableExtra(DATA);
        if (exGroup != null) {
            tvContent.setText(deleteNull(exGroup.getDetail()).replaceAll(";", "\n") + "");
            String substring = "";
            try {
                substring = deleteNull(exGroup.getDealState()).substring(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvDealState.setText(substring + "");
            tvRemark.setText(deleteNull(exGroup.getDealRemark()) + "");
            tvDealTime.setText(deleteNull(exGroup.getDealTime()) + "");
            tvRecord.setText(deleteNull(exGroup.getRecordTime()) + "");
        }
    }

    public String deleteNull(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.replace("<NULL>", "");
        }
        return str;
    }


    public static void startContent(Context context, ExGroup exGroup) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DATA, exGroup);
        context.startActivity(intent);
    }


    private void initView() {

        tvContent = (TextView) findViewById(R.id.tv_content);
        btBack = (Button) findViewById(R.id.bt_back);
        btBack.setOnClickListener(this);
        tvDealState = (TextView) findViewById(R.id.tv_deal_state);
        tvRecord = (TextView) findViewById(R.id.tv_record);
        tvRemark = (TextView) findViewById(R.id.tv_remark);
        tvDealTime = (TextView) findViewById(R.id.tv_deal_time);

        viewToolbar = (Toolbar) findViewById(R.id.view_toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
