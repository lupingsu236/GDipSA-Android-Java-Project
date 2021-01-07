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
                .setNegativeButton("Resume", (dialog, id) ->
                                listener.onResumeGameClick(PauseDialogFragment.this))
                .setPositiveButton("End Game", (dialog, id) ->
                        listener.onEndGameClick(PauseDialogFragment.this));

        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        //adjust button look and center all elements
        Button resumeGameBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        resumeGameBtn.setTextSize(20);
        resumeGameBtn.setPadding(60,0,60,0);
        Button endGameBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        endGameBtn.setTextSize(20);
        endGameBtn.setPadding(60,0,60,0);
        endGameBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        LinearLayout parent = (LinearLayout) resumeGameBtn.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftspacer = parent.getChildAt(1);
        leftspacer.setVisibility(View.GONE);

        return dialog;
    }

    public interface IPauseDialogListener {
        void onResumeGameClick(DialogFragment dialog);
        void onEndGameClick(DialogFragment dialog);
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