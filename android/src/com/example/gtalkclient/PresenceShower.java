package com.example.gtalkclient;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PresenceShower extends Activity {

    private static final int REQUEST_CODE_ACCOUNT = 1;

    private static final int LOGIN_DELAY = 4 * 1000;

    private final Handler mHandler = new Handler();

    // Set to null if no service is bound
    private XmppController mController;

    // Values retrieved from the editor that must be used to login
    private String mPendingJid;
    private String mPendingPw;

    public class ServiceConnectionCallback implements ServiceConnection {

        // Set to false when the activity is not interested in getting this
        // callback. This should be called when unbinding the service.
        private boolean mActive = true;

        public void setInactive() {
            mActive = false;
        }

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder rawBinder) {
            if (mActive && rawBinder != null) {
                PresenceShower.this.onServiceConnected((GTalkBinder) rawBinder);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Nothing to be done
        }
    }

    private ServiceConnectionCallback mServiceConnectionCallback;

    private void showMessageForm(final Contact destination) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();

        final View view = LayoutInflater.from(dialog.getContext())
                .inflate(R.layout.message_form, null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = ((TextView) view.findViewById(R.id.titleField))
                        .getText().toString();
                String body = ((TextView) view.findViewById(R.id.bodyField))
                        .getText().toString();

                mController.sendMessage(destination.bareJid, title, body);
                Toast.makeText(PresenceShower.this, "Message sent",
                        Toast.LENGTH_SHORT).show();
            }
        };

        dialog.setView(view);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.sendButton), listener);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presence_shower);

        ((ListView) findViewById(R.id.listView)).setOnItemClickListener(
                new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
                Contact destination = ((ContactAdapter) adapterView.getAdapter())
                        .getItem(position);

                showMessageForm(destination);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent serviceIntent = new Intent(this, GTalkService.class);
        startService(serviceIntent);

        mServiceConnectionCallback = new ServiceConnectionCallback();
        boolean result = bindService(serviceIntent, mServiceConnectionCallback,
                Context.BIND_AUTO_CREATE);

        if (!result) {
            throw new IllegalStateException();
        }
    }

    private void updateViews() {
        if (mController == null) {
            return;
        }

        String jid = mController.getCurrentJid();

        if (jid != null) {
            ((TextView)findViewById(R.id.jidInUse)).setText(jid);
        }

        Set<Contact> contactSet = mController.getContacts();
        ArrayList<Contact> contactList = new ArrayList<Contact>(contactSet.size());
        contactList.addAll(contactSet);

        ((ListView)findViewById(R.id.listView)).setAdapter(
                new ContactAdapter(this, contactList));
    }

    private void onServiceConnected(GTalkBinder binder) {
        mController = binder.getXmppController();

        final Runnable viewUpdater = new Runnable() {

            @Override
            public void run() {
                updateViews();
            }
        };

        String jid = mPendingJid;
        if (jid != null) {
            mController.login(jid, mPendingPw);
            mPendingJid = null;
            mPendingPw = null;
            mHandler.postDelayed( viewUpdater, LOGIN_DELAY);
        }
        else {
            jid = mController.getCurrentJid();

            if (jid == null) {

                Intent intent = new Intent(this, AccountEditor.class);
                startActivityForResult(intent, REQUEST_CODE_ACCOUNT);
            }
            else {
                viewUpdater.run();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ACCOUNT && data != null) {
            Bundle extras = data.getExtras();
            mPendingJid = extras.getString(AccountEditor.Result.JID);
            mPendingPw = extras.getString(AccountEditor.Result.PASSWORD);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.presence_shower, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mController = null;
        unbindService(mServiceConnectionCallback);
        mServiceConnectionCallback.setInactive();
        mServiceConnectionCallback = null;
    }
}
