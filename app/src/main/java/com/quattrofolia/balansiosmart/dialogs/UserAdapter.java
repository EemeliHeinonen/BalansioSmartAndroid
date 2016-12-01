package com.quattrofolia.balansiosmart.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.RealmResults;


public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, RealmResults<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView userId = (TextView) convertView.findViewById(R.id.textView_userId);
        TextView userFirstName = (TextView) convertView.findViewById(R.id.textView_userFirstName);
        TextView userLastName = (TextView) convertView.findViewById(R.id.textView_userLastName);
        userId.setText("#" + user.getId() + ":");
        userFirstName.setText(user.getFirstName());
        userLastName.setText(user.getLastName());

        return convertView;
    }
}
