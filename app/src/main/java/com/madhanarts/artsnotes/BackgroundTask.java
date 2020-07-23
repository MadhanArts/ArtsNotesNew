package com.madhanarts.artsnotes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.adapter.NotesTitleAdapter;
import com.madhanarts.artsnotes.database.NotesContract;
import com.madhanarts.artsnotes.database.NotesDbOpener;
import com.madhanarts.artsnotes.model.NoteItem;

import java.io.File;
import java.util.ArrayList;

public class BackgroundTask extends AsyncTask<String, NoteItem, String> {

    private Context context;
    private NotesTitleAdapter titleAdapter;
    private ArrayList<NoteItem> noteItems = new ArrayList<>();
    private NotesTitleFragment.BackgroundTaskCompleteListener backgroundTaskCompleteListener;
    private NotesContentFragment.BackgroundTaskNoteIdListener backgroundTaskNoteIdListener;

    private long noteId = 0;

    public BackgroundTask(Context context)
    {
        this.context = context;
    }

    public BackgroundTask(Context context, NotesContentFragment.BackgroundTaskNoteIdListener backgroundTaskNoteIdListener)
    {
        this.context = context;
        this.backgroundTaskNoteIdListener = backgroundTaskNoteIdListener;

    }

    public BackgroundTask(Context context, NotesTitleFragment notesTitleFragment, RecyclerView titleRecyclerView, NotesTitleAdapter.OnNotesTitleClickListener titleClickListener, NotesTitleFragment.BackgroundTaskCompleteListener backgroundTaskCompleteListener)
    {
        this.context = context;
        this.backgroundTaskCompleteListener = backgroundTaskCompleteListener;
        titleAdapter = new NotesTitleAdapter(notesTitleFragment, noteItems, titleClickListener);
        titleRecyclerView.setAdapter(titleAdapter);

    }

    public BackgroundTask(Context context, RecyclerView titleRecyclerView)
    {
        this.context = context;
        titleAdapter = (NotesTitleAdapter) titleRecyclerView.getAdapter();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        switch (params[0]) {
            case "add_new_note": {

                NotesDbOpener notesDbOpener = new NotesDbOpener(context);

                SQLiteDatabase database = notesDbOpener.getWritableDatabase();


                String noteTitle = params[1];
                String noteFilePath = params[2];
                String noteLastModified = params[3];

                noteId = notesDbOpener.addNewNote(noteTitle, noteFilePath, noteLastModified, database);

                notesDbOpener.close();
                return "add_note";

            }
            case "get_notes": {

                //Should not set the value of view in doInBackground()
                // doInBackground() will run in separate thread... View can only be initialized in main thread


                NotesDbOpener notesDbOpener = new NotesDbOpener(context);

                SQLiteDatabase database = notesDbOpener.getReadableDatabase();

                Cursor cursor = notesDbOpener.getNotes(database);

                while (cursor.moveToNext()) {
                    String noteId = Integer.toString(cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_ID)));
                    String noteTitle = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTE_TITLE));
                    String noteFilepath = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_FILE_PATH));
                    String noteLastModified = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_LAST_MODIFIED));

                    ArrayList<File> noteContentPathFiles;
/*                if (noteFilepath.equals(""))
                {
                    noteContentPathFiles = new ArrayList<>();
                }
                else
                {
                    noteContentPathFiles = convertPathToFiles(noteFilepath);
                }*/

                    noteContentPathFiles = convertPathToFiles(noteFilepath);

                    NoteItem noteItem = new NoteItem(Integer.parseInt(noteId), noteTitle, noteContentPathFiles, Long.parseLong(noteLastModified));

                    publishProgress(noteItem);

                }

                notesDbOpener.close();
                cursor.close();

                return "get_contacts";

            }
            case "update_notes": {

                NotesDbOpener notesDbOpener = new NotesDbOpener(context);

                SQLiteDatabase database = notesDbOpener.getWritableDatabase();

                String notesId = params[1];
                String notesTitle = params[2];
                String notesFilePath = params[3];
                String notesLastModified = params[4];

                notesDbOpener.updateNotes(Integer.parseInt(notesId), notesTitle, notesFilePath, notesLastModified, database);

                notesDbOpener.close();

                return "Contact updated";


            }
            case "delete_note": {

                NotesDbOpener notesDbOpener = new NotesDbOpener(context);

                SQLiteDatabase database = notesDbOpener.getWritableDatabase();

                int position = Integer.parseInt(params[1]);

                NoteItem noteItem = titleAdapter.getNoteItems().get(position);
                ArrayList<File> noteItemFiles = noteItem.getNotesContentPathFiles();
                deleteNoteItemFiles(noteItemFiles);
                int noteId = noteItem.getNoteId();

                notesDbOpener.deleteNote(noteId, database);


                titleAdapter.getNoteItems().remove(position);

                notesDbOpener.close();

                return "note_deleted";
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(NoteItem... values) {

        noteItems.add(values[0]);
        titleAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onPostExecute(String result) {

        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        if (result.equals("add_note"))
        {
            backgroundTaskNoteIdListener.sendAddedNoteId(noteId);
        }
        if (result.equals("get_contacts"))
        {
            backgroundTaskCompleteListener.onTaskComplete(titleAdapter);

        }
        if (result.equals("note_deleted"))
        {
            titleAdapter.notifyDataSetChanged();
        }

    }

    private void deleteNoteItemFiles(ArrayList<File> noteItemFiles)
    {
        for (int i = 0; i < noteItemFiles.size(); i++)
        {
            noteItemFiles.get(i).delete();
        }

        //Toast.makeText(context, "All note files are deleted", Toast.LENGTH_SHORT).show();

    }

    private ArrayList<File> convertPathToFiles(String paths)
    {

        ArrayList<File> noteFiles = new ArrayList<>();

        if (paths.trim().equals(""))
        {
            noteFiles = new ArrayList<>();
        }
        else
        {
            String[] pathArr = paths.trim().split("%%");
            for (int i = 0; i < pathArr.length; i++)
            {
                noteFiles.add(new File(pathArr[i]));
            }

        }
        return noteFiles;

    }
}
