package com.lugeek.client_playback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class Mybackview extends View{
	private Paint paint = null;
	private int screenW,screenH;
	private int spanW,spanH;
	public Mybackview(Context context, AttributeSet attrs){
		super(context);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		screenW = this.getWidth();
		screenH = this.getHeight();
		spanW = (screenW-20)/14;
		spanH = (screenH-20)/8;
		canvas.drawColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.GRAY);
		//canvas.drawRect(10, 10, screenW-10, screenH-10, paint);
		for(int i=0; i<=7; i++){
			xuxian(canvas, paint, 10+spanW, 10+i*spanH, 10+14*spanW+3, 10+i*spanH);
		}
		for(int i=1; i<=14; i++){
			xuxian(canvas, paint, 10+i*spanW, 10, 10+i*spanW, 10+7*spanH+3);
		}
		paint.setTextSize(22);
		//canvas.drawText("00", 10+spanW-30, 10+7*spanH+8, paint);
		for(int i=0;i<=7;i++){
			canvas.drawText((7-i)*10+"", 10+spanW-30, 10+i*spanH+8, paint);
		}
		canvas.save();
		canvas.rotate(-90f,25, screenH/2);
		canvas.drawText("·ù¶È(dB)", 25, screenH/2, paint);
		canvas.restore();
		canvas.drawText("ÆµÂÊ(kHz)", screenW/2, (float) (10+7.5*spanH), paint);
		
	}
	
	private void xuxian(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2){
		if(x1 == x2){
			for(int i=y1+5 ; i<y2 ; i+=10){
				canvas.drawLine(x1, y1, x1, i, paint);
				y1 = i+5;
			}
		}
		if (y1 == y2) {
			for(int i=x1+5 ; i<x2 ; i+=10){
				canvas.drawLine(x1, y1, i, y1, paint);
				x1 = i+5;
			}
		}
	}

}
