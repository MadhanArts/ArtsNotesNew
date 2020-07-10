package com.madhanarts.artsnotes.sort;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.madhanarts.artsnotes.model.NoteItem;

import java.util.Comparator;

public class LastModifiedSort implements Comparator<NoteItem> {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(NoteItem o1, NoteItem o2) {
        return Long.compare(o1.getNotesLastModified(), o1.getNotesLastModified());
    }
}
