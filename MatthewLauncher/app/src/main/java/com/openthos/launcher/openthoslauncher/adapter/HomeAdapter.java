package com.openthos.launcher.openthoslauncher.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Window;
import android.view.WindowManager;
import android.text.Editable;
import android.text.TextUtils;


import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.IconEntity;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import com.openthos.launcher.openthoslauncher.view.OpenWithDialog;
import com.openthos.launcher.openthoslauncher.view.FrameSelectView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<IconEntity> data;
    private List<Integer> selectedPositions;
    private RecycleCallBack mRecycleClick;

    private int mLastClickId = -1;
    private long mLastClickTime = 0;
    public boolean isClicked = false;
    public boolean isRename = false;

    private static final int LESS = 0;
    private static final int MORE = 1;

    private int mIndex;
    private Editable mEdit;
    private HomeViewHolder mHolder;
    public boolean mIsRenameFirst;
    private FrameSelectView mFrameSelectView;
    float mDownX, mDownY, mMoveX, mMoveY;

    public HomeAdapter(List<IconEntity> data, RecycleCallBack click) {
        this.data = data;
        this.mRecycleClick = click;
        selectedPositions = new ArrayList<>();
        mFrameSelectView = ((MainActivity) click).mFrameSelectView;
    }

    public void setData(List<IconEntity> data) {
        this.data = data;
    }



    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeViewHolder holder = new HomeViewHolder(LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.item_icon, parent, false), mRecycleClick);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.tv.setText(data.get(position).getName());
        if (data.get(position).isChecked()) {
            holder.item.setBackgroundResource(R.drawable.icon_background);
        } else if (!data.get(position).isChecked()) {
            holder.item.setBackgroundResource(R.drawable.icon_background_trans);
        }
        if (data.get(position).isBlank()) {
            holder.nullnull.setChecked(true);
        } else if (!data.get(position).isBlank()) {
            holder.nullnull.setChecked(false);
        }
        if (data.get(position).getIcon() != null) {
            holder.iv.setImageDrawable(data.get(position).getIcon());
        } else {
            holder.iv.setImageDrawable(new ColorDrawable(0));
        }
        if (isRename == true && position == getLastClickPos()) {
            holder.tv.setFocusable(true);
            holder.tv.setFocusableInTouchMode(true);
            holder.tv.requestFocus();
            mHolder = holder;
        } else {
            holder.tv.setFocusable(false);
            holder.tv.clearFocus();
        }
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        RelativeLayout item;
        ImageView iv;
        EditText tv;
        CheckBox checkBox;
        CheckBox nullnull;
        private RecycleCallBack mClick;

        public HomeViewHolder(View view, RecycleCallBack click) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            iv = (ImageView) view.findViewById(R.id.icon);
            tv = (EditText) view.findViewById(R.id.texts);
            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnKeyListener(keyListener);
            tv.setOnFocusChangeListener(focusChangeListener);
            tv.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isClicked = true;
                        mIsRenameFirst = false;
                        if (isRename == false || getLastClickPos() != getAdapterPosition()) {
                            ctrlProcess(v,event);
                        }
                    }
                    return false;
                }
            });
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
            itemView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (MenuDialog.isExistMenu() == false) {
                            MenuDialog dialog = new MenuDialog(item.getContext(), Type.BLANK, "");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                            MenuDialog.setExistMenu(true);
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (isClicked == true && getLastClickPos() != -1) {
                            ((MainActivity) mRecycleClick).setIsSelected(true);
                                setSelectedCurrent(-1);
                                notifyDataSetChanged();
                            }
                            isClicked = false;
                            if (isRename == true) {
                                isRename = false;
                                notifyDataSetChanged();
                            }
                            //mDownY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //mFrameSelectView.setVisibility(View.VISIBLE);
                            //mMoveX = event.getRawX();
                            //mMoveY = event.getRawY();
                            //mFrameSelectView.setPositionCoordinate(mDownX < mMoveX? mDownX : mMoveX, mDownY < mMoveY? mDownY : mMoveY,
                            //        mDownX > mMoveX? mDownX : mMoveX, mDownY > mMoveY? mDownY : mMoveY);
                            //mFrameSelectView.invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            //mFrameSelectView.setVisibility(View.INVISIBLE);
                            break;
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ctrlProcess(v, event);
            }
            return true;
        }

        private void ctrlProcess(View v, MotionEvent event) {
                            ((MainActivity) mRecycleClick).setIsSelected(false);
            if (getAdapterPosition() != -1) {
                if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    if (selectedPositions != null) {
                        if (data.get(getAdapterPosition()).isBlank()) {
                            setSelectedCurrent(-1);
                            showDialog(event, LESS);
                        } else {
                            if (selectedPositions.size() > 1) {
                                showMoreDialog(event);
                            } else {
                                showLessDialog(event);
                            }
                        }
                    }
                } else if (!data.get(getAdapterPosition()).isBlank()) {
                    isClicked = true;
                    if (MainActivity.mIsCtrlPress) {
                        boolean isSelected = false;
                        for (int i = 0; i < selectedPositions.size(); i++) {
                            if (selectedPositions.get(i) == getAdapterPosition()) {
                                isSelected = true;
                                data.get(selectedPositions.get(i)).setIsChecked(false);
                                selectedPositions.remove(i);
                                break;
                            }
                        }
                        if (!isSelected) {
                            selectedPositions.add(getAdapterPosition());
                            data.get(getAdapterPosition()).setIsChecked(true);
                        }
                    } else {
                        if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                && (System.currentTimeMillis() - mLastClickTime)
                                                              < OtoConsts.DOUBLE_CLICK_TIME
                                && getLastClickPos() == getAdapterPosition()) {
                            OperateUtils.enter(item.getContext(),
                                             data.get(getAdapterPosition()).getPath(),
                                             data.get(getAdapterPosition()).getType());
                        } else {
                            setSelectedCurrent(getAdapterPosition());
                        }
                    }
                    mLastClickTime = System.currentTimeMillis();
                } else {
                    setSelectedCurrent(-1);
                }
                notifyDataSetChanged();
            }
        }

        private void showLessDialog(MotionEvent event) {
            setSelectedCurrent(getAdapterPosition());
            showDialog(event, LESS);
            notifyDataSetChanged();
        }

        private void showMoreDialog(MotionEvent event) {
            boolean isSelected = false;
            for (int i = 0; i < selectedPositions.size(); i++) {
                if (selectedPositions.get(i) == getAdapterPosition()) {
                    isSelected = true;
                    break;
                }
            }
            if (isSelected) {
                Type type = data.get(getAdapterPosition()).getType();
                if (type == Type.COMPUTER || type == Type.RECYCLE) {
                    setSelectedCurrent(getAdapterPosition());
                    showDialog(event, LESS);
                } else {
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        Type types = data.get(i).getType();
                        if (types == Type.COMPUTER || types == Type.RECYCLE) {
                            data.get(i).setIsChecked(false);
                        }
                    }
                    showDialog(event, MORE);
                }
            } else {
                showLessDialog(event);
                return;
            }
            notifyDataSetChanged();
        }

        private void showDialog(MotionEvent event, int selectedNum) {
            Type type = null;
            String path = null;
            switch (selectedNum) {
                case LESS:
                    type = data.get(getAdapterPosition()).getType();
                    path = data.get(getAdapterPosition()).getPath();
                    break;
                case MORE:
                    type = Type.MORE;
                    path = "";
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        path = path + "OtoDeleteFile:///" +
                                            data.get(selectedPositions.get(i)).getPath();
                    }
                    break;
            }
            MenuDialog dialog = new MenuDialog((MainActivity) mRecycleClick, type, path);
            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
            MenuDialog.setExistMenu(true);
        }
    }

    public void setSelectedCurrent(int current) {
        if (data != null && data.size() > 0) {
            for (int i : selectedPositions) {
                data.get(i).setIsChecked(false);
            }
        }
        selectedPositions.clear();
        if (current >= 0 && current < data.size()){
            data.get(current).setIsChecked(true);
            selectedPositions.add(current);
        }
    }


    public List<Integer> getSelectedPosList() {
        return selectedPositions;
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                        && v.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN) {
                return confirmRename((EditText) v, getLastClickPos());
            }
            return false;
        }
    };

    private boolean confirmRename(final EditText v, final int position) {
        final IconEntity icon = data.get(position);
        String path = icon.getPath();
        String newName = String.valueOf(v.getText());
        for (IconEntity currentIcon : data) {
            if (currentIcon.getName().equals(newName)) {
                if (!icon.getName().equals(newName)) {
                DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                };
                OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                           R.string.rename_fail_by_same, click);
                v.setText(icon.getName());
                v.selectAll();
                } else {
                    v.setFocusable(false);
                    v.clearFocus();
                    isRename = false;
                    mHolder = null;
                }
                return true;
            }
        }
        switch (isValidFileName(newName)) {
            case OtoConsts.FILE_NAME_LEGAL:
                rename(v, position);
                break;
            case OtoConsts.FILE_NAME_NULL:
                DialogInterface.OnClickListener nullClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                };
                OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_not_null, nullClick);
                break;
            case OtoConsts.FILE_NAME_ILLEGAL:
                DialogInterface.OnClickListener illClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                };
                OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_illegal, illClick);
                break;
            case OtoConsts.FILE_NAME_WARNING:
                DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        rename(v, position);
                        dialog.cancel();
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        v.setText(icon.getName());
                        v.setSelection(icon.getName().length());
                        dialog.cancel();
                    }
                };
                OperateUtils.showChooseAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_warning, okClick, cancel);
                break;
        }
        return true;
    }

    private void rename(EditText v, int position) {
        IconEntity icon = data.get(position);
        final String path = icon.getPath();
        final String newName = String.valueOf(v.getText());
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent(), newName);
        boolean isSuccess = oldFile.renameTo(newFile);
        if (!isSuccess) {
            DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
            OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                       R.string.rename_fail, click);
            v.setText(icon.getName());
            v.selectAll();
            return;
        }
        icon.setName(newName);
        icon.setPath(newFile.getAbsolutePath());
        data.set(position, icon);
        notifyDataSetChanged();
        IconEntity mIcon = ((MainActivity) mRecycleClick).mDatas.get(getLastClickPos());
        mIcon.setName(newName);
        mIcon.setPath(newFile.getAbsolutePath());
        ((MainActivity)mRecycleClick).mDatas.set(getLastClickPos(), mIcon);
        v.setFocusable(false);
        v.clearFocus();
        isRename = false;
        mHolder = null;
        MainActivity.mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
    }

    private int isValidFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return OtoConsts.FILE_NAME_NULL;
        } else {
            if (fileName.indexOf("/") != -1) {
                return OtoConsts.FILE_NAME_ILLEGAL;
            }
            for (int i = 0; i < OtoConsts.NAMESTART.length; i++) {
                if (fileName.startsWith(OtoConsts.NAMESTART[i])) {
                    return OtoConsts.FILE_NAME_WARNING;
                }
            }
            for (int i = 0; i < OtoConsts.NAMEBODY.length; i++) {
                if (fileName.indexOf(OtoConsts.NAMEBODY[i]) != -1) {
                    return OtoConsts.FILE_NAME_WARNING;
                }
            }
            return OtoConsts.FILE_NAME_LEGAL;
        }
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) { // TODO: will add code, next.
            } else {
                v.setFocusable(false);
                v.clearFocus();
                isRename = false;
            }
        }
    };

    public int getLastClickPos(){
        if (selectedPositions.size() == 0) {
            return -1;
        }
        return selectedPositions.get(selectedPositions.size() - 1);
    }
}
