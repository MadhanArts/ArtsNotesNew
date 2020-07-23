package com.madhanarts.artsnotes;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordDialog extends DialogFragment {

    private File recordFile = null;
    private String recordFileName;

    private ImageButton recordButton;
    private Button mCancelButton, mSaveButton;
    private Chronometer timer;

    private MediaRecorder mediaRecorder;
    private static final int PERMISSION_CODE = 21;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;


    private boolean isRecording = false;

    public interface RecordDialogListener
    {
        void onRecordSave(File recordFile, int position);
    }

    public RecordDialogListener recordDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        View view = getLayoutInflater().inflate(R.layout.record_dialog_layout, null);

        recordButton = view.findViewById(R.id.record_dialog_record_button);
        mCancelButton = view.findViewById(R.id.record_dialog_cancel_button);
        mSaveButton = view.findViewById(R.id.record_dialog_save_button);
        timer = view.findViewById(R.id.record_dialog_record_timer);


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording)
                {
                    //stop recording
                    stopRecording();

                }
                else
                {
                    //start recording
                    if (checkPermission())
                    {
                        startRecording();
                    }
                }


            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording)
                {
                    stopRecording();
                }
                if (recordFile != null)
                {
                    recordFile.delete();
                }

                getDialog().dismiss();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording) {
                    stopRecording();
                }
                if (recordFile != null) {
                    recordDialogListener.onRecordSave(recordFile, 1);
                }


                getDialog().dismiss();
            }
        });

        builder.setView(view);

        return builder.create();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*adapterPosition = getArguments().getInt("current_adapter_position");
        adapterName = getArguments().getString("current_adapter_name");
*/
    }



    private void stopRecording() {
        timer.stop();


        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
        isRecording = false;

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;

        Toast.makeText(getActivity(), "Recording stopped...", Toast.LENGTH_SHORT).show();

    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();


        SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();


        recordFileName = "Recording_" + formatter.format(now) + ".3gp";
        recordFile = new File(recordPath + "/" + recordFileName);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(recordFile);
        }
        else
        {
            mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        }

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));
        isRecording = true;

        Toast.makeText(getActivity(), "Recording...", Toast.LENGTH_SHORT).show();

    }

    private boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (isRecording) {

            stopRecording();

        }

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //Activity activity = (Activity) context;
        try {

            recordDialogListener = (RecordDialogListener) getTargetFragment();

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("must implement RenameListener method..");
        }
    }
}

