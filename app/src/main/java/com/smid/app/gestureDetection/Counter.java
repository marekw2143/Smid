package com.smid.app.gestureDetection;

/**
 * Created by marek on 01.07.16.
 */
public class Counter {

    private long counter;
    private final Object CounterSync = new Object();

    public long getNextCounter() {
        synchronized (CounterSync) {
            counter++;

            return counter;
        }
    }

    public void resetCounter () {
        synchronized (CounterSync) {
            counter = 0;
        }
    }
}
