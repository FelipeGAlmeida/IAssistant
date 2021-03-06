package com.fgapps.voicetest.Services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;

import android.media.AudioManager;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

import com.fgapps.voicetest.Model.Song;

@SuppressWarnings("unchecked") //ArrayList<>.clone returns an Object of ArrayList<>, meaningless warning supressed
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();

    private MediaPlayer player; //media player
    private ArrayList<Song> songs; //song list
    private ArrayList<Song> songs_bck; //backuped list
    private int songPosn; //current position
    private boolean shuffle = false;

    private IntentFilter intentFilter;
    private MyReceiver myReceiver;

    public MusicService() {
        intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        myReceiver = new MyReceiver();
    }

    public void onCreate(){
        super.onCreate();
        songPosn=0; //initialize position
        player = new MediaPlayer(); //create player
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(){
        registerReceiver(myReceiver, intentFilter);
        player.reset();
        Song playSong;
        if(songPosn >= songs.size()) songPosn = 0;
        playSong = songs.get(songPosn);//get song
        long currSong = playSong.getID();//get id
        Uri trackUri = ContentUris.withAppendedId(//set uri
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    public void playNext(){
        songPosn++;
        if(songPosn>=songs.size()) songPosn=0;
        playSong();
    }

    public void volumeDown(){
        player.setVolume(0.15f,0.15f);
    }

    public void volumeUp(){
        player.setVolume(1f,1f);
    }

    public void setList(ArrayList<Song> theSongs){
        songs= (ArrayList<Song>)theSongs.clone();
    }

    public void add2List(ArrayList<Song> newSong){
        if(songs_bck == null) songs_bck = (ArrayList<Song>)songs.clone();
        songs_bck.addAll(newSong);
        songs.addAll(newSong);
        if(shuffle){
            Song s = songs.get(songPosn);
            Collections.shuffle(songs);
            songs.remove(s);
            songs.add(0,s);
            songPosn = 0;
        }
    }

    public void playThisSong(Song s){
        int idx = songs.indexOf(s);
        if(idx > -1) {
            songPosn = idx;
            playSong();
        }
    }

    //Player Status Functions

    public void setShuffle(boolean b){
        Song s = songs.get(songPosn);
        if(b){
            if(!shuffle) songs_bck = (ArrayList<Song>) songs.clone();
            Collections.shuffle(songs);
            songs.remove(s);
            songs.add(0,s);
            songPosn = 0;
            shuffle = true;
        }else{
            songs = (ArrayList<Song>) songs_bck.clone();
            songPosn = songs.indexOf(s);
            shuffle = false;
        }
    }

    public boolean getShuffle(){return shuffle;}

    public String getSongName(){
        return songs.get(songPosn).getName();
    }

    public ArrayList<Song> getSongList() { return songs; }

//    public void setSong(int songIndex){ //UNUSED FUNCTIONS
//        songPosn=songIndex;
//    }
//
//    public int getSongIndex(){
//        return songPosn;
//    }
//
//    public int getPosn(){
//        return player.getCurrentPosition();
//    }
//
//    public int getDur(){
//        return player.getDuration();
//    }
//
//    public void seek(int posn){
//        player.seekTo(posn);
//    }

    public boolean isPng(){
        boolean b;
        try {
            b = player.isPlaying();
        }catch (Exception e){
            return false;
        }
        return b;
    }

    public void pausePlayer(){
        player.pause();
    }

    public void go(){
        player.start();
        volumeUp();
    }

    public void stop(){
        player.stop();
        player.reset();
        unregisterReceiver(myReceiver);
    }

    //Player binding and callbacks functions

    class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(player.getCurrentPosition()>0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        finalizePlayer();
        return false;
    }

    public void finalizePlayer(){
        player.reset();
        player.stop();
        player.release();
    }

}
