package cn.legend.com.day7_6qiu_fresco_mvp_retrofit.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.R;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;
/**
 * ----------BigGod be here!----------/
 * ***┏┓******┏┓*********
 * *┏━┛┻━━━━━━┛┻━━┓*******
 * *┃             ┃*******
 * *┃     ━━━     ┃*******
 * *┃             ┃*******
 * *┃  ━┳┛   ┗┳━  ┃*******
 * *┃             ┃*******
 * *┃     ━┻━     ┃*******
 * *┃             ┃*******
 * *┗━━━┓     ┏━━━┛*******
 * *****┃     ┃天狗保佑--代码无Bug!
 * *****┃     ┗━━━━━━━━┓*****
 * *****┃              ┣┓****
 * *****┃              ┏┛****
 * *****┗━┓┓┏━━━━┳┓┏━━━┛*****
 * *******┃┫┫****┃┫┫********
 * *******┗┻┛****┗┻┛*********
 * Writing by brain,no bug !
 * leader：  Administrator
 * 时间：  2016/7/6 0006.
 * 工程名：MyAdapter
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int STATE1=0;
    private static final int STATE2=1;
    private Context mContext=null;
    private LayoutInflater layoutInflater=null;
    private List<QiushiBean.ItemsEntity> mlist=null;

    public MyAdapter(Context context, List<QiushiBean.ItemsEntity> mlist){
        this.mContext=context;
        this.mlist=mlist;
        this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        String url = getImageUrl(mlist.get(position).getImage() + "");
        return url == null ? STATE2 : STATE1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        RecyclerView.ViewHolder vHolder=null;
        switch (viewType){
            case STATE1:
                view=layoutInflater.inflate(R.layout.item_listview_main1,parent,false);
                vHolder=new ImgViewHolder(view);
                break;
            case STATE2:
                view=layoutInflater.inflate(R.layout.item_listview_main2,parent,false);
                vHolder=new MyViewHolder(view);
                break;
        }
        return vHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mlist!=null){
            if(holder instanceof ImgViewHolder){
                ImgViewHolder mHolder= (ImgViewHolder) holder;
                String url = getImageUrl(mlist.get(position).getImage() + "");
                    Uri imageUri=Uri.parse(url);
                if(imageUri!=null){
                    mHolder.simpleDraweeView_item.setImageURI(imageUri);
                }
                mHolder.textView_item_commentscount.setText(mlist.get(position).getComments_count());
                if(mlist.get(position).getUser()!=null){
                    mHolder.textView_item_login.setText(mlist.get(position).getUser().getLogin());
                }
            }else if(holder instanceof MyViewHolder){
                MyViewHolder mHolder= (MyViewHolder) holder;
                mHolder.textView_item_content.setText(mlist.get(position).getContent());
                mHolder.textView_item_commentscount.setText(mlist.get(position).getComments_count
                        () + "");
                if (mlist.get(position).getUser() != null) {
                    String login = mlist.get(position).getUser().getLogin();
                    mHolder.textView_item_login.setText(login);
                }
            }
        }
    }
    @Override
    public int getItemCount() {
        return mlist.size();
    }
    /*内部类*/
    class ImgViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView simpleDraweeView_item;
        private TextView textView_item_login;
        private TextView textView_item_commentscount;
        public ImgViewHolder(View convertView){
            super(convertView);
             simpleDraweeView_item= (SimpleDraweeView) convertView.findViewById(R.id.simpleDraweeView_item);
            textView_item_login= (TextView) convertView.findViewById(R.id.textView_item_login);
            textView_item_commentscount= (TextView) convertView.findViewById(R.id.textView_item_commentscount);
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_item_content;
        private TextView textView_item_login;
        private TextView textView_item_commentscount;
        public MyViewHolder(View convertView){
            super(convertView);
            textView_item_content= (TextView) convertView.findViewById(R.id.textView_item_content);
            textView_item_login= (TextView) convertView.findViewById(R.id.textView_item_login);
            textView_item_commentscount= (TextView) convertView.findViewById(R.id.textView_item_commentscount);
        }
    }
    // 根据图片的名称拼凑图片的网络访问地址
    private String getImageUrl(String imageName) {
        String urlFirst = "", urlSecond = "";
        if (imageName.indexOf('.') > 0) {
            StringBuilder sb = new StringBuilder();
            if (imageName.indexOf("app") == 0) {
                urlSecond = imageName.substring(3, imageName.indexOf('.'));
                switch (urlSecond.length()) {
                    case 8:
                        urlFirst = imageName.substring(3, 7);
                        break;
                    case 9:
                        urlFirst = imageName.substring(3, 8);
                        break;
                    case 10:
                        urlFirst = imageName.substring(3, 9);
                        break;
                }
            } else {
                urlSecond = imageName.substring(0, imageName.indexOf('.'));
                urlFirst = imageName.substring(0, 6);
            }
            sb.append("http://pic.qiushibaike.com/system/pictures/");
            sb.append(urlFirst);
            sb.append("/");
            sb.append(urlSecond);
            sb.append("/");
            sb.append("small/");
            sb.append(imageName);
            return sb.toString();
        } else {
            return null;
        }
    }
    public void reloadListView(List<QiushiBean.ItemsEntity> _list, boolean isClear) {
        if (isClear) {
            mlist.clear();
        }
        mlist.addAll(_list);
        notifyDataSetChanged();
    }
}
