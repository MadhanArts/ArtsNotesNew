package com.madhanarts.artsnotes.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class NoteItem implements Serializable {

    public int noteId;
    public String notesTitle;
    public int noteContentId;
    public ArrayList<File> notesContentPathFiles;
    public long notesLastModified;

    public NoteItem(int noteId, String notesTitle, ArrayList<File> notesContentPathFiles, long notesLastModified) {
        this.noteId = noteId;
        this.notesTitle = notesTitle;
        this.notesContentPathFiles = notesContentPathFiles;
        this.notesLastModified = notesLastModified;
    }


    public int getNoteId() {
        return noteId;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public int getNoteContentId() {
        return noteContentId;
    }

    public ArrayList<File> getNotesContentPathFiles() {
        return notesContentPathFiles;
    }

    public long getNotesLastModified() {
        return notesLastModified;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setNotesTitle(String notesTitle) {
        this.notesTitle = notesTitle;
    }

    public void setNotesContentPathFiles(ArrayList<File> notesContentPathFiles) {
        this.notesContentPathFiles = notesContentPathFiles;
    }

    public void setNotesLastModified(long notesLastModified) {
        this.notesLastModified = notesLastModified;
    }


}
