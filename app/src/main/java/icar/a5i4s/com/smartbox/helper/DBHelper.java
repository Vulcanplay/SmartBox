package icar.a5i4s.com.smartbox.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hxsd on 2015/7/6.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;//數據庫版本
    private static final String NAME = "orderRepair.db";//數據庫名稱

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists orders (" +
                "id integer primary key autoincrement," +
                "orderId varchar(20)," +
                "appName varchar(20)," +
                "transId varchar(20)," +
                "resultCode varchar(5)," +
                "resultMsg varchar(20)," +
                "transData varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本更新時執行
    }
}
