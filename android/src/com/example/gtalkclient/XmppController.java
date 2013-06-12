package com.example.gtalkclient;

import java.util.Set;

public interface XmppController {

    /**
     * Starts communications with the server
     *
     * @param jid Jabber ID identifying the user.
     * @param password Needed to open the connection.
     *
     * @return Whether it succeeded or not.
     */
    public boolean login(String jid, String password);

    /**
     * Close the connections to the IM server.
     *
     * @return Whether it succeeded or not.
     */
    public boolean logout();

    /**
     * Returns the current JID in used. This will be null if the session is
     * closed.
     *
     * @return The Jabber ID provided in the last time the method login was
     * called.
     */
    public String getCurrentJid();

    /**
     * Retrieve the current status for the contacts.
     *
     * It is required to log in the IM server first.
     *
     * @return A set of contacts. The returned instance could be the same used
     * internally in the controller. This means the contents of this list may
     * change. Do not modify the contents of this list.
     */
    public Set<Contact> getContacts();

    /**
     * Sends a message to the given destination.
     *
     * @param destinationJid Bare Jabber ID for the destiny of the message.
     * @param title String to be displayed as the title of the message.
     * @param body String to be sent as the body of the message.
     * @return Whether it succeeded or not.
     */
    public boolean sendMessage(String destinationJid, String title, String body);
}
