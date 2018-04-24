package com.fgapps.voicetest.Services;

import android.content.SharedPreferences;

import com.fgapps.voicetest.Activities.MainActivity;

/**
 * Created by (Engenharia) Felipe on 08/03/2018.
 */

public class StorageService {

    private MainActivity activity;
    private SharedPreferences sp;

    private static final String FILENAME = "config";
    public static final String DSM = "delay-sem-musica";
    public static final String DCM = "delay-com-musica";
    public static final String FUNDO = "fundo_num";

    public StorageService(MainActivity activity) {
        this.activity = activity;
        sp = activity.getSharedPreferences(FILENAME, 0);
    }

    public void saveData(int nFundo){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(DSM, MainActivity.DEFAULT_DELAY);
        editor.putInt(DCM, MainActivity.MUSIC_DELAY);
        editor.putInt(FUNDO, nFundo);
        editor.commit();
    }

    public int loadData(){
        if(sp.contains(DSM)){
            MainActivity.DEFAULT_DELAY = sp.getInt(DSM,35);
        }else MainActivity.DEFAULT_DELAY = 35;
        if(sp.contains(DCM)){
            MainActivity.MUSIC_DELAY = sp.getInt(DCM, 20);
        }else MainActivity.MUSIC_DELAY = 20;
        if(sp.contains(FUNDO)){
            return sp.getInt(FUNDO, 1);
        }else return 1;
    }
}
