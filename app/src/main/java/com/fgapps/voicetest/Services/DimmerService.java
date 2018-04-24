package com.fgapps.voicetest.Services;

import android.view.WindowManager;

import com.fgapps.voicetest.Activities.MainActivity;

/**
 * Created by (Engenharia) Felipe on 15/03/2018.
 */

public class DimmerService {

    private static MainActivity a;

    public static boolean isDimmedMin;
    public static int idle_sec;
    public static int wait_sec;

    public static void init(MainActivity activity){
        a = activity;
        isDimmedMin = false;
        idle_sec = 0;
        wait_sec = 35;
    }

    public static void dimmerMin(){
        final WindowManager.LayoutParams params = a.getWindow().getAttributes();
        params.screenBrightness = 0;
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                a.getWindow().setAttributes(params);
                isDimmedMin = true;
            }
        });
    }

    public static void dimmerBack(){
        final WindowManager.LayoutParams params = a.getWindow().getAttributes();
        params.screenBrightness = -1;
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                a.getWindow().setAttributes(params);
                idle_sec = 0;
                isDimmedMin = false;
            }
        });
    }

}
