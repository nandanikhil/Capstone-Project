/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikhil.reached.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private UserDbHelper mOpenHelper;

    static final int USER = 300;


    private static final String sUserMobileSelection =
            UserContract.UserEntry.TABLE_NAME +
                    "." + UserContract.UserEntry.COLUMN_MOBILE_NUMBER + " = ? ";

    private static final String sUserFirebaseSelection =
            UserContract.UserEntry.TABLE_NAME +
                    "." + UserContract.UserEntry.COLUMN_FIRBASE_REGID + " = ? ";

    static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USER, USER);
        return uriMatcher;


    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new UserDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                return UserContract.UserEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {


            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Users to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case USER: {
                long _id = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserContract.UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (selection == null)
            selection = "1";
        int match = sUriMatcher.match(uri);


        switch (match) {
            case USER:
                rowsDeleted = db.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);

        }

        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();


        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);


        switch (match) {
            case USER:
                rowsUpdated = db.update(UserContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);

        }


        if (rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();

        return rowsUpdated;

    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}