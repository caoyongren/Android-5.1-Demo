package com.a2_13_14_customtouch;

import android.app.Activity;
import android.os.Bundle;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RotateZoomImageView imageView = new RotateZoomImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        
        setContentView(imageView);
    }
}
