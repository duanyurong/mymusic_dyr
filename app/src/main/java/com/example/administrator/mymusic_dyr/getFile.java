package com.example.administrator.mymusic_dyr;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class getFile {
    private File[] fs;
    private int Song_sum = 0;

    public getFile() {
        search_file();
    }

    private void search_file()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/kugou_music/";
        File mp3Dir = new File(path);
        if(mp3Dir.isDirectory()){
            fs=mp3Dir.listFiles();
            Song_sum = fs.length;
            Log.i("文件名", fs[0].getName());
        }
    }

    public ArrayList<String> getName()
    {
        ArrayList<String> datas = new ArrayList<>();

        for(File f : fs)
        {
            Log.i("字符串", f.getName());
            datas.add(f.getName());
        }

        return datas;
    }

    public int getSong_sum() {
        return Song_sum;
    }

    public void setSong_sum(int song_sum) {
        Song_sum = song_sum;
    }

    public File getFs(int i)
    {
        return fs[i];
    }
}
