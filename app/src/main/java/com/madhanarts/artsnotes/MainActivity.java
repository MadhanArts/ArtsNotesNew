package com.madhanarts.artsnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.madhanarts.artsnotes.database.NotesDbOpener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null)
        {

            if (savedInstanceState != null)
            {
                return;
            }

            NotesTitleFragment notesTitleFragment = new NotesTitleFragment(this);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, notesTitleFragment).commit();

        }

    }
}