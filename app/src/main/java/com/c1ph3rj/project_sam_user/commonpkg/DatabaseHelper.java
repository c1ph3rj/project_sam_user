package com.c1ph3rj.project_sam_user.commonpkg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agent_details.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "agent_table";

    private static final String COLUMN_AGENT_CODE = "agent_code";
    private static final String COLUMN_EMAIL_ID = "email_id";
    private static final String COLUMN_MOBILE_NUMBER = "mobile_number";
    private static final String COLUMN_AGENT_NAME = "agent_name";
    private static final String COLUMN_AGENT_TYPE = "agent_type";
    private static final String COLUMN_ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_AGENT_CODE + " TEXT PRIMARY KEY, " +
                COLUMN_EMAIL_ID + " TEXT, " +
                COLUMN_MOBILE_NUMBER + " TEXT, " +
                COLUMN_AGENT_NAME + " TEXT, " +
                COLUMN_AGENT_TYPE + " TEXT, " +
                COLUMN_ADDRESS + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addAgentDetail(AgentDetailModel agent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AGENT_CODE, agent.agentCode);
        values.put(COLUMN_EMAIL_ID, agent.emailId);
        values.put(COLUMN_MOBILE_NUMBER, agent.mobileNumber);
        values.put(COLUMN_AGENT_NAME, agent.agentName);
        values.put(COLUMN_AGENT_TYPE, agent.agentType);
        values.put(COLUMN_ADDRESS, agent.address);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public AgentDetailModel getAgentDetail() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            AgentDetailModel agent = new AgentDetailModel();
            agent.agentCode = cursor.getString(0);
            agent.emailId = cursor.getString(1);
            agent.mobileNumber = cursor.getString(2);
            agent.agentName = cursor.getString(3);
            agent.agentType = cursor.getString(4);
            agent.address = cursor.getString(5);
            cursor.close();
            return agent;
        }
        return null;
    }

    public void deleteAgentDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
