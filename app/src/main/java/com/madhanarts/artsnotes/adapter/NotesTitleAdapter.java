package com.madhanarts.artsnotes.adapter;

import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.NotesTitleFragment;
import com.madhanarts.artsnotes.R;
import com.madhanarts.artsnotes.TimeAgo;
import com.madhanarts.artsnotes.model.NoteItem;
import com.madhanarts.artsnotes.sort.LastModifiedSort;
import com.madhanarts.artsnotes.sort.TitleSort;

import java.util.ArrayList;
import java.util.Collections;

public class NotesTitleAdapter extends RecyclerView.Adapter<NotesTitleAdapter.NotesTitleViewHolder> {

    private boolean isSortedNameAs = false;
    private boolean isLastModifiedAs = true;

    private NotesTitleFragment notesTitleFragment;

    public ArrayList<NoteItem> getNoteItems() {
        return noteItems;
    }

    public ArrayList<NoteItem> noteItems;

    private OnNotesTitleClickListener onNotesTitleClickListener;

    private TimeAgo timeAgo;


    public NotesTitleAdapter(NotesTitleFragment notesTitleFragment, ArrayList<NoteItem> noteItems, OnNotesTitleClickListener onNotesTitleClickListener)
    {
        this.noteItems = noteItems;
        this.onNotesTitleClickListener = onNotesTitleClickListener;
        this.notesTitleFragment = notesTitleFragment;

    }


    @NonNull
    @Override
    public NotesTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notes_title_item, parent, false);

        timeAgo = new TimeAgo();

        return new NotesTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesTitleViewHolder holder, final int position) {

        holder.NotesTitle.setText(noteItems.get(position).getNotesTitle());
 //       holder.NotesLastModified.setText(noteItems.get(position).getNotesLastModified());

        if (noteItems.get(position).getNotesLastModified() == 0)
        {
            holder.NotesLastModified.setText("Empty");
        }
        else {
            holder.NotesLastModified.setText(timeAgo.getTimeAgo(noteItems.get(position).getNotesLastModified()));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!notesTitleFragment.inActionMode)
                {
                    notesTitleFragment.actionMode(position);
                    return true;
                }

                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return noteItems.size();
    }



    public class NotesTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView NotesTitle, NotesLastModified;

        public NotesTitleViewHolder(@NonNull View itemView)
        {
            super(itemView);

            NotesTitle = itemView.findViewById(R.id.notes_title);
            NotesLastModified = itemView.findViewById(R.id.notes_last_modified);
            itemView.setOnClickListener(this);

            //itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if (notesTitleFragment.inActionMode)
            {
                notesTitleFragment.selectItem(getAdapterPosition());
            }
            else {
                onNotesTitleClickListener.onClickTitle(noteItems.get(getAdapterPosition()), getAdapterPosition());
            }
        }

/*        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 201, 0, "Delete");
        }*/

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTitle()
    {

        if (!isSortedNameAs)
        {
            noteItems.sort(new TitleSort());
            isSortedNameAs = true;
        }
        else
        {
            Collections.reverse(noteItems);
            isSortedNameAs = false;
        }
        notifyDataSetChanged();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByLastModified()
    {

        if (!isLastModifiedAs)
        {
            noteItems.sort(new LastModifiedSort());
            isLastModifiedAs = true;
        }
        else
        {
            Collections.reverse(noteItems);
            isLastModifiedAs = false;
        }
        notifyDataSetChanged();

    }


    public interface OnNotesTitleClickListener
    {
        void onClickTitle(NoteItem noteItem, int position);
    }

}
