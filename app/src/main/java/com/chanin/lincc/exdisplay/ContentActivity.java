package com.chanin.lincc.exdisplay;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chanin.lincc.exdisplay.adapter.ContentAdapter;
import com.chanin.lincc.exdisplay.model.ExClass;
import com.chanin.lincc.exdisplay.model.ExGroup;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_name;
    private TextView tv_count;
    private RecyclerView rl_content;
    private ContentAdapter adapter;
    private Button bt_back;
    private CheckBox cb_all;
    private CheckBox cb_undo;
    private CheckBox cb_do;
    private ArrayList<ExGroup> groups;
    private ArrayList<ExGroup> undoGroups;
    private ArrayList<ExGroup> doGroups;
    private int count = 3;
    private Toolbar viewToolbar;
    private ExClass exClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        Intent intent = this.getIntent();
        String data = intent.getStringExtra("data");
        exClass = new Gson().fromJson(data, ExClass.class);
        setToolBar(viewToolbar,exClass.getName(),true);
        initData();
    }

    private void initData() {

        tv_name.setText(exClass.getName());
        groups = exClass.getGroups();
        undoGroups = new ArrayList<>();
        doGroups = new ArrayList<>();
        for (ExGroup group : groups) {
            if ("1".equals(group.getDealState().substring(0, 1))) {
                doGroups.add(group);
            } else {
                undoGroups.add(group);
            }
        }
        showAll();
    }

    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_count = (TextView) findViewById(R.id.tv_count);
        rl_content = (RecyclerView) findViewById(R.id.rl_content);
        rl_content.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(this);
        rl_content.setAdapter(adapter);
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_back.setOnClickListener(this);
        cb_all = (CheckBox) findViewById(R.id.cb_all);
        cb_undo = (CheckBox) findViewById(R.id.cb_undo);
        cb_do = (CheckBox) findViewById(R.id.cb_do);

        cb_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_undo.isChecked()) {
                    cb_all.setChecked(false);
                    count = count - 2;
                } else {
                    count = count + 2;
                    if (count == 3) {
                        cb_all.setChecked(true);
                    }
                }
                showData();
            }
        });
        cb_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_do.isChecked()) {
                    cb_all.setChecked(false);
                    count = count - 1;
                } else {
                    count = count + 1;
                    if (count == 3) {
                        cb_all.setChecked(true);
                    }
                }
                showData();

            }
        });

        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_all.isChecked()) {
                    cb_undo.setChecked(false);
                    cb_do.setChecked(false);
                    count = 0;
                } else {
                    cb_undo.setChecked(true);
                    cb_do.setChecked(true);
                    count = 3;
                }
                showData();
            }
        });


        viewToolbar = (Toolbar) findViewById(R.id.view_toolbar);

    }


    public void showAll() {
        adapter.setDatas(groups);
        tv_count.setText("条数:" + groups.size() + "");
    }

    public void showNull() {
        adapter.setDatas(null);
        tv_count.setText("条数:0");
    }

    public void showUnDo() {
        adapter.setDatas(undoGroups);
        tv_count.setText("条数:" + undoGroups.size() + "");
    }

    public void showDo() {
        adapter.setDatas(doGroups);
        tv_count.setText("条数:" + doGroups.size() + "");
    }

    public void showData() {
        switch (count) {
            case 3:
                showAll();
                return;
            case 2:
                showUnDo();
                return;
            case 1:
                showDo();
                return;
            case 0:
                showNull();
                return;
        }


    }

    public static void startContent(Context context, ExClass exClass) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("data", new Gson().toJson(exClass));
        context.startActivity(intent);
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
