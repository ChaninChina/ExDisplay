package com.chanin.lincc.exdisplay;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.chanin.lincc.exdisplay.adapter.MessageDetailAdapter;
import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.model.MessageDetail;
import com.chanin.lincc.exdisplay.model.NewMessageEvent;
import com.chanin.lincc.exdisplay.model.PushMessage;
import com.chanin.lincc.exdisplay.utils.DBHelper;
import com.chanin.lincc.exdisplay.utils.ListUtils;
import com.chanin.lincc.exdisplay.utils.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends BaseActivity {

    private LinearLayout ll_title;
    private RecyclerView rv_message;
    private MessageDetailAdapter adapter;
    private Toolbar viewToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        SystemUtil.clearNotification(this);
        initView();
        setToolBar(viewToolbar,"消息详情",true);
        initData();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageEvent(NewMessageEvent newMessageEvent) {
        initData();
    }

    private void initData() {
        // adapter.addNewData(initTestData());
        List<PushMessage> messages =null;
        if(App.getInstance().dbHelper!=null){
            messages = App.getInstance().dbHelper.getMessage();
        }

        if (ListUtils.isEmpty(messages)) {
            ll_title.setVisibility(View.VISIBLE);
            rv_message.setVisibility(View.INVISIBLE);
        } else {
            ll_title.setVisibility(View.INVISIBLE);
            rv_message.setVisibility(View.VISIBLE);
            adapter.updateData(messages);
        }

    }


    public ArrayList<MessageDetail> initTestData() {
        ArrayList<MessageDetail> messageDetails = new ArrayList<>();
        for (int i = 10; i > 0; i--) {
            messageDetails.add(new MessageDetail(i + ".消息内容，这个可能有点短", "2018年7月5日"));
        }
        return messageDetails;
    }

    private void initView() {
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        rv_message = (RecyclerView) findViewById(R.id.rv_message);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        //layout.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layout.setReverseLayout(true);//列表翻转
        rv_message.setLayoutManager(layout);
        adapter = new MessageDetailAdapter(this);
        rv_message.setAdapter(adapter);
        viewToolbar = (Toolbar) findViewById(R.id.view_toolbar);
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
