package com.fgapps.voicetest.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fgapps.voicetest.Activities.MainActivity;
import com.fgapps.voicetest.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by (Engenharia) Felipe on 02/03/2018.
 */
public class VoiceService {

    private static final int SPEECH_REQUEST_CODE = 0;

    @SuppressLint("StaticFieldLeak") //MainActivity needs to be static for static functions
    private static MainActivity main;
    private static TextToSpeech ts;
    private static SpeechRecognizer sr;

    public static boolean can_listen;
    private static String last_msg;

    public static void init(MainActivity activity){
        main = activity;
        can_listen = true;
        sr = SpeechRecognizer.createSpeechRecognizer(main);
        sr.setRecognitionListener(new listener_sr());
    }

    public static void say(final String toSay){
        if(ts == null) ts = new TextToSpeech(main.getApplicationContext(), onInit -> speak_routine(toSay));
        else speak_routine(toSay);
    }

    private static void speak_routine(final String toSay) {
        String s, listenAgain = "N";
        if(toSay.contains("[Q]")){
            listenAgain = "Y";
            s = toSay.replace("[Q]","");
        }else s = toSay.replace("[A]", "");
        if(!s.isEmpty()) {
            ts.speak(s, TextToSpeech.QUEUE_FLUSH, null, listenAgain);
            ts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    Intent msgrcv = new Intent("Vsc");
                    msgrcv.putExtra("result", "done");
                    Log.v("VOICE_SERVICE","SPEAK SUCESS ! msg: "+toSay);

                    LocalBroadcastManager.getInstance(main.getApplicationContext()).sendBroadcast(msgrcv);
                    can_listen = true;
                    if(s.equals("Y")) main.runOnUiThread(VoiceService::listen);
                }

                @Override
                public void onError(String s) {
                    Intent msgrcv = new Intent("Vsc");
                    msgrcv.putExtra("result", "error");
                    Log.v("VOICE_SERVICE","SPEAK ERROR ! msg: "+toSay);

                    LocalBroadcastManager.getInstance(main.getApplicationContext()).sendBroadcast(msgrcv);
                }
            });
        }
    }

    public static void closeTTS(){
        if(ts != null) {
            ts.shutdown();
            ts = null;
        }
    }

    public static void listen(){
        can_listen = false;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        // Start the activity, the intent will be populated with the speech text
        // and will be returned in the parent class
        sr.startListening(intent);
        last_msg = main.getResult_view().getText().toString();
        main.getResult_view().setTextColor(Color.YELLOW);
        main.getResult_view().setTextSize(45);
        main.getResult_view().setText(main.getResources().getString(R.string.speak_now));
    }

    public static void stopListen(){
        sr.stopListening();
    }

    static class listener_sr implements RecognitionListener
    {
        public void onRmsChanged(float rmsdB) {
            if(!main.getResult_view().getText().equals("Fale agora !")){
                sr.stopListening();
            }
        }
        public void onError(int error)
        {
            main.getResult_view().setTextColor(Color.RED);
            main.resultRedirect(SPEECH_REQUEST_CODE,RESULT_CANCELED,null,last_msg);
        }
        public void onResults(Bundle results)
        {
            main.getResult_view().setTextColor(Color.GREEN);
            ArrayList info = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Intent data = new Intent();
            data.putExtra(RecognizerIntent.EXTRA_RESULTS, info);
            main.resultRedirect(SPEECH_REQUEST_CODE,RESULT_OK,data,last_msg);
        }
        public void onReadyForSpeech(Bundle params) {}
        public void onBeginningOfSpeech() {}
        public void onBufferReceived(byte[] buffer) {}
        public void onEndOfSpeech() {}
        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}
    }

}
