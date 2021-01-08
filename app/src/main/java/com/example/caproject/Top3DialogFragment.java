package com.example.caproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;


public class Top3DialogFragment extends DialogFragment {
    private EditText nameInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_top3, null))
                .setPositiveButton("Confirm", (dialog, id) ->
                        listener.onConfirmClick(nameInput.getText().toString()));

        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.show();

        Button confirmBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        confirmBtn.setTextSize(18);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        nameInput = getDialog().findViewById(R.id.nameInput);
    }

    public interface ITop3DialogListener {
        void onConfirmClick(String nameInput);
    }

    ITop3DialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the listener so we can send events to the activity
            listener = (ITop3DialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getContext()).toString()
                    + " must implement NoticeDialogListener");
        }
    }
}