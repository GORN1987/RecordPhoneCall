package com.guilherme.recordphonecall;

import java.util.Observable;

/**
 * Created by dell on 02/09/2018.
 */

public class BroadcastObserver extends Observable {


    private static BroadcastObserver _broadcastObserver;
    public static BroadcastObserver getIntance()
    {
        if (_broadcastObserver == null)
        {
            _broadcastObserver =  new BroadcastObserver();
            return _broadcastObserver;

        }
        return _broadcastObserver;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    public void change() {
        triggerObservers();
    }

}
