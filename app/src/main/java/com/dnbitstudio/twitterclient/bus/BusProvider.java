package com.dnbitstudio.twitterclient.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Class to get a Bus singleton for Otto event bus
 */
public class BusProvider
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static Bus bus;

    /**
     * Method to retrieve the bus singleton
     *
     * @return the bus singleton
     */
    public synchronized static Bus getBus()
    {
        if (bus == null)
        {
            bus = new MainThreadBus();
        }

        return bus;
    }

    /**
     * Class to ensure the bus works in the main looper
     */
    public static class MainThreadBus extends Bus
    {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event)
        {
            if (Looper.myLooper() == Looper.getMainLooper())
            {
                super.post(event);
            }
            else
            {
                mainThreadHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        post(event);
                    }
                });
            }
        }
    }
}