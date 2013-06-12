package com.example.gtalkclient;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<Contact> mContacts;

    public ContactAdapter(Context context, List<Contact> contacts) {
        if (contacts == null) {
            throw new IllegalArgumentException();
        }

        mContacts = contacts;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View previousView, ViewGroup parent) {

        View view = previousView;
        if (previousView == null) {
            view = mInflater.inflate(R.layout.contact_entry, parent, false);
        }

        final Contact contact = mContacts.get(position);
        ((TextView) view.findViewById(R.id.contactEntryText)).setText(contact.bareJid);
        ((TextView) view.findViewById(R.id.contactEntryStatus)).setText(contact.displayedStatus);

        return view;
    }

}
