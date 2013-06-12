package com.example.gtalkclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AccountEditor extends Activity {

    public static final class Result {
        public static final String JID = "rJID";
        public static final String PASSWORD = "rPW";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_editor);
    }

    public void onSaveButtonClick(View button) {
        final String jid = ((TextView) findViewById(R.id.jidField)).getText().toString();
        final String pw = ((TextView) findViewById(R.id.passwordField)).getText().toString();

        Intent intent = new Intent();
        intent.putExtra(Result.JID, jid);
        intent.putExtra(Result.PASSWORD, pw);

        setResult(RESULT_OK, intent);
        finish();
    }
}
