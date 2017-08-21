package com.a2_13_14_customtouch;

import android.app.Activity;
import android.os.Bundle;

public class DelegateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        TouchDelegateLayout layout = new TouchDelegateLayout(this);
        setContentView(layout);
    }
}
