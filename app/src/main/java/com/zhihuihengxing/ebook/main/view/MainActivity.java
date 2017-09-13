package com.zhihuihengxing.ebook.main.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.bean.UserInfoBean;
import com.zhihuihengxing.ebook.login.view.LogInActivity;
import com.zhihuihengxing.ebook.main.MainContract;
import com.zhihuihengxing.ebook.main.MainPresenter;
import com.zhihuihengxing.ebook.utils.UserInfoSPUtil;
import com.zhihuihengxing.ebook.utils.imageutils.MyBitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MainActivityInterface{

    private ArrayList<Fragment> fragmentArrayList;
    private Fragment mCurrentFrgment;

    public static final int LIBFRA_NUM=0;
    public static final int BOOKINFOFRA_NUM=4;
    public static final int COLLECTIONFRA_NUM=1;
    public static final int BOOKSBYKINDFRA_NUM=2;
    public static final int LOCALFRA_NUM=3;

    private LibFragment mLibFragment;
    private BookInfoFragment mBookInfoFragment;
    private CollectionFragment mCollectionFragment;
    private BooksByKindFragment mBooksByKindFragment;
    private LocalFragment mLocalFragment;

    private TextView tvUserNameNavHeader;
    private FloatingActionButton fabAddLocalFra;
    MainContract.Presenter mPresenter;

    private Intent intent;
    private static final int OPEN_FILE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //编译器自动生成
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //用户区域的操作
        View header=navigationView.getHeaderView(0);
        tvUserNameNavHeader=(TextView)header.findViewById(R.id.tv_username_nav_header);

        tvUserNameNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.mUserInfoBean==null){
                    startActivity(new Intent(MainActivity.this,LogInActivity.class));
                }
            }
        });
        fabAddLocalFra=(FloatingActionButton)findViewById(R.id.fab_add_local_fra);
        fabAddLocalFra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<String,String> userMap=UserInfoSPUtil.loadUserInfo();
        String userName=userMap.get("userName");
        if (userName!=null){
            if (!userName.equals("")){
                tvUserNameNavHeader.setText(userName);
                //MyApplication.mUserInfoBean=new UserInfoBean();
                //MyApplication.mUserInfoBean.setUserName(userName);
            }
        }
    }

    /**
     *初始化Fragment
     */
    private void initFragment() {
        fragmentArrayList = new ArrayList<>();

        mLibFragment=new LibFragment();
        mBookInfoFragment=new BookInfoFragment();
        mBooksByKindFragment=new BooksByKindFragment();
        mLocalFragment=new LocalFragment();
        mCollectionFragment=new CollectionFragment();

        mPresenter=new MainPresenter(mLibFragment,mBookInfoFragment,mCollectionFragment,mBooksByKindFragment);
        mPresenter.subscribe();
        //设置每个fragment的presenter
        mLibFragment.setPresenter(mPresenter);
        mBookInfoFragment.setPresenter(mPresenter);
        mBooksByKindFragment.setPresenter(mPresenter);
        mCollectionFragment.setPresenter(mPresenter);
        //设置每个Fragment的MainView回调接口
        mLibFragment.setMainViewListener(this);
        mBooksByKindFragment.setMainViewListener(this);
        mLocalFragment.setMainViewListener(this);
        mCollectionFragment.setMainViewListener(this);
        mBookInfoFragment.setMainViewListener(this);

        fragmentArrayList.add(mLibFragment);
        //fragmentArrayList.add(mBookInfoFragment);
        fragmentArrayList.add(mCollectionFragment);
        fragmentArrayList.add(mBooksByKindFragment);
        fragmentArrayList.add(mLocalFragment);

        changeTab(LIBFRA_NUM, null);

    }

    /*
    * 用于切换Fragment页面
    * */
    @Override
    public void changeTab(int index,Bundle bundle) {
        int currentIndex = index;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        //判断当前的Fragment是否为空，不为空则隐藏
        if (index==BOOKINFOFRA_NUM){
            mBookInfoFragment.setArguments(bundle);
            ft.add(R.id.rl_content_main, mBookInfoFragment);
            ft.addToBackStack(null);
            ft.commit();
            return;
        }
        if (index==BOOKSBYKINDFRA_NUM){
            mBooksByKindFragment.setArguments(bundle);
            ft.add(R.id.rl_content_main, mBooksByKindFragment);
            ft.addToBackStack(null);
            ft.commit();
            return;
        }


        if (null != mCurrentFrgment) {
            ft.hide(mCurrentFrgment);
        }
        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentArrayList.get(currentIndex).getClass().getName());


        //传递Bundle 如果继承了FraDataTransferListener这个接口的话
        if (fragment instanceof FraDataTransferListener){
            Log.e("bundle","start");
                    ((FraDataTransferListener) fragment).setBundle(bundle);

        }
        if (null == fragment) {
            //如fragment为空，则之前未添加此Fragment。便从集合中取出
            fragment = fragmentArrayList.get(index);
            fragment.setArguments(bundle);
        }
        mCurrentFrgment = fragment;

        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!fragment.isAdded()) {
            //传递数据 如果存在的话
            ft.add(R.id.fl_main, fragment, fragment.getClass().getName());
            //ft.addToBackStack()
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

    /*
   * 调用文件管理器来获取文件URI
   * */
    private void chooseFile(){
        intent=new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("text/plain");
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + "/Ebook/";
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        intent.setDataAndType(Uri.fromFile(fileDir), "text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个需要打开的TXT电子书"), OPEN_FILE);

    }

    /*
    * 选择文件完成之后
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            Uri uri = data.getData();
            String path=getPhotoPathFromContentUri(uri);

            Bundle bundle=new Bundle();
            bundle.putString("uriStr",path);//传过去的是字符串
            Intent intent=new Intent(this,ReadActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取真实文件路径
    public  String getPhotoPathFromContentUri(Uri uri) {
        String photoPath = "";
        if(this == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String [] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    if("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            else if(isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(this, contentUri, null, null);
            }
            else if(isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[] { split[1] };
                    photoPath = getDataColumn(this, contentUris, selection, selectionArgs);
                }
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        }
        else {
            photoPath = getDataColumn(this, uri, null, null);
        }

        return photoPath;
    }
    private  boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private  boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private  boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*
        * 用户登出操作
        * */
        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setMessage("确定退出当前账号吗？")
                    .setTitle("注意")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserInfoSPUtil.clearUserInfo();
                            tvUserNameNavHeader.setText("请登录");
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create()
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * 左侧滑的选择器
    * */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lib) {
            changeTab(0,null);
        } else if (id == R.id.nav_collection) {
            changeTab(COLLECTIONFRA_NUM,null);
        }else if (id==R.id.nav_search){
            changeTab(LOCALFRA_NUM,null);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.setPresenter(MainPresenter.STATUS_ON_COLLECTIONVIEW);
    }
}
