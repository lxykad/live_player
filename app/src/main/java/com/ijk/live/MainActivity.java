package com.ijk.live;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
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

import java.io.InputStream;
import java.security.DomainCombiner;
import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
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
    // 弹幕
    private BaseDanmakuParser mParser;//解析器对象
    private DanmakuContext mContext;
    private DanmakuView mDanmakuView;

    public void initDanMu(){
        mDanmakuView = (DanmakuView) findViewById(R.id.danmu_view);
        //
        mContext = DanmakuContext.create();
        // 设置弹幕的最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);

        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) //设置描边样式
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //是否启用合并重复弹幕
                .setScaleTextSize(1.2f) //设置弹幕滚动速度系数,只对滚动弹幕有效
                //     .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer  设置缓存绘制填充器，默认使用{@link SimpleTextCacheStuffer}只支持纯文字显示, 如果需要图文混排请设置{@link SpannedCacheStuffer}如果需要定制其他样式请扩展{@link SimpleTextCacheStuffer}|{@link SpannedCacheStuffer}
                .setMaximumLines(maxLinesPair) //设置最大显示行数
                .preventOverlapping(overlappingEnablePair); //设置防弹幕重叠，null为允许重叠

        if (mDanmakuView!=null) {
            //mParser = createParser(this.getResources().openRawResource(R.raw.comments)); //创建解析器对象，从raw资源目录下解析comments.xml文本

            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });

            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.showFPS(false); //是否显示FPS
            mDanmakuView.enableDanmakuDrawingCache(true);

        }


    }

    /**
     * 添加文本弹幕
     * @param islive
     */
    private void addDanmaku(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = "这是一条弹幕" + System.nanoTime();
        danmaku.padding = 5;
        danmaku.priority = 0;  //0 表示可能会被各种过滤器过滤并隐藏显示 //1 表示一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive; //是否是直播弹幕
      //  danmaku.time = mDanmakuView.getCurrentTime() + 1200; //显示时间
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE; //阴影/描边颜色
        danmaku.borderColor = Color.GREEN; //边框颜色，0表示无边框
        mDanmakuView.addDanmaku(danmaku);

    }
    /**
     * 创建解析器对象，解析输入流
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        // DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI) //xml解析
        // DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_ACFUN) //json文件格式解析
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
    //    BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
     //   parser.load(dataSource);
    //    return parser;
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        //
        initDanMu();

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
                //mVideoView.start();
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

        // 释放弹幕资源
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }

    public void sendDanMu(View view){
        Toast.makeText(view.getContext(),"danmu",Toast.LENGTH_SHORT).show();
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
