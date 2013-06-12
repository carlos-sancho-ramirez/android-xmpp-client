package com.example.gtalkclient;

import java.util.HashSet;
import java.util.Set;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.factory.UniversalFactory;
import tigase.jaxmpp.core.client.factory.UniversalFactory.FactorySpi;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.PresenceEvent;
import tigase.jaxmpp.j2se.Jaxmpp;
import tigase.jaxmpp.j2se.connectors.socket.SocketConnector.DnsResolver;

public class XmppControllerImpl implements XmppController {

    private static final String GTALK_DOMAIN_NAME = "talk.google.com";
    private final Jaxmpp mJaxmpp = new Jaxmpp();
    private String mCurrentJid;

    private final HashSet<Contact> mPresentContacts = new HashSet<Contact>();

    public XmppControllerImpl() {

        // Needed since Android does not include classes related to DNS that j2se does
        UniversalFactory.setSpi(DnsResolver.class.getName(),
                new FactorySpi<AndroidDNSResolver>() {

            @Override
            public AndroidDNSResolver create() {
                return new AndroidDNSResolver();
            }
        });

        mJaxmpp.getModulesManager().getModule( PresenceModule.class )
                .addListener( PresenceModule.ContactChangedPresence,
                        new Listener<PresenceModule.PresenceEvent>() {

            @Override
            public void handleEvent( PresenceEvent be ) throws JaxmppException {
                String jid = be.getJid().toString();
                String shownStatus = be.getShow().toString();

                Contact contact = new Contact(jid, shownStatus);

                // Remove any matching contact to force status updates
                mPresentContacts.remove(contact);

                mPresentContacts.add(new Contact(jid, shownStatus));
            }
        } );
    }

    @Override
    public boolean login(String jid, String password) {

        if (mCurrentJid != null) {
            throw new IllegalStateException();
        }

        mJaxmpp.getProperties().setUserProperty( SessionObject.DOMAIN_NAME, GTALK_DOMAIN_NAME);
        mJaxmpp.getProperties().setUserProperty( SessionObject.USER_BARE_JID, BareJID.bareJIDInstance( jid ));
        mJaxmpp.getProperties().setUserProperty( SessionObject.PASSWORD, password );

        // Specific for Google Talk
        //mJaxmpp.getConnectionConfiguration().setServer("talk.google.com");
        //mJaxmpp.getConnectionConfiguration().setServer("gmail.com");
        //mJaxmpp.getConnectionConfiguration().setPort(5222);

        new LoginPerformer(mJaxmpp).execute((Void[]) null);
        mCurrentJid = jid;

        return true;
    }

    @Override
    public boolean logout() {

        if (mCurrentJid == null) {
            throw new IllegalStateException();
        }
        mCurrentJid = null;

        try {
            mJaxmpp.disconnect();
        } catch (JaxmppException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public Set<Contact> getContacts() {
        return mPresentContacts;
    }

    @Override
    public boolean sendMessage(String destinationJid, String title, String body) {
        try {
            mJaxmpp.sendMessage(JID.jidInstance(destinationJid), title, body);
            return true;
        } catch (XMLException e) {
            e.printStackTrace();
        } catch (JaxmppException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getCurrentJid() {
        return mCurrentJid;
    }
}
