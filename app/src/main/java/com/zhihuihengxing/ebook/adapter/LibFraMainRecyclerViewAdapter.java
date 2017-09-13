package com.zhihuihengxing.ebook.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.main.view.LibFragment;
import com.zhihuihengxing.ebook.main.view.MainActivity;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;

import java.util.List;

/**
 * Created by 3.书城页面 的适配器 on 2017/2/12 0012.
 */
public class LibFraMainRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;
    List<RecyclerViewListItemBean> mDatas;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_RV = 3;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    private LibFragment mLibFragment;
    private MyBitmapUtils myBitmapUtils;
    //private MainActivityInterface activityListener;

    public LibFraMainRecyclerViewAdapter(Context context, LibFragment libFragment, List<RecyclerViewListItemBean> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        mLibFragment=libFragment;
        myBitmapUtils=new MyBitmapUtils();
    }

    /*
    * 创建ViewHolder
    * */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.fra_lib_main_rv_item, parent, false);

            return new ItemViewHolder(itemView);
        }else if (viewType == TYPE_HEADER) {
            View itemView = mInflater.inflate(R.layout.fra_lib_main_rv_header_item, parent, false);

            return new HeaderViewHolser(itemView);
        }
        return null;
    }

    /*
    * item中的控件以及item中的控件中的控件的设置
    * */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final int mPosition=position;//外层的位置

        if (holder instanceof ItemViewHolder) {

            //标题栏信息
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            String str = mDatas.get(position).getKindName();
            itemViewHolder.mTvContent.setText(str);
            itemViewHolder.mTvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("bookKind",mDatas.get(mPosition).getKindID());
                    bundle.putSerializable("bookKindName", mDatas.get(mPosition).getKindName());
                    mLibFragment.changeTab(MainActivity.BOOKSBYKINDFRA_NUM,bundle);
                }
            });
            //子RV控件中的信息 mPosition-1
            final List<BookBean> bookBeans =mDatas.get(mPosition).getListItemChildren();
            //子RV中间中的item的适配器
            BaseRecyclerViewAdapter<BookBean> mAdapter=
                    new BaseRecyclerViewAdapter<BookBean>(mContext,
                            bookBeans) {
                        @Override
                        public int getItemLayoutId(int viewType) {
                            return R.layout.fra_lib_child_rv_item;
                        }

                        @Override
                        public void bindData(BaseRecyclerViewHolder holder, final int position, BookBean item) {
                            holder.setText(R.id.tv_item_rv_child, item.getBookName());
                            /*holder.getImageButton(R.id.ib_item_rv_child).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(mContext, "1", Toast.LENGTH_SHORT).show();
                                }
                            });*/

                            //获取书本图片
                            ImageView iv=holder.getImageView(R.id.iv_item_rv_child);
                            myBitmapUtils.disPlay(iv,MyApplication.url+"/BookSystem/load?path="+bookBeans.get(position).getImageUrl());
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //点击图片就能跳转页面
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("BookBean", bookBeans.get(position));
                                    mLibFragment.changeTab(MainActivity.BOOKINFOFRA_NUM, bundle);
                                }
                            });

                        }
                    };
            itemViewHolder.recyclerView.setAdapter(mAdapter);
            itemViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        } else if (holder instanceof HeaderViewHolser) {
            HeaderViewHolser headerViewHolser = (HeaderViewHolser) holder;
        }

    }

    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数（footerView）
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {

        /*if (position == 0) {
            return TYPE_HEADER;
        } else {*/
            return TYPE_ITEM;
        //}
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        //@BindView(R.id.tvContent)
        TextView mTvContent;
        RecyclerView recyclerView;
        TextView mTvMore;
        public ItemViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            initListener(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.tvContent);
            mTvMore=(TextView) itemView.findViewById(R.id.tv_more_fra_lib_main_rv_item);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.rv_item_rv_rv_lib);
        }

        private void initListener(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "poistion " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class HeaderViewHolser extends RecyclerView.ViewHolder {
        ViewPager viewPager;

        public HeaderViewHolser(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.vp_item_header_rv_lib);
        }
    }

}