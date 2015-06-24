package com.lugeek.client_playback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.animation.Animation;

public class MySurfaceView extends SurfaceView implements Callback{
	private static short[] mShort = new short[512];
	private static float[] mPoints = new float[2048];
	private static int ScreenW,ScreenH,initx,inity,tableW,tableH;
	private static SurfaceHolder sfh;
	private static Canvas canvas;
	private static Paint mPaint;
	
	public MySurfaceView(Context context, AttributeSet attrs) {
        super(context);
        init();
    }
	
	private void init(){
		sfh = this.getHolder();
    	sfh.addCallback(this);
    	mPaint = new Paint();
    	mPaint.setAntiAlias(true);
    	mPaint.setColor(Color.GREEN);
    	mPaint.setStrokeWidth(1f);
    	setZOrderOnTop(true);        //͸���õģ������������
    	sfh.setFormat(PixelFormat.TRANSLUCENT);
	}
	
	public static void update(byte[] bytes){                //1024��byteתΪ512��short��תΪ2048������
		mShort[0] = 0;
		for (int i = 0; i < mShort.length-1; i++) {
			mPoints[4*i] = i/(float)512*tableW+initx;
			mPoints[4*i+1] = inity+mShort[i]/(float)65535*(tableH/2);
			int high = bytes[2*(i+1)+1];
			int low = bytes[2*(i+1)];
			mShort[i+1] = (short) ((high << 8) + (low & 0x00ff));
			mPoints[4*i+2] = (i+1)/(float)512*tableW+initx;
			mPoints[4*i+3] = inity+mShort[i+1]/(float)65535*(tableH/2);
		}
		draw();
	}
	
    private static void draw(){
    	try {
    		canvas = sfh.lockCanvas(new Rect(0, 0, ScreenW, ScreenH));	
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
            canvas.drawLines(mPoints, mPaint);  //mPointsÿ4�������һ����
		} catch (Exception e) {
			
		} finally {
			if (canvas != null){
				sfh.unlockCanvasAndPost(canvas);
			}
		}
    }
    ////////////////������callback�ӿ�//////////////////
    @Override
    public void startAnimation(Animation animation){
    	super.startAnimation(animation);
    }
    
    public void surfaceCreated(SurfaceHolder sHolder){
    	ScreenW = this.getWidth();//surfaceview�Ŀ��
    	ScreenH = this.getHeight();//surfaceview�ĸ߶�
    	initx = (ScreenW-20)/14+10;//������������ĺ�����
    	inity = (ScreenH-20)/8*7/2+10;//���������е� 
    	tableW = (ScreenW-20)/14*13;//���Ŀ��
    	tableH = (ScreenH-20)/8*7;//���ĸ߶�
    }
    
    public void surfaceChanged(SurfaceHolder sHolder, int format, int width, int height){
    }
    
    public void surfaceDestroyed(SurfaceHolder sHolder){
    }
   
}
