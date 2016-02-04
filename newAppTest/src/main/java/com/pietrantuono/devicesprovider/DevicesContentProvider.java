package com.pietrantuono.devicesprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
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

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DevicesContentProvider extends ContentProvider {
    private CustomSQLiteOpenHelper mOpenHelper;
    private static final UriMatcher sUriMatcher;
    private static final int DEVICES = 1;
    private static final int DEVICES_ID = 2;

    public static final String CONTENT_TYPE ="vnd.android.cursor.dir/vnd.analytica.devices";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.analytica.devices";


    public static final String AUTHORITY = "com.analytica.devicesprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/devices");
    @Override
    public boolean onCreate() {
        mOpenHelper= CustomSQLiteOpenHelper.getCustomSQLiteOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
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
                return qb.query(db,projection,selection,selectionArgs,null,null,orderBy);
            case DEVICES_ID:
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


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "devices", DEVICES);
        sUriMatcher.addURI(AUTHORITY, "devices/#", DEVICES_ID);
    }
}
