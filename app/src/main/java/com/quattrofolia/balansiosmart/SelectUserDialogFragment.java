package com.quattrofolia.balansiosmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectUserDialogFragment extends DialogFragment {
    private static final String TAG = "SelectUserDialogFragmen";
    private Realm realm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();
        UserAdapter adapter = new UserAdapter(getActivity(), users);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_select_user_title)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* User selected */
                        Log.d(TAG, "User " + i);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
