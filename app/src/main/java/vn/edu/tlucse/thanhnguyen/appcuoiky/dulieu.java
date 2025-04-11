package vn.edu.tlucse.thanhnguyen.appcuoiky;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;

public class dulieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dulieucuahang.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_KHACHHANG = "taikhoankhachhang";
    public static final String COLUMN_TAIKHOAN = "taikhoan";
    public static final String COLUMN_MATKHAU = "matkhau";
    public static final String COLUMN_HOTEN = "hoten";
    public static final String COLUMN_SDT = "sodienthoai";
    public static final String COLUMN_DIACHI = "diachi";



    public dulieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_KHACHHANG + " ("
                + COLUMN_TAIKHOAN + " TEXT PRIMARY KEY, "
                + COLUMN_MATKHAU + " TEXT, "
                + COLUMN_HOTEN + " TEXT, "
                + COLUMN_SDT + " TEXT, "
                + COLUMN_DIACHI + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KHACHHANG);
        onCreate(db);
    }

    public boolean insertKhachHang(String taikhoan, String matkhau, String hoten, String sdt, String diachi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TAIKHOAN, taikhoan);
        values.put(COLUMN_MATKHAU, matkhau);
        values.put(COLUMN_HOTEN, hoten);
        values.put(COLUMN_SDT, sdt);
        values.put(COLUMN_DIACHI, diachi);
        long result = db.insert(TABLE_KHACHHANG, null, values);
        return result != -1;
    }

    public Cursor checkLogin(String taikhoan, String matkhau) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_KHACHHANG +
                " WHERE taikhoan=? AND matkhau=?", new String[]{taikhoan, matkhau});
    }

}








