package com.fgapps.voicetest.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fgapps.voicetest.R;
import com.fgapps.voicetest.Services.StorageService;

import java.util.Locale;

public class ConfigActivity extends AppCompatActivity {

    public static final String PACKAGE_NAME_GOOGLE_NOW = "com.google.android.googlequicksearchbox";
    public static final String ACTIVITY_INSTALL_OFFLINE_FILES = "com.google.android.voicesearch.greco3.languagepack.InstallActivity";

    private EditText ts_musica;
    private EditText tc_musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        ts_musica = findViewById(R.id.tsmus_id);
        tc_musica = findViewById(R.id.tcmus_id);

        Button idioma_btn = findViewById(R.id.idiomas_id);
        idioma_btn.setOnClickListener(view -> {
            if(showInstallOfflineVoiceFiles()){
                Toast.makeText(ConfigActivity.this,"Verifique a disponibilidade do idioma, se não instalado, faça o download", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ConfigActivity.this,"Talvez seu dispositivo não ofereça suporte para idiomas offline", Toast.LENGTH_LONG).show();
            }
        });
        Button restaurar_btn = findViewById(R.id.restaurar_id);
        restaurar_btn.setOnClickListener(view -> {
            ts_musica.setText(getResources().getString(R.string.dimmer_no_music));
            tc_musica.setText(getResources().getString(R.string.dimmer_music));
        });
        Button voltar_btn = findViewById(R.id.voltar_id);
        voltar_btn.setOnClickListener(view -> {
            int ts = Integer.parseInt(ts_musica.getText().toString());
            int tc = Integer.parseInt(tc_musica.getText().toString());
            MainActivity.DEFAULT_DELAY = ts;
            MainActivity.MUSIC_DELAY = tc;

            Intent devolve = new Intent();
            devolve.putExtra(StorageService.DSM, ts);
            devolve.putExtra(StorageService.DCM, tc);
            setResult(RESULT_OK, devolve);
            finish();
        });

        ts_musica.setText(String.format(Locale.getDefault(),"%d", MainActivity.DEFAULT_DELAY));
        tc_musica.setText(String.format(Locale.getDefault(),"%d", MainActivity.MUSIC_DELAY));
    }

    public boolean showInstallOfflineVoiceFiles() {

        final Intent intent = new Intent();
        intent.setComponent(new ComponentName(PACKAGE_NAME_GOOGLE_NOW, ACTIVITY_INSTALL_OFFLINE_FILES));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            this.startActivity(intent);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
