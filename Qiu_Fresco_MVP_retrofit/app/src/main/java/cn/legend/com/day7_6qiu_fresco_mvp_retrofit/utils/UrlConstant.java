package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.utils;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class UrlConstant {
    ///////////////////////////////////////////////////////////////////////////
    // 以下为糗百的通用地址
    ///////////////////////////////////////////////////////////////////////////

    //baseurl
    public final static String URL_BASE = "http://m2.qiushibaike.com/";

    // 最新
    public final static String URL_LATEST = URL_BASE + "article/list/latest?page=%d";

    // 图片
    public final static String URL_PIC = URL_BASE + "article/list/pic?page=%d";

    // 视频
    public final static String URL_VIDEO = URL_BASE + "article/list/video?page=%d";

    // 文本
    public final static String URL_TEXT = URL_BASE + "article/list/text?page=%d";

    //头像获取(+ id掉后4位 + "/" + id + "/thumb/" + icon图片名.jpg)
    //userIcon======http://pic.qiushibaike.com/system/avtnew/1499/14997026/thumb/20140404194843.jpg
    public final static String URL_USER_ICON = URL_BASE + "system/avtnew/%s/%s/thumb/%s";

    //内容图片获取(+图片名所有数字去掉后4位+"/"+图片名从数字开始数全部+"/"+"/"+small或者medium+"/"+图片名)
    //====图片Url=http://pic.qiushibaike.com/system/pictures/7128/71288069/small/app71288069.jpg
    public final static String URL_IMAGE = URL_BASE + "system/pictures/%s/%s/%s/%s";

    public final static String URL_DOWNLOAD_IMAGE = "http://img.265g" +
            ".com/userup/1201/201201071126534773.jpg";

    ///////////////////////////////////////////////////////////////////////////
    // 以下地址专为Retrofit框架使用
    ///////////////////////////////////////////////////////////////////////////

    // 最新发帖
    public final static String URL_QIUBAI = "article/list/{type}?page={page}";

}
