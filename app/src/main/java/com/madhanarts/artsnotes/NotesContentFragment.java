package com.madhanarts.artsnotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.adapter.NotesContentAdapter;
import com.madhanarts.artsnotes.dialog.RecordDialog;
import com.madhanarts.artsnotes.model.NoteItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotesContentFragment extends Fragment implements NotesContentAdapter.ToolbarViewChanger, RecordDialog.RecordDialogListener, NotesContentAdapter.PlayButtonListener {

    public TextView contentNoteModeInfo;
    private RecyclerView notesContentRecycler;
    private NotesContentAdapter notesContentAdapter;

    private NoteItem noteItem;
    private ArrayList<File> noteItemsFile;

    private ArrayList<File> selectedNoteItemsFile = null;
    private ConstraintLayout contentToolbarLayout;
    private ConstraintLayout contentToolbarActionModeLayout;
    private ImageButton contentActionToolbarBackButton;
    private TextView contentActionToolbarTextView;

    private Toolbar toolbar;
    private ImageButton addButton;
    private EditText toolbarEditText;
    private Drawable toolbarEditTextBack;

    public static boolean inEditMode;

    private CharSequence[] menuOptions = {"Text", "Recorder"};
    private String addOptionSelected = menuOptions[0].toString();

    public static String option;


    private File fileToPlay;
    private int fileToPlayPosition = -1;
    private boolean isPlaying = false;
    private boolean isPlayerCompleted = false;
    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;
    private NotesContentAdapter.NotesContentViewHolder viewHolder;
    private long timeWhenStopped = 0;

    public boolean inActionMode = false;
    private int counter = 0;

    private AppCompatActivity activity;

    // Settings Values
    private Bundle settingsBundle = new Bundle();

    public NotesContentFragment(AppCompatActivity activity, String option, NoteItem noteItem)
    {
        this.activity = activity;
        NotesContentFragment.option = option;
        this.noteItem = noteItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notes_content, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        contentToolbarLayout = view.findViewById(R.id.content_toolbar_layout);
        contentToolbarActionModeLayout = view.findViewById(R.id.content_action_toolbar_layout);
        contentActionToolbarBackButton = view.findViewById(R.id.content_toolbar_action_back_button);
        contentActionToolbarTextView = view.findViewById(R.id.content_toolbar_action_text_view);

        contentActionToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentToolbarActionModeLayout.setVisibility(View.GONE);
                contentToolbarLayout.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));

                //toolbar.getMenu().clear();
                inActionMode = false;
                //toolbar.inflateMenu(R.menu.menu_bar_layout);
                removeActionMode();
                selectedNoteItemsFile.clear();
                selectedNoteItemsFile = null;
                counter = 0;

                //makeEditTextFocusable();

            }
        });

        return view;

    }

    private void removeActionMode()
    {
        for (int i = 0; i < selectedNoteItemsFile.size(); i++)
        {
            int index = noteItemsFile.indexOf(selectedNoteItemsFile.get(i));
            NotesContentAdapter.NotesContentViewHolder holder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(index);
            if (holder != null) {
                holder.itemView.setBackgroundResource(R.drawable.notes_item_bg);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private String getExtension(File currentFile)
    {
        String fileName = currentFile.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.note_content_action_mode_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (option.equals("create_new"))
        {
            menu.setGroupVisible(R.id.content_action_text_mode, false);
            menu.findItem(R.id.content_action_done).setVisible(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.content_action_edit:
                NotesContentAdapter.doubleTapped = true;
                inEditMode = true;
                editViewModeToolbar();
                notesContentAdapter.notifyDataSetChanged();
                return true;

            case R.id.content_action_delete:

                if (noteItemsFile.size() == 0)
                {
                    Toast.makeText(getActivity(), "There is no File to delete", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (selectedNoteItemsFile == null)
                {
                    //makeEditTextNotFocusable();
                    selectedNoteItemsFile = new ArrayList<>();
                    inActionMode = true;
                    contentToolbarLayout.setVisibility(View.GONE);
                    contentToolbarActionModeLayout.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(Color.parseColor("#A45B03"));
                    updateToolbarTextView(0);

                }
                else
                {
                    removeActionMode();
                    for (int i = 0; i < selectedNoteItemsFile.size(); i++) {
                        int index = noteItemsFile.indexOf(selectedNoteItemsFile.get(i));
                        deleteNoteItems(index);
                    }

                    selectedNoteItemsFile.clear();
                    contentActionToolbarBackButton.callOnClick();

                }

                Toast.makeText(getActivity(), "Delete is selected", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.content_action_send:

                Toast.makeText(getActivity(), "Send is selected", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.content_action_done:
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = activity.getCurrentFocus();
                if (view == null)
                {
                    view = new View(activity);
                }
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void deleteNoteItems(int index) {

        /*if (getExtension(noteItemsFile.get(index)).equals(".txt")) {

        }*/
        if (getExtension(noteItemsFile.get(index)).equals(".3gp"))
        {
            if (fileToPlay != null){
                viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(index);
                reset(index);
            }
        }

        if (noteItemsFile.get(index).delete()) {
            File file = noteItemsFile.remove(index);
            Log.d("content_op", file.getName() + " Item deleted " + index);

            if (noteItemsFile.size() <= 0) {
                noteItemsFile = new ArrayList<>();
            }

            noteItem.setNotesContentPathFiles(noteItemsFile);
            notesContentRecycler.setAdapter(notesContentAdapter);
            notesContentAdapter.notifyDataSetChanged();
            saveNotes();
        } else {
            Toast.makeText(getContext(), "This item cannot be deleted", Toast.LENGTH_SHORT).show();
        }



    }

/*    private void makeEditTextNotFocusable()
    {
        for (int i = 0; i < noteItemsFile.size(); i++)
        {
            NotesContentAdapter.NotesContentViewHolder viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(i);

            if (viewHolder != null)
            {
                viewHolder.notesEditText.setFocusable(false);
            }
            else
            {
                Toast.makeText(getActivity(), "View Holder in makeEditTextNotFocusable is null", Toast.LENGTH_SHORT).show();
            }


        }
    }*/

    /*private void makeEditTextFocusable()
    {
        for (int i = 0; i < noteItemsFile.size(); i++)
        {
            NotesContentAdapter.NotesContentViewHolder viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(i);

            if (viewHolder != null) {
                viewHolder.notesEditText.setFocusable(true);
                viewHolder.notesEditText.setFocusableInTouchMode(true);
                viewHolder.notesEditText.requestFocus();
                viewHolder.notesEditText.setTextIsSelectable(true);
            }
            else
            {
                Toast.makeText(getActivity(), "View Holder in makeEditTextFocusable is null", Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    public void selectItem(View itemView, int position)
    {
        if (!selectedNoteItemsFile.contains(noteItemsFile.get(position)))
        {
            selectedNoteItemsFile.add(noteItemsFile.get(position));
            counter++;
            itemView.setBackgroundResource(R.drawable.notes_item_action_mode_bg);
        }
        else
        {
            selectedNoteItemsFile.remove(noteItemsFile.get(position));
            counter--;
            itemView.setBackgroundResource(R.drawable.notes_item_bg);

        }
        updateToolbarTextView(counter);
    }

    private void updateToolbarTextView(int counter)
    {
        if (counter >= 0)
        {
            contentActionToolbarTextView.setText(counter + " item selected");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentNoteModeInfo = view.findViewById(R.id.content_mode_info);
        notesContentRecycler = view.findViewById(R.id.notes_content_recycler);
        addButton = view.findViewById(R.id.notes_content_add_button);
        toolbarEditText = view.findViewById(R.id.toolbar_edittext);
        toolbarEditTextBack = toolbarEditText.getBackground();


        //option = getArguments().getString("option", "create_new");
        if (option.equals("create_new"))
        {
            if (noteItem == null)
            {
                noteItem = new NoteItem(0, "", new ArrayList<File>(), 0);
            }
            noteItemsFile = noteItem.getNotesContentPathFiles();
            activity.invalidateOptionsMenu();

            inEditMode = true;

        }
        else if(option.equals("get_exist"))
        {

            //noteItem = (NoteItem) getArguments().getSerializable("notes_Obj");
            if (noteItem != null) {
                if (noteItem.getNotesContentPathFiles() != null && noteItem.getNotesContentPathFiles().size() > 0)
                {
                    noteItemsFile = noteItem.getNotesContentPathFiles();

                    for (int i = 0; i < noteItemsFile.size(); i++)
                    {
                        Log.d("files_list", String.valueOf(noteItemsFile.size()));
                    }

                }
                else
                {
                    noteItemsFile = new ArrayList<>();

                    for (int i = 0; i < noteItemsFile.size(); i++)
                    {
                        Log.d("files_list", noteItemsFile.get(i).getName());
                    }

                    Log.d("files_list", String.valueOf(noteItemsFile.size()));
                }

                toolbarEditText.setText(noteItem.getNotesTitle());

                textViewModeToolbar();
                NotesContentAdapter.doubleTapped = false;

                inEditMode = false;

            }

        }


        notesContentAdapter = new NotesContentAdapter(NotesContentFragment.this, getContext(), noteItemsFile,this, this, settingsBundle);
        notesContentRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        notesContentRecycler.setHasFixedSize(true);

        notesContentRecycler.setAdapter(notesContentAdapter);


        toolbarEditText.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                Log.d("touch_event", "double clicked toolbartextview");
                NotesContentAdapter.doubleTapped = true;
                inEditMode = true;
                editViewModeToolbar();
                notesContentAdapter.notifyDataSetChanged();

            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                addOptionSelected = menuOptions[0].toString();

                builder.setSingleChoiceItems(menuOptions, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addOptionSelected = menuOptions[which].toString();
                        Toast.makeText(getContext(), menuOptions[which] + " is selected", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (addOptionSelected.toLowerCase().equals("text"))
                        {
                            addTextNote();
                            Toast.makeText(getContext(), addOptionSelected, Toast.LENGTH_SHORT).show();
                        }
                        else if (addOptionSelected.toLowerCase().equals("recorder"))
                        {

                            dialog.dismiss();

                            RecordDialog recordDialog = new RecordDialog();

                            recordDialog.setTargetFragment(NotesContentFragment.this, 11);

                            recordDialog.show(activity.getSupportFragmentManager(), "rename_tag");


                            Toast.makeText(getContext(), addOptionSelected + " is selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.setNegativeButton("CANCEL", null);

                builder.create();
                builder.show();
            }
        });


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (option.equals("create_new")) {

                    if (inEditMode && (noteItemsFile.size() > 0 || !toolbarEditText.getText().toString().equals(""))) {

                        textViewModeToolbar();
                        textViewModeEditText();
                        saveNotes();

                        inEditMode = false;
                        NotesContentAdapter.doubleTapped = false;

                    } else {
                        this.setEnabled(false);
                        activity.getSupportFragmentManager().popBackStackImmediate();

                    }
                }
                else if (option.equals("get_exist"))
                {
                    if (inEditMode)
                    {
                        textViewModeToolbar();
                        textViewModeEditText();
                        saveNotes();
                        inEditMode = false;
                        NotesContentAdapter.doubleTapped = false;


                        //Toast.makeText(getContext(), "Back button is pressed", Toast.LENGTH_SHORT).show();

                    }
                    else  if (inActionMode)
                    {
                        contentActionToolbarBackButton.callOnClick();
                    }
                    else {
                        this.setEnabled(false);
                        activity.getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

    }

    private void addTextNote() {
        String filesPathDir = activity.getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        String textFileName = "Text_" + formatter.format(now) + ".txt";

        Log.d("content_op", String.valueOf(now));
        Log.d("content_op", textFileName);

        File textFile = new File(filesPathDir + "/" + textFileName);
        try {
            textFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        noteItemsFile.add(textFile);
        for (int i = 0; i < noteItemsFile.size(); i++) {

            Log.d("content_op", noteItemsFile.get(i).getName());
        }
        noteItem.setNotesContentPathFiles(noteItemsFile);

        notesContentRecycler.setAdapter(notesContentAdapter);

        notesContentAdapter.notifyDataSetChanged();


    }

    private void addRecordNote(File recordFile)
    {
        noteItemsFile.add(recordFile);
        noteItem.setNotesContentPathFiles(noteItemsFile);
        notesContentRecycler.setAdapter(notesContentAdapter);
        notesContentAdapter.notifyDataSetChanged();

    }

    private void reset(int position)
    {

        if (position != -1) {
            if (isPlaying && !isPlayerCompleted) {
                stopAudio();
                viewHolder = null;

            } else if (!isPlaying && !isPlayerCompleted) {
                stopAudio();
                viewHolder = null;
            }


            fileToPlay = null;
        }

    }

    private void pauseAudio()
    {
        mediaPlayer.pause();

        timeWhenStopped = viewHolder.notesRecordTimer.getBase() - SystemClock.elapsedRealtime();
        viewHolder.notesRecordTimer.stop();
        viewHolder.notesRecordPlayButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notes_record_list_play_btn));
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void resumeAudio()
    {
        mediaPlayer.start();
        viewHolder.notesRecordPlayButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notes_record_list_pause_btn));

        viewHolder.notesRecordTimer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        viewHolder.notesRecordTimer.start();

        isPlaying = true;

        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);

    }


    private void stopAudio() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        viewHolder.notesRecordTimer.setBase(SystemClock.elapsedRealtime());
        viewHolder.notesRecordTimer.stop();

        viewHolder.notesRecordPlayButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notes_record_list_play_btn));
        viewHolder.notesRecordSeekBar.setProgress(0);
        isPlaying = false;
        isPlayerCompleted = true;


        seekBarHandler.removeCallbacks(updateSeekBar);

        Toast.makeText(getActivity(), "player stopped", Toast.LENGTH_SHORT).show();
    }


    private void playAudio(File fileToPlay) {
        //Play the audio

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewHolder.notesRecordPlayButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notes_record_list_pause_btn));


        isPlaying = true;
        isPlayerCompleted = false;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                viewHolder.notesRecordSeekBar.setProgress(mediaPlayer.getCurrentPosition());

                stopAudio();
                //playerHeader.setText("Finished");
            }
        });


        viewHolder.notesRecordSeekBar.setMax(mediaPlayer.getDuration());

        viewHolder.notesRecordTimer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
        viewHolder.notesRecordTimer.start();

        seekBarHandler = new Handler();

        updateRunnable();

        //First initialize the thread and starts the seekbar
        seekBarHandler.postDelayed(updateSeekBar, 0);

    }

    private void updateRunnable()
    {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {

                viewHolder.notesRecordSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                //updates the seek bar by calling updateSeekBar at 500 millis
                seekBarHandler.postDelayed(this, 500);

            }
        };
    }



    private void textViewModeToolbar()
    {
        toolbarEditText.setFocusable(false);
        toolbarEditText.setFocusableInTouchMode(false);
        //toolbarEditText.setClickable(false);
        toolbarEditText.setCursorVisible(false);
        toolbarEditText.setBackground(null);

        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.note_content_action_mode_menu);
        toolbar.getMenu().setGroupVisible(R.id.content_action_text_mode, true);
        toolbar.getMenu().findItem(R.id.content_action_done).setVisible(false);

        addButton.setVisibility(View.GONE);


        Toast.makeText(activity, "Changed to text view", Toast.LENGTH_SHORT).show();


        Log.d("content_op", "converted to textview");

    }

    private void editViewModeToolbar()
    {

        //toolbar.getMenu().clear();
        toolbar.getMenu().setGroupVisible(R.id.content_action_text_mode, false);
        toolbar.getMenu().findItem(R.id.content_action_done).setVisible(true);

        toolbarEditText.setFocusable(true);
        toolbarEditText.setFocusableInTouchMode(true);
        //toolbarEditText.setClickable(true);
        toolbarEditText.setCursorVisible(true);
        toolbarEditText.setBackground(toolbarEditTextBack);

        addButton.setVisibility(View.VISIBLE);



        Log.d("content_op", "changed to editMode");

    }

    private void textViewModeEditText()
    {
        for (int i = 0; i < notesContentAdapter.getItemCount(); i++) {
            NotesContentAdapter.NotesContentViewHolder viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(i);

            //notesContentAdapter.saveTextFile(viewHolder.notesEditText.getText().toString(), i);
            if (viewHolder != null) {
                //viewHolder.itemView.setOnCreateContextMenuListener(viewHolder);
                //viewHolder.notesEditText.setTextIsSelectable(true);
                if (viewHolder.notesEditText.getVisibility() == View.VISIBLE && !viewHolder.notesEditText.getText().toString().equals(notesContentAdapter.getText(i)))
                {
                    notesContentAdapter.saveTextFile(viewHolder.notesEditText.getText().toString(), i);
                    Toast.makeText(getActivity(), "Saved in textViewModeEditText", Toast.LENGTH_SHORT).show();
                    Log.d("content_op", "Saved in textViewModeEditText " + i);
                }
                viewHolder.mKeyListener = viewHolder.notesEditText.getKeyListener();
                viewHolder.notesEditText.setKeyListener(null);
                viewHolder.notesEditText.setOnFocusChangeListener(null);
                //viewHolder.notesEditText.setFocusable(false);
                //viewHolder.notesEditText.setFocusableInTouchMode(false);

                //viewHolder.notesEditText.setEnabled(false);
                //viewHolder.notesEditText.setClickable(false);

                viewHolder.notesEditText.setCursorVisible(false);
                viewHolder.notesEditText.setAutoLinkMask(Linkify.ALL);
                viewHolder.notesEditText.setLinksClickable(true);

                String tempText = viewHolder.notesEditText.getText().toString();

                if (!tempText.equals("")) {
                    viewHolder.notesEditText.setText(tempText);
                }

                contentNoteModeInfo.setText("Non Edit Mode");

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.notesEditText.setShowSoftInputOnFocus(false);
                }*/
            }
            else
            {
                Log.d("content_op", "In textView Edittext... view holder is null");
            }
        }
    }

    private void saveNotes()
    {


        if (option.equals("create_new")) {
            String noteTitle = toolbarEditText.getText().toString();
            if (noteTitle.trim().equals("")) {
                noteTitle = "Notes ";
            }

            StringBuilder notesFilePath = new StringBuilder();

            long notesLastModified;
            if (noteItemsFile.size() > 0) {
                notesLastModified = noteItemsFile.get(0).lastModified();
            }
            else
            {
                Date now = new Date();
                notesLastModified = now.getTime();
            }


            for (int i = 0; i < noteItemsFile.size(); i++) {
                notesFilePath.append(noteItemsFile.get(i).getAbsolutePath()).append("%%");
                if (noteItemsFile.get(i).lastModified() > notesLastModified) {
                    notesLastModified = noteItemsFile.get(i).lastModified();
                }

            }

            Log.d("notes_files", notesFilePath.toString());

            BackgroundTask backgroundTask = new BackgroundTask(getActivity(), new BackgroundTaskNoteIdListener() {
                @Override
                public void sendAddedNoteId(long noteId) {
                    noteItem.setNoteId((int) noteId);
                }
            });
            backgroundTask.execute("add_new_note", noteTitle, notesFilePath.toString(), Long.toString(notesLastModified));

            option = "get_exist";

            Log.d("content_op", "create_new Executed... Note added");

        }
        else if (option.equals("get_exist"))
        {

            String noteTitle = toolbarEditText.getText().toString();

            StringBuilder notesFilePath = new StringBuilder();

            long notesLastModified;

            if (noteItemsFile.size() > 0)
            {
                notesLastModified = noteItemsFile.get(0).lastModified();
            }
            else
            {
                Date now = new Date();
                notesLastModified = now.getTime();
            }


            for (int i = 0; i < noteItemsFile.size(); i++) {
                notesFilePath.append(noteItemsFile.get(i).getAbsolutePath()).append("%%");
                if (noteItemsFile.get(i).lastModified() > notesLastModified) {
                    notesLastModified = noteItemsFile.get(i).lastModified();
                }

            }

            Log.d("notes_files", notesFilePath.toString());

            BackgroundTask backgroundTask = new BackgroundTask(getActivity());
            backgroundTask.execute("update_notes", Integer.toString(noteItem.getNoteId()), noteTitle, notesFilePath.toString(), Long.toString(notesLastModified));

            Log.d("content_op", "get_exist is executed... Note Updated");
        }


    }


    @Override
    public void changeToolbarView() {
        NotesContentAdapter.doubleTapped = true;
        inEditMode = true;
        editViewModeToolbar();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (inEditMode && noteItem.getNotesContentPathFiles().size() > 0)
        {
            saveNotes();
        }


        //option = "get_exist";

    }

    @Override
    public void onRecordSave(File recordFile, int position) {
        addRecordNote(recordFile);
    }

    @Override
    public void onPlayButtonClick(File currentFile, int position) {

        if (fileToPlayPosition == position)
        {

            viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(position);
            if (isPlaying)
            {
                if (fileToPlay != null) {
                    pauseAudio();
                }
            }
            else
            {
                if (fileToPlay != null)
                {
                    if(!isPlayerCompleted) {
                        resumeAudio();
                    }
                    else
                    {
                        playAudio(fileToPlay);
                    }
                }
            }
        }
        else {
            reset(fileToPlayPosition);
            viewHolder = (NotesContentAdapter.NotesContentViewHolder) notesContentRecycler.findViewHolderForAdapterPosition(position);
            fileToPlay = currentFile;
            fileToPlayPosition = position;
            if (isPlaying) {
                stopAudio();
            }
            playAudio(fileToPlay);
        }

        viewHolder.notesRecordSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (fileToPlay != null)
                {
                    pauseAudio();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(fileToPlay != null)
                {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });



    }

    @Override
    public void onStop() {
        super.onStop();


        if (isPlaying)
        {
            stopAudio();
        }
        else if (mediaPlayer != null)
        {
            mediaPlayer.release();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String textSizeVal = sharedPreferences.getString(SettingFragment.PREF_TEXT_SIZE, "Medium");

        List<String> textSizeKeys = Arrays.asList(activity.getResources().getStringArray(R.array.pref_text_size_values));
        float[] textSizeValues = {20, 24, 28, 32};
        float testSize;
        testSize = textSizeValues[textSizeKeys.indexOf(textSizeVal)];
        settingsBundle.putFloat("pref_setting_text_size", testSize);


    }

    public interface BackgroundTaskNoteIdListener
    {
        void sendAddedNoteId(long noteId);
    }

}