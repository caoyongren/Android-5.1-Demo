package cn.legend.com.day7_6qiu_fresco_mvp_retrofit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.adapter.MyAdapter;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.entity.QiushiBean;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.presenter.IInfoPresenter;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.presenter.InfoPresenterImpl;
import cn.legend.com.day7_6qiu_fresco_mvp_retrofit.view.IInfoView;

public class MainActivity extends AppCompatActivity implements IInfoView{
    private XRecyclerView xRecyclerview_main;
    private Context mContext=this;
    private MyAdapter adapter=null;
    private List<QiushiBean.ItemsEntity> totalist=new ArrayList<>();
    private int curPage = 1;
    private IInfoPresenter iInfoPresenter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*进行初始化*/
        initView();
        initData();
    }
    /*触发器加载数据*/
    private void initData() {
        iInfoPresenter=new InfoPresenterImpl(this);
        iInfoPresenter.loadInfo("latest",curPage);
    }

    private void initView() {
        xRecyclerview_main= (XRecyclerView) findViewById(R.id.xrecyclerView_main);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerview_main.setLayoutManager(linearLayoutManager);

        adapter=new MyAdapter(mContext,totalist);
        xRecyclerview_main.setAdapter(adapter);
        xRecyclerview_main.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerview_main.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onLoadMore() {
                curPage++;
            }
        });
    }
    @Override
    public void addInfo(List<QiushiBean.ItemsEntity> infoList) {
        if (curPage == 1) {
            adapter.reloadListView(infoList, true);
        } else {
            curPage++;
            adapter.reloadListView(infoList, false);
        }
    }
}
