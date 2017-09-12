package com.ijk.live;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ijk.live.application.Settings;
import com.ijk.live.player.AndroidMediaController;
import com.ijk.live.player.IMediaController;
import com.ijk.live.player.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements IjkVideoView.OnClickListener {

    private String url = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";
    private String url2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private IjkVideoView mVideoView;
    private RelativeLayout mLiveLayout;

    private Settings mSetting;


    AndroidMediaController mController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mLiveLayout = (RelativeLayout) findViewById(R.id.video_layout);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mSetting = new Settings(this);
        mController = new AndroidMediaController(this,false);
        mVideoView.setMediaController(mController);




//        mVideoView.setVideoURI(Uri.parse(url));
//        mVideoView.start();

        mVideoView.setVideoPath(url);

        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView.start();


            }
        });

        mVideoView.setOnClickListener(this);
        mLiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mVideoView.isPlaying()) {
                   // mVideoView.pause();
                } else {
                   // mVideoView.start();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView!=null) {

            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView!=null) {

            mVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mVideoView!=null) {

          //  mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "click", Toast.LENGTH_SHORT).show();// 无效
    }

    public void pauseOrPlay(View view) {


        if (mVideoView.isPlaying()) {

            mVideoView.pause();

        } else {
            mVideoView.start();
        }

    }

}
