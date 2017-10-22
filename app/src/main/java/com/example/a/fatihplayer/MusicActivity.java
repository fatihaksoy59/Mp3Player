package com.example.a.fatihplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by A on 10/22/2017.
 */

    public class MusicActivity extends AppCompatActivity {

    private TextView startTimeTV,endTimeTV,songTV,artistTV;
    private MediaPlayer mediaPlayer;
    private ImageView playBtn;
    private ImageView nextBtn;
    private ImageView prevBtn;
    private SeekBar songSB;

    int myPosition=0;
    int mySongsSize=0;
    private double currentTime=0;
    private double durationTime=0;
    ArrayList<String> mySongsName;
    ArrayList<String> mySongsArtistName;
    ArrayList<String> mySongsLoc;
    ArrayList<Bitmap> mySongsPhoto;

    private Handler myHandler=new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_layout);

        startTimeTV=(TextView)findViewById(R.id.text_view_progress) ;
        endTimeTV=(TextView)findViewById(R.id.text_view_duration);
        songTV=(TextView)findViewById(R.id.text_view_name) ;
        artistTV=(TextView)findViewById(R.id.text_view_artist);

        playBtn=(ImageView)findViewById(R.id.button_play_toggle);
        nextBtn=(ImageView)findViewById(R.id.button_play_next);
        prevBtn=(ImageView)findViewById(R.id.button_play_last);
        songSB=(SeekBar)findViewById(R.id.seek_bar);


        mySongsName=new ArrayList<>();
        mySongsArtistName=new ArrayList<>();
        mySongsLoc=new ArrayList<>();
        mySongsPhoto=new ArrayList<>();

        getMusic();

        Uri uri =Uri.parse(mySongsLoc.get(myPosition).toString());
        prevBtn.setClickable(false);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);

    }

    public void setTimes(){
        durationTime=mediaPlayer.getDuration();
        currentTime=mediaPlayer.getCurrentPosition();

        songSB.setMax((int) durationTime);
        endTimeTV.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) durationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) durationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationTime)))
        );

        startTimeTV.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) currentTime)))
        );

        songSB.setProgress((int)currentTime);
        myHandler.postDelayed(UpdateSongTime,100);
    }

    public void clickBtn(View view){

        try {
            if (!mediaPlayer.isPlaying()) {


                mediaPlayer.start();
                playBtn.setImageResource(R.drawable.pause);
                artistTV.setText(mySongsArtistName.get(myPosition));
                songTV.setText(mySongsName.get(myPosition));

                Intent intent=new Intent();
                PendingIntent pIntent=PendingIntent.getActivity(MusicActivity.this,0,intent,0);
                Notification notify=new Notification.Builder(MusicActivity.this)
                        .setTicker("Fatih Player")
                        .setContentTitle(mySongsName.get(myPosition))
                        .setContentText(mySongsArtistName.get(myPosition))
                        .setSmallIcon(R.drawable.music_placeholder)
                        .addAction(R.mipmap.ic_launcher,"Prev",pIntent)
                        .addAction(R.drawable.play,"Pause",pIntent)
                        .addAction(R.drawable.next,"Next",pIntent)

                        .setContentIntent(pIntent).getNotification();
                notify.flags=Notification.FLAG_AUTO_CANCEL;
                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.notify(0,notify);

                setTimes();


            } else {
                mediaPlayer.pause();

                playBtn.setImageResource(R.drawable.play);
            }
        }catch (Exception e){
            Log.e("hata",e.getMessage());
        }



    }

    public void play(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            Uri uri2 =Uri.parse(mySongsLoc.get(myPosition).toString());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri2);
            artistTV.setText(mySongsArtistName.get(myPosition));
            songTV.setText(mySongsName.get(myPosition));
            mediaPlayer.start();
            setTimes();
        }else{

            Uri uri2 =Uri.parse(mySongsLoc.get(myPosition).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri2);
            artistTV.setText(mySongsArtistName.get(myPosition));
            songTV.setText(mySongsName.get(myPosition));
            setTimes();
        }
    }

    public void buttonKontrol(){
        if(mySongsSize-1==myPosition){
            nextBtn.setClickable(false);
        }else{
            nextBtn.setClickable(true);
        }
        if(myPosition<=0){
            prevBtn.setClickable(false);
        }else{
            prevBtn.setClickable(true);
        }
    }

    public void prevBtnClick(View view){
        myPosition--;
        buttonKontrol();
        play();
    }

    public void nextBtnClick(View view){
        myPosition++;
        buttonKontrol();
        play();
    }

    public void getMusic (){
        ContentResolver contentResolver =getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=contentResolver.query(songUri,null,null,null,null);

        if(songCursor!=null &&songCursor.moveToFirst()){
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String songLoc = songCursor.getString(songLocation);
                String songTit = songCursor.getString(songTitle);
                String songArt = songCursor.getString(songArtist);
                mySongsName.add(songTit);
                mySongsLoc.add(songLoc);
                mySongsArtistName.add(songArt);
            }while (songCursor.moveToNext());
        }
        mySongsSize=mySongsName.size();

       /* ContentResolver contentResolv =getContentResolver();
        Uri coverUri= MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor coverCursor=contentResolv.query(coverUri,null,null,null,null);

        if(coverCursor!=null && coverCursor.moveToFirst()){
                try{
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(contentResolv,coverUri);
                    mySongsPhoto.add(bitmap);
                }catch (Exception e){
                    Log.e("Hata",e.getMessage());
                }
            }while (coverCursor.moveToNext());
*/
        }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(!mediaPlayer.isPlaying()){

                playBtn.setImageResource(R.drawable.play);
            }
            currentTime = mediaPlayer.getCurrentPosition();
            startTimeTV.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) currentTime)))
            );

            songSB.setProgress((int)currentTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
