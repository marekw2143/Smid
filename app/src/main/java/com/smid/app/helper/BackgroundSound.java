package com.smid.app.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.smid.app.smid.R;

/**
 * Created by marek on 05.07.16.
 */
public class BackgroundSound extends AsyncTask<Void, Void, Void> {
    static boolean isPlaying = false;

    private Context ctx;

    public BackgroundSound(Context ctx) {

        this.ctx = ctx;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if(isPlaying)
            return null;

        isPlaying = true;
        MediaPlayer mp = MediaPlayer.create(ctx, R.raw.effect_tick);
        mp.start();

        isPlaying = false;

        return null;
    }
}
