package com.madhanarts.artsnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NotesDbOpener extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE = "create table " + NotesContract.NotesEntry.TABLE_NAME +
            "(" +
            NotesContract.NotesEntry.NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NotesContract.NotesEntry.NOTE_TITLE + " text," +
            NotesContract.NotesEntry.NOTES_FILE_PATH + " text," +
            NotesContract.NotesEntry.NOTES_LAST_MODIFIED + " text"
            + ")";

    public static final String DROP_TABLE = "drop table if exists " + NotesContract.NotesEntry.TABLE_NAME;

    public NotesDbOpener(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }


    public long addNewNote(String noteTitle, String notePath, String noteLastModified, SQLiteDatabase database)
    {
        ContentValues notesValues = new ContentValues();

        notesValues.put(NotesContract.NotesEntry.NOTE_TITLE, noteTitle);
        notesValues.put(NotesContract.NotesEntry.NOTES_FILE_PATH, notePath);
        notesValues.put(NotesContract.NotesEntry.NOTES_LAST_MODIFIED, noteLastModified);

        //Log.d("content_op", noteId + " is inserted");
        return database.insert(NotesContract.NotesEntry.TABLE_NAME, null, notesValues);
    }

    public Cursor getNotes(SQLiteDatabase database)
    {

        String[] projections = {

                NotesContract.NotesEntry.NOTES_ID,
                NotesContract.NotesEntry.NOTE_TITLE,
                NotesContract.NotesEntry.NOTES_FILE_PATH,
                NotesContract.NotesEntry.NOTES_LAST_MODIFIED

        };

        return database.query(NotesContract.NotesEntry.TABLE_NAME, projections,
                null, null, null, null, NotesContract.NotesEntry.NOTES_LAST_MODIFIED + " DESC"
        );

    }


    public void updateNotes(int noteId, String noteTitle, String notesFilePath, String notesLastModified, SQLiteDatabase database)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotesContract.NotesEntry.NOTE_TITLE, noteTitle);
        contentValues.put(NotesContract.NotesEntry.NOTES_FILE_PATH, notesFilePath);
        contentValues.put(NotesContract.NotesEntry.NOTES_LAST_MODIFIED, notesLastModified);

        String selection = NotesContract.NotesEntry.NOTES_ID + " = " + noteId;

        database.update(NotesContract.NotesEntry.TABLE_NAME, contentValues, selection, null);

    }

    public void deleteNote(int noteId, SQLiteDatabase database)
    {
        String selection = NotesContract.NotesEntry.NOTES_ID + " = " + noteId;

        database.delete(NotesContract.NotesEntry.TABLE_NAME, selection, null);

    }

}
