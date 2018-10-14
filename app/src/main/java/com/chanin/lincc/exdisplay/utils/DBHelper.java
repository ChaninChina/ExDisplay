package com.chanin.lincc.exdisplay.utils;

import android.text.TextUtils;
import android.util.Log;

import com.chanin.lcc.db.DaoMaster;
import com.chanin.lcc.db.DaoSession;
import com.chanin.lcc.db.PushMessageDao;
import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.model.PushMessage;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DBHelper {

    private static final String DB_NAME = "db";
    private static final String TAG = "DBHelper";
    private final Database writableDb;
    private final DaoSession daoSession;
    private static DBHelper dbHelper;
    private String userName;

//    public static class INNER{
//        public static DBHelper dbHelper = new DBHelper(App.getInstance());
//
//    }
//
//    public static DBHelper getInstance(){
//        return INNER.dbHelper;
//    }
    public void disconnect(){
        this.userName=null;
        dbHelper = null;
    }

    public static DBHelper getInstance(String userName){
        if(dbHelper!=null&&dbHelper.userName.equalsIgnoreCase(userName)){
            return dbHelper;
        }else {
             dbHelper = new DBHelper(App.getInstance(), userName);
            return dbHelper;
        }

    }


    public DBHelper(App app,String userName){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(app, DB_NAME+userName);
        writableDb = devOpenHelper.getWritableDb();
        daoSession = new DaoMaster(writableDb).newSession();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        this.userName = userName;
    }

    public void saveMessage(PushMessage pushMessage){
        try {
            daoSession.getPushMessageDao().insert(pushMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"saveMessage error");
        }
    }

    public List<PushMessage> getMessage(){
        try {
            return daoSession.getPushMessageDao().queryBuilder().orderDesc(PushMessageDao.Properties.Time).limit(1000).list();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"getMessage error");
        }
        return null;
    }

    public boolean clear(){
        try {
            daoSession.getPushMessageDao().deleteAll();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"deleteAll error");
            return false;
        }
    }



}
