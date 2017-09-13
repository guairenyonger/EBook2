package com.zhihuihengxing.ebook.main.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewAdapter;
import com.zhihuihengxing.ebook.adapter.BaseRecyclerViewHolder;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.data.ApiException;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.source.BookInfoService;
import com.zhihuihengxing.ebook.data.source.SearchService;
import com.zhihuihengxing.ebook.utils.ScreenUtils;
import com.zhihuihengxing.ebook.utils.SizeUtils;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 搜索页面
 * Created by 15534 on 2017/3/4.
 */

public class LocalFragment extends Fragment {

    MainActivityInterface mMainViewListener;
    @Bind(R.id.sv_search_book)
    SearchView svSearchBook;
    @Bind(R.id.rv_search)
    RecyclerView rvSearch;
    private Retrofit mRetrofit;
    private static final String BASE_URL= MyApplication.url+"/BookSystem/";
    SearchService mSearchService;
    private int mScreenWidth;
    private int mItemWidth;
    Gson gson=new Gson();
    List<BookBean> mList;    BaseRecyclerViewAdapter mAdapter;
    private MyBitmapUtils myBitmapUtils=new MyBitmapUtils();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        ButterKnife.bind(this, view);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(5000, TimeUnit.SECONDS);
        //配置Retrofit
        mRetrofit=new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mSearchService =mRetrofit.create(SearchService.class);

        svSearchBook.setOnQueryTextListener(new OnQueryBookListener());
        return view;
    }

    public void setMainViewListener(MainActivityInterface listener) {
        mMainViewListener = listener;
    }

    /*
    * 搜索
    * */
    private class OnQueryBookListener implements SearchView.OnQueryTextListener{

        /*
        * 点击搜索
        * */
        @Override
        public boolean onQueryTextSubmit(String s) {
            Log.e("onQueryTextSubmit",s);
            Subscriber<String> subscriber=new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Local",e.getMessage());
                    Toast.makeText(getContext(), "获取书本失败 ： "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(String s) {
                    List<BookBean> list =
                            gson.fromJson(s, new TypeToken<List<BookBean>>() {
                            }.getType());
                    Log.e("Local",list.toString());
                    if (mList==null){
                        mList=list;
                        initViews();
                    }else {
                        mList.clear();
                        mList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            };
            mSearchService.getDataBySearch(s).map(new HttpResultFunc<String>())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            Log.e("onQueryTextChange",s);
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

/*
* 将获取到的图书信息显示到本地
* */
    private void initViews(){
        //初始化适配器
        mAdapter=new BaseRecyclerViewAdapter<BookBean>(getContext(),mList) {

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
        rvSearch.addItemDecoration(spaceItemDecoration);
        rvSearch.setAdapter(mAdapter);
        rvSearch.setLayoutManager(new GridLayoutManager(getContext(), 3));
        Log.d("mScreenWidth", mScreenWidth + "");
        Log.d("mScreenWidth", mItemWidth + "");
        Log.d("mScreenWidth", (mScreenWidth - mItemWidth * 3) / 6 + "");
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


    /**
     * 数据直接 剥离 本文件未调用
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T> {
        @Override
        public T call(HttpResult<T> tHttpResult) {
            if (tHttpResult.getState()==1){
                //错误处理
                throw new ApiException(tHttpResult.getMessage());
            }
            return tHttpResult.getDate();
        }
    }
    public void changeTab(int i,Bundle bundle){
        mMainViewListener.changeTab(i,bundle);
    }
}
