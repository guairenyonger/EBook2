package com.zhihuihengxing.ebook.widget.ReadView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.zhihuihengxing.ebook.R;


public class ReadView extends View {
	private Bitmap mCurrentPageBitmap;
	private Canvas mCurrentPageCanvas;
	private PageFactory pagefactory;
	private int font_size = 60;
	private SharedPreferences sp;
	private int[] position = new int[]{0, 0};
	private int width;
	private int height;
	private OnClickListener mOnClickListener;

	public ReadView(Context context ,String path) {
		super(context);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		font_size = sp.getInt("font_size", 60);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
		Log.e(width + ":宽", height + "：高");
		mCurrentPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCurrentPageCanvas = new Canvas(mCurrentPageBitmap);
		pagefactory = new PageFactory(context,width, height, font_size);
		try {
			position[0] = sp.getInt("begin",0);
			position[1] = sp.getInt("end",0);
			Log.e("start" + position[0], "end" + position[1]);
			pagefactory.setBgBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bg));
			pagefactory.openBook(path, position);
			pagefactory.onDraw(mCurrentPageCanvas);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/*
	* 选择页面
	* */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.drawBitmap(mCurrentPageBitmap, 0, 0, null);
		canvas.restore();
	}

	/*
	* 设置背景
	* */
	public void setBackGround(Bitmap bitmap){
		pagefactory.setBgBitmap(bitmap);
	}
	public void setDrawBitMap(Bitmap bitmap){
		mCurrentPageBitmap = bitmap;
	}

	/*
	* 暂停 把进度保存到本地
	* */
	public void setOnPause(){
		position = pagefactory.getPosition();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("begin", position[0]);
        editor.putInt("end", position[1]);
        editor.apply();
        int fontSize = pagefactory.getTextFont();
        SharedPreferences.Editor editor2 = sp.edit();
        editor2.putInt("font_size", fontSize);
        editor2.apply();
	}

	public void setFont_size(int font_size) {
		pagefactory.setTextFont(font_size);
	}

	public void setTextColor(int color){
		pagefactory.setTextColor(color);
	}
	public void setPresent(int present){
		pagefactory.setPercent(present);
	}

	public void refresh(){
		pagefactory.onDraw(mCurrentPageCanvas);
		setDrawBitMap(mCurrentPageBitmap);
		invalidate();
	}
	//&& event.getY() > height * 2 / 3
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("onTouchEvent",event.getX()+" "+event.getY());
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				if (event.getX() > 2*width / 3) {
					pagefactory.nextPage();
					pagefactory.onDraw(mCurrentPageCanvas);
					setDrawBitMap(mCurrentPageBitmap);
				} else if(event.getX() < width / 3){
					pagefactory.prePage();
					pagefactory.onDraw(mCurrentPageCanvas);
					setDrawBitMap(mCurrentPageBitmap);
				}else{
					mOnClickListener.onClick(this);
				}
				invalidate();
				return true;
			}
		return false;
	}


	public void setOnCenterClick(OnClickListener onClickListener){
		mOnClickListener=onClickListener;
	}

}
