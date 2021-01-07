package com.example.caproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Objects;


public class PauseDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_pause, null))
                .setPositiveButton("Resume", (dialog, id) ->
                        listener.onDialoguePositiveClick(PauseDialogFragment.this));
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setTextSize(20);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveBtn.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveBtn.setLayoutParams(positiveButtonLL);
        return dialog;
    }

    public interface IPauseDialogListener {
        void onDialoguePositiveClick(DialogFragment dialog);
    }

    IPauseDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the listener so we can send events to the activity
            listener = (IPauseDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getContext()).toString()
                    + " must implement NoticeDialogListener");
        }
    }
}