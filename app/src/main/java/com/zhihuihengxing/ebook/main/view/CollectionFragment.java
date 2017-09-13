package com.zhihuihengxing.ebook.main.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewAdapter;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewHolder;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.main.MainContract;
import com.zhihuihengxing.ebook.main.MainPresenter;
import com.zhihuihengxing.ebook.utils.ScreenUtils;
import com.zhihuihengxing.ebook.utils.SizeUtils;
import com.zhihuihengxing.ebook.utils.UserInfoSPUtil;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;

import java.util.ArrayList;
import java.util.List;


public class CollectionFragment extends Fragment implements MainContract.CollectionView {

    private int mScreenWidth;
    private int mItemWidth;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    MainActivityInterface mMainViewListener;
    MainContract.Presenter mPresenter;
    private View view;

    List<BookBean> mList;
    BaseRecyclerViewAdapter mAdapter;
    private MyBitmapUtils myBitmapUtils=new MyBitmapUtils();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_collection, container, false);
        mPresenter.setPresenter(MainPresenter.STATUS_ON_COLLECTIONVIEW);
        mPresenter.getDataByCollection();
        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getDataByCollection();
            }
        });
        return view;
    }

    public void setMainViewListener(MainActivityInterface listener){
        mMainViewListener=listener;
    }

    /*
    * 初始化控件
    * */
    private void initViews(){
        //初始化适配器
        recyclerView=(RecyclerView)view.findViewById(R.id.rv_collection_fragment);
        mAdapter=new BaseRecyclerViewAdapter<BookBean>(getContext(),mList) {

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.fra_collection_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final BookBean item) {
                final BookBean book=item;
                ImageView iv1=holder.getImageView(R.id.iv_col_item_rv_child);
                TextView tv=holder.getTextView(R.id.tv_col_item_rv_child);
                ImageView iv2=holder.getImageView(R.id.iv_del_col_item_rv_child);
                tv.setText(item.getBookName());
                myBitmapUtils.disPlay(iv1, MyApplication.url + "/BookSystem/load?path=" + item.getImageUrl());
                iv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BookBean", book);
                        changeTab(MainActivity.BOOKINFOFRA_NUM, bundle);
                    }
                });
                iv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("确定取消收藏该图书吗？")
                                .setTitle("注意")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mPresenter.cancelCollection(item.getBookID());
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .create()
                                .show();

                    }
                });
            }
        };

        mScreenWidth = ScreenUtils.getScreenWidth(getContext()); //屏幕宽度
        mItemWidth = SizeUtils.dip2px(getContext(), 110); //每个item的宽度

        SpaceItemDecoration spaceItemDecoration=new SpaceItemDecoration((mScreenWidth - mItemWidth* 3)/6);
        recyclerView.addItemDecoration(spaceItemDecoration);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        Log.d("mScreenWidth", mScreenWidth + "");
        Log.d("mScreenWidth",mItemWidth+"");
        Log.d("mScreenWidth",(mScreenWidth - mItemWidth* 3)/6+"");
    }
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;  //位移间距
        public SpaceItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) %3 == 0) {

                outRect.left = space;
            } else {
                if (parent.getChildAdapterPosition(view) %3 == 1) {
                    outRect.left = space;//第二列移动一个位移间距
                } else {
                    outRect.left = space * 2;//由于第二列已经移动了一个间距，所以第三列要移动两个位移间距就能右边贴边，且item间距相等
                }
            }
            if (parent.getChildAdapterPosition(view) >= 3) {
                outRect.top = SizeUtils.dip2px(getContext(), 10);
            } else {
                outRect.top = space;
            }
        }
    }


    @Override
    public void getDataByCollectionSucceed(List<BookBean> bookBeans) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (mList==null){
            mList=bookBeans;
            initViews();
        }else {
            mList.clear();
            mList.addAll(bookBeans);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void getDataByCollectionError(String mag) {

    }

    @Override
    public void addCollectionError(String msg) {

    }

    @Override
    public void addCollectionSucceed(String msg) {

    }

    @Override
    public void cancelCollectionSucceed(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelCollectionError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter=presenter;
    }

    public void changeTab(int i,Bundle bundle){
        mMainViewListener.changeTab(i,bundle);
    }
}
