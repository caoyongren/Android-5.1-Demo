package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.model;

/**
 * Created by StevenWang on 16/6/24.
 */
public interface IInfoModel {
    void loadInfo(String type, int page, InfoModelImpl.OnLoadInfoListListener listener);
}

/*这个接口用于监听成功与失败*/