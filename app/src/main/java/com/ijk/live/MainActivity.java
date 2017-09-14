package com.ijk.live;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ijk.live.application.Settings;
import com.ijk.live.player.AndroidMediaController;
import com.ijk.live.player.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

    private String url = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";
    private String url2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private IjkVideoView mVideoView;
    private RelativeLayout mLiveLayout;

    private Settings mSetting;

    private IjkVideoView mVideoView2;
    private RelativeLayout mLiveLayout2;


    AndroidMediaController mController;

    private AudioManager mAudioManager;
    private AssetManager mAssetManager;
    private int mVolume;
    private int mAudioMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioMode = mAudioManager.getMode();


        mLiveLayout = (RelativeLayout) findViewById(R.id.video_layout);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);

        mLiveLayout2 = (RelativeLayout) findViewById(R.id.video_layout2);
        mVideoView2 = (IjkVideoView) findViewById(R.id.video_view2);

        mSetting = new Settings(this);
        mController = new AndroidMediaController(this, false);
        mVideoView.setMediaController(mController);


//        mVideoView.setVideoURI(Uri.parse(url));
//        mVideoView.start();

        mVideoView.setVideoPath(url);
        mVideoView2.setVideoPath(url2);

        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView.start();


            }
        });

        mVideoView2.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView2.start();


            }
        });

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

        //注册一个回调函数，在视频预处理完成后调用。在视频预处理完成后被调用。此时视频的宽度、高度、宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
        // public void setOnPreparedListener(OnPreparedListener l);
        //播放完成回调
        //public void setOnCompletionListener (IMediaPlayer.OnCompletionListener l);
        //播放错误回调
        //public void setOnErrorListener (IMediaPlayer.OnErrorListener l);
        //事件发生回调
        //public void setOnInfoListener (IMediaPlayer.OnInfoListener l);
        //获取总长度
        //public int getDuration ();
        //获取当前播放位置。
        // public long getCurrentPosition ();
        //设置播放位置。单位毫秒
        // public void seekTo ( long msec);
        //是否正在播放。
        // public boolean isPlaying ();
        //获取缓冲百分比。
        // public int getBufferPercentage();

        /**
         *

         int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
         int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
         int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
         int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
         int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
         int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
         int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收


         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {

            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {

            mVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mVideoView != null) {

            //  mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }

    public void clickGone(View view) {
        Toast.makeText(view.getContext(), "gone", Toast.LENGTH_SHORT).show();
        mVideoView.pause();
        mVideoView.setVisibility(View.GONE);
        mLiveLayout.setVisibility(View.GONE);
    }

    public void clickShow(View view) {
        Toast.makeText(view.getContext(), "gone", Toast.LENGTH_SHORT).show();
        mVideoView.setVisibility(View.VISIBLE);
        mLiveLayout.setVisibility(View.VISIBLE);
        mVideoView.resume();
    }

    public void pauseOrPlay(View view) {


        if (mVideoView.isPlaying()) {

            mVideoView.pause();

        } else {
            mVideoView.start();
        }

    }

    public void closeVoice(View view) {
        //Toast.makeText(view.getContext(),"voice"+mVolume,Toast.LENGTH_SHORT).show();
        a = 0;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, a, AudioManager.FLAG_PLAY_SOUND);
    }

    int a = 0;

    public void addVoice(View view) {
        a++;

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, a, AudioManager.FLAG_PLAY_SOUND);


        Toast.makeText(view.getContext(), "add" + mAudioMode, Toast.LENGTH_SHORT).show();


    }

    int light = 0;

    public void closeLight(View view) {
        if (light > 0) {
            light--;
        }
        //setScreenBrightness(light);
    }

    public void addLight(View view) {
        light++;
//        setScreenBrightness(light);
    }

    private void setScreenBrightness(int value) {
        Window w = getWindow();
        WindowManager.LayoutParams l = w.getAttributes();
        l.screenBrightness = value;
        w.setAttributes(l);
        // Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, value);
    }

}
