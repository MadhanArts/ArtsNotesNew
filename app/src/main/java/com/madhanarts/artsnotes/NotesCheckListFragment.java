package com.madhanarts.artsnotes;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.madhanarts.artsnotes.adapter.NotesChecklistAdapter;
import com.madhanarts.artsnotes.adapter.NotesContentAdapter;
import com.madhanarts.artsnotes.dialog.ChecklistAddItemDialog;
import com.madhanarts.artsnotes.model.NoteItem;

import java.io.File;
import java.util.ArrayList;


public class NotesCheckListFragment extends Fragment implements ChecklistAddItemDialog.ChecklistAddItemDialogListener {

    private Toolbar toolbar;

    private ConstraintLayout noteChecklistAddFirst;
    private ConstraintLayout noteChecklistAddLast;

    private RecyclerView noteChecklistRecycler;
    private NotesChecklistAdapter notesChecklistAdapter;
    public static String option;
    private NoteItem noteItem;

    public static boolean inEditMode;

    private AppCompatActivity activity;
    private ArrayList<String> noteChecklistItems = new ArrayList<>();
    private EditText toolbarEditText;
    private Drawable toolbarEditTextBack;
    private ArrayList<File> noteChecklistItemsFile;

    public NotesCheckListFragment(AppCompatActivity activity, String option, NoteItem noteItem)
    {
        this.activity = activity;
        NotesCheckListFragment.option = option;
        this.noteItem = noteItem;

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
                noteItem = new NoteItem(0, "", new ArrayList<File>(), 0);
            }
            noteChecklistItemsFile = noteItem.getNotesContentPathFiles();
            activity.invalidateOptionsMenu();

            inEditMode = true;

        }
        else if(option.equals("get_exist")) {

            //noteItem = (NoteItem) getArguments().getSerializable("notes_Obj");
            if (noteItem != null) {
                if (noteItem.getNotesContentPathFiles() != null && noteItem.getNotesContentPathFiles().size() > 0) {
                    noteChecklistItemsFile = noteItem.getNotesContentPathFiles();

                } else {
                    noteChecklistItemsFile = new ArrayList<>();

                    for (int i = 0; i < noteChecklistItemsFile.size(); i++) {
                        Log.d("files_list", noteChecklistItemsFile.get(i).getName());
                    }

                    Log.d("files_list", String.valueOf(noteChecklistItemsFile.size()));
                }

                toolbarEditText.setText(noteItem.getNotesTitle());

                textViewModeToolbar();
                NotesContentAdapter.doubleTapped = false;

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
                        this.setEnabled(false);
                        activity.getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


    }

    private void textViewModeChecklist() {

        for (int i = 0; i < noteChecklistItems.size(); i++)
        {
            NotesChecklistAdapter.NotesChecklistViewHolder viewHolder = (NotesChecklistAdapter.NotesChecklistViewHolder) noteChecklistRecycler.findViewHolderForAdapterPosition(i);

            if (viewHolder != null)
            {
                viewHolder.noteChecklistMover.setVisibility(View.GONE);
                viewHolder.noteChecklistMover.setVisibility(View.GONE);

            }

        }

        noteChecklistAddFirst.setVisibility(View.GONE);
        noteChecklistAddLast.setVisibility(View.GONE);

    }

    private void saveNotes()
    {

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
        noteChecklistItems.add(position, item);
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

        Log.d("content_op", "changed to editMode");

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