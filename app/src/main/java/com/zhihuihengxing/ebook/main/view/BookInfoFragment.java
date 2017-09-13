package com.zhihuihengxing.ebook.main.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.main.MainContract;
import com.zhihuihengxing.ebook.main.MainPresenter;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;
import com.zhihuihengxing.ebook.widget.progress.DownloadProgressHandler;
import com.zhihuihengxing.ebook.widget.progress.ProgressHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BookInfoFragment extends Fragment implements MainContract.BookInfoView, FraDataTransferListener {


    @Bind(R.id.tv_bookname_fra_book_info)
    TextView tvBooknameFraBookInfo;
    @Bind(R.id.iv_fra_book_info)
    ImageView ivFraBookInfo;
    @Bind(R.id.tv_author_fra_book_info)
    TextView tvAuthorFraBookInfo;
    @Bind(R.id.tv_add_collect_fra_book_info)
    TextView tvAddCollectFraBookInfo;
    @Bind(R.id.tv_download_fra_book_info)
    TextView tvDownloadFraBookInfo;
    @Bind(R.id.tv_discribe_fra_book_info)
    TextView tvDiscribeFraBookInfo;
    @Bind(R.id.iv_main_fra_book_info)
    ImageView ivMainFraBookInfo;
    @Bind(R.id.tv_download_and_read_fra_book_info)
    TextView tvDownloadAndReadFraBookInfo;
    //用于信息传递的Bundle 非setArgument 方法获得、
    private Bundle mBundle;

    private BookBean b;

    private MainContract.Presenter mPresenter;
    private MainActivityInterface mMainViewListener;
    private MyBitmapUtils myBitmapUtils = new MyBitmapUtils();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    /*
    * 加载RecyclerView中的图书内容
    * */
    private void init() {
        mBundle = getArguments();
        b = (BookBean) mBundle.get("BookBean");
        tvBooknameFraBookInfo.setText(b.getBookName());
        tvAuthorFraBookInfo.setText(b.getAuthor());
        tvDiscribeFraBookInfo.setText(b.getDescribeBook());
        //tvKindFraBookInfo.setText(b.getBookKindID());
        myBitmapUtils.disPlay(ivMainFraBookInfo, MyApplication.url + "/BookSystem/load?path=" + b.getImageUrl());

        mPresenter.setPresenter(MainPresenter.STATUS_ON_BOOKINFOVIEW);
        tvAddCollectFraBookInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addCollection(b.getBookID());
            }
        });
        tvDownloadFraBookInfo.setOnClickListener(new onDownloadOnClick());
        tvDownloadAndReadFraBookInfo.setOnClickListener(new onDownloadAndOpenOnClick());
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    /*
    * 加入收藏成功
    * */
    @Override
    public void addCollectionSucceed(String msg) {
        Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
    }

    /*
    * 加入收藏失败
    * */
    @Override
    public void addCollectionError(String msg) {
        Toast.makeText(getActivity(), "收藏失败 : " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelCollectionSucceed(String msg) {

    }

    @Override
    public void cancelCollectionError(String msg) {

    }

    @Override
    public void downloadBookSucceedAndOpen(String uri) {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/Ebook/" + b.getUrl();
        Bundle bundle = new Bundle();
        bundle.putString("uriStr", path);//传过去的是字符串
        Intent intent = new Intent(getContext(), ReadActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void downloadBookError(String msg) {
        Toast.makeText(getContext(), "下载失败" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void downloadBookSucceed(String msg) {
        Toast.makeText(getContext(), "下载完成，文件存储在Ebook文件夹中", Toast.LENGTH_SHORT).show();
    }



    /*
    *下载按钮 监听事件
    * */
    private class onDownloadOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //监听下载进度
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setProgressNumberFormat("%1d KB/%2d KB");
            dialog.setTitle("下载");
            dialog.setMessage("正在下载，请稍后...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(true);
            dialog.show();

            ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
                @Override
                protected void onProgress(long bytesRead, long contentLength, boolean done) {
                    Log.e("是否在主线程中运行", String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
                    Log.e("onProgress", String.format("%d%% done\n", (100 * bytesRead) / contentLength));
                    Log.e("done", "--->" + String.valueOf(done));
                    dialog.setMax((int) (contentLength / 1024));
                    dialog.setProgress((int) (bytesRead / 1024));

                    if (done) {
                        dialog.dismiss();
                    }
                }
            });
            //调用请求
            mPresenter.downloadBook(b.getUrl(), 0);
        }
    }


    /*
    *下载按钮 监听事件
    * */
    private class onDownloadAndOpenOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //监听下载进度
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setProgressNumberFormat("%1d KB/%2d KB");
            dialog.setTitle("下载");
            dialog.setMessage("正在下载，请稍后...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(true);
            dialog.show();

            ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
                @Override
                protected void onProgress(long bytesRead, long contentLength, boolean done) {
                    Log.e("是否在主线程中运行", String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
                    Log.e("onProgress", String.format("%d%% done\n", (100 * bytesRead) / contentLength));
                    Log.e("done", "--->" + String.valueOf(done));
                    dialog.setMax((int) (contentLength / 1024));
                    dialog.setProgress((int) (bytesRead / 1024));

                    if (done) {
                        dialog.dismiss();
                    }
                }
            });
            //调用请求
            mPresenter.downloadBook(b.getUrl(), 1);
        }
    }

    /**
     * @param data
     */
    @Override
    public void setBundle(Bundle data) {
        mBundle = data;
        b = (BookBean) mBundle.get("BookBean");
        Toast.makeText(getActivity(), b.getBookName(), Toast.LENGTH_SHORT).show();
        tvBooknameFraBookInfo.setText(b.getBookName());
        tvAuthorFraBookInfo.setText(b.getAuthor());
        tvDiscribeFraBookInfo.setText(b.getDescribeBook());
        Log.e("setBundle", "start");
    }

    @Override
    public Bundle getBundle() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * @param listener
     */
    public void setMainViewListener(MainActivityInterface listener) {
        mMainViewListener = listener;
    }
}
