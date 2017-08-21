package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.interfaces;


import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by StevenWang on 16/6/22.
 */
public interface ServiceInterface {

    @GET("article/list/latest?page=1")
    Call<ResponseBody> getLatestResponseBody();

    @GET("article/list/text?page=1")
    Call<ResponseBody> getTextResponseBody();

    @GET("article/list/{type}?")
    Call<QiushiBean> getInfoList(@Path("type") String myType, @Query("page") int page);
}
