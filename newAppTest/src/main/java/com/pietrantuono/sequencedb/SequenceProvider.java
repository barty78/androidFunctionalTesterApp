package com.pietrantuono.sequencedb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class SequenceProvider extends ContentProvider {
    private static final String RECORDS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.analytica.records";
    private static final String RECORDS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.analytica.records";
    private static final String TESTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.analytica.tests";
    private static final String TESTS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.analytica.tests";
    private SequenceSQLiteOpenHelper mOpenHelper;
    private static final UriMatcher sUriMatcher;
    private static final String AUTHORITY = "com.analytica.pericoach.sequence";
    private static final int RECORDS = 1;
    private static final int RECORDS_ID = 2;
    private static final int TESTS = 3;
    private static final int TESTS_ID = 4;
    public static final Uri RECORDS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SequenceContracts.Records.TABLE_RECORDS);
    public static final Uri TESTS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SequenceContracts.Tests.TABLE_TESTS);


    @Override
    public boolean onCreate() {
        mOpenHelper = SequenceSQLiteOpenHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = "";
        String id = "";
        switch (sUriMatcher.match(uri)) {
            case RECORDS:
                table = SequenceContracts.Records.TABLE_RECORDS;
                break;
            case RECORDS_ID:
                table = SequenceContracts.Records.TABLE_RECORDS;
                id = uri.getPathSegments().get(1);
                selection = selection + " AND " + SequenceContracts.Records._ID + "=" + id;
                break;
            case TESTS:
                table = SequenceContracts.Tests.TABLE_TESTS;
                break;
            case TESTS_ID:
                table = SequenceContracts.Tests.TABLE_TESTS;
                id = uri.getPathSegments().get(1);
                selection = selection + " AND " + SequenceContracts.Records._ID + "=" + id;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor c = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RECORDS:
                return RECORDS_CONTENT_TYPE;
            case RECORDS_ID:
                return RECORDS_CONTENT_ITEM_TYPE;
            case TESTS:
                return TESTS_CONTENT_TYPE;
            case TESTS_ID:
                return TESTS_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != TESTS && sUriMatcher.match(uri) != RECORDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String TABLE_NAME;
        String NULL_COLUMN_HACK;
        Uri CONTENT_URI;

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case RECORDS:
                TABLE_NAME = SequenceContracts.Records.TABLE_RECORDS;
                NULL_COLUMN_HACK = SequenceContracts.Records.RECORDS_BARCODE;
                CONTENT_URI = RECORDS_CONTENT_URI;
                break;
            case TESTS:
                TABLE_NAME = SequenceContracts.Tests.TABLE_TESTS;
                NULL_COLUMN_HACK = SequenceContracts.Tests.TABLE_TESTS_NAME;
                CONTENT_URI = TESTS_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = db.insert(TABLE_NAME, NULL_COLUMN_HACK, values);
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
        String id;
        switch (sUriMatcher.match(uri)) {
            case RECORDS:
                count = db.delete(SequenceContracts.Records.TABLE_RECORDS, selection, selectionArgs);
                break;
            case RECORDS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(SequenceContracts.Records.TABLE_RECORDS, SequenceContracts.Records._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case TESTS:
                count = db.delete(SequenceContracts.Tests.TABLE_TESTS, selection, selectionArgs);
                break;
            case TESTS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(SequenceContracts.Tests.TABLE_TESTS, SequenceContracts.Tests._ID + "=" + id
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
        String id;
        switch (sUriMatcher.match(uri)) {
            case RECORDS:
                count = db.update(SequenceContracts.Records.TABLE_RECORDS, values, selection, selectionArgs);
                break;
            case RECORDS_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(SequenceContracts.Records.TABLE_RECORDS, values, SequenceContracts.Records._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case TESTS:
                count = db.update(SequenceContracts.Tests.TABLE_TESTS, values, selection, selectionArgs);
                break;
            case TESTS_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(SequenceContracts.Tests.TABLE_TESTS, values, SequenceContracts.Tests._ID + "=" + id
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

        sUriMatcher.addURI(AUTHORITY, SequenceContracts.Records.TABLE_RECORDS, RECORDS);
        sUriMatcher.addURI(AUTHORITY, SequenceContracts.Records.TABLE_RECORDS+"/#", RECORDS_ID);

        sUriMatcher.addURI(AUTHORITY, SequenceContracts.Tests.TABLE_TESTS, TESTS);
        sUriMatcher.addURI(AUTHORITY, SequenceContracts.Records.TABLE_RECORDS+"/#", TESTS_ID);
    }

}
