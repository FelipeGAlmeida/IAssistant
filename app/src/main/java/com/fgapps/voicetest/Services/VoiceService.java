package com.fgapps.voicetest.Services;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import com.fgapps.voicetest.Activities.MainActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by (Engenharia) Felipe on 02/03/2018.
 */

public class VoiceService {

    private static final int SPEECH_REQUEST_CODE = 0;

    private static MainActivity a;
    private static TextToSpeech ts;
    private static SpeechRecognizer sr;

    public static boolean can_listen;
    private static String last_msg;

    public static void init(MainActivity activity){
        a = activity;
        can_listen = true;
        sr = SpeechRecognizer.createSpeechRecognizer(a);
        sr.setRecognitionListener(new listener());
    }

    public static void say(final String toSay){
        ts = new TextToSpeech(a.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                boolean listenAgain = false;
                String s;
                if(toSay.contains("[Q]")){
                    listenAgain = true;
                    s = toSay.replace("[Q]","");
                }else s = toSay.replace("[A]", "");
                ts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                while(ts.isSpeaking());
                ts.shutdown();
                can_listen = true;
                if(listenAgain) listen();
            }
        });
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
        last_msg = a.getResult_view().getText().toString();
        a.getResult_view().setTextColor(Color.YELLOW);
        a.getResult_view().setTextSize(45);
        a.getResult_view().setText("Fale agora !");
    }

    public static void stopListen(){
        sr.stopListening();
    }

    static class listener implements RecognitionListener
    {
        public void onRmsChanged(float rmsdB) {
            if(!a.getResult_view().getText().equals("Fale agora !")){
                sr.stopListening();
            }
        }
        public void onError(int error)
        {
            a.getResult_view().setTextColor(Color.RED);
            a.resultRedirect(SPEECH_REQUEST_CODE,RESULT_CANCELED,null,last_msg);
        }
        public void onResults(Bundle results)
        {
            a.getResult_view().setTextColor(Color.GREEN);
            ArrayList info = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Intent data = new Intent();
            data.putExtra(RecognizerIntent.EXTRA_RESULTS, info);
            a.resultRedirect(SPEECH_REQUEST_CODE,RESULT_OK,data,last_msg);
        }
        public void onReadyForSpeech(Bundle params) {}
        public void onBeginningOfSpeech() {}
        public void onBufferReceived(byte[] buffer) {}
        public void onEndOfSpeech() {}
        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}
    }

}
