package com.example.administrator.mymusic_dyr;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Music_Service extends Service {
    private int Song_sum = 0;
    private int Song_num = 0;
    private MediaPlayer player;
    private getFile dir_file;

    @Override
    public void onCreate() {
        dir_file = new getFile();
        player = new MediaPlayer();
        Song_sum = dir_file.getSong_sum();
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentPosition()
    {
        return player.getCurrentPosition();
    }

    public int getDuration()
    {
        return player.getDuration();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public MyBinder mybinder = new MyBinder();
    public class MyBinder extends Binder{
        Music_Service getService(){
            return Music_Service.this;
        }
    }

    public void init() throws IOException {
        Log.i("初始化", Integer.toString(Song_num));
        player.reset();
        Log.i("chushihua", "init: ");
        player.setDataSource(dir_file.getFs(Song_num).getAbsolutePath());
        player.prepare();
        Log.i("asdf", "zhunbei");
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    next_song();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void play(){
        Log.i("play", "play: ");
        player.start();
    }

    public void play(int i) throws IOException {
        Song_num = i;
        init();
        player.start();
    }

    public void last_song() throws IOException {
        if( Song_num <= 0 )
        {
            Song_num += Song_sum;
        }
        Song_num = (Song_num - 1) % Song_sum;
        init();
        play();
    }

    public ArrayList<String> getFilename()
    {
        return dir_file.getName();
    }

    public void next_song() throws IOException {
        Log.i("i", Integer.toString(Song_num));
        Song_num = (Song_num + 1) % Song_sum;
        init();
        play();
    }

    public void pause() { player.pause(); }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("服务端", "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("服务", "onDestroy: ");
    }

    public void seekTo(int sec)
    {
        player.seekTo(sec);
    }

    public void setSong_num(int num)
    {
        Song_num = num;
    }
}
