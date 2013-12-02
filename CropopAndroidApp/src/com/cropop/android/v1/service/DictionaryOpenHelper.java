package com.cropop.android.v1.service;

import static com.cropop.android.v1.service.DictionaryOpenHelper.*;

import java.sql.SQLClientInfoException;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cropop.android.v1.model.Message;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class DictionaryOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME = "messages";
	public static final String OBJECT_ID = "objectId";
	public static final String EXP_USER = "exp_user";
	public static final String DEST_USER = "dest_user";
	public static final String TARGET_LAT = "target_lat";
	public static final String TARGET_LNG = "target_lng";
	public static final String CONTENT = "content";
	public static final String DELIVERED = "delivered";
	private static final String DICTIONARY_TABLE_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" +
					OBJECT_ID + " TEXT PRIMARY KEY NOT NULL, " +
					EXP_USER + " TEXT, " +
					DEST_USER + " TEXT, " +
					TARGET_LAT + " REAL, " +
					TARGET_LNG + " REAL, " +
					DELIVERED + " INTEGER, " +
					CONTENT + " TEXT);";

	public DictionaryOpenHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}
	
	public void dropTable() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		db.execSQL(DICTIONARY_TABLE_CREATE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DICTIONARY_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public void addNewMessage(Message m) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(OBJECT_ID, m.getObjectId());
			values.put(CONTENT, m.getContent());
			values.put(TARGET_LAT, m.getTarget().getLatitude());
			values.put(TARGET_LNG, m.getTarget().getLongitude());
			values.put(DELIVERED, 0);
			if (m.getDest_user() != null){
				values.put(DEST_USER, m.getDest_user().fetchIfNeeded().getJSONObject("profile").getString("name"));
			}
			if (m.getExp_user() != null){
				values.put(EXP_USER, m.getExp_user().fetchIfNeeded().getJSONObject("profile").getString("name"));
			}	
			try {
				long res = db.insertWithOnConflict(TABLE_NAME, null, values, 0);
				if (res == -1) throw new SQLException("Row already existing");
				else {
					System.out.println("Message Saved.");
				}
			} catch (SQLException e){
//				System.out.println("Message already there");
			} finally {
				db.close();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Cursor getUnDeliveredMessages() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null , DELIVERED+" = 0", null, null, null, null);
//		Cursor cursor = db.query(TABLE_NAME, null , null, null, null, null, null);
		return cursor;
	}
	public Cursor getDeliveredMessages() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null , DELIVERED+" = 1", null, null, null, null);
		return cursor;
	}	
	public Message getMessage(String mId) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, null , OBJECT_ID+"='"+mId+"'", null, null, null, null);
		c.moveToFirst();
		Message message = createMessageFromCursor(c);
		
		return message;
	}	
	
	public Message createMessageFromCursor(Cursor c){
		Message message = new Message();
		message.setObjectId(c.getString(c.getColumnIndex(OBJECT_ID)));
		message.setContent(c.getString(c.getColumnIndex(CONTENT)));
		message.setTarget(new ParseGeoPoint(c.getDouble(c.getColumnIndex(TARGET_LAT)), c.getDouble(c.getColumnIndex(TARGET_LNG))));
		message.setDelivered(false);
		
		ParseUser dest = new ParseUser();
		String dn = c.getString(c.getColumnIndex(DEST_USER));
		if (dn != null) {
			dest.setUsername(dn);
		}
		
		message.setDest_user(dest);
		
		ParseUser exp = new ParseUser();
		String en = c.getString(c.getColumnIndex(EXP_USER));
		if (en != null) {
			exp.setUsername(en);
		}
		message.setExp_user(exp);
		
		
		return message;
	}
	
	public void markAsRead(Message m) {
		SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DELIVERED, "1");
//			values.put(OBJECT_ID, m.getObjectId());
			try {
//				long res = db.updateWithOnConflict(TABLE_NAME, values, OBJECT_ID+"="+m.getObjectId(), null, 5);
				long res = db.update(TABLE_NAME, values, OBJECT_ID+"='"+m.getObjectId()+"'", null);
//				long res = db.update(TABLE_NAME, values, null, null);
				System.out.println(res);
			} catch (SQLException e){
				e.printStackTrace();
//				System.out.println("Message already there");
			} finally {
				db.close();
			}

	}

}
