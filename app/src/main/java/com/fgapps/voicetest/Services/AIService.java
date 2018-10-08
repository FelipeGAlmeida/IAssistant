package com.fgapps.voicetest.Services;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.view.View;

import com.fgapps.voicetest.Activities.MainActivity;
import com.fgapps.voicetest.Model.Song;
import com.fgapps.voicetest.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static android.os.SystemClock.sleep;

/**
 * Created by (Engenharia) Felipe on 05/03/2018.
 */

public class AIService extends PhoneStateListener {

    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;
    private static final int NEXT = 3;
    private static final int PREV = 4;
    private static final int RANDOM = 5;
    private static final int ALPHA = 6;
    private static final int ALL = 7;
    private static final int FUNK = 8;
    private static final int GENRE = 9;
    private static final int CHOOSEN = 10;
    private static final int FOLDER = 11;
    private static final int YES = 12;
    private static final int NO = 13;
    private static final int SET = 14;
    private static final int ADD = 15;
    private static final int GOTO = 16;

    private ArrayList<Song> songList;
    private ArrayList<Song> song2Play;

    private static MainActivity activity;
    private static MusicService musicSrv;
    private Handler h;
    private Intent playIntent;
    private Thread UIThread;

    public static boolean wasPlaying = false;
    private boolean musicBound = false;
    private boolean finalize = false;
    private  boolean desiring = false;
    private static String toSay;
    private String justSaid;
    private String listen;
    private int fundo_ctrl;

    public AIService(MainActivity a) {
        activity = a;
        h = new Handler();
        toSay = "[A]Toque para interagir";
        justSaid = "";
    }

