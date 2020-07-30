package com.madhanarts.artsnotes;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.madhanarts.artsnotes.adapter.NotesChecklistAdapter;
import com.madhanarts.artsnotes.dialog.ChecklistAddItemDialog;
import com.madhanarts.artsnotes.model.NoteItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;


public class NotesCheckListFragment extends Fragment implements ChecklistAddItemDialog.ChecklistAddItemDialogListener {

    private Toolbar toolbar;

    private ConstraintLayout noteChecklistAddFirst;
    private ConstraintLayout noteChecklistAddLast;

    private RecyclerView noteChecklistRecycler;
    private NotesChecklistAdapter notesChecklistAdapter;
    public static String option;
    private NoteItem noteItem;
    private File noteChecklistItemFile;

    public static boolean inEditMode;

    private AppCompatActivity activity;
    private ArrayList<String> noteChecklistItems = new ArrayList<>();
    private EditText toolbarEditText;
    private Drawable toolbarEditTextBack;

    public NotesCheckListFragment(AppCompatActivity activity, String option, NoteItem noteItem)
    {
        this.activity = activity;
        NotesCheckListFragment.option = option;
        this.noteItem = noteItem;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                NotesChecklistAdapter.doubleTapped = true;
                inEditMode = true;
                editViewModeToolbar();
                notesChecklistAdapter.notifyDataSetChanged();
                return true;

            case R.id.content_action_delete:

                /*if (noteItemsFile.size() == 0)
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
*/
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_list, container, false);
        toolbar = view.findViewById(R.id.checklist_toolbar);
        activity.setSupportActionBar(toolbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteChecklistAddFirst = view.findViewById(R.id.notes_checklist_add_first);
        noteChecklistAddLast = view.findViewById(R.id.notes_checklist_add_last);
        noteChecklistRecycler = view.findViewById(R.id.notes_checklist_recycler);
        toolbarEditText = view.findViewById(R.id.checklist_toolbar_edittext);
        toolbarEditTextBack = toolbarEditText.getBackground();

        if (option.equals("create_new"))
        {
            if (noteItem == null)
            {
                noteItem = new NoteItem(0, "", new ArrayList<File>(), 0, "checklist");
            }
            //noteChecklistItemFile = noteItem.getNotesContentPathFiles().get(0);
            activity.invalidateOptionsMenu();

            inEditMode = true;

        }
        else if(option.equals("get_exist")) {

            //noteItem = (NoteItem) getArguments().getSerializable("notes_Obj");
            if (noteItem != null) {
                if (noteItem.getNotesContentPathFiles() != null && noteItem.getNotesContentPathFiles().size() > 0) {
                    noteChecklistItemFile = noteItem.getNotesContentPathFiles().get(0);

                } else {
                    //noteChecklistItemsFile = new ArrayList<>();

                    for (int i = 0; i < noteItem.getNotesContentPathFiles().size(); i++) {
                        Log.d("files_list", noteItem.getNotesContentPathFiles().get(i).getName());
                    }

                    Log.d("files_list", String.valueOf(noteItem.getNotesContentPathFiles().size()));
                }

                noteChecklistItems = new ArrayList<>(Arrays.asList(getFileText(noteChecklistItemFile).split("%%")));

                toolbarEditText.setText(noteItem.getNotesTitle());

                textViewModeToolbar();
                NotesChecklistAdapter.doubleTapped = false;

                inEditMode = false;

            }

        }


        noteChecklistAddFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckListDialog("create_item", 0);
            }
        });

        noteChecklistAddLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckListDialog("create_item", noteChecklistItems.size());
            }
        });


        notesChecklistAdapter = new NotesChecklistAdapter(this, noteChecklistItems);

        noteChecklistRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        noteChecklistRecycler.setHasFixedSize(true);
        noteChecklistRecycler.setAdapter(notesChecklistAdapter);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (option.equals("create_new")) {

                    if (inEditMode && (noteChecklistItems.size() > 0 || !toolbarEditText.getText().toString().equals(""))) {

                        textViewModeToolbar();
                        textViewModeChecklist();
                        saveNotes();

                        inEditMode = false;

                    } else {

                        String tempText = convertItemsToText();

                        if (!getFileText(noteChecklistItemFile).equals(tempText))
                        {
                            setFileText(tempText);
                        }

                        this.setEnabled(false);
                        activity.getSupportFragmentManager().popBackStackImmediate();

                    }
                }
                else if (option.equals("get_exist"))
                {
                    if (inEditMode)
                    {
                        textViewModeToolbar();
                        textViewModeChecklist();
                        saveNotes();
                        inEditMode = false;

                        //Toast.makeText(getContext(), "Back button is pressed", Toast.LENGTH_SHORT).show();

                    }
                    else {

                        String tempText = convertItemsToText();

                        if (!getFileText(noteChecklistItemFile).equals(tempText))
                        {
                            setFileText(tempText);
                        }

                        this.setEnabled(false);
                        activity.getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        toolbarEditText.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                Log.d("touch_event", "double clicked toolbartextview");
                NotesChecklistAdapter.doubleTapped = true;
                inEditMode = true;
                editViewModeToolbar();
                notesChecklistAdapter.notifyDataSetChanged();

            }
        });

    }

    private void textViewModeChecklist() {

        for (int i = 0; i < noteChecklistItems.size(); i++)
        {
            NotesChecklistAdapter.NotesChecklistViewHolder viewHolder = (NotesChecklistAdapter.NotesChecklistViewHolder) noteChecklistRecycler.findViewHolderForAdapterPosition(i);

            if (viewHolder != null)
            {
                viewHolder.noteChecklistMover.setVisibility(View.GONE);
                viewHolder.noteChecklistClear.setVisibility(View.GONE);

            }

        }

        noteChecklistAddFirst.setVisibility(View.GONE);
        noteChecklistAddLast.setVisibility(View.GONE);

    }

    private void saveNotes()
    {

        if (option.equals("create_new")) {

            String noteTitle = toolbarEditText.getText().toString();
            if (noteTitle.trim().equals("")) {
                noteTitle = "Notes ";
            }

            String notesFilePath;

            String filesPathDir = activity.getExternalFilesDir("/").getAbsolutePath();

            SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_hh_mm_ss", Locale.CANADA);
            Date now = new Date();

            String textFileName = "TextChecklist_" + formatter.format(now) + ".txt";

            Log.d("check_op", String.valueOf(now));
            Log.d("check_op", textFileName);

            noteChecklistItemFile = new File(filesPathDir + "/" + textFileName);
            try {
                noteChecklistItemFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String tempText = convertItemsToText();

            Log.d("check_op", "create new saving : " + tempText);

            setFileText(tempText);

            long notesLastModified;

            notesFilePath = noteChecklistItemFile.getAbsolutePath() + "%%";

            notesLastModified = noteChecklistItemFile.lastModified();


            Log.d("notes_files", notesFilePath);

            BackgroundTask backgroundTask = new BackgroundTask(getActivity(), new NotesContentFragment.BackgroundTaskNoteIdListener() {
                @Override
                public void sendAddedNoteId(long noteId) {
                    noteItem.setNoteId((int) noteId);
                }
            });
            backgroundTask.execute("add_new_note", noteTitle, notesFilePath, Long.toString(notesLastModified), "checklist");

            option = "get_exist";

            Log.d("content_op", "create_new Executed... Note added");

        }
        else if (option.equals("get_exist"))
        {

            String noteTitle = toolbarEditText.getText().toString();

            String notesFilePath;

            long notesLastModified;

            String tempText = convertItemsToText();

            if (!getFileText(noteChecklistItemFile).equals(tempText))
            {
                setFileText(tempText);
            }

            Log.d("check_op", "get_exist saving : " + tempText);

            notesFilePath = noteChecklistItemFile.getAbsolutePath() + "%%";
            Log.d("notes_files", notesFilePath);

            notesLastModified = noteChecklistItemFile.lastModified();

            BackgroundTask backgroundTask = new BackgroundTask(getActivity());
            backgroundTask.execute("update_notes", Integer.toString(noteItem.getNoteId()), noteTitle, notesFilePath, Long.toString(notesLastModified));

            Log.d("content_op", "get_exist is executed... Note Updated");
        }


    }

    private String convertItemsToText()
    {
        StringBuilder tempText = new StringBuilder();

        for (int i = 0; i < noteChecklistItems.size(); i++)
        {
            tempText.append(noteChecklistItems.get(i)).append("%%");
        }

        return tempText.toString().trim();
    }

    private String getFileText(File file)
    {
        Scanner myFileReader;
        StringBuilder text = new StringBuilder();

        if (file != null) {
            try {

                myFileReader = new Scanner(file);

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

    private void setFileText(String text)
    {
        if (noteChecklistItemFile != null) {
            try {

                FileOutputStream myFileWriter = new FileOutputStream(noteChecklistItemFile);

                myFileWriter.write(text.getBytes());

                myFileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("content_op", noteChecklistItemFile.getName() + " saved");
        }

    }

    public void showCheckListDialog(String option, int position)
    {
        ChecklistAddItemDialog checklistAddItemDialog = null;
        if (option.equals("create_item"))
        {
            checklistAddItemDialog = new ChecklistAddItemDialog(option, "", position);
        }
        else if (option.equals("edit_item"))
        {
            checklistAddItemDialog = new ChecklistAddItemDialog(option, noteChecklistItems.get(position), position);
        }

        if (checklistAddItemDialog != null) {
            checklistAddItemDialog.setTargetFragment(NotesCheckListFragment.this, 11);
            checklistAddItemDialog.show(activity.getSupportFragmentManager(), "checklist_tag");
        }

    }

    private void addCheckListItem(String item, int position)
    {
        noteChecklistItems.add(position, item);
        notesChecklistAdapter.notifyDataSetChanged();
    }

    private void editCheckListItem(String item, int position)
    {
        noteChecklistItems.set(position, item);
        notesChecklistAdapter.notifyDataSetChanged();
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

        noteChecklistAddFirst.setVisibility(View.GONE);
        noteChecklistAddLast.setVisibility(View.GONE);


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

        noteChecklistAddFirst.setVisibility(View.VISIBLE);
        noteChecklistAddLast.setVisibility(View.VISIBLE);

        Log.d("content_op", "changed to editMode");

    }

    @Override
    public void onPause() {
        super.onPause();

        saveNotes();

    }

    @Override
    public void onAddItemSave(String item, int position) {
        addCheckListItem(item, position);
    }

    @Override
    public void onEditItemSave(String item, int position) {

        editCheckListItem(item, position);

    }
}