package neutrinos.addme.databaseclass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.ModelClass.ModelClassChecklist;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Addme.db";
    public static final String TABLE_NAME = "checklist_table";
    public static final String ORGANIZATION_TABLE = "organization_table";
    public static final String SUBCATEGORY_TABLE = "subcategory_table";
    public static final String TABLE_NAME_TODO = "todolist_table";
    public static final String COL_1 = "ITEM_NAME";
    public static final String COL_2 = "ITEM_QUANTITY";
    public static final String COL_3 = "DATE";
    public static final String COL_4 = "UUID";
    public static final String ORG1 = "ID";
    public static final String ORG2 = "ORGANIZATION_ID";
    public static final String ORG3 = "IMAGE";
    public static final String ORG4 = "CREATED_DATE";
    public static final String ORG5 = "ORGANIZATION_NAME";
    public static final String CAT1 = "CATEGORY_ID";
    public static final String CAT2 = "ORGANIZATION_ID";
    public static final String CAT3 = "CATEGORY_IMAGE";
    public static final String CAT4 = "CAT_ORGANIZATION_NAME";
    public static final String CAT5 = "CATEGORY_NAME";
    public static final String FAVOURITE = "favourite_table";
    public static final String WISHLIST = "wishlist_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " + TABLE_NAME + "(ITEM_NAME VARCHAR  ,ITEM_QUANTITY TEXT, DATE TEXT, UUID TEXT PRIMARY KEY)");
        db.execSQL(" create table " + TABLE_NAME_TODO + "(ITEM_NAME VARCHAR  ,ITEM_QUANTITY TEXT, DATE TEXT, UUID TEXT PRIMARY KEY)");
        db.execSQL(" create table " + ORGANIZATION_TABLE + "(ID VARCHAR PRIMARY KEY ,ORGANIZATION_ID TEXT, IMAGE TEXT, CREATED_DATE VARCHAR ,ORGANIZATION_NAME VARCHAR)");
        db.execSQL(" create table " + SUBCATEGORY_TABLE + "(ID VARCHAR PRIMARY KEY ,ORGANIZATION_ID TEXT, IMAGE TEXT,ORGANIZATION_NAME VARCHAR,CATEGORY_NAME VARCHAR)");
        db.execSQL("CREATE TABLE "+FAVOURITE+ "(FILE_ID INTEGER PRIMARY KEY,ORG_NAME VARCHAR ,CATEGORYNAME VARCHAR,ORGANIZATION_IMAGE VARCHAR,CATEGORY_IMAGE VARCHAR,FILE_NAME VARCHAR,STATUS INTEGER)");
        db.execSQL("CREATE TABLE "+WISHLIST+ "(FILE_ID INTEGER PRIMARY KEY,ORG_NAME VARCHAR ,CATEGORYNAME VARCHAR,ORGANIZATION_IMAGE VARCHAR,CATEGORY_IMAGE VARCHAR,FILE_NAME VARCHAR,STATUS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + ORGANIZATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBCATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAVOURITE);
        db.execSQL("DROP TABLE IF EXISTS " + WISHLIST);
        onCreate(db);
    }

    public boolean insertData(String name, String quantity, String date, String uuid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, quantity);
        contentValues.put(COL_3, date);
        contentValues.put(COL_4, uuid);
        long getPid = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Logger.logD("getPid", "" + getPid);
        return true;
    }
    public boolean insertdatadashboard(String id, String organizationID, String createddate, String image, String organizationname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORG1, id);
        contentValues.put(ORG2, organizationID);
        contentValues.put(ORG3, createddate);
        contentValues.put(ORG4, image);
        contentValues.put(ORG5, organizationname);
        long getproductid = db.insertWithOnConflict(ORGANIZATION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Logger.logD("getProductid", "" + getproductid);
        return true;
    }
    public boolean insertdatadashboardcategory(String catid, String catorganizationID, String orgname, String catimage, String catorganizationname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CAT1, catid);
        contentValues.put(CAT2, catorganizationID);
        contentValues.put(CAT3, orgname);
        contentValues.put(CAT4, catimage);
        contentValues.put(CAT5, catorganizationname);
        long getproductid = db.insertWithOnConflict(SUBCATEGORY_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Logger.logD("getProductid", "" + getproductid);
        return true;
    }

    public void getdatafromTOdo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(" select * from " + TABLE_NAME_TODO, null);
        Logger.logD("countofDBTodo ", "" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String item_name = cursor.getString(cursor.getColumnIndex("ITEM_NAME"));
                String item_quantity = cursor.getString(cursor.getColumnIndex("ITEM_QUANTITY"));
                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                String date = cursor.getString(cursor.getColumnIndex("DATE"));

                Logger.logD("Itemname", "" + item_name);
                Logger.logD("UUIDfromDB", "" + uuid);
                Logger.logD("ItemQtyfromDB", "" + item_quantity);
                Logger.logD("ItemDateDB", "" + date);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<ModelClassChecklist> getDatadb() {
        List<ModelClassChecklist> MymainCheckList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        Logger.logD("countofDB ", "" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String item_name = cursor.getString(cursor.getColumnIndex("ITEM_NAME"));
                String item_quantity = cursor.getString(cursor.getColumnIndex("ITEM_QUANTITY"));
                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                String date = cursor.getString(cursor.getColumnIndex("DATE"));
                Logger.logD("Itemname", "" + item_name);
                Logger.logD("UUIDfromDB", "" + uuid);
                Logger.logD("ItemQtyfromDB", "" + item_quantity);
                Logger.logD("ItemDateDB", "" + date);
                ModelClassChecklist modelClassChecklist = new ModelClassChecklist();
                modelClassChecklist.setModifiedDate(date);
                modelClassChecklist.setId(uuid);
                modelClassChecklist.setQuantity(item_quantity);
                modelClassChecklist.setName(item_name);
                modelClassChecklist.setCheckstatus(true);
                MymainCheckList.add(modelClassChecklist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return MymainCheckList;
    }
    public List<CategoryModelClass> getDatafromdbdashboard() {
        List<CategoryModelClass> Mymaindashboard = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ORGANIZATION_TABLE, null);
        Logger.logD("countofDB ", "" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("ID"));
                String organizationid = cursor.getString(cursor.getColumnIndex("ORGANIZATION_ID"));
                String createddate = cursor.getString(cursor.getColumnIndex("CREATED_DATE"));
                String image = cursor.getString(cursor.getColumnIndex("IMAGE"));
                String organizationname = cursor.getString(cursor.getColumnIndex("ORGANIZATION_NAME"));
                Logger.logD("ID", "" + id);
                Logger.logD("orgID", "" + organizationid);
                Logger.logD("Createddate", "" + createddate);
                Logger.logD("image", "" + image);
                Logger.logD("orgname", "" + organizationname);
                CategoryModelClass categoryModelClass = new CategoryModelClass();
                categoryModelClass.setId(id);
                categoryModelClass.setOrganizationid(organizationid);
                categoryModelClass.setDate(createddate);
                categoryModelClass.setImage(image);
                categoryModelClass.setName(organizationname);
                Mymaindashboard.add(categoryModelClass);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return Mymaindashboard;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public ArrayList<String> getAllContents() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        return array_list;
    }

    public void deletePreviousRecord(String uuid) {
        try {
            String Query = "Delete from checklist_table where UUID = '" + uuid + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(Query);
        } catch (Exception e) {
            Logger.logV("", "deleteExistingSurveyRecord from Survey table" + e);
        }
    }

    public long updateFavTable(GridModelClass gridModelClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", gridModelClass.getOrganizationid());
        contentValues.put("org_name", gridModelClass.getOrganizationname());
        contentValues.put("cat_name", gridModelClass.getOrganizationname());
        contentValues.put("image", gridModelClass.getImageUrl());
        contentValues.put("modifieddate", gridModelClass.getImageUrl());
        contentValues.put("status", 1);
        contentValues.put("filename", gridModelClass.getFileName());
        contentValues.put("filetype", gridModelClass.getFileid());
        long favprimarykey = db.insertWithOnConflict(FAVOURITE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Logger.logD("favpid", "" + favprimarykey);
        return favprimarykey;
    }
}
