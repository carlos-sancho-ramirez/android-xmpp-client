package com.example.gtalkclient;

import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.j2se.Jaxmpp;
import android.os.AsyncTask;

public class LoginPerformer extends AsyncTask<Void, Void, Boolean> {

    private final Jaxmpp mJaxmpp;

    public LoginPerformer(Jaxmpp jaxmpp) {
        mJaxmpp = jaxmpp;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            mJaxmpp.login();

        } catch (JaxmppException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
