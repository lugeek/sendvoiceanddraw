package com.lugeek.client_playback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	public static int trackbufsize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
	public static AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, trackbufsize, AudioTrack.MODE_STREAM);
	Button start;
	public static boolean flag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button)findViewById(R.id.start);
		start.setOnClickListener(new myclicklistener());
	}
	
	class myclicklistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(start.getText().equals("start")){
				flag = true;
				audioTrack.play();
				new playthread().start();
				start.setText("stop");
			}else if (start.getText().equals("stop")) {
				flag = false;
				audioTrack.stop();
				start.setText("start");
			}
			
		}
		
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		flag = false;
		audioTrack.stop();
		audioTrack.release();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

class playthread extends Thread{
	public playthread(){
		super("语音播放线程");
	}
	
	byte [] buf=new byte[1024];
	DatagramSocket socket=null;
	DatagramPacket packet=null;
	
	public void run()
	{
		try
		{		
			socket=new DatagramSocket(20011);
			packet=new DatagramPacket(buf,buf.length);
		}
		catch(Exception f)
		{
		}
		while(MainActivity.flag)// 启动该线程后就不断接收消息 接收到一条就处理一条
		{			
			try
			{			
				socket.receive(packet);
				MainActivity.audioTrack.write(packet.getData(), 0, packet.getData().length);
				MySurfaceView.update(packet.getData());
			}
			catch(IOException e)
			{
				
			}
		}
	}
}
