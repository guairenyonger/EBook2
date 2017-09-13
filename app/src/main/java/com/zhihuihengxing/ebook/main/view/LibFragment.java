package com.zhihuihengxing.ebook.main.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.adapter.LibFraMainRecyclerViewAdapter;
import com.zhihuihengxing.ebook.main.MainContract;
import com.zhihuihengxing.ebook.main.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class LibFragment extends Fragment implements MainContract.LibView,FraDataTransferListener {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LibFraMainRecyclerViewAdapter mLibFraMainRecyclerViewAdapterl;
    private List<RecyclerViewListItemBean> mDatas = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    //private SwipeRefreshLayout mSwipeRefreshLayout;

    private MainActivityInterface mMainViewListener;
    private MainContract.Presenter mPresenter;


    private static Handler handler=new Handler();

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_read, container, false);
        //initViews(view);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                    }
                });
            }
        }).start();*/
        mPresenter.setPresenter(MainPresenter.STATUS_ON_LIBVIEW);
        mPresenter.getLibData();
       /* mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getLibData();
            }
        });*/
        return view;
    }

    /**
     *
     * @param listener
     */
    public void setMainViewListener(MainActivityInterface listener){
        mMainViewListener=listener;
    }

    /*
    * 获取书本信息成功
    * */
    @Override
    public void loadLibDataSucceed(List<RecyclerViewListItemBean> list) {
        Log.e("sdsdf",list.get(0).getListItemChildren().get(0).toString());
        //mSwipeRefreshLayout.setRefreshing(false);
        mLibFraMainRecyclerViewAdapterl =new LibFraMainRecyclerViewAdapter(getContext(),this,list);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.rv_read_fragment);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setAdapter(mLibFraMainRecyclerViewAdapterl);
    }

    @Override
    public void loadLibDataError(String msg) {

    }

    @Override
    public void addCollectionSucceed(String msg) {

    }

    @Override
    public void addCollectionError(String msg) {

    }

    @Override
    public void cancelCollectionSucceed(String msg) {

    }

    @Override
    public void cancelCollectionError(String msg) {

    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void setBundle(Bundle data) {

    }
    public void changeTab(int i,Bundle bundle){
        mMainViewListener.changeTab(i,bundle);
    }

    @Override
    public Bundle getBundle() {
        return null;
    }
}