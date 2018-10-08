package com.fgapps.voicetest.Services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;


public class NotificationService extends NotificationListenerService {

    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);

        if(isRelevantNotification(pack, title, text))
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }

    private boolean isRelevantNotification(String pack,  String title, String text){
        if(!title.equals("WhatsApp") && !title.contains("Chamada de voz") && //title deve ser o nome do contato
                !title.contains("Procurando novas mensagens") &&
                (pack.contains(".whatsapp") || pack.contains("android.mms")) && //pack deve ser whatapp ou sms
                !text.contains("Chamada de voz") && !text.contains(" novas mensagens")) return true; //text deve conter msg válida
        return false;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}