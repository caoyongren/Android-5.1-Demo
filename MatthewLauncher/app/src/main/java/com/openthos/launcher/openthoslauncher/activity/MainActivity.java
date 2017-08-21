package com.openthos.launcher.openthoslauncher.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.adapter.HomeAdapter;
import com.openthos.launcher.openthoslauncher.adapter.ItemCallBack;
import com.openthos.launcher.openthoslauncher.adapter.RecycleCallBack;
import com.openthos.launcher.openthoslauncher.entity.IconEntity;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.view.CompressDialog;
import com.openthos.launcher.openthoslauncher.view.CopyInfoDialog;
import com.openthos.launcher.openthoslauncher.view.FrameSelectView;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import com.openthos.launcher.openthoslauncher.view.PropertyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BasicActivity implements RecycleCallBack {
    private RecyclerView mRecyclerView;
    public List<IconEntity> mDatas;
    public HomeAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    public static Handler mHandler;
    public static boolean mIsCtrlPress;
    private int mHeightNum;
    private SharedPreferences mSp;
    private int mSumNum;
    private CopyInfoDialog mCopyInfoDialog;
    private IconEntity mBlankIcon = new IconEntity();
    private String mCommitText;
    public FrameSelectView mFrameSelectView;
    private FrameLayout mFrameLayout, mDispatchFrame;
    float mDownX, mDownY, mMoveX, mMoveY;
    private boolean mIsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File f = new File("/data/create/biao.xls");
        if (!f.exists()) {
            OperateUtils.exec(new String[]{"tar", "xvf", "/system/create.tar.gz", "-C", "/data"});
            OperateUtils.exec(new String[]{"su", "-c", "chmod -R 777 /data/create"});
        }
        mSp = getSharedPreferences(OtoConsts.DESKTOP_DATA, Context.MODE_PRIVATE);
        mDatas = new ArrayList<>();
        mSumNum = getNum();
        mBlankIcon.setName("");
        mBlankIcon.setIsChecked(false);
        mBlankIcon.setIcon(null);
        mBlankIcon.setIsBlank(true);
        mBlankIcon.setPath("");
        mBlankIcon.setType(Type.BLANK);
        if (savedInstanceState != null) {
            try {
                mDatas = stringToData(savedInstanceState.getString(OtoConsts.DESKTOP_DATA));
            } catch (JSONException e) {
                initData();
            }
        } else {
            initData();
        }
        init();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case OtoConsts.SORT:
                        mDatas.clear();
                        initDesktop();
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.DELETE_REFRESH:
                        inner:
                        for (int i = 0; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals(msg.obj)) {
                                mDatas.set(i, mBlankIcon);
                                break inner;
                            }
                        }
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.NEWFOLDER:
                        createNewFileOrFolder(Type.DIRECTORY, null);
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.NEWFILE:
                        createNewFileOrFolder(Type.FILE, (String) msg.obj);
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.SHOW_FILE:
                        File showFile = new File((String) msg.obj);
                        if (!showFile.exists()) {
                            return;
                        }
                        for (int i = 1; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals(showFile.getAbsolutePath())) {
                                return;
                            }
                        }
                        for (int i = 1; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals("")) {
                                IconEntity icon = new IconEntity();
                                icon.setName(showFile.getName());
                                icon.setPath(showFile.getAbsolutePath());
                                icon.setIsChecked(false);
                                icon.setIsBlank(false);
                                if (showFile.isDirectory()) {
                                    icon.setIcon(MainActivity.this
                                            .getResources().getDrawable(R.drawable.ic_directory));
                                    icon.setType(Type.DIRECTORY);
                                } else {
                                    icon.setIcon(FileUtils.getFileIcon(
                                            showFile.getAbsolutePath(), MainActivity.this));
                                    icon.setType(Type.FILE);
                                }
                                mDatas.set(i, icon);
                                break;
                            }
                        }
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.RENAME:
                        mAdapter.isRename = true;
                        mAdapter.mIsRenameFirst = true;
                        mAdapter.notifyDataSetChanged();
                        break;
                    case OtoConsts.PROPERTY:
                        PropertyDialog propertyDialog = new PropertyDialog(MainActivity.this,
                                (String) msg.obj);
                        propertyDialog.showDialog();
                        break;
                    case OtoConsts.COMPRESS:
                        CompressDialog compressDialog = new CompressDialog(MainActivity.this,
                                (String) msg.obj);
                        compressDialog.showDialog();
                        break;
                    case OtoConsts.DECOMPRESS:
                        showDialogForDecompress((String) msg.obj);
                        break;
                    case OtoConsts.DELETE:
                        showDialogForMoveToRecycle((String) msg.obj);
                        break;
                    case OtoConsts.SAVEDATA:
                        mSp.edit().putString(OtoConsts.DESKTOP_DATA, dataToString()).commit();
                        break;
                    case OtoConsts.DELETE_DIRECT:
                        showDialogForDirectDelete((String) msg.obj);
                        break;
                    case OtoConsts.COPY_PASTE:
                        new CopyThread((String) msg.obj, false).start();
                        break;
                    case OtoConsts.CROP_PASTE:
                        new CopyThread((String) msg.obj, true).start();
                        break;
                    case OtoConsts.COPY_INFO_SHOW:
                        mCopyInfoDialog.showDialog();
                        mCopyInfoDialog.changeTitle(MainActivity.this.getResources()
                                .getString(R.string.copy_info));
                        break;
                    case OtoConsts.COPY_INFO:
                        mCopyInfoDialog.changeMsg((String) msg.obj);
                        break;
                    case OtoConsts.COPY_INFO_HIDE:
                        mCopyInfoDialog.cancel();
                        break;
                    case OtoConsts.CLEAN_CLIPBOARD:
                        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                                .setText("");
                        break;
                }
            }
        };
    }

    private class CopyThread extends Thread {
        private String mPath;
        private boolean mIsCut;

        public CopyThread(String path, boolean isCut) {
            super();
            mPath = path;
            mIsCut = isCut;
        }

        @Override
        public void run() {
            super.run();

        }
    }

    private void createNewFileOrFolder(Type type, String suffix) {
        for (int i = 1; i < mDatas.size(); i++) {
            if ((mDatas.get(i).getPath()).equals("")) {
                File root = new File(OtoConsts.DESKTOP_PATH);
                for (int j = 1; ; j++) {
                    File file = null;
                    if (type == Type.FILE) {
                        file = new File(root, getResources()
                                .getString(R.string.new_file) + j + suffix);
                    } else if (type == Type.DIRECTORY) {
                        file = new File(root, getResources().getString(R.string.new_folder) + j);
                    }
                    if (!file.exists()) {
                        if (type == Type.FILE) {
                            copyBaseFile(file, suffix);
                        } else if (type == Type.DIRECTORY) {
                            file.mkdir();
                        }
                        IconEntity icon = new IconEntity();
                        icon.setName(file.getName());
                        icon.setPath(file.getAbsolutePath());
                        icon.setIsChecked(false);
                        icon.setIsBlank(false);
                        if (type == Type.FILE) {
                            icon.setIcon(FileUtils.getFileIcon(file.getAbsolutePath(), this));
                            icon.setType(Type.FILE);
                        } else if (type == Type.DIRECTORY) {
                            icon.setIcon(getResources().getDrawable(R.drawable.ic_directory));
                            icon.setType(Type.DIRECTORY);
                        }
                        mDatas.set(i, icon);
                        return;
                    }
                }
            }
        }
    }

    private void copyBaseFile(File file, String end) {
        if (getResources().getString(R.string.launcher_txt).equals(end)) {
            OperateUtils.exec(new String[]{"cp", "-i", "/data/create/ben.txt",
                    file.getAbsolutePath()});
        } else if (getResources().getString(R.string.launcher_doc).equals(end)) {
            OperateUtils.exec(new String[]{"cp", "-i", "/data/create/wen.doc",
                    file.getAbsolutePath()});
        } else if (getResources().getString(R.string.launcher_xls).equals(end)) {
            OperateUtils.exec(new String[]{"cp", "-i", "/data/create/biao.xls",
                    file.getAbsolutePath()});
        } else {
            OperateUtils.exec(new String[]{"cp", "-i", "/data/create/yan.ppt",
                    file.getAbsolutePath()});
        }
    }

    private void initData() {
        String tempData = mSp.getString(OtoConsts.DESKTOP_DATA, "");
        if (!TextUtils.isEmpty(tempData)) {
            try {
                mDatas = stringToData(tempData);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initDesktop();
    }

    private void initDesktop() {
        //default icon
        String[] defaultNames = getResources().getStringArray(R.array.default_icon_name);
        TypedArray defaultIcons = getResources().obtainTypedArray(R.array.default_icon);
        String[] defaultPaths = {"", OtoConsts.RECYCLE_PATH};
        File recycle = DiskUtils.getRecycle();
        if (!recycle.exists()) {
            recycle.mkdir();
        }
        Type[] defaultTypes = {Type.COMPUTER, Type.RECYCLE};
        for (int i = 0; i < defaultNames.length; i++) {
            IconEntity icon = new IconEntity();
            icon.setName(defaultNames[i]);
            icon.setPath(defaultPaths[i]);
            icon.setIsChecked(false);
            icon.setIsBlank(false);
            icon.setIcon(getResources().getDrawable(
                    defaultIcons.getResourceId(i, R.mipmap.ic_launcher)));
            icon.setType(defaultTypes[i]);
            mDatas.add(icon);
        }
        //desktop icon
        List<IconEntity> userDatas = new ArrayList<>();
        File dir = new File(OtoConsts.DESKTOP_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                IconEntity icon = new IconEntity();
                if (files[i].isDirectory()) {
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_directory));
                    icon.setType(Type.DIRECTORY);
                } else {
                    icon.setIcon(FileUtils.getFileIcon(files[i].getAbsolutePath(), this));
                    icon.setType(Type.FILE);
                }
                icon.setName(files[i].getName());
                icon.setPath(files[i].getAbsolutePath());
                icon.setIsChecked(false);
                icon.setIsBlank(false);
                if (userDatas.size() < (mSumNum - mDatas.size())) {
                    userDatas.add(icon);
                }
            }
        }
        Collections.sort(userDatas, new Comparator<IconEntity>() {

            @Override
            public int compare(IconEntity object, IconEntity anotherObject) {
                String anotherObjectName = anotherObject.getName();
                String objectName = object.getName();
                return objectName.compareTo(anotherObjectName);
            }
        });
        mDatas.addAll(userDatas);
        while (mDatas.size() < mSumNum) {
            mDatas.add(mBlankIcon);
        }
    }

    private void getHeightNum(DisplayMetrics dm) {
        int heightPixels = dm.heightPixels;
        mHeightNum = heightPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
    }

    private int getNum() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getHeightNum(dm);
        int widthPixels = dm.widthPixels;
        int widthNum = widthPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
        return widthNum * mHeightNum;
    }

    private ArrayList<IconParams> getParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int iconHeight = dm.heightPixels / mHeightNum;
        int spaceH = (int) ((iconHeight
                - getResources().getDimensionPixelSize(R.dimen.icon_size)) / 2 + 0.5);
        int spaceW = (int) ((getResources().getDimensionPixelSize(R.dimen.icon_size)
                - getResources().getDimensionPixelSize(R.dimen.icon_shadow_size)) / 2 + 0.5);
        int height = getResources().getDimensionPixelSize(R.dimen.icon_size);
        int width = getResources().getDimensionPixelSize(R.dimen.icon_shadow_size);
        int left, right, top, bottom;
        ArrayList<IconParams> list = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            left = spaceW + (i / mHeightNum) * (2 * spaceW + width);
            right = spaceW + width + (i / mHeightNum) * (2 * spaceW + width);
            top = spaceH + (i % mHeightNum) * (2 * spaceH + height);
            bottom = spaceH + height + (i % mHeightNum) * (2 * spaceH + height);
            list.add(new IconParams(left, top, right, bottom));
        }

        return list;
    }

    class IconParams {
        public int mLeft;
        public int mRight;
        public int mTop;
        public int mBottom;

        public IconParams(int left, int top, int right, int bottom) {
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }
    }


    class ss extends GridView{

        public ss(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return super.onTouchEvent(ev);
        }
    }


    private void init() {
        //mFrameLayout = (FrameLayout) findViewById(R.id.frame);
        //mFrameSelectView = new FrameSelectView(this);
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
        //            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mFrameSelectView.setLayoutParams(params);
        //mFrameLayout.addView(mFrameSelectView);
        //mFrameSelectView.setVisibility(View.INVISIBLE);
        //mDispatchFrame = (FrameLayout) findViewById(R.id.dispatch_frame);
        //mDispatchFrame.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return false;
        //    }
        //});
        mFrameSelectView = (FrameSelectView) findViewById(R.id.frame_select_view);
        //mFrameSelectView.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return true;
        //    }
        //});
        getParams();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mHeightNum,
                StaggeredGridLayoutManager.HORIZONTAL));
        mAdapter = new HomeAdapter(mDatas, this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (!MenuDialog.isExistMenu()) {
                            MenuDialog dialog = new MenuDialog(MainActivity.this, Type.BLANK, "");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (!mAdapter.isClicked && mAdapter.getLastClickPos() != -1) {
                        mDatas.get(mAdapter.getLastClickPos()).setIsChecked(false);
                        mAdapter.setSelectedCurrent(-1);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.isRename) {
                            mAdapter.isRename = false;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mAdapter.isClicked = false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mIsSelected && !mAdapter.isClicked) {
                            mDownX = event.getRawX();
                            mDownY = event.getRawY();
                            break;

                        }
                        ;
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (mIsSelected) {
                            mMoveX = event.getRawX();
                            mMoveY = event.getRawY();
                            mFrameSelectView.setPositionCoordinate(mDownX < mMoveX ? mDownX : mMoveX, mDownY < mMoveY ? mDownY : mMoveY,
                                    mDownX > mMoveX ? mDownX : mMoveX, mDownY > mMoveY ? mDownY : mMoveY);
                            mFrameSelectView.invalidate();
                        }
                        ;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mIsSelected) {
                            mFrameSelectView.setPositionCoordinate(0, 0, 0, 0);
                            mFrameSelectView.invalidate();
                            mIsSelected = false;
                        }
                        ;
                        break;
                }
                return false;
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemCallBack(this));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mCopyInfoDialog = CopyInfoDialog.getInstance(this);

    }


    @Override
    public void itemOnClick(int position, View view) {

    }

    @Override
    public void onMove(int from, int to) {
        if (!mDatas.get(from).isBlank()) {
            if (to > 0 && from > 0) {
                synchronized (this) {
                    if (from > to) {
                        int count = from - to;
                        for (int i = 0; i < count; i++) {
                            Collections.swap(mDatas, from - i, from - i - 1);
                        }
                    }
                    if (from < to) {
                        int count = to - from;
                        for (int i = 0; i < count; i++) {
                            Collections.swap(mDatas, from + i, from + i + 1);
                        }
                    }
                    mAdapter.setData(mDatas);
                    mAdapter.notifyItemMoved(from, to);
                    mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mIsCtrlPress = event.isCtrlPressed();
        if (!mAdapter.isRename) {
            return keyDealing(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean keyDealing(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed()) {
            if (keyCode == KeyEvent.KEYCODE_A) {
                mAdapter.setSelectedCurrent(-1);
                for (int i = 0; i < mDatas.size(); i++) {
                    if (mDatas.get(i).isBlank() == false) {
                        IconEntity icon = mDatas.get(i);
                        icon.setIsChecked(true);
                        mDatas.set(i, icon);
                        mAdapter.getSelectedPosList().add(i);
                    }
                }
                mAdapter.setData(mDatas);
                mAdapter.notifyDataSetChanged();
            }
            if (keyCode == KeyEvent.KEYCODE_D && mAdapter.getSelectedPosList() != null) {
                String deletePath = getSelectedPath(OtoConsts.DELETE);
                if (deletePath != null) {
                    Message deleteFile = new Message();
                    deleteFile.obj = deletePath;
                    deleteFile.what = OtoConsts.DELETE;
                    mHandler.sendMessage(deleteFile);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_X && mAdapter.getSelectedPosList() != null) {
                String cropPath = getSelectedPath(OtoConsts.CROP_PASTE);
                if (cropPath != null) {
                    ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(cropPath);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_C && mAdapter.getSelectedPosList() != null) {
                String copyPath = getSelectedPath(OtoConsts.COPY_PASTE);
                if (copyPath != null) {
                    ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(copyPath);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_V) {
                String sourcePath = "";
                try {
                    sourcePath = (String)
                            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
                } catch (ClassCastException e) {
                    sourcePath = "";
                }


            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FORWARD_DEL
                && mAdapter.getSelectedPosList() != null) {
            String deletePath = getSelectedPath(OtoConsts.DELETE);
            if (deletePath != null) {
                Message deleteFile = new Message();
                deleteFile.obj = deletePath;
                if (event.isShiftPressed()) {
                    deleteFile.what = OtoConsts.DELETE_DIRECT;
                } else {
                    deleteFile.what = OtoConsts.DELETE;
                }
                mHandler.sendMessage(deleteFile);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_F5) {
            mHandler.sendEmptyMessage(OtoConsts.SORT);
        } else if (keyCode == KeyEvent.KEYCODE_F2 && mAdapter.getLastClickPos() != -1) {
            mAdapter.setSelectedCurrent(mAdapter.getLastClickPos());
            Type type = mDatas.get(mAdapter.getLastClickPos()).getType();
            if (type == Type.DIRECTORY || type == Type.FILE) {
                mHandler.sendEmptyMessage(OtoConsts.RENAME);
            }
        } else if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                && mAdapter.getLastClickPos() != -1 && !mAdapter.isRename) {
            OperateUtils.enter(this, mDatas.get(mAdapter.getLastClickPos()).getPath(),
                    mDatas.get(mAdapter.getLastClickPos()).getType());
        } else {

        }
        return super.onKeyDown(keyCode, event);
    }

    private String getSelectedPath(int copyType) {
        StringBuffer buff = new StringBuffer();
        if (mAdapter.getSelectedPosList() != null && mAdapter.getSelectedPosList().size() > 0) {
            for (int i = 0; i < mAdapter.getSelectedPosList().size(); i++) {
                Type type = mDatas.get(mAdapter.getSelectedPosList().get(i)).getType();
                if (type == Type.DIRECTORY || type == Type.FILE) {
                    switch (copyType) {

                    }
                }
            }
            return buff.toString();
        }
        return null;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mIsCtrlPress = event.isCtrlPressed();
        return super.onKeyUp(keyCode, event);
    }

    private void showDialogForMoveToRecycle(String path) {
        MoveToRecycleClickListener listener = new MoveToRecycleClickListener(path);
        OperateUtils.showBaseAlertDialog(this, R.string.dialog_delete_text, listener);
    }

    private void showDialogForDirectDelete(String path) {
        DirectDeleteClickListener listener = new DirectDeleteClickListener(path);
        OperateUtils.showBaseAlertDialog(this, R.string.dialog_direct_delete_text, listener);
    }

    private class MoveToRecycleClickListener implements OnClickListener {
        String mPath;

        public MoveToRecycleClickListener(String path) {
            mPath = path;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            MoveToRecycleThread thread = new MoveToRecycleThread();
            thread.start();
            dialog.cancel();
        }

        private class MoveToRecycleThread extends Thread {

            public MoveToRecycleThread() {
                super();
            }

            @Override
            public void run() {
                super.run();

            }
        }
    }

    private class DirectDeleteClickListener implements OnClickListener {
        String mPath;

        public DirectDeleteClickListener(String path) {
            mPath = path;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            DirectDeleteThread thread = new DirectDeleteThread();
            thread.start();
            dialog.cancel();
        }

        private class DirectDeleteThread extends Thread {

            public DirectDeleteThread() {
                super();
            }

            @Override
            public void run() {
                super.run();

            }
        }
    }

    private void showDialogForDecompress(final String path) {
        String[] files = DiskUtils.list(path);
        for (String s : files) {
            for (IconEntity icon : mDatas) {
                if (icon.getName().equals(s)) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage(String.format(getResources().getString(
                                    R.string.dialog_decompress_text), s))
                            .setPositiveButton(getResources().getString(R.string.dialog_delete_yes),
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new DecompressThread(path).start();
                                        }
                                    })
                            .setNegativeButton(getResources().getString(R.string.dialog_delete_no),
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();
                    return;
                }
            }
        }
        new DecompressThread(path).start();
    }

    private class DecompressThread extends Thread {
        String mPath;

        public DecompressThread(String path) {
            super();
            mPath = path;
        }

        @Override
        public void run() {
            super.run();
            String[] fileList = DiskUtils.decompress(mPath);
            for (int i = 0; i < fileList.length; i++) {
                Message showFile = new Message();
                showFile.obj = OtoConsts.DESKTOP_PATH + "/" + fileList[i];
                showFile.what = OtoConsts.SHOW_FILE;
                MainActivity.mHandler.sendMessage(showFile);
            }
        }
    }

    private String dataToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean first = true;
        for (IconEntity icon : mDatas) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append("{\"type\":\"");
            sb.append(icon.getType());
            sb.append("\",\"name\":\"");
            sb.append(icon.getName());
            sb.append("\",\"path\":\"");
            sb.append(icon.getPath());
            sb.append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private ArrayList<IconEntity> stringToData(String s) throws JSONException {
        JSONArray array = new JSONArray(s);
        ArrayList<IconEntity> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Type type = Type.valueOf(obj.getString("type"));
            if (type == Type.BLANK) {
                list.add(mBlankIcon);
                continue;
            }
            IconEntity icon = new IconEntity();
            switch (type) {
                case COMPUTER:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_app_computer));
                    icon.setName(getResources().getString(R.string.my_computer));
                    break;
                case RECYCLE:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_app_recycle));
                    icon.setName(getResources().getString(R.string.recycle));
                    break;
                case FILE:
                    icon.setIsBlank(false);
                    icon.setIcon(FileUtils.getFileIcon(obj.getString("path"), this));
                    icon.setName(obj.getString("name"));
                    break;
                case DIRECTORY:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_directory));
                    icon.setName(obj.getString("name"));
                    break;
            }
            icon.setIsChecked(false);
            icon.setPath(obj.getString("path"));
            icon.setType(type);
            list.add(icon);
        }
        return list;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(OtoConsts.DESKTOP_DATA, dataToString());
    }

    @Override
    public void onDestroy() {
        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);

        super.onDestroy();
    }


    public void setIsSelected(boolean isSelected) {
        mIsSelected = true;
        //if (isSelected) {
        //    mFrameSelectView.setVisibility(View.VISIBLE);
        //} else {
        //    mFrameSelectView.setVisibility(View.INVISIBLE);
        //}
    }
}
