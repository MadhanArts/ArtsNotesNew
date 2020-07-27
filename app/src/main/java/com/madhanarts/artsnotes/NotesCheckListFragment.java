package com.madhanarts.artsnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NotesCheckListFragment extends Fragment {

    private Toolbar toolbar;

    private RecyclerView noteChecklistRecycler;

    private AppCompatActivity activity;

    public NotesCheckListFragment(AppCompatActivity activity)
    {
        this.activity = activity;
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

        noteChecklistRecycler = view.findViewById(R.id.notes_checklist_recycler);

        noteChecklistRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        noteChecklistRecycler.setHasFixedSize(true);

    }
}