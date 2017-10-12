package com.ijk.live.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by a on 2017/9/12.
 */

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

}
