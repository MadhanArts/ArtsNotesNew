package com.madhanarts.artsnotes.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.NotesCheckListFragment;
import com.madhanarts.artsnotes.R;
import java.util.ArrayList;

public class NotesChecklistAdapter extends RecyclerView.Adapter<NotesChecklistAdapter.NotesChecklistViewHolder> {

    private ArrayList<String> noteChecklistItems;
    private NotesCheckListFragment notesCheckListFragment;
    public static boolean doubleTapped = false;

    private Bundle settingsBundle;

    public NotesChecklistAdapter(NotesCheckListFragment notesCheckListFragment, ArrayList<String> noteChecklistItems, Bundle settingsBundle)
    {
        this.noteChecklistItems = noteChecklistItems;
        this.notesCheckListFragment = notesCheckListFragment;
        this.settingsBundle = settingsBundle;

    }

    @NonNull
    @Override
    public NotesChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notes_checklist_item, parent, false);
        Log.d("content_op", "On layout inflater");


        return new NotesChecklistViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final NotesChecklistViewHolder holder, int position) {

        String tempItem = noteChecklistItems.get(position);
        if (noteChecklistItems.get(position).contains("[c]"))
        {

            tempItem = tempItem.substring(0, tempItem.lastIndexOf("["));
            holder.noteChecklistText.setPaintFlags(holder.noteChecklistText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.noteChecklistText.setTextColor(Color.DKGRAY);
            holder.checked = true;

        }
        else
        {
            holder.noteChecklistText.setPaintFlags(holder.noteChecklistText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.noteChecklistText.setTextColor(Color.BLACK);
            holder.checked = false;
        }
        holder.noteChecklistText.setText(tempItem);

        if (doubleTapped)
        {
            notesCheckListFragment.checklistModeInfo.setText("Edit Mode");
            holder.noteChecklistMover.setVisibility(View.VISIBLE);
            holder.noteChecklistClear.setVisibility(View.VISIBLE);
            holder.noteChecklistDoneCheck.setVisibility(View.GONE);
        }

        if (NotesCheckListFragment.option.equals("get_exist") && !doubleTapped)
        {

            if (holder.checked)
            {
                holder.noteChecklistDoneCheck.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.noteChecklistDoneCheck.setVisibility(View.GONE);
            }

        }

        holder.noteChecklistMover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    notesCheckListFragment.itemTouchHelper.startDrag(holder);
                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteChecklistItems.size();
    }

    public class NotesChecklistViewHolder extends RecyclerView.ViewHolder {

        public ImageView noteChecklistMover;
        public TextView noteChecklistText;
        public boolean checked;
        public ImageButton noteChecklistClear;
        public ImageView noteChecklistDoneCheck;

        public NotesChecklistViewHolder(@NonNull final View itemView) {
            super(itemView);

            noteChecklistMover = itemView.findViewById(R.id.notes_checklist_item_mover);
            noteChecklistText = itemView.findViewById(R.id.notes_checklist_item_text);
            noteChecklistText.setTextSize(settingsBundle.getFloat("pref_setting_text_size"));
            noteChecklistText.setTypeface(null, settingsBundle.getInt("pref_setting_text_style"));

            noteChecklistClear = itemView.findViewById(R.id.notes_checklist_item_clear);
            noteChecklistDoneCheck = itemView.findViewById(R.id.notes_checklist_item_done_check);

            if (NotesCheckListFragment.option.equals("create_new"))
            {

                noteChecklistMover.setVisibility(View.VISIBLE);
                noteChecklistClear.setVisibility(View.VISIBLE);

            }
            else if (NotesCheckListFragment.option.equals("get_exist"))
            {

                noteChecklistMover.setVisibility(View.GONE);
                noteChecklistClear.setVisibility(View.GONE);

            }

            noteChecklistText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NotesCheckListFragment.inEditMode)
                    {
                        notesCheckListFragment.showCheckListDialog("edit_item", getAdapterPosition());
                        Log.d("check_op", "in check adapter show dialog");
                    }
                    else
                    {
                        if (!checked)
                        {
                            noteChecklistText.setPaintFlags(noteChecklistText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            noteChecklistText.setTextColor(Color.DKGRAY);
                            String tempItem = noteChecklistItems.get(getAdapterPosition());
                            noteChecklistItems.set(getAdapterPosition(), tempItem + "[c]");

                            Log.d("check_op", noteChecklistItems.get(getAdapterPosition()));
                            checked = true;
                            noteChecklistDoneCheck.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            noteChecklistText.setPaintFlags(noteChecklistText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            noteChecklistText.setTextColor(Color.BLACK);
                            String tempItem = noteChecklistItems.get(getAdapterPosition());
                            noteChecklistItems.set(getAdapterPosition(), tempItem.substring(0, tempItem.lastIndexOf("[")));

                            Log.d("check_op", noteChecklistItems.get(getAdapterPosition()));
                            checked = false;
                            noteChecklistDoneCheck.setVisibility(View.GONE);
                        }
                    }
                }
            });

            noteChecklistClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    noteChecklistItems.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

        }
    }
}
