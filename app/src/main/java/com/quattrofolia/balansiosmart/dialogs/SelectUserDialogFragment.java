package com.quattrofolia.balansiosmart.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectUserDialogFragment extends DialogFragment {
    private static final String TAG = "SelectUserDialogFragmen";
    private Realm realm;
    private Storage storage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        storage = new Storage();
        final RealmResults<User> users = realm.where(User.class).findAll();
        UserAdapter adapter = new UserAdapter(getActivity(), users);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_select_user_title)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {

                        /* User selected */
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<User> managedUsers;
                                managedUsers = realm.where(User.class).findAll();
                                final User selected = managedUsers.get(i);
                                realm.copyToRealmOrUpdate(new Session(selected.getId()));
                            }
                        },
                        new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                RealmResults<User> managedUsers;
                                managedUsers = realm.where(User.class).findAll();
                                final User selected = managedUsers.get(i);
                                Log.d(TAG, "User " + selected.getId() + " logged in");
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                error.printStackTrace();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
