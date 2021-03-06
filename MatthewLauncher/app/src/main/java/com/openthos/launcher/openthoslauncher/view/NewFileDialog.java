package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;


import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

import java.io.File;
import java.io.IOException;

/**
 * Created by xu on 2016/11/2.
 */
public class NewFileDialog extends Dialog {
    private Context mContext;
    private String mFormat = ".txt";

    public NewFileDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.dialog_new_file);
        initBody();
        initFoot();
    }

    private void initBody() {
        RadioButton txt = (RadioButton) findViewById(R.id.txt);
        RadioButton doc = (RadioButton) findViewById(R.id.doc);
        RadioButton ppt = (RadioButton) findViewById(R.id.ppt);
        RadioButton xls = (RadioButton) findViewById(R.id.xls);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txt:
                    case R.id.doc:
                    case R.id.ppt:
                    case R.id.xls:
                        mFormat = ((RadioButton) v).getText().toString();
                        break;
                }
            }
        };
        txt.setOnClickListener(click);
        doc.setOnClickListener(click);
        xls.setOnClickListener(click);
        ppt.setOnClickListener(click);
    }

    private void initFoot() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.confirm:
                        MainActivity.mHandler.sendMessage(
                               Message.obtain(MainActivity.mHandler, OtoConsts.NEWFILE, mFormat));
                        break;
                    case R.id.cancel:
                        break;
                }
                dismiss();
            }
        };
        confirm.setOnClickListener(click);
        cancel.setOnClickListener(click);
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }
}
