package nottoworry.clickaway.connections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by sahil on 18/1/17.
 */

public abstract class mySqlDb extends SQLiteOpenHelper {

    private final String TABLE_CREATE_COMMAND, DB_NAME, TABLE_NAME;
    protected SQLiteDatabase db;

    @Deprecated
    public mySqlDb(Context context){
        super(context, "dummyDB", null, 3);
        TABLE_NAME= "dummyTable";
        TABLE_CREATE_COMMAND = "";
        DB_NAME = "dummyDB";
    }

    public mySqlDb(Context context, String TCQ, String DB_NAME, String TABLE_NAME){
        super(context, DB_NAME, null, 3);
        this.TABLE_CREATE_COMMAND = TCQ;
        this.DB_NAME = DB_NAME;
        this.TABLE_NAME = TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create our table
        db.execSQL(TABLE_CREATE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        Log.v("ONUPGRADE", "DROPING THE TABLE AND CREATING A NEW ONE!");

        //create a new one
        onCreate(db);
    }

    //delete data
    protected boolean deleteFromDB(String somethingUnique, String uniqueField) {
        try {
            db = getWritableDatabase();
            db.delete(TABLE_NAME, uniqueField + " = ? ",
                    new String[]{somethingUnique});
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean addData(ContentValues values){
        try{
            db = getWritableDatabase();
            db.insert(TABLE_NAME, null, values);
            db.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    protected boolean editData(String somethingUnique, String uniquefield, ContentValues values){
        try {
            deleteFromDB(somethingUnique, uniquefield);
            addData(values);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //@Deprecated
    protected Cursor getCursor(String somethingUnique, String uniqueField, String[] fields, @Nullable String order){
        try {
            db = getWritableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, uniqueField + " = ?", new String[]{somethingUnique}, null, null, order);
            Log.v("Cursor is ", "empty "+(cursor==null));
            return cursor;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(db!=null)
            db.close();
        super.finalize();
    }
}