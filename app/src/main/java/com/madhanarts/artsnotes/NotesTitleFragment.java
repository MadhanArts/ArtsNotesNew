package com.madhanarts.artsnotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
    private TextView toolbarTitle;
    private ConstraintLayout toolbarActionModeLayout;
    private ImageButton toolbarBackButton;
    private TextView toolbarTextView;

    private ArrayList<NoteItem> selectedNoteItems = new ArrayList<>();

    private RecyclerView notesTitlesRecycler;
    private NotesTitleAdapter notesTitleAdapter;

    public boolean inActionMode = false;
    private int counter = 0;

    private AppCompatActivity activity;
    private RelativeLayout emptyNoteView;


    public NotesTitleFragment(AppCompatActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notes_title, container, false);

        toolbar = view.findViewById(R.id.toolbar_layout);
        activity.setSupportActionBar(toolbar);

        toolbarTitle = view.findViewById(R.id.title_toolbar_title);
        toolbarBackButton = view.findViewById(R.id.toolbar_action_back_button);
        toolbarTextView = view.findViewById(R.id.toolbar_action_text_view);
        toolbarActionModeLayout = view.findViewById(R.id.action_toolbar_layout);

        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarActionModeLayout.setVisibility(View.GONE);
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarActionModeLayout.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                toolbar.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                inActionMode = false;
                toolbar.inflateMenu(R.menu.menu_bar_layout);
                removeActionMode();
                selectedNoteItems.clear();
                counter = 0;

            }
        });

        return view;

    }

    private void removeActionMode()
    {
        for (int i = 0; i < selectedNoteItems.size(); i++)
        {
            int index = notesTitleAdapter.getNoteItems().indexOf(selectedNoteItems.get(i));
            NotesTitleAdapter.NotesTitleViewHolder holder = (NotesTitleAdapter.NotesTitleViewHolder) notesTitlesRecycler.findViewHolderForAdapterPosition(index);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar_layout, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_add_icon:

                NotesContentFragment contentFragment = new NotesContentFragment(activity, "create_new", null);
                //Bundle bundle = new Bundle();
                //bundle.putString("option", "create_new");
                //contentFragment.setArguments(bundle);
                activity.getSupportFragmentManager()
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
                    Toast.makeText(getActivity(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_delete:
                removeActionMode();
                for (int i = 0; i < selectedNoteItems.size(); i++) {
                    int index = notesTitleAdapter.getNoteItems().indexOf(selectedNoteItems.get(i));
                    deleteNote(index);
                }

                selectedNoteItems.clear();
                toolbarBackButton.callOnClick();

                if (NotesTitleFragment.this.notesTitleAdapter.getItemCount() == 0)
                {
                    notesTitlesRecycler.setVisibility(View.GONE);
                    emptyNoteView.setVisibility(View.VISIBLE);
                }
                else
                {
                    notesTitlesRecycler.setVisibility(View.VISIBLE);
                    emptyNoteView.setVisibility(View.GONE);
                }

                return true;

            case R.id.action_share:
                Toast.makeText(getActivity(), "Share option is selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_settings:

                Intent intent = new Intent(activity, SettingActivity.class);

                startActivity(intent);

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesTitlesRecycler = view.findViewById(R.id.notes_title_recycler);
        emptyNoteView = view.findViewById(R.id.note_title_empty_view);


        //NotesTitleAdapter notesTitleAdapter = new NotesTitleAdapter(noteItems, this);
        notesTitlesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        notesTitlesRecycler.setHasFixedSize(true);

        //NotesTitlesRecycler.setAdapter(notesTitleAdapter);

    }

    public void actionMode(View itemView, int position)
    {
        if (!inActionMode)
        {
            toolbar.getMenu().clear();

            toolbar.setBackgroundColor(Color.parseColor("#A45B03"));
            toolbarTitle.setVisibility(View.GONE);
            toolbarActionModeLayout.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.context_action_mode_menu);
            selectedNoteItems.add(notesTitleAdapter.getNoteItems().get(position));
            counter++;
            itemView.setBackgroundResource(R.drawable.notes_item_action_mode_bg);
            updateToolbarTextView(counter);
            inActionMode = true;

        }
    }

    public void selectItem(View itemView, int position)
    {
        if (!selectedNoteItems.contains(notesTitleAdapter.getNoteItems().get(position)))
        {
            selectedNoteItems.add(notesTitleAdapter.getNoteItems().get(position));
            counter++;
            itemView.setBackgroundResource(R.drawable.notes_item_action_mode_bg);
        }
        else
        {
            selectedNoteItems.remove(notesTitleAdapter.getNoteItems().get(position));
            counter--;
            itemView.setBackgroundResource(R.drawable.notes_item_bg);

        }
        if (counter == 0)
        {
            toolbarBackButton.callOnClick();
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

        NotesContentFragment contentFragment = new NotesContentFragment(activity, "get_exist", noteItem);
        //Bundle bundle = new Bundle();
        //bundle.putString("option", "get_exist");
        //bundle.putSerializable("notes_Obj", noteItem);


/*        bundle.putString("note_title", noteTitle);
        bundle.putLong("note_last_modified", lastModified);*/
        //contentFragment.setArguments(bundle);


        activity.getSupportFragmentManager()
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
                if (NotesTitleFragment.this.notesTitleAdapter.getItemCount() == 0)
                {
                    notesTitlesRecycler.setVisibility(View.GONE);
                    emptyNoteView.setVisibility(View.VISIBLE);
                }
                else
                {
                    notesTitlesRecycler.setVisibility(View.VISIBLE);
                    emptyNoteView.setVisibility(View.GONE);
                }
            }
        });
        backgroundTask.execute("get_notes");


    }

    public interface BackgroundTaskCompleteListener
    {
        void onTaskComplete(NotesTitleAdapter notesTitleAdapter);
    }

}