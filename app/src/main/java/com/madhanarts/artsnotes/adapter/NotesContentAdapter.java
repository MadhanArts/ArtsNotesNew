package com.madhanarts.artsnotes.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.DoubleClickListener;
import com.madhanarts.artsnotes.NotesContentFragment;
import com.madhanarts.artsnotes.R;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class NotesContentAdapter extends RecyclerView.Adapter<NotesContentAdapter.NotesContentViewHolder> {

    private Context context;
    private ArrayList<File> notesItemFiles;

    public static boolean doubleTapped = false;
    private ToolbarViewChanger toolbarViewChanger;
    private PlayButtonListener playButtonListener;

    private NotesContentFragment notesContentFragment;


    public NotesContentAdapter(NotesContentFragment notesContentFragment, Context context, ArrayList<File> notesItemFiles, ToolbarViewChanger toolbarViewChanger, PlayButtonListener playButtonListener)
    {
        this.context = context;
        this.notesItemFiles = notesItemFiles;
        this.toolbarViewChanger = toolbarViewChanger;
        this.playButtonListener = playButtonListener;
        this.notesContentFragment = notesContentFragment;

    }


    @NonNull
    @Override
    public NotesContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notes_content_item, parent, false);
        Log.d("content_op", "On layout inflater");

        return new NotesContentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final NotesContentViewHolder holder, int position) {

        String extension = getExtension(notesItemFiles.get(position));

        Log.d("content_op", "On bindView");
        Log.d("content_op", extension);

        if (extension.toLowerCase().equals(".txt"))
        {

            Log.d("content_op", notesItemFiles.get(position).getName() + " " + position);

            String text = getText(position);
            holder.notesEditText.setText(text);

            if (doubleTapped)
            {
                holder.notesEditText.setTextIsSelectable(true);
                holder.notesEditText.setFocusable(true);
                holder.notesEditText.setFocusableInTouchMode(true);

                //holder.notesEditText.setClickable(true);
                //holder.notesEditText.setEnabled(true);

                NotesContentFragment.inEditMode = true;

            }

        }
        else if (extension.equals(".3gp"))
        {
            holder.notesEditText.setVisibility(View.GONE);
            holder.notesRecordLayout.setVisibility(View.VISIBLE);
            if (doubleTapped) {
                //holder.itemView.setOnCreateContextMenuListener(null);
                NotesContentFragment.inEditMode = true;
            }

        }

    }

    @Override
    public int getItemCount() {
        return notesItemFiles.size();
    }

    public class NotesContentViewHolder extends RecyclerView.ViewHolder {

        // for text layout
        public EditText notesEditText;

        // for recording layout
        public ConstraintLayout notesRecordLayout;
        public ImageButton notesRecordPlayButton;
        public SeekBar notesRecordSeekBar;
        public Chronometer notesRecordTimer;

        public NotesContentViewHolder(@NonNull final View itemView) {

            super(itemView);

            notesEditText = itemView.findViewById(R.id.notes_item_text);
            //saveTextButton = itemView.findViewById(R.id.notes_item_text_save_button);

            notesRecordLayout = itemView.findViewById(R.id.notes_item_record);
            notesRecordPlayButton = itemView.findViewById(R.id.notes_item_record_play_button);
            notesRecordSeekBar = itemView.findViewById(R.id.notes_item_record_seekbar);
            notesRecordTimer = itemView.findViewById(R.id.notes_item_record_seekbar_timer);

            if (NotesContentFragment.option.equals("create_new"))
            {
                notesEditText.setTextIsSelectable(true);
                notesEditText.setFocusable(true);
                notesEditText.setFocusableInTouchMode(true);

                //notesEditText.setClickable(true);
                //notesEditText.setEnabled(true);
                doubleTapped = true;
                NotesContentFragment.inEditMode = true;

                notesEditText.setOnClickListener(new DoubleClickListener() {
                                                    @Override
                                                    public void onSingleClick(View v) {

                                                        if(notesContentFragment.inActionMode)
                                                        {
                                                            if (itemView != null)
                                                            {
                                                                notesContentFragment.selectItem(itemView, getAdapterPosition());
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onDoubleClick(View v) {

                                                        if (!notesContentFragment.inActionMode){
                                                            doubleTapped = true;
                                                            toolbarViewChanger.changeToolbarView();

                                                            notifyDataSetChanged();
                                                        }

                                                    }
                                                });
            }
            else if (NotesContentFragment.option.equals("get_exist"))
            {

                notesEditText.setTextIsSelectable(true);
                notesEditText.setFocusable(false);
                notesEditText.setFocusableInTouchMode(false);

                //notesEditText.setClickable(false);
                //notesEditText.setEnabled(false);

                NotesContentFragment.inEditMode = false;

                notesEditText.setOnClickListener(new DoubleClickListener() {
                                                     @Override
                                                     public void onSingleClick(View v) {
                                                         if(notesContentFragment.inActionMode)
                                                         {
                                                             if (itemView != null)
                                                             {
                                                                 notesContentFragment.selectItem(itemView, getAdapterPosition());
                                                             }
                                                         }
                                                         Log.d("touch_event", "Single Clicked");
                                                         //notesEditText.setFocusableInTouchMode(true);
                                                     }

                                                     @Override
                                                     public void onDoubleClick(View v) {
                                                         Log.d("touch_event", "Double Clicked");

                                                         if (!notesContentFragment.inActionMode) {
                                                             doubleTapped = true;
                                                             toolbarViewChanger.changeToolbarView();
                                                             notifyDataSetChanged();
                                                         }

                                                     }
                                                 });
            }

            notesRecordLayout.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (notesContentFragment.inActionMode)
                    {
                        if (itemView != null)
                        {
                            notesContentFragment.selectItem(itemView, getAdapterPosition());
                        }
                    }
                }

                @Override
                public void onDoubleClick(View v) {

                }
            });


            notesEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                    {
                        saveTextFile(notesEditText.getText().toString(), getAdapterPosition());
                        Toast.makeText(context, "focus changed... and saved", Toast.LENGTH_SHORT).show();
                        //notesEditText.setFocusableInTouchMode(false);
                        //notesEditText.setClickable(false);

                    }
                }
            });


            notesRecordPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playButtonListener.onPlayButtonClick(notesItemFiles.get(getAdapterPosition()), getAdapterPosition());

                }
            });






            Log.d("content_op", "View holder class");

        }

    }


    public void saveTextFile(String text, int position)
    {
        if (position != -1) {
            try {

                FileOutputStream myFileWriter = new FileOutputStream(notesItemFiles.get(position));

                myFileWriter.write(text.getBytes());

                myFileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("content_op", notesItemFiles.get(position).getName() + " saved");
        }

    }

    public String getText(int position)
    {

        Scanner myFileReader = null;
        String text = "";

        try {

            myFileReader = new Scanner(notesItemFiles.get(position));

            while (myFileReader.hasNextLine())
            {
                text = text + myFileReader.nextLine() + "\n";
            }

            text = text.trim();

            Log.d("content_op", text);

            myFileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;

    }


    private String getExtension(File currentFile)
    {
        String fileName = currentFile.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }



    public interface ToolbarViewChanger
    {
        void changeToolbarView();
    }

    public interface PlayButtonListener
    {
        void onPlayButtonClick(File currentFile, int position);
    }


}
