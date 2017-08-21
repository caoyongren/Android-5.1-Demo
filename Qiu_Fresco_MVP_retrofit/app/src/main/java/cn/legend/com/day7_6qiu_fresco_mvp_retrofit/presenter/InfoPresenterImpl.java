package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.presenter;

import java.util.List;

import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.model.IInfoModel;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.model.InfoModelImpl;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.view.IInfoView;


/**
 * Created by StevenWang on 16/6/24.
 */
public class InfoPresenterImpl implements IInfoPresenter {
    private static final String TAG = "InfoPresenterImpl";

    private IInfoView mInfoView;
    private IInfoModel mInfoModel;

    /*进行初始化*/
    public InfoPresenterImpl(IInfoView mInfoView) {
        this.mInfoView = mInfoView;
        this.mInfoModel = new InfoModelImpl();
    }
    @Override
    public void loadInfo(String type, int page) {
        mInfoModel.loadInfo(type, page, new InfoModelImpl.OnLoadInfoListListener() {
            /*通过model层将数据传过来*/
            @Override
            public void onSuccess(List<QiushiBean.ItemsEntity> list) {
                mInfoView.addInfo(list);
            }
            @Override
            public void onFailure(String msg, Exception ex) {
            }

        });
    }
}
