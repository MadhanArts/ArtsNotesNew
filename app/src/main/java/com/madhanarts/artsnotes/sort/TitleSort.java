package com.madhanarts.artsnotes.sort;

import com.madhanarts.artsnotes.model.NoteItem;

import java.util.Comparator;

public class TitleSort implements Comparator<NoteItem> {

    @Override
    public int compare(NoteItem o1, NoteItem o2) {
        return o1.getNotesTitle().compareToIgnoreCase(o2.getNotesTitle());
    }
}
