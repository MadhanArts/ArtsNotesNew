package com.madhanarts.artsnotes.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.madhanarts.artsnotes.R;


public class ChecklistAddItemDialog extends DialogFragment {

    private TextView checklistAddItemTitle;
    private EditText checklistAddItemEditText;
    private Button checklistAddItemSaveButton;
    private Button checklistAddItemCancelButton;

    private String option;
    private int position;
    private String item;

    public ChecklistAddItemDialog(String option, String item, int position)
    {
        this.option = option;
        this.item = item;
        this.position = position;
    }

    public interface ChecklistAddItemDialogListener
    {
        void onAddItemSave(String item, int position);
        void onEditItemSave(String item, int position);
    }

    public ChecklistAddItemDialogListener checklistAddItemDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getLayoutInflater().inflate(R.layout.checklist_add_item_dialog_layout, null);

        checklistAddItemTitle = view.findViewById(R.id.checklist_add_item_title);
        checklistAddItemEditText = view.findViewById(R.id.checklist_add_item_edittext);
        checklistAddItemCancelButton = view.findViewById(R.id.checklist_add_item_cancel_button);
        checklistAddItemSaveButton = view.findViewById(R.id.checklist_add_item_save_button);

        if (option.equals("edit_item"))
        {
            checklistAddItemEditText.setText(item);
            checklistAddItemEditText.setSelection(item.length());
        }


        checklistAddItemSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checklistAddItemEditText.getText().toString().equals(""))
                {
                    if (option.equals("create_item"))
                    {
                        checklistAddItemDialogListener.onAddItemSave(checklistAddItemEditText.getText().toString(), position);
                    }
                    else
                    {
                        checklistAddItemDialogListener.onEditItemSave(checklistAddItemEditText.getText().toString(), position);
                    }
                }

                getDialog().dismiss();

            }
        });

        checklistAddItemCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setView(view);

        return builder.create();

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //Activity activity = (Activity) context;
        try {

            checklistAddItemDialogListener = (ChecklistAddItemDialogListener) getTargetFragment();

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("must implement RenameListener method..");
        }
    }
}
