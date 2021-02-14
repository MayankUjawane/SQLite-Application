package com.example.sqliteapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    public static final String COLUMN_CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String COLUMN_CUSTOMER_AGE = "CUSTOMER_AGE";
    public static final String COLUMN_ACTIVE_CUSTOMER = "ACTIVE_CUSTOMER";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "customer.db", null, 1);
    }

    //this is called only once when database is created for the first time. There should be code in here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CUSTOMER_NAME + " TEXT, "
                + COLUMN_CUSTOMER_AGE + " INT, "
                + COLUMN_ACTIVE_CUSTOMER + " BOOL)";

        //void execSQL(String sql) this executes the sql query not select query.
        sqLiteDatabase.execSQL(createTableStatement);
    }

    //this is called if the database version number changes (called when database needs to be upgraded).
    //It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //this method will add the record to the database.
    public boolean addOne(CustomerModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        //ContentValues stores data in pairs. For example cv.put("name", value)  cv.getString("name")
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CUSTOMER_NAME, customerModel.getName());
        cv.put(COLUMN_CUSTOMER_AGE, customerModel.getAge());
        cv.put(COLUMN_ACTIVE_CUSTOMER, customerModel.getIsActive());

        //if db.insert fails then the value of insert will be -1.
        long insert = db.insert(CUSTOMER_TABLE, null, cv);
        db.close(); // Closing database connection
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Deleting single contact
    public boolean deleteCustomer(CustomerModel customerModel) {
        //find customer model in database. if it found, delete it and return true.
        //if it is not found, return false.
        
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + customerModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        } else {
            db.close();
            cursor.close();
            return false;
        }

    }

    //get all customer details
    public List<CustomerModel> selectEveryone() {
        //1.Create an empty list  2.Fill it from the database query  3.Return it to the MainActivity
        List<CustomerModel> returnList = new ArrayList<>();

        //Select All Query
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE;

        //use getWritableDatabase only when you plan to inset, update or delete records.
        //getWritableDatabase locks the data file so other processes may not access it.
        //use getReadableDatabase when you plan to SELECT items from the database.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //looping through all rows and adding to list
        //cursor.moveToFirst returns true if there were items selected.
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                int customerID = cursor.getInt(0);
                String customerName = cursor.getString(1);
                int customerAge = cursor.getInt(2);

                //We have to convert the result from an int to a boolean because cursor does not have getBoolean method.
                boolean customerActive = cursor.getInt(3) == 1 ? true : false;

                CustomerModel customer = new CustomerModel(customerID, customerName, customerAge, customerActive);
                returnList.add(customer);

            } while (cursor.moveToNext());

        } else {
            //failure, do not add anything to the list.
        }

        //close both the cursor and db when done.
        cursor.close();
        db.close();

        return returnList;
    }

    //for checking already details exist or not
    public int getName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CUSTOMER_TABLE+" WHERE "+COLUMN_CUSTOMER_NAME+" = '"+name+"'", null);
        int noOfContacts = cursor.getCount();
        cursor.close();
        db.close();
        return noOfContacts;
    }

    // code to update the customer
    public void updateContact(CustomerModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, customerModel.getName());
        values.put(COLUMN_CUSTOMER_AGE, customerModel.getAge());
        values.put(COLUMN_ACTIVE_CUSTOMER, customerModel.getIsActive());

        // updating row
        db.update(CUSTOMER_TABLE, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(customerModel.getId()) });
        db.close();
    }

    //for searching
    public List<CustomerModel> search (String keyword) {
        List<CustomerModel> contacts = null;
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + CUSTOMER_TABLE + " where " + COLUMN_CUSTOMER_NAME + " like ?", new String[] { "%" + keyword + "%" });
            if (cursor.moveToFirst()) {
                contacts = new ArrayList<>();
                do {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setId(cursor.getInt(0));
                    customerModel.setName(cursor.getString(1));
                    customerModel.setAge(cursor.getInt(2));
                    customerModel.setIsActive(cursor.getInt(3) == 1 ? true : false);
                    contacts.add(customerModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            contacts = null;
        }
        return contacts;
    }

    // Getting Customers Count
    public int getCustomersCount() {
        String countQuery = "SELECT * FROM " + CUSTOMER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int totalCount = cursor.getCount();
        cursor.close();

        // return count
        return totalCount;
    }

}
