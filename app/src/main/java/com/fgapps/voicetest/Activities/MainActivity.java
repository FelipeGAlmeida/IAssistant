package com.fgapps.voicetest.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgapps.voicetest.R;
import com.fgapps.voicetest.Services.AIService;
import com.fgapps.voicetest.Services.DimmerService;
import com.fgapps.voicetest.Services.NotificationService;
import com.fgapps.voicetest.Services.StorageService;
import com.fgapps.voicetest.Services.VoiceService;

import java.util.List;

import pl.droidsonroids.gif.GifTextView;


public class MainActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int THEME_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 3;
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 4;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public static int DEFAULT_DELAY = 35;
    public static int MUSIC_DELAY = 20;

    private LinearLayout voice_layout;

    private Button read_btn;
    private GifTextView fundo_view;
    private TextView result_view;
    private TextView voice_view;
    private ImageView playStyle;
    private ImageView themebtn;
    private ImageView play_btn;
    private ImageView next_btn;
    private ImageView prev_btn;
    private ImageView settingsbtn;

    private Handler h;
    private AIService ai;
    private StorageService ss;

    private String listen;
    private String toSay;
    private int p_request = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isNotificationServiceEnabled()) {
            buildNotificationServiceAlertDialog();
        }

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        h = new Handler();
        VoiceService.init(this);
        DimmerService.init(this);
        ai = new AIService(this);

        TelephonyManager telephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(ai, PhoneStateListener.LISTEN_CALL_STATE);

        result_view = findViewById(R.id.result_id);
        result_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    if (VoiceService.can_listen) {
                        VoiceService.can_listen = false;
                        ai.setWasPlaying();
                        while (ai.isPlaying()) {
                            ai.pause();
                        }
                        VoiceService.init(MainActivity.this);
                        VoiceService.listen();
                    } else {
                        VoiceService.stopListen();
                    }
                }
                screenTapped();
            }
        });
        read_btn = findViewById(R.id.read_id);
        read_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    if(ai.isPlaying()) ai.volDown();
                    VoiceService.init(MainActivity.this);
                    VoiceService.say(toSay);
                }
                screenTapped();
            }
        });
        play_btn = findViewById(R.id.music_id);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    if(ai.isPlaying()) ai.volDown();
                    else ai.playMusic();
                }
                screenTapped();
            }

        });
        next_btn = findViewById(R.id.next_id);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    ai.next();
                }
                screenTapped();
            }
        });
        prev_btn = findViewById(R.id.prev_id);
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    ai.prev();
                }
                screenTapped();
            }
        });
        playStyle = findViewById(R.id.playStyle_id);
        playStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    ai.setShuffle();
                }
                screenTapped();
            }
        });
        themebtn = findViewById(R.id.theme_id);
        themebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    Intent intent = new Intent(MainActivity.this, SkinActivity.class);
                    intent.putExtra(StorageService.FUNDO, ai.getFundo_ctrl());
                    startActivityForResult(intent, THEME_REQUEST_CODE);
                }
                screenTapped();
            }
        });
        voice_layout = findViewById(R.id.voiceLayout_id);
        voice_view = findViewById(R.id.voicetxt_id);
        fundo_view = findViewById(R.id.gif_id1);
        fundo_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            screenTapped();
            }
        });
        settingsbtn = findViewById(R.id.settings_id);
        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DimmerService.isDimmedMin) {
                    Intent i = new Intent(MainActivity.this, ConfigActivity.class);
                    startActivityForResult(i, SETTINGS_REQUEST_CODE);
                }
                screenTapped();
            }
        });


        ss = new StorageService(this);
        ai.setFundo_ctrl(ss.loadData());
        ai.setUIImage();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!checkPermissionForRecordAudio()){
                    DimmerService.wait_sec = 300;
                    toSay = "[A]Permita que a aplicação escute você. Essa permissão é indispensável para sua utilização";
                    VoiceService.say(toSay);
                    try {
                        requestPermissionForRecordAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1500);

        toSay = "[A]Toque para interagir";
        ai.initUIThread();

        toggleNotificationListenerService();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Msg");
        intentFilter.addAction("Vsc");


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotify, intentFilter);
    }

    public void screenTapped() {
        DimmerService.dimmerBack();
    }

    public void resultRedirect(final int requestCode,final int resultCode,
                               final Intent data, final String last_msg){
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                result_view.setTextSize(30);
                result_view.setTextColor(Color.WHITE);
                result_view.setText(last_msg);
                onActivityResult(requestCode, resultCode, data);
            }
        },150);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        //Return of what had been listen
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                listen = results.get(0);
                voice_view.setText(listen);
                voice_layout.setVisibility(View.VISIBLE);
                toSay = ai.ai(listen);
                VoiceService.say(toSay);
            } else {
                VoiceService.can_listen = true;
                if (ai.wasPlaying()) {
                    ai.start();
                }
            }
        }else if(requestCode == THEME_REQUEST_CODE){
            if(data!=null) {
            int i = data.getIntExtra(StorageService.FUNDO,0);

                ai.setFundo_ctrl(i);
                ai.setUIImage();
            }
            if(ai.isPlaying()) DimmerService.wait_sec = MUSIC_DELAY;
            else DimmerService.wait_sec = DEFAULT_DELAY;
        }else if(requestCode == SETTINGS_REQUEST_CODE){
            //Nothing to do
        }
        ss.saveData(ai.getFundo_ctrl());
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public boolean checkPermissionForRecordAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void requestPermissionForRecordAudio() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE){
            if(grantResults[0] == 0){
                toSay = "[A]Permita agora que a aplicação acesse seus arquivos para que possa reproduzir suas músicas";
                VoiceService.say(toSay);
                try {
                    requestPermissionForReadExtertalStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                if(p_request < 3) {
                    toSay = "[A]Infelizmente a aplicação não irá funcionar sem a permissão. Permita ou feche a aplicação";
                    VoiceService.say(toSay);
                    p_request++;
                    try {
                        requestPermissionForRecordAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    toSay = "[A]Permissão não concedida. Fechando aplicação";
                    VoiceService.say(toSay);
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1500);
                }
            }
        }else if(requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE){
            if(grantResults[0] == 0){
                toSay = "[A]Ok, agora você poderá aproveitar tudo que a aplicação oferece";
                VoiceService.say(toSay);

            }else{
                toSay = "[A]Você não poderá ouvir suas músicas até que conceda a permissão";
                VoiceService.say(toSay);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setScreenSettings(){
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notificações");
        alertDialogBuilder.setMessage("Para que o aplicativo leia as notificações para você é " +
                "necessário que você habilite uma configuração. Deseja habilitar agora?");
        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        alertDialogBuilder.create().show();
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private BroadcastReceiver onNotify = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("Msg")){
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            toSay = "";
            if(pack.contains(".whatsapp")){ //Mensagem do WhatsApp
                if(!title.equals("WhatsApp")) {
                    toSay = title.replace(" @ ", " no grupo ");
                    if(text.equals("📷 Foto")) toSay += " enviou uma imagem";
                    else{
                        toSay += " disse: ";
                        toSay += text;
                    }
                }
            }
            if(pack.contains("android.mms")){ //Mensagem de SMS
                toSay = title;
                toSay += "disse: ";
                toSay += text;
            }

            if (VoiceService.can_listen) {
                VoiceService.can_listen = false;

                ai.volDown();

                VoiceService.init(MainActivity.this);
                VoiceService.say(toSay);
            }
        } else if(intent.getAction().equals("Vsc")){
                String result = intent.getStringExtra("result");
                if(result.equals("done")){
                    ai.volUp();
                }else if(result.equals("error")) { //Se der erro, tente falar novamente
                    VoiceService.say(toSay);
                }
            }
        }
    };

    public TextView getResult_view() {
        return result_view;
    }

    public ImageView getPlay_btn(){ return play_btn;}

    public ImageView getNextBtn() {return next_btn;}

    public ImageView getPrevBtn() {return prev_btn;}

    public ImageView getPlayStyle() { return  playStyle; }

    public LinearLayout getVoice_layout() { return  voice_layout; }

    public GifTextView getFundo_view() { return fundo_view; }

    public void saveData(int n){
        ss.saveData(n);
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenTapped();
    }

    @Override
    protected void onDestroy() {
        VoiceService.closeTTS();
        if(onNotify != null)
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onNotify);
        ss.saveData(ai.getFundo_ctrl());
        ai.finalizeAI();
        super.onDestroy();
    }
}
