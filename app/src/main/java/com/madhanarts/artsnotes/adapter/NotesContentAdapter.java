package com.madhanarts.artsnotes.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.DoubleClickListener;
import com.madhanarts.artsnotes.LinedEditText;
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

    // Settings Values

    private Bundle settingsBundle;


    public NotesContentAdapter(NotesContentFragment notesContentFragment, Context context, ArrayList<File> notesItemFiles, ToolbarViewChanger toolbarViewChanger, PlayButtonListener playButtonListener, Bundle settingsBundle)
    {
        this.context = context;
        this.notesItemFiles = notesItemFiles;
        this.toolbarViewChanger = toolbarViewChanger;
        this.playButtonListener = playButtonListener;
        this.notesContentFragment = notesContentFragment;
        this.settingsBundle = settingsBundle;

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

            if (doubleTapped)
            {
                //holder.notesEditText.setTextIsSelectable(true);
                holder.notesEditText.setKeyListener(holder.mKeyListener);
                holder.notesEditText.setOnFocusChangeListener(holder.mFocusChangeListener);
                //holder.notesEditText.setFocusable(true);
                //holder.notesEditText.setFocusableInTouchMode(true);

                //holder.notesEditText.setClickable(true);
                Log.d("content_op", "On bind KeyListener : " + holder.mKeyListener.toString());

                holder.notesEditText.setCursorVisible(true);
                holder.notesEditText.setLinksClickable(false);
                holder.notesEditText.setAutoLinkMask(0);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.notesEditText.setShowSoftInputOnFocus(true);
                }
*/
                //holder.notesEditText.setEnabled(true);

                NotesContentFragment.inEditMode = true;
                notesContentFragment.contentNoteModeInfo.setText("Edit Mode");

            }

            String text = getText(position);
            holder.notesEditText.setText(text);



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
        public KeyListener mKeyListener;
        public LinedEditText notesEditText;
        public View.OnTouchListener mOnTouchListener;
        public View.OnFocusChangeListener mFocusChangeListener;

        // for recording layout
        public ConstraintLayout notesRecordLayout;
        public ImageButton notesRecordPlayButton;
        public SeekBar notesRecordSeekBar;
        public Chronometer notesRecordTimer;

        public NotesContentViewHolder(@NonNull final View itemView) {

            super(itemView);

            notesEditText = itemView.findViewById(R.id.notes_item_text);

            // Settings Values
            //settingsBundle.getFloat("pref_setting_text_size");
            notesEditText.setTextSize(settingsBundle.getFloat("pref_setting_text_size"));

            // settings close

            //notesEditText.setRawInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
            //saveTextButton = itemView.findViewById(R.id.notes_item_text_save_button);

            notesRecordLayout = itemView.findViewById(R.id.notes_item_record);
            notesRecordPlayButton = itemView.findViewById(R.id.notes_item_record_play_button);
            notesRecordSeekBar = itemView.findViewById(R.id.notes_item_record_seekbar);
            notesRecordTimer = itemView.findViewById(R.id.notes_item_record_seekbar_timer);

            final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }
            });

            gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if(notesContentFragment.inActionMode && !NotesContentFragment.inEditMode)
                    {
                        if (itemView != null)
                        {
                            notesContentFragment.selectItem(itemView, getAdapterPosition());
                        }
                    }
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if (!notesContentFragment.inActionMode && !NotesContentFragment.inEditMode)
                    {
                        doubleTapped = true;
                        toolbarViewChanger.changeToolbarView();

                        notifyDataSetChanged();
                    }
                    return false;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }
            });

            mOnTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }

            };

            mFocusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                    {
                        Log.d("content_op", "Not Has focus at " + getAdapterPosition());

                        if (!notesEditText.getText().toString().equals(getText(getAdapterPosition())))
                        {

                            saveTextFile(notesEditText.getText().toString(), getAdapterPosition());
                            Toast.makeText(context, "focus changed... and saved", Toast.LENGTH_SHORT).show();
                            //notesEditText.setFocusableInTouchMode(false);
                            //notesEditText.setClickable(false);
                        }
                    }
                }
            };

            if (NotesContentFragment.option.equals("create_new"))
            {
                //notesEditText.setTextIsSelectable(true);
                mKeyListener = notesEditText.getKeyListener();
                notesEditText.setKeyListener(mKeyListener);
                //notesEditText.setFocusable(true);
                //notesEditText.setFocusableInTouchMode(true);

                //notesEditText.setClickable(true);

                notesEditText.setCursorVisible(true);
                //notesEditText.setEnabled(true);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notesEditText.setShowSoftInputOnFocus(true);
                }
                */
                doubleTapped = true;
                NotesContentFragment.inEditMode = true;
                notesEditText.setAutoLinkMask(0);
                notesEditText.setLinksClickable(false);

                notesEditText.setOnTouchListener(mOnTouchListener);
                notesEditText.setOnFocusChangeListener(mFocusChangeListener);


