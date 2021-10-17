package com.example.chattomate.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import com.example.chattomate.users.*;

import java.util.ArrayList;
import java.util.List;

public class DBChattomate extends SQLiteOpenHelper {
    private static final String DATABASE_NAME   = "chattomate.db";
    private final String SLIDER_TABLE_NAME      = "tableChattomate";
    private final String IMAGE_SLIDER           = "imageSlider";
    private final String TITLE                  = "title";
    private final String TEXT_CONTENT           = "content";

    /* --------------------Tạo table USER_NAME------------------------- */
    private final String USER_TABLE_NAME        = "tableUser";
    private final String USER_FIRST_NAME        = "firstName";
    private final String USER_LAST_NAME         = "lastName";
    private final String USER_NAME              = "userName";
    private final String USER_PASSWORD          = "password";

    /* --------------------Tạo table USER_EMAIL------------------------- */
    private final String USER_TABLE_EMAIL       = "tableEmail";
    private final String USER_EMAIL             = "email";
    private final String USER_EMAIL_NAME        = "emailName";

    private Context context;
    public DBChattomate(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    private String createTableSlider() {
        return "CREATE TABLE " + SLIDER_TABLE_NAME + " (" +
                IMAGE_SLIDER + " BLOB, " +
                TITLE           + " TEXT, " +
                TEXT_CONTENT    + " TEXT )";
    }

    public String createTableUser() {
        return "CREATE TABLE " + USER_TABLE_NAME + " ( " +
                USER_FIRST_NAME         + " TEXT, " +
                USER_LAST_NAME          + " TEXT, " +
                USER_NAME + " TEXT PRIMARY KEY, " +
                USER_EMAIL          + " TEXT, " +
                USER_PASSWORD           + " TEXT )";
    }

    public String createTableEmail() {
        return "CREATE TABLE " + USER_TABLE_EMAIL + " ( " +
                USER_EMAIL + " TEXT PRIMARY KEY, " +
                USER_EMAIL_NAME         + " TEXT )";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSlider());
        db.execSQL(createTableUser());
        db.execSQL(createTableEmail());
        Toast.makeText(context, "Create table successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + SLIDER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_EMAIL);
        // Và tạo lại.
        onCreate(db);
    }

//    public void deleteDataTable() {
//        SQLiteDatabase db = this.getWritableDatabase();
//    }

    // Insert data in database
    public void insertDataSlider(byte[] image, String sliderTitle, String textContent) {
        String sql = "INSERT INTO " + SLIDER_TABLE_NAME + " VALUES( ?, ?, ? )";
        // vì là lưu hình ảnh nên phải sử dụng SQLiteStatement
        SQLiteStatement statement = this.getWritableDatabase().compileStatement(sql);
        // khi mà byte dữ liệu xong rồi thì xóa đi
        statement.clearBindings();
        // vị trí thử 0 để lưu id ko thể chọn đc
        statement.bindBlob(1, image);
        statement.bindString(2, sliderTitle);
        statement.bindString(3, textContent);

        statement.executeInsert();
    }

    // get dữ liệu ra màn hình
//    public List<Slider> getAllDataSlider() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        List<Slider> list = new ArrayList<Slider>();
//        String sql = "SELECT * FROM " + SLIDER_TABLE_NAME;
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) {
//            do {
//                list.add(new Slider(
//                        cursor.getBlob(0),
//                        cursor.getString(1),
//                        cursor.getString(2)
//                ));
//            } while(cursor.moveToNext());
//        }
//
//        return list;
//    }

    /* -------------------------------code username table---------------------------------- */
    // insert du lieu nhap vao
    public boolean insertUser(UserName userName) {
        ContentValues values = new ContentValues();
        values.put(USER_FIRST_NAME, userName.getFirstName());
        values.put(USER_LAST_NAME, userName.getLastName());
        values.put(USER_NAME, userName.getUserName());
        values.put(USER_PASSWORD, userName.getPassword());
        long result = this.getWritableDatabase().insert(USER_TABLE_NAME, null, values);
        this.getWritableDatabase().insert(USER_TABLE_NAME, null, values);
        if(result == -1)
            return false;
        return true;
    }

    // kiểm tra xem username đã tồn tại hay chưa, nếu > 0 thì đã tồn tại và trả về true
    @SuppressLint("Recycle")
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_NAME + "=?";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {username});
        // nếu mà tồn tại thì nó sẽ trả về true
        return cursor.getCount() > 0;
    }

    /*
     * kiểm tra khi người dùng muốn login vào, nếu > 0 thì đúng và trả về true
     */
    @SuppressLint("Recycle")
    public boolean checkUserLogin(String username, String password) {
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_NAME + "=? and " + USER_PASSWORD + "=?";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {username, password});
        // nếu mà tồn tại user thì trả về true
        return cursor.getCount() > 0;
    }

    /* -------------------------------code user email table---------------------------------- */
    // insert du lieu nhap vao
    public boolean insertUserEmail(UserGoogle userGoogle) {
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, userGoogle.getUserEmail());
        values.put(USER_EMAIL_NAME, userGoogle.getName());
        long result = this.getWritableDatabase().insert(USER_TABLE_EMAIL, null, values);
        this.getWritableDatabase().insert(USER_TABLE_EMAIL, null, values);
        if(result == -1)
            return false;
        return true;
    }

    @SuppressLint("Recycle")
    public boolean checkEmailLogin(String email) {
        String sql = "SELECT * FROM " + USER_TABLE_EMAIL + " WHERE " + USER_EMAIL + "=?";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {email});
        // nếu mà tồn tại user thì trả về true
        return cursor.getCount() > 0;
    }

    /*
     * kiểm tra người dùng khi muốn lấy lại mật khẩu, nếu > 0 thì đúng và trả về true
     */
    @SuppressLint("Recycle")
    public boolean checkUserForgotPassword(String username) {
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_NAME + "=?"; //and " + USER_SECRET_QUESTION + "=? and " + USER_ANSWER + "=?";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {username});
        return cursor.getCount() > 0;
    }

    /*
     * lấy dữ liệu trong db trả cho người dùng sau khi login = username thành công
     */
    @SuppressLint("Recycle")
    public List<UserName> getDataUser(String username) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        List<UserName> listUserName = new ArrayList<UserName>();
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_NAME + "=?";
        Cursor cursor = dbRead.rawQuery(sql, new String[] {username});
        UserName userName = new UserName();
        // đưa con trỏ về vị trí đầu
        cursor.moveToFirst();
        userName.setFirstName(cursor.getString(0));
        userName.setLastName(cursor.getString(1));
        userName.setUserName(cursor.getString(2));
        userName.setPassword(cursor.getString(3));
        listUserName.add(userName);
        cursor.close();
        dbRead.close();
        return listUserName;
    }

    /*
     * lấy dữ liệu trong db trả cho người dùng sau khi login thành công
     * @ email: truyền vào email
     */
    @SuppressLint("Recycle")
    public List<UserGoogle> getDataUserGoogle(String userEmail) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        List<UserGoogle> userGoogles = new ArrayList<>();
        String sql = "SELECT * FROM " + USER_TABLE_EMAIL + " WHERE " + USER_EMAIL + "=?";
        Cursor cursor = dbRead.rawQuery(sql, new String[] {userEmail});
        UserGoogle userGoogle = new UserGoogle();
        // đưa con trỏ về vị trí đầu
        cursor.moveToFirst();
        userGoogle.setUserEmail(cursor.getString(0));
        userGoogle.setName(cursor.getString(1));
        userGoogles.add(userGoogle);
        cursor.close();
        dbRead.close();
        return userGoogles;
    }

    /*
     * Cập nhật lại mật khẩu, nếu > 0 thì đúng và trả về true
     */
    public boolean updatePassword(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(USER_PASSWORD, password);
        long result = this.getWritableDatabase().update(USER_TABLE_NAME, values, USER_NAME + "=?", new String[]{ username });
        if(result == -1) {
            return false;
        }
        return true;
    }
}
