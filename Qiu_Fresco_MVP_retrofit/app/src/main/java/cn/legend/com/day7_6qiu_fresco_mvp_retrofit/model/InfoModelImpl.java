package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.model;

import java.util.List;

import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.interfaces.ServiceInterface;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.utils.UrlConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by StevenWang on 16/6/24.
 */
public class InfoModelImpl implements IInfoModel {
    private static final String TAG = "InfoModelImpl";

    /*根据实际需求，在封装的方法中添加 type ,page*/
    @Override
    public void loadInfo(String type, int page, final OnLoadInfoListListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConstant.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //连接需要拼接
        Call<QiushiBean> call = retrofit.create(ServiceInterface.class).getInfoList(type, page);
        call.enqueue(new Callback<QiushiBean>() {
            @Override
            public void onResponse(Call<QiushiBean> call, Response<QiushiBean> response) {
                if (response.isSuccess() && response.body() != null) {
                    List<QiushiBean.ItemsEntity> list = response.body().getItems();
                    /*实现接口回调*/
                    listener.onSuccess(list);
                }
            }

            @Override
            public void onFailure(Call<QiushiBean> call, Throwable t) {
                listener.onFailure("load news list failure." , (Exception) t);
            }

        });
    }
/*监听器*/
    public interface OnLoadInfoListListener {
        void onSuccess(List<QiushiBean.ItemsEntity> list);

        void onFailure(String msg, Exception ex);
    }

}
