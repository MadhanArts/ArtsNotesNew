package com.madhanarts.artsnotes.database;

public class NotesContract {

    private NotesContract()
    {

    }

    public static class NotesEntry
    {

        public static final String TABLE_NAME = "notes";
        public static final String NOTES_ID = "note_id";
        public static final String NOTE_TITLE = "note_title";
        public static final String NOTES_FILE_PATH = "notes_file_path";
        public static final String NOTES_LAST_MODIFIED = "notes_last_modified";
        public static final String NOTES_MODE = "notes_mode";

    }

}
