package com.example.mord.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DBProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.mord.myapplication.dbprovider";
    private static final String BASE_PATH = "";
    public static final Uri TERM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );


    private static final int TERM = 1;
    private static final int TERM_ID = 2;
    public static final String CONTENT_ITEM_TYPE = "Note";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERM);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);
    }
    private SQLiteDatabase database;
    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());

        database = helper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(uriMatcher.match(uri) == TERM_ID) {
            selection = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
        }
        return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_ALL_COLUMNS, selection, null, null, null, DBOpenHelper.TERM_TITLE + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_TERMS, null, values);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_TERMS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_TERMS, values, selection, selectionArgs);
    }
}
