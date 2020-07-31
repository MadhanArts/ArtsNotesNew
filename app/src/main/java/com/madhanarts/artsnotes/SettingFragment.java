package com.madhanarts.artsnotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

public class SettingFragment extends PreferenceFragment {

    public SettingFragment()
    {
        super();
    }

    public static final String PREF_NOTE_TEXT_SIZE = "pref_note_text_size";
    public static final String PREF_CHECKLIST_TEXT_SIZE = "pref_checklist_text_size";

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                /*if (key.equals(PREF_SYNC))
                {
                    Preference syncPreference = findPreference(key);
                    syncPreference.setSummary(sharedPreferences.getBoolean(key, true) + "");
                }*/

                if (key.equals(PREF_NOTE_TEXT_SIZE))
                {
                    Preference noteTextSizePreference = findPreference(key);
                    noteTextSizePreference.setSummary(sharedPreferences.getString(key,"Medium"));
                }
                if (key.equals(PREF_CHECKLIST_TEXT_SIZE))
                {
                    Preference checklistTextSizePreference = findPreference(key);
                    checklistTextSizePreference.setSummary(sharedPreferences.getString(key, "Medium"));
                }

            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

/*
        Preference syncPreference = findPreference(PREF_SYNC);
        syncPreference.setSummary(getPreferenceScreen().getSharedPreferences().getBoolean(PREF_SYNC, true) + "");
*/

        Preference textSizePreference = findPreference(PREF_NOTE_TEXT_SIZE);
        textSizePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_NOTE_TEXT_SIZE, "Medium"));

        Preference checklistTextSizePreference = findPreference(PREF_CHECKLIST_TEXT_SIZE);
        checklistTextSizePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_CHECKLIST_TEXT_SIZE, "Medium"));

    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);

    }
}
