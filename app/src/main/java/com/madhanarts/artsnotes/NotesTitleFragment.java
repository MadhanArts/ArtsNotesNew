package com.madhanarts.artsnotes;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsnotes.adapter.NotesTitleAdapter;
import com.madhanarts.artsnotes.model.NoteItem;

import java.util.ArrayList;

public class NotesTitleFragment extends Fragment implements NotesTitleAdapter.OnNotesTitleClickListener {

    private Toolbar toolbar;

    private RecyclerView notesTitlesRecycler;
    private NotesTitleAdapter notesTitleAdapter;

    private ArrayList<NoteItem> noteItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_title, container, false);

        toolbar = view.findViewById(R.id.toolbar_layout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case 201:

                deleteNote(item.getGroupId());

                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

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


            default:

                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesTitlesRecycler = view.findViewById(R.id.notes_title_recycler);


        //noteItems = getNotes();

        //NotesTitleAdapter notesTitleAdapter = new NotesTitleAdapter(noteItems, this);
        notesTitlesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        notesTitlesRecycler.setHasFixedSize(true);
        //NotesTitlesRecycler.setAdapter(notesTitleAdapter);

        //NotesTitlesRecycler.getAdapter();
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

        BackgroundTask backgroundTask = new BackgroundTask(getActivity(), notesTitlesRecycler, this, new BackgroundTaskCompleteListener() {
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