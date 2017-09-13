package com.zhihuihengxing.ebook.main.view;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewAdapter;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewHolder;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.main.MainContract;
import com.zhihuihengxing.ebook.main.MainPresenter;
import com.zhihuihengxing.ebook.utils.ScreenUtils;
import com.zhihuihengxing.ebook.utils.SizeUtils;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15534 on 2017/3/4.
 */

public class BooksByKindFragment extends Fragment implements MainContract.BooksByKindView {

    private int mScreenWidth;
    private int mItemWidth;
    private RecyclerView recyclerView;
    private TextView tv_kind;
    private MainActivityInterface mMainViewListener;
    private MainContract.Presenter mPresenter;
    private View view;
    private Bundle mBundle;
    private MyBitmapUtils myBitmapUtils=new MyBitmapUtils();


    String bookKind;
    String bookKindName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view=inflater.inflate(R.layout.fragment_books_by_kind,container,false);
        //initViews(view);
        tv_kind=(TextView)view.findViewById(R.id.tv_kind);
        mPresenter.setPresenter(MainPresenter.STATUS_ON_BOOKSBYKINDVIEW);
        mBundle=getArguments();
        bookKind=mBundle.getString("bookKind");
        bookKindName=mBundle.getString("bookKindName");
        tv_kind.setText(bookKindName);
        mPresenter.getDataByKind(bookKind);
        return view;
    }

    public void setMainViewListener(MainActivityInterface listener){
        mMainViewListener=listener;
    }

    /*
    * 设置RecyclerView中的item 即书的信息
    * */
    private void initViews(List<BookBean> list){
        //模拟数据
        /*List<BookBean> list=new ArrayList<>();
        for (int i=0;i<33;i++){
            BookBean bookBean=new BookBean();
            bookBean.setBookName("name:"+i);
            list.add(bookBean);
        }*/
        //初始化适配器
        recyclerView=(RecyclerView)view.findViewById(R.id.rv_book_by_kind_fra);
        BaseRecyclerViewAdapter mAdapter=new BaseRecyclerViewAdapter<BookBean>(getContext(),list) {

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.fra_lib_child_rv_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, BookBean item) {
                final BookBean book=item;
                ImageView iv=holder.getImageView(R.id.iv_item_rv_child);
                TextView tv=holder.getTextView(R.id.tv_item_rv_child);
                tv.setText(item.getBookName());
                myBitmapUtils.disPlay(iv, MyApplication.url + "/BookSystem/load?path=" + item.getImageUrl());
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BookBean", book);
                        changeTab(MainActivity.BOOKINFOFRA_NUM, bundle);
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
    /*
    * 配合上面的方法 设置布局
    * */
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

    /*
    * 获取图书信息成功
    * */
    @Override
    public void getDataByKindSucceed(List<BookBean> bookBeans) {
        initViews(bookBeans);
    }

    /*
    *
    * 获取失败
    *
    * */
    @Override
    public void getDataByKindError(String msg) {
        Toast.makeText(getActivity(), "图书获取出错 : "+msg, Toast.LENGTH_SHORT).show();
        Log.e("getDataByKindError", msg);
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
        this.mPresenter=presenter;
    }

    /*
    * 页面切换
    * */
    public void changeTab(int i,Bundle bundle){
        mMainViewListener.changeTab(i,bundle);
    }
}
