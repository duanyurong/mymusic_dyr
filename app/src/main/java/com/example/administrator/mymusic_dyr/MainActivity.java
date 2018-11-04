package com.example.administrator.mymusic_dyr;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Music_Service music_service ;
    private Music_Service.MyBinder mybinder;
    private SeekBar seekBar;
    private ListView song_list;
    private Button btn_play;
    private int previous = 1;
    private boolean flag = false;                                      //未点击进行更新
    private boolean isplay = false;
    Intent intent;
    private final ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mybinder = (Music_Service.MyBinder) iBinder;
            music_service = mybinder.getService();
            Adpter();                                            //回调函数后才能完成赋值
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(music_service != null)
            {
                if(!flag)
                {
                    seekBar.setProgress(music_service.getCurrentPosition());
                    seekBar.setMax(music_service.getDuration());
                }
            }
        }
    };




    private void Adpter()
    {
        Log.i("数组", music_service.getFilename().toString());
        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,music_service.getFilename());
        song_list.setAdapter(adapter);
    }

    private void connect_Service(){
        intent = new Intent(MainActivity.this,Music_Service.class);
        startService(intent);
        Log.i("准备进行服务绑定", "connect_Service: ");
        bindService(intent,myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermession();                           //权限请求
        connect_Service();                             //连接服务
        find_ID();                                     //控件绑定
        addListener();                                 //添加监听器
    }

    private void addListener()
    {
        song_list.setOnItemClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                music_service.seekTo(seekBar.getProgress());
                flag = false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {                         //开启线程发送消息，要求更新SeekBar
            @Override
            public void run() {
                while (true)
                {
                    Message message = new Message();
                    handler.sendMessage(message);
                    //Log.i("发送消息", "run: ");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onStop() {
        Log.i("程序", "onStop: ");
        //unbindService(myServiceConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("程序", "onDestroy: ");
        unbindService(myServiceConnection);
        //stopService(intent);
        super.onDestroy();
    }

    public void requestPermession(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void find_ID()
    {
        seekBar = findViewById(R.id.song_bar);
        Log.i("控件绑定", "find_ID: ");
        song_list = findViewById(R.id.song_list);
        btn_play = findViewById(R.id.play);
    }

    public void play(View view) throws IOException {
        if(isplay)
        {
            music_service.pause();
            isplay = false;
            btn_play.setText("播放");
        }
        else
        {
            music_service.play();
            isplay = true;
            btn_play.setText("暂停");
        }
    }

    public void last_song(View view) throws IOException {
        music_service.last_song();
        isplay = true;
        btn_play.setText("暂停");
    }

    public void next_song(View view) throws IOException {
        music_service.next_song();
        isplay = true;
        btn_play.setText("暂停");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if(previous == position)
            {
                Toast.makeText(MainActivity.this,"请勿重复点击！",Toast.LENGTH_SHORT).show();
            }
            else{
                TextView textView=(TextView)parent.getChildAt(previous);
                textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                view.setBackgroundColor(Color.parseColor("#1E90FF"));
                previous = position;
                music_service.setSong_num(position);
                music_service.play(position);
                isplay = true;
                btn_play.setText("暂停");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
