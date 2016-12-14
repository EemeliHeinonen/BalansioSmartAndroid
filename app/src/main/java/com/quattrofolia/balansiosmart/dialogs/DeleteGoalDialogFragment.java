package com.quattrofolia.balansiosmart.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Goal;

import io.realm.Realm;


public class DeleteGoalDialogFragment extends DialogFragment {

    private int goalId;

    public static DeleteGoalDialogFragment newInstance(int goalId) {
        DeleteGoalDialogFragment f = new DeleteGoalDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id", goalId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goalId = getArguments().getInt("id");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_delete_goal)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                }).setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Goal goalToDelete = realm.where(Goal.class).equalTo("id", goalId).findFirst();
                        goalToDelete.deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        dismiss();
                    }
                });

                realm.close();
            }
        });
        return builder.create();
    }
}