package com.fgapps.voicetest.Services;

import android.annotation.SuppressLint;
import android.view.WindowManager;

import com.fgapps.voicetest.Activities.MainActivity;

/**
 * Created by (Engenharia) Felipe on 15/03/2018.
 */

public class DimmerService {

    @SuppressLint("StaticFieldLeak") //MainActivity needs to be static for static functions
    private static MainActivity a;

    public static boolean isDimmedMin;
    static int idle_sec;
    public static int wait_sec;

    public static void init(MainActivity activity){
        a = activity;
        isDimmedMin = false;
        idle_sec = 0;
        wait_sec = 35;
    }

    static void dimmerMin(){
        final WindowManager.LayoutParams params = a.getWindow().getAttributes();
        params.screenBrightness = 0;
        a.runOnUiThread(() -> {
            a.getWindow().setAttributes(params);
            isDimmedMin = true;
        });
    }

    public static void dimmerBack(){
        final WindowManager.LayoutParams params = a.getWindow().getAttributes();
        params.screenBrightness = -1;
        a.runOnUiThread(() -> {
            a.getWindow().setAttributes(params);
            idle_sec = 0;
            isDimmedMin = false;
        });
    }

}
