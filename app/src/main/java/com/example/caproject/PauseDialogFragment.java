package com.example.caproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
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
                .setNegativeButton("Restart", (dialog, id) ->
                                listener.onRestartGameClick(PauseDialogFragment.this))
                .setNeutralButton("Resume", (dialog, id) ->
                                listener.onResumeGameClick(PauseDialogFragment.this))
                .setPositiveButton("End Game", (dialog, id) ->
                        listener.onEndGameClick(PauseDialogFragment.this));

        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.show();

        //adjust button look and center all elements
        Button[] buttons = {dialog.getButton(AlertDialog.BUTTON_NEUTRAL),
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE),
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)};

        for (Button button : buttons) {
            button.setTextSize(20);
            button.setPadding(25,0,25,0);
        }
        buttons[1].setTextColor(ContextCompat.getColor(getContext(), R.color.purple_700));
        buttons[2].setTextColor(ContextCompat.getColor(getContext(), R.color.red));

        LinearLayout parent = (LinearLayout) buttons[0].getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftspacer = parent.getChildAt(1);
        leftspacer.setVisibility(View.GONE);

        return dialog;
    }

    public interface IPauseDialogListener {
        void onResumeGameClick(DialogFragment dialog);
        void onEndGameClick(DialogFragment dialog);
        void onRestartGameClick(DialogFragment dialog);
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