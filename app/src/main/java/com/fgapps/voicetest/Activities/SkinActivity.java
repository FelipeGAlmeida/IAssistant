package com.fgapps.voicetest.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fgapps.voicetest.R;
import com.fgapps.voicetest.Services.StorageService;

import pl.droidsonroids.gif.GifTextView;

public class SkinActivity extends AppCompatActivity {

    private GifTextView opc1;
    private GifTextView opc2;
    private GifTextView opc3;
    private GifTextView opc4;
    private Button voltar;

    private int fundo;
    private int novoFundo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        voltar = findViewById(R.id.voltar_id);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent devolve = new Intent();
                devolve.putExtra(StorageService.FUNDO, fundo);
                setResult(RESULT_OK, devolve);
                finish();
            }
        });
        opc1 = findViewById(R.id.gif_id1);
        opc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fundo == 1){
                    novoFundo = 2;
                }else{
                    novoFundo = 1;
                }
                choosen();
            }
        });
        opc2 = findViewById(R.id.gif_id2);
        opc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fundo == 1 || fundo == 2){
                    novoFundo = 3;
                }else{
                    novoFundo = 2;
                }
                choosen();
            }
        });
        opc3 = findViewById(R.id.gif_id3);
        opc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fundo == 0){
                    novoFundo = 3;
                }else{
                    novoFundo = 4;
                }
                choosen();
            }
        });
        opc4 = findViewById(R.id.gif_id4);
        opc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fundo == 0){
                    novoFundo = 4;
                }else{
                    novoFundo = 0;
                }
                choosen();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            fundo = bundle.getInt(StorageService.FUNDO);
            switch (fundo){
                case 1: {
                    opc1.setBackgroundResource(R.drawable.back_app3);
                    opc2.setBackgroundResource(R.drawable.back_app6);
                    opc3.setBackgroundResource(R.drawable.back_app2);
                    opc4.setBackgroundResource(R.drawable.back_app);
                }
                    break;
                case 2: {
                    opc1.setBackgroundResource(R.drawable.back_app5);
                    opc2.setBackgroundResource(R.drawable.back_app6);
                    opc3.setBackgroundResource(R.drawable.back_app2);
                    opc4.setBackgroundResource(R.drawable.back_app);
                }
                    break;
                case 3: {
                    opc1.setBackgroundResource(R.drawable.back_app5);
                    opc2.setBackgroundResource(R.drawable.back_app3);
                    opc3.setBackgroundResource(R.drawable.back_app2);
                    opc4.setBackgroundResource(R.drawable.back_app);
                }
                    break;
                case 4: {
                    opc1.setBackgroundResource(R.drawable.back_app5);
                    opc2.setBackgroundResource(R.drawable.back_app3);
                    opc3.setBackgroundResource(R.drawable.back_app6);
                    opc4.setBackgroundResource(R.drawable.back_app);
                }
                    break;
                default: {
                    opc1.setBackgroundResource(R.drawable.back_app5);
                    opc2.setBackgroundResource(R.drawable.back_app3);
                    opc3.setBackgroundResource(R.drawable.back_app6);
                    opc4.setBackgroundResource(R.drawable.back_app2);
                }
            }
        }
    }

    private void choosen(){
        Intent devolve = new Intent();
        devolve.putExtra(StorageService.FUNDO, novoFundo);
        setResult(RESULT_OK, devolve);
        finish();
    }
}
