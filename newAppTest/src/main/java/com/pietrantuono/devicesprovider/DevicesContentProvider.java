package com.pietrantuono.devicesprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import analytica.pericoach.android.Contract;
import analytica.pericoach.android.CustomSQLiteOpenHelper;
import server.pojos.Device;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DevicesContentProvider extends ContentProvider {
    private CustomSQLiteOpenHelper mOpenHelper;
    private static final UriMatcher sUriMatcher;
    private static final int DEVICES = 1;
    private static final int DEVICES_ID = 2;

    private static final String CONTENT_TYPE ="vnd.android.cursor.dir/vnd.analytica.devices";
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.analytica.devices";


    private static final String AUTHORITY = "com.analytica.devicesprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/devices");



    @Override
    public boolean onCreate() {
        mOpenHelper= CustomSQLiteOpenHelper.getCustomSQLiteOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db;
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Contract.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        switch (sUriMatcher.match(uri)){
            case DEVICES:
                db= mOpenHelper.getReadableDatabase();
                return db.query(Contract.DevicesColumns.DEVICES_TABLE_NAME,projection,selection,selectionArgs,null,null,orderBy);
            case DEVICES_ID:
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                qb.setTables(Contract.DevicesColumns.DEVICES_TABLE_NAME);
                db = mOpenHelper.getReadableDatabase();
                qb.appendWhere(Contract.DevicesColumns._ID + "=" + uri.getPathSegments().get(1));
                return qb.query(db,projection,selection,selectionArgs,null,null,orderBy);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case DEVICES:
                return CONTENT_TYPE;
            case DEVICES_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != DEVICES && sUriMatcher.match(uri) != DEVICES_ID) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(Contract.DevicesColumns.DEVICES_TABLE_NAME, Contract.DevicesColumns.DEVICES_DEVICES_ID, values);
        if (rowId > 0) {
            Uri notifyUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(notifyUri, null);
            return notifyUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case DEVICES:
                count = db.delete(Contract.DevicesColumns.DEVICES_TABLE_NAME, selection, selectionArgs);
                break;

            case DEVICES_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(Contract.DevicesColumns.DEVICES_TABLE_NAME, Contract.DevicesColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case DEVICES:
                count = db.update(Contract.DevicesColumns.DEVICES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case DEVICES_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(Contract.DevicesColumns.DEVICES_TABLE_NAME, values, Contract.DevicesColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        int match = sUriMatcher.match(uri);
        if(match!=DEVICES) throw new IllegalArgumentException("Invalid URI " + uri+" for bulk insert");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = db.insertOrThrow(Contract.DevicesColumns.DEVICES_TABLE_NAME, Contract.DevicesColumns.DEVICES_DEVICES_ID, cv);
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            db.endTransaction();
        }
        return numInserted;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "devices", DEVICES);
        sUriMatcher.addURI(AUTHORITY, "devices/#", DEVICES_ID);
    }

    public static Device reconstructDevice(Cursor c){
        if(c.getCount()<=0)return null;
        Device device= new Device();
        long deviceId=c.getLong(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_DEVICES_ID));
        device.setDeviceId(deviceId);

        String barcode=c.getString(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_BARCODE));
        device.setBarcode(barcode != null ? barcode : "");

        String serial=c.getString(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_SERIAL));
        device.setSerial(serial != null ? serial : "");

        String model=c.getString(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_SERIAL));
        device.setModel(model != null ? model : "");

        String fwver=c.getString(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_FWVER));
        device.setFwver(fwver != null ? fwver : "");

        String addr=c.getString(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_ADDRESS));
        device.setBt_addr(addr != null ? addr : "");

        device.setExec_Tests(c.getLong(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_EXEC_TESTS)));

        device.setJobId(c.getLong(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_JOB_ID)));

        device.setStatus(c.getLong(c.getColumnIndexOrThrow(Contract.DevicesColumns.DEVICES_STATUS)));

        return device;
    }

    public static boolean isBarcodeAlreadySeen(String barcode, Context context) {
        Cursor c=context.getContentResolver().query(CONTENT_URI,null, Contract.DevicesColumns.DEVICES_BARCODE+" = ?", new String[]{barcode},null);
        return (c!=null && c.getCount()>0);
    }

    public static boolean isMacAlreadySeen(String barcode, String mac,Context context) {
        String selection=Contract.DevicesColumns.DEVICES_BARCODE+ " = ? AND "+ Contract.DevicesColumns.DEVICES_ADDRESS+ " = ?";
        String[] selectionargs= new String[]{barcode,mac};
        Cursor c=context.getContentResolver().query(CONTENT_URI,null, selection, selectionargs,null);
        return (c!=null && c.getCount()>0);
    }

    public static boolean isDeviceAlreadySeen(String barcode, String serial,Context context) {
        String selection=Contract.DevicesColumns.DEVICES_BARCODE+ " = ? AND "+ Contract.DevicesColumns.DEVICES_SERIAL+ " = ?";
        String[] selectionargs= new String[]{barcode,serial};
        Cursor c=context.getContentResolver().query(CONTENT_URI,null, selection, selectionargs,null);
        return (c!=null && c.getCount()>0);
    }
}