    public String ai(String l){
        listen = l.toLowerCase();
        if(toSay.length()>0 && !toSay.contains("não entendi") && !wasPlaying && !toSay.contains("hora")
                && !toSay.contains("retomando") && !toSay.contains("fundo") && !toSay.contains("imagem"))
            justSaid = toSay;
        toSay = "";

        if(wasPlaying) {
            if (listen.contains("toca") || listen.contains("inicia") || listen.contains("ouvi")) {
                if (listen.contains("outra") || listen.contains("nova")) {
                    initSong(SET);
                }
            }else if(listen.contains("adicionar")) {
                if (listen.contains("outra") || listen.contains("nova")) {
                    initSong(ADD);
                }
            }else if(listen.contains("ir para") || listen.contains("pular para")){
                ctrlSong(GOTO);
            }else if (listen.contains("pausa")||listen.contains("pause")) {
                ctrlSong(PAUSE);
            }else if (listen.contains("para")||listen.contains("pare")||listen.contains("pará")) {
                ctrlSong(STOP);
            }else if (listen.contains("próxim")||listen.contains("muda")||listen.contains("troca")
                    ||listen.contains("passa")||listen.contains("pula")) {
                if(listen.contains("fundo")||listen.contains("animação")||listen.contains("imagem")){
                    backImage(NEXT);
                }else {
                    ctrlSong(NEXT);
                }
            }else if (listen.contains("anterio")||listen.contains("volta")) {
                if(listen.contains("fundo")||listen.contains("animação")||listen.contains("imagem")){
                    backImage(PREV);
                }else {
                    ctrlSong(PREV);
                }
            }else if (listen.contains("ordem")||listen.contains("aleatori")) {
                if(listen.contains("normal")||listen.contains("alfab")) {
                    ctrlSong(ALPHA);
                }else{
                    ctrlSong(RANDOM);
                }
            }

            else if(listen.contains("horas")) {
                if (listen.contains("são")) {
                    hours();
                }
            }

        }else{
            if(desiring){
                int code;
                if(justSaid.contains("adicionar")) code = ADD;
                else code = SET;
                if((listen.contains("qualquer") || listen.contains("tudo") || listen.contains("toda"))){
                    if(listen.contains("pasta")) desired(FOLDER,code);
                    else desired(ALL,code);
                }else if(listen.contains("funk")){
                    if(listen.contains("pasta")) desired(FOLDER,code);
                    else desired(FUNK,code);
                }else if(listen.contains("pasta")) {
                    desired(FOLDER, code);
                }else if(listen.contains("sertanejo")||listen.contains("rock")||listen.contains("eletrônica")||listen.contains("pop")) {
                    desired(GENRE, code);
                }else if(listen.contains("não") && (listen.contains("retomar") && listen.contains("reprodução")
                            || listen.contains("obrigado"))){
                    if(musicSrv!=null && !song2Play.isEmpty())
                        musicSrv.go();
                    desiring = false;
                }else{
                    desired(CHOOSEN,code);
                }
            }else {
                if (listen.contains("toca") || listen.contains("inicia") || listen.contains("ouvi")
                        || listen.contains("continua") || listen.contains("reproduzi")
                        || listen.contains("prossegui")) {
                    if (activity.checkPermissionForReadExtertalStorage()) {
                        if (justSaid.contains("pausado")) {
                            ctrlSong(PLAY);
                        } else {
                            initSong(SET);
                        }
                    } else {
                        toSay = "[Q]Você não permitiu que o aplicativo acesse suas músicas ainda. Deseja permitir agora?";
                        activity.getResult_view().setText("Você não permitiu que o aplicativo acesse suas músicas ainda. Deseja permitir agora?");
                    }

                } else if (listen.contains("horas")) {
                    if (listen.contains("são")||listen.contains("que")) {
                        hours();
                    }

                } else if (listen.contains("próxima") || listen.contains("muda") || listen.contains("troca")
                        || listen.contains("passa") || listen.contains("pula")) {
                    if (listen.contains("fundo") || listen.contains("animação") || listen.contains("imagem")) {
                        backImage(NEXT);
                    }
                } else if (listen.contains("anterio") || listen.contains("volta")) {
                    if (listen.contains("fundo") || listen.contains("animação") || listen.contains("imagem")) {
                        backImage(PREV);
                    }
                } else if(listen.contains("sim")){
                    if(justSaid.contains("permitir") || justSaid.contains("permissão")){
                        askPermission(YES);
                    }
                } else if(listen.contains("não")){
                    if(toSay.contains("permitir")){
                        askPermission(NO);
                    }
                }
            }
        }

        if(!toSay.contains("[")){
            toSay = "[A]Desculpe, não entendi o que você quis dizer. Comando não aceito";
            activity.getResult_view().setText("Desculpe, não entendi o que você quis dizer. Comando não aceito");
        }

        if(wasPlaying){
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    musicSrv.go();
                }
            },3350);
        }
        return toSay;
    }

    private void initSong(int code){
        if(code == SET) {
            startMusicService();
            prepareMusicList();
            wasPlaying = false;
            initUIThread();
            toSay = "[Q]O que deseja ouvir?";
            activity.getResult_view().setText("O que deseja ouvir?");
        }else{
            toSay = "[Q]O que deseja adicionar à essa playlist?";
            activity.getResult_view().setText("O que deseja adicionar à essa playlist?");
        }
        desiring = true;
        wasPlaying = false;
    }

    private void backImage(int code){
        if(code == NEXT) {
            fundo_ctrl++;
            if (fundo_ctrl > 4) fundo_ctrl = 0;
        }else{
            fundo_ctrl--;
            if(fundo_ctrl < 0) fundo_ctrl = 4;
        }
        setUIImage();
        toSay = "[A]";
    }

    private void hours(){
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int m = Calendar.getInstance().get(Calendar.MINUTE);
        toSay = "[A]Agora são "+Integer.toString(h)+" horas e "+Integer.toString(m)+" minutos";
        String h_zero = "", m_zero = "";
        if(h<10)
            h_zero = "0";
        if(m<10)
            m_zero = "0";
        activity.getResult_view().setText("Agora são "+h_zero+Integer.toString(h)+"h"+m_zero+Integer.toString(m));
    }

    private void ctrlSong(int code){
        if(code == PLAY){
            toSay = "[A]Retomando reprodução";
            justSaid = "[A]Ok, inicializando o player";
            activity.getResult_view().setText("Retomando reprodução");
            DimmerService.wait_sec = MainActivity.MUSIC_DELAY;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    musicSrv.go();
                }
            }, 2300);
        }else if(code == PAUSE){
            musicSrv.pausePlayer();
            wasPlaying = false;
            DimmerService.wait_sec = MainActivity.MUSIC_DELAY;
            toSay = "[A]O player está pausado";
            activity.getResult_view().setText("O player está pausado");
        }else if(code == STOP){
            musicSrv.stop();
            wasPlaying = false;
            DimmerService.wait_sec = MainActivity.DEFAULT_DELAY;
            toSay = "[A]O player foi parado com sucesso";
            activity.getResult_view().setText("O player foi parado com sucesso");
        }else if(code == NEXT){
            next();
            toSay = "[A]";
        }else if(code == PREV){
            prev();
            toSay = "[A]";
        }else if(code == RANDOM){
            musicSrv.setShuffle(true);
            toSay = "[A]";
        }else if(code == ALPHA) {
            musicSrv.setShuffle(false);
            toSay = "[A]";
        }else if(code == GOTO){
            listen = listen.replace("ir para", "")
                    .replace("pular para", "");
            toSay = "[A]";
            desired(CHOOSEN, GOTO);
        }
    }

    private void desired(int code, final int mode){
        song2Play.clear();
        boolean shuffleList = false;
        if(listen.contains("aleatóri")||listen.contains("aleatori")) shuffleList = true;
        if(code == ALL){
            toSay = "[A]Ok, inicializando o player";
            justSaid = toSay;
            activity.getResult_view().setText("Ok, inicializando o player");
            if(songList.size()>0) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        song2Play = songList;
                        musicSrv.setList(song2Play);
                        musicSrv.playSong();
                        desiring = false;
                    }
                }, 3000);
            }else{
                toSay = "[A]Não existem músicas no seu dispositivo";
                justSaid = toSay;
                activity.getResult_view().setText("Não existem músicas no seu dispositivo");
            }
        }else if(code == FUNK){
            for (Song s:songList) {
                if(s.getName().toLowerCase().contains("mc")||s.getFolder().toLowerCase().contains("funk")){
                    song2Play.add(s);
                }
            }
            if(song2Play.size()>0) {
                if(mode == ADD) {
                    toSay = "[A]Novas músicas adicionadas";
                    justSaid = toSay;
                    activity.getResult_view().setText("Novas músicas adicionadas");
                }else{
                    toSay = "[A]Ok, inicializando o player";
                    justSaid = toSay;
                    activity.getResult_view().setText("Ok, inicializando o player");
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mode == ADD) {
                            musicSrv.add2List(song2Play);
                            musicSrv.go();
                        }else {
                            musicSrv.setList(song2Play);
                            musicSrv.playSong();
                        }
                        desiring = false;
                    }
                },3000);
            }else{
                toSay = "[A]Infelizmente não consegui separar músicas de funk, mas você pode falar o nome dos artistas, músicas ou pastas que deseja ouvir";
                activity.getResult_view().setText("Infelizmente não consegui separar músicas de funk, mas você pode falar o nome dos artistas, músicas ou pastas que deseja ouvir");
            }
        }else if(code == GENRE) {
            for(Song s : songList){
                if(matchTo(s.getFolder(), listen)){
                    song2Play.add(s);
                }
            }
            if (song2Play.size() > 0) {
                if(mode == ADD) {
                    toSay = "[A]Novas músicas adicionadas";
                    justSaid = toSay;
                    activity.getResult_view().setText("Novas músicas adicionadas");
                }else{
                    toSay = "[A]Ok, inicializando o player";
                    justSaid = toSay;
                    activity.getResult_view().setText("Ok, inicializando o player");
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mode == ADD) {
                            musicSrv.add2List(song2Play);
                            musicSrv.go();
                        }else {
                            musicSrv.setList(song2Play);
                            musicSrv.playSong();
                        }
                        desiring = false;
                    }
                }, 3000);
            }else {
                toSay = "[A]Infelizmente não consigo separar músicas destes gêneros, mas você pode falar o nome dos artistas, músicas ou pastas que deseja ouvir";
                activity.getResult_view().setText("Infelizmente não consigo separar músicas destes gêneros, mas você pode falar o nome dos artistas, músicas ou pastas que deseja ouvir");
            }
        }else if(code == FOLDER){
            listen = listen.replace(" de", "").replace(" da","").replace(" do","");
            int idx = listen.indexOf("pasta")+6; //pasta de funk
            if(idx < listen.length()){
                String folder = listen.substring(idx, listen.length());
                for(Song s : songList){
                    if(matchTo(s.getFolder(), folder)){
                        song2Play.add(s);
                    }
                }
            }
            if (song2Play.size() > 0) {
                if(mode == ADD) {
                    toSay = "[A]Novas músicas adicionadas";
                    justSaid = toSay;
                    activity.getResult_view().setText("Novas músicas adicionadas");
                }else{
                    toSay = "[A]Ok, inicializando o player";
                    justSaid = toSay;
                    activity.getResult_view().setText("Ok, inicializando o player");
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mode == ADD) {
                            musicSrv.add2List(song2Play);
                            musicSrv.go();
                        }else {
                            musicSrv.setList(song2Play);
                            musicSrv.playSong();
                        }
                        desiring = false;
                    }
                }, 3000);
            }
        }else{
            if(listen.length()>2) {
                String[] desired = listen.split(" ");
                int n_song = songList.size();
                int n_max = desired.length;
                int[] k = new int[n_song];
                for(int i=0;i<n_song;i++){
                    Song s = songList.get(i);
                    for (String d : desired) {
                        if (matchTo(s.getName(), d) || matchTo(s.getArtist(), d) || matchTo(s.getTitle(), d)) {
                            k[i]++;
                        }
                    }
                }


                for (int i=0;i<k.length;i++) {
                    if(k[i] == n_max){
                        if (!song2Play.contains(songList.get(i)))
                            song2Play.add(songList.get(i));
                    }
                }

                if (song2Play.size() > 0) {
                    if (mode == GOTO) {
                        musicSrv.playThisSong(song2Play.get(0));
                    }else {
                        if (mode == ADD) {
                            toSay = "[A]Novas músicas adicionadas";
                            justSaid = toSay;
                            activity.getResult_view().setText("Novas músicas adicionadas");
                        } else {
                            toSay = "[A]Ok, inicializando o player";
                            justSaid = toSay;
                            activity.getResult_view().setText("Ok, inicializando o player");
                        }
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mode == ADD) {
                                    musicSrv.add2List(song2Play);
                                    musicSrv.go();
                                } else if (mode == SET) {
                                    musicSrv.setList(song2Play);
                                    musicSrv.playSong();
                                }
                                desiring = false;
                            }
                        }, 3000);
                    }
                } else {
                    toSay = "[A]Infelizmente consegui nada relacionado ao que disse, se quiser tente novamente";
                    activity.getResult_view().setText("Infelizmente consegui nada relacionado ao que disse, se quiser tente novamente");
                }
            }
        }
        if(!song2Play.isEmpty()){
            if(shuffleList){
                musicSrv.setShuffle(true);
            }
        }
        DimmerService.wait_sec = MainActivity.MUSIC_DELAY;
    }

    private boolean matchTo(String source, String toMatch){
        source = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
        toMatch = Normalizer.normalize(toMatch, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();

        if(source.toLowerCase().contains(toMatch)){
            return true;
        }

        char[] s = source.toLowerCase().toCharArray();
        char[] m = toMatch.toCharArray();

        if(s[0] != m[0]){
            return false;
        }

        boolean flag = false;
        int i=0, i2, equality = 0;
        for(i2=0;i2<s.length;i2++){
            if(i2<m.length+(m.length-equality) && flag) i++;
            if(s[i2]==m[i]){
                equality++;
                flag = true;
            }else flag = false;
            if(i==m.length-1) break;
        }

        if(equality >= i2-2) return true;
        else return false;
    }

    public void initUIThread(){
        if (UIThread == null) {
            UIThread = new Thread(runnable);
            UIThread.start();
        } else if (UIThread.getState() == Thread.State.TERMINATED) {
            UIThread = new Thread(runnable);
            UIThread.start();
        }
    }

    private void askPermission(int code){
        if(code == YES) {
            try {
                toSay = "[A]Toque em 'Permitir' para dar a permissão";
                activity.getResult_view().setText("Permissão pedida");
                activity.requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            toSay = "[A]Você não poderá iniciar o player sem a permissão";
            activity.getResult_view().setText("Você não poderá iniciar o player sem a permissão");
        }
    }

    public void setUIImage() {
        switch (fundo_ctrl){
            case 1: activity.getFundo_view().setBackgroundResource(R.drawable.back_app5);
                break;
            case 2: activity.getFundo_view().setBackgroundResource(R.drawable.back_app3);
                break;
            case 3: activity.getFundo_view().setBackgroundResource(R.drawable.back_app6);
                break;
            case 4: activity.getFundo_view().setBackgroundResource(R.drawable.back_app2);
                break;
            default: activity.getFundo_view().setBackgroundResource(R.drawable.back_app);
        }
        activity.saveData(fundo_ctrl);
    }

    //Music service interface
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicSrv = binder.getService(); //get service
            //musicSrv.setList(songList); //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void prepareMusicList(){
        songList = new ArrayList<Song>();
        song2Play = new ArrayList<Song>();

        getSongList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getName().compareTo(b.getName());
            }
        });
    }

    public void startMusicService(){
        if(playIntent==null){
            playIntent = new Intent(activity, MusicService.class);
            if(!finalize) {
                activity.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                activity.startService(playIntent);
            }
        }
    }

    public void getSongList() {
        ContentResolver musicResolver = activity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int nameColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int c = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisName = musicCursor.getString(nameColumn);
                String thisFolder = musicCursor.getString(c);
                String path[] = thisFolder.split("/");
                thisFolder = path[path.length-2];
                songList.add(new Song(thisId, thisTitle, thisArtist, thisName.replace(".mp3",""), thisFolder));
            }
            while (musicCursor.moveToNext());
        }
    }

    public int getFundo_ctrl() {
        return fundo_ctrl;
    }

    public void setFundo_ctrl(int fundo_ctrl) {
        this.fundo_ctrl = fundo_ctrl;
    }

    //Player controls

    public void setWasPlaying() {
        if(musicSrv!=null)
            wasPlaying = musicSrv.isPng();
    }
    public boolean wasPlaying(){
        return wasPlaying;
    }

    public void start() {
        if(musicSrv!=null)
            musicSrv.go();
    }

    public void playMusic(){
        setWasPlaying();
        if(songList == null) {
            startMusicService();
            prepareMusicList();
            song2Play = songList;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    while (!musicBound);
                    musicSrv.setList(song2Play);
                    musicSrv.playSong();
                    toSay = "[A]Novas músicas adicionadas";
                    justSaid = toSay;
                }
            },500);
        }else {
            if (musicSrv != null && song2Play!=null) {
                musicSrv.go();
                toSay = "[A]Novas músicas adicionadas";
                justSaid = toSay;
            }
        }
    }

    public static void pauseMusic(){
        musicSrv.pausePlayer();
        wasPlaying = false;
        toSay = "[A]O player está pausado";
        activity.getResult_view().setText("O player está pausado");
    }

    public void pause() {
        if(musicSrv!=null)
            if(musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
    }

    public void next(){
        if(musicSrv!=null)
            musicSrv.playNext();
    }

    public void prev(){
        if(musicSrv!=null)
            musicSrv.playPrev();
    }

    public void setShuffle() {
        if(musicSrv!=null) {
            musicSrv.setShuffle(!musicSrv.getShuffle());
        }
    }

    public void volDown(){
        if(musicSrv!=null) {
            musicSrv.volumeDown();
        }
    }

    public void volUp(){
        if(musicSrv!=null) {
            musicSrv.volumeUp();
        }
    }

    public boolean isPlaying() {
        if(musicSrv!=null && musicBound) {
            return musicSrv.isPng();
        }
        return false;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            activity.setScreenSettings();
            while (!finalize) {
                if (musicSrv != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (musicSrv.isPng()) {
                                    activity.getNextBtn().setVisibility(View.VISIBLE);
                                    activity.getPrevBtn().setVisibility(View.VISIBLE);
                                    activity.getPlayStyle().setVisibility(View.VISIBLE);
                                    activity.getPlay_btn().setImageResource(android.R.drawable.ic_media_pause);
                                    String s = musicSrv.getSongName();
                                    activity.getResult_view().setText(s);
                                }else{
                                    activity.getPlay_btn().setImageResource(android.R.drawable.ic_media_play);
                                }
                            }
                        });

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(musicSrv.getShuffle()) activity.getPlayStyle().setImageResource(R.drawable.media_shuffle);
                                else activity.getPlayStyle().setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
                            }
                        });
                }
                if(activity.getResult_view().getText().equals("O player foi parado com sucesso")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getNextBtn().setVisibility(View.INVISIBLE);
                            activity.getPrevBtn().setVisibility(View.INVISIBLE);
                            activity.getPlayStyle().setVisibility(View.INVISIBLE);
                            activity.getPlay_btn().setImageResource(android.R.drawable.ic_media_play);
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    activity.getResult_view().setText("Toque para interagir");
                                }
                            }, 3000);
                            DimmerService.wait_sec = MainActivity.DEFAULT_DELAY;
                        }
                    });
                }
                if(activity.getVoice_layout().getVisibility() == View.VISIBLE){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    activity.getVoice_layout().setVisibility(View.INVISIBLE);
                                }
                            }, 3500);
                        }
                    });
                }

                if(DimmerService.idle_sec >=DimmerService.wait_sec) {
                    if (!DimmerService.isDimmedMin)
                        DimmerService.dimmerMin();
                    DimmerService.idle_sec = 0;
                }
                DimmerService.idle_sec++;

                if(isPlaying()) DimmerService.wait_sec = MainActivity.MUSIC_DELAY;
                else DimmerService.wait_sec = MainActivity.DEFAULT_DELAY;

                sleep(1000);
            }
            activity = null;
        }
    };

    public void finalizeAI(){
        if(musicSrv!=null) {
            finalize = true;
            activity.unbindService(musicConnection);
        }
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if(isPlaying() && state > 0) {
            pause();
        }
        if(state == 0 && song2Play!=null) playMusic();
    }
}
