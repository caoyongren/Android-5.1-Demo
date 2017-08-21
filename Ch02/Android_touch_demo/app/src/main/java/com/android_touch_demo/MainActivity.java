package com.android_touch_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mBtn = (Button) findViewById(R.id.my_button);
        mBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("MasterMan", "### onTouch : " + event.getAction());
                return false;
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("MasterMan", "### onClick : " + v);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("MasterMan", "### activity dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }
}
/**因为　up 和　ｄown 事件　　－－　＞　所以执行两次．
 *
 * 1. activity dispatchTouchEvent
 *
 * 2. onTouch: 0
 *
 * 3. activity dispatchTouchEvent
 *
 * 4. onTouch 1
 *
 * 5. onClick
 *
 * */