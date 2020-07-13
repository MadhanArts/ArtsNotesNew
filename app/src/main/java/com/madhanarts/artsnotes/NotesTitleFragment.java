package com.madhanarts.artsnotes;

import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.adapter.NotesTitleAdapter;
import com.madhanarts.artsnotes.model.NoteItem;

import java.util.ArrayList;


public class NotesTitleFragment extends Fragment implements NotesTitleAdapter.OnNotesTitleClickListener {

    private Toolbar toolbar;
    private ConstraintLayout toolbarActionModeLayout;
    private ImageButton toolbarBackButton;
    private TextView toolbarTextView;

    private ArrayList<NoteItem> selectedNoteItems = new ArrayList<>();

    private RecyclerView notesTitlesRecycler;
    private NotesTitleAdapter notesTitleAdapter;

    public boolean inActionMode = false;
    private int counter = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_title, container, false);

        toolbar = view.findViewById(R.id.toolbar_layout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbarBackButton = view.findViewById(R.id.toolbar_action_back_button);
        toolbarTextView = view.findViewById(R.id.toolbar_action_text_view);
        toolbarActionModeLayout = view.findViewById(R.id.action_toolbar_layout);

        toolbarActionModeLayout.setVisibility(View.GONE);
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarActionModeLayout.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                inActionMode = false;
                toolbar.inflateMenu(R.menu.menu_bar_layout);
                selectedNoteItems.clear();
                counter = 0;
            }
        });

        return view;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar_layout, menu);
    }

/*    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case 201:

                deleteNote(item.getGroupId());

                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_add_icon:

                NotesContentFragment contentFragment = new NotesContentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("option", "create_new");
                contentFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, contentFragment)
                        .commit();


                return true;

            case R.id.menu_sort_title:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (notesTitleAdapter != null) {
                        notesTitleAdapter.sortByTitle();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "It cannot be sorted", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_sort_last_modified:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (notesTitleAdapter != null) {
                        notesTitleAdapter.sortByLastModified();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "It cannot be sorted", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_delete:
                for (int i = 0; i < selectedNoteItems.size(); i++) {
                    deleteNote(i);
                }
                toolbarBackButton.callOnClick();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesTitlesRecycler = view.findViewById(R.id.notes_title_recycler);


        //NotesTitleAdapter notesTitleAdapter = new NotesTitleAdapter(noteItems, this);
        notesTitlesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        notesTitlesRecycler.setHasFixedSize(true);

        //NotesTitlesRecycler.setAdapter(notesTitleAdapter);

    }

    public void actionMode(int position)
    {
        if (!inActionMode)
        {
            toolbar.getMenu().clear();

            toolbarActionModeLayout.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.context_action_mode_menu);
            selectedNoteItems.add(notesTitleAdapter.getNoteItems().get(position));
            counter++;
            updateToolbarTextView(counter);
            inActionMode = true;
        }
    }

    public void selectItem(int position)
    {
        if (!selectedNoteItems.contains(notesTitleAdapter.getNoteItems().get(position)))
        {
            selectedNoteItems.add(notesTitleAdapter.getNoteItems().get(position));
            counter++;
        }
        else
        {
            selectedNoteItems.remove(notesTitleAdapter.getNoteItems().get(position));
            counter--;
        }

        updateToolbarTextView(counter);
    }

    private void updateToolbarTextView(int counter)
    {
        if (counter >= 0)
        {
            toolbarTextView.setText(counter + " item selected");
        }
    }

    private void deleteNote(int position)
    {

        BackgroundTask backgroundTask = new BackgroundTask(getActivity(), notesTitlesRecycler);
        backgroundTask.execute("delete_note", Integer.toString(position));

    }


    @Override
    public void onClickTitle(NoteItem noteItem, int position) {

        NotesContentFragment contentFragment = new NotesContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("option", "get_exist");
        bundle.putSerializable("notes_Obj", noteItem);


/*        bundle.putString("note_title", noteTitle);
        bundle.putLong("note_last_modified", lastModified);*/
        contentFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, contentFragment)
                .commit();

    }


    @Override
    public void onResume() {
        super.onResume();

        BackgroundTask backgroundTask = new BackgroundTask(getActivity(), NotesTitleFragment.this, notesTitlesRecycler, this, new BackgroundTaskCompleteListener() {
            @Override
            public void onTaskComplete(NotesTitleAdapter notesTitleAdapter) {
                NotesTitleFragment.this.notesTitleAdapter = notesTitleAdapter;
            }
        });
        backgroundTask.execute("get_notes");


    }

    public interface BackgroundTaskCompleteListener
    {
        void onTaskComplete(NotesTitleAdapter notesTitleAdapter);
    }

}