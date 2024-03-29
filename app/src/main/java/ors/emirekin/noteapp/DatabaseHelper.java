package ors.emirekin.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "list.db";
    public static final String TABLE_NAME = "list_table";
    public static final String COL1 = "TITLE";
    public static final String COL2 = "CONTENT";

    String tag = "kontrolnoktası";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(tag,"db Database, onCreate called.");
        db.execSQL("create table " + TABLE_NAME + " (TITLE TEXT, CONTENT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        int position = MainActivity.titleArray.size() - 1;

        Log.i(tag,"Insert Data --> Position = " + position);
        Log.i(tag,"----------------");
        contentValues.put(COL1, MainActivity.titleArray.get(position));
        contentValues.put(COL2, MainActivity.contentArray.get(position));

        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }

    public boolean updateData(int position){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL1,MainActivity.titleArray.get(position));
        contentValues.put(COL2,MainActivity.contentArray.get(position));

        db.update(TABLE_NAME,contentValues,"CONTENT = ?",new String[] { NoteActivity.prevContent });
        //db.execSQL("update " + TABLE_NAME + " set CONTENT = " + MainActivity.contentArray.get(position) + " where ");
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME,null);

        return res;
    }

    public Integer deleteData(int position){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"CONTENT = ?",new String[] { MainActivity.contentArray.get(position) });
    }

    public void deleteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"1",null);
    }

}

















