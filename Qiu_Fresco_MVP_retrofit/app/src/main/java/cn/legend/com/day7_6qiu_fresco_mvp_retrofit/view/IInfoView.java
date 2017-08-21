package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.view;


import java.util.List;

import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;

/**
 * Created by StevenWang on 16/6/24.
 */
public interface IInfoView {

    void addInfo(List<QiushiBean.ItemsEntity> infoList);
}
