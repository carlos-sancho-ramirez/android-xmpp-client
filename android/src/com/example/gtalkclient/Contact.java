package com.example.gtalkclient;

public class Contact {

    public final String bareJid;
    public String displayedStatus;

    public Contact(String jid, String displayedStatus) {

        if (jid == null || displayedStatus == null) {
            throw new IllegalArgumentException();
        }

        bareJid = jid.split("/")[0];
        this.displayedStatus = displayedStatus;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Contact)) {
            return false;
        }

        Contact contact = (Contact)other;
        return other != null && bareJid.equals(contact.bareJid);
    }
}
