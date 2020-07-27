package com.madhanarts.artsnotes.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.NotesCheckListFragment;

public class NotesChecklistAdapter extends RecyclerView.Adapter<NotesChecklistAdapter.NotesChecklistViewHolder> {



    public NotesChecklistAdapter(NotesCheckListFragment notesCheckListFragment)
    {

    }

    @NonNull
    @Override
    public NotesChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesChecklistViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NotesChecklistViewHolder extends RecyclerView.ViewHolder {
        public NotesChecklistViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
