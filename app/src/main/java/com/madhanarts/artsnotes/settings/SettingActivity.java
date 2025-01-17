package com.madhanarts.artsnotes.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import com.madhanarts.artsnotes.R;

public class SettingActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar_title);
        title.setText("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);

        if (findViewById(R.id.settings_fragment_container) != null)
        {

            if (savedInstanceState != null)
            {
                return;
            }

            getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingFragment()).commit();

        }

    }

}