/*                notesEditText.setOnClickListener(new DoubleClickListener() {
                                                    @Override
                                                    public void onSingleClick(View v) {

                                                        if(notesContentFragment.inActionMode && !NotesContentFragment.inEditMode)
                                                        {
                                                            if (itemView != null)
                                                            {
                                                                notesContentFragment.selectItem(itemView, getAdapterPosition());
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onDoubleClick(View v) {

                                                        if (!notesContentFragment.inActionMode && !NotesContentFragment.inEditMode)
                                                        {
                                                            doubleTapped = true;
                                                            toolbarViewChanger.changeToolbarView();

                                                            notifyDataSetChanged();
                                                        }

                                                    }
                                                });*/
            }
            else if (NotesContentFragment.option.equals("get_exist"))
            {

                //notesEditText.setTextIsSelectable(true);
                mKeyListener = notesEditText.getKeyListener();
                Log.d("content_op", "ViewHolder KeyListener : " + mKeyListener.toString());
                notesEditText.setKeyListener(null);
                //notesEditText.setFocusable(false);
                //notesEditText.setFocusableInTouchMode(false);

                //notesEditText.setClickable(false);

                notesEditText.setCursorVisible(false);

                //notesEditText.setEnabled(false);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notesEditText.setShowSoftInputOnFocus(false);
                }
*/
                NotesContentFragment.inEditMode = false;

                notesEditText.setOnTouchListener(mOnTouchListener);
                notesEditText.setOnFocusChangeListener(null);

                notesEditText.setAutoLinkMask(Linkify.ALL);
                notesEditText.setLinksClickable(true);


/*
                notesEditText.setOnClickListener(new DoubleClickListener() {
                                                     @Override
                                                     public void onSingleClick(View v) {
                                                         if(notesContentFragment.inActionMode && !NotesContentFragment.inEditMode)
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

                                                         if (!notesContentFragment.inActionMode && !NotesContentFragment.inEditMode) {
                                                             doubleTapped = true;
                                                             toolbarViewChanger.changeToolbarView();
                                                             notifyDataSetChanged();
                                                         }

                                                     }
                                                 });*/

            }

/*            notesEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!NotesContentFragment.inEditMode && !notesContentFragment.inActionMode)
                    {
                        notesEditText.setFocusable(true);
                        notesEditText.setFocusableInTouchMode(true);
                        notesEditText.requestFocus();
                        notesEditText.setTextIsSelectable(true);
                    }


                    return true;
                }
            });*/


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

        Scanner myFileReader;
        StringBuilder text = new StringBuilder();

        if (position != -1) {
            try {

                myFileReader = new Scanner(notesItemFiles.get(position));

                while (myFileReader.hasNextLine()) {
                    text.append(myFileReader.nextLine()).append("\n");
                }

                text = new StringBuilder(text.toString().trim());

                Log.d("content_op", text.toString());

                myFileReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return text.toString();

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
