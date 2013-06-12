package com.example.gtalkclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class GTalkService extends Service {

    private final XmppControllerImpl mXmppController = new XmppControllerImpl();
    private final ServiceBinder mBinder = new ServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class ServiceBinder extends Binder implements GTalkBinder {

        @Override
        public XmppControllerImpl getXmppController() {
            return mXmppController;
        }
    }
}
