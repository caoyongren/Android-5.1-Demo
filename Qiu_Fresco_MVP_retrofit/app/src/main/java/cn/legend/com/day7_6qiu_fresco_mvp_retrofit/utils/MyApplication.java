package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.utils;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by StevenWang on 16/6/3.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }


}
