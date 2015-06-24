package com.lugeek.record;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

interface Module{
	Frame frame = new Frame("Record and Send");
	Button start = new Button("start");
	Button localWAV = new Button("本地wav");
	Button close = new Button("close");
	
}
public class Main extends Frame implements ActionListener, Module{

	public static boolean flag;
	Main(){
		
		frame.setSize(500, 500);
		frame.setLocation(300, 300);
		frame.setLayout(new FlowLayout());
		frame.add(start);
		frame.add(localWAV);
		frame.add(close);
		start.addActionListener(this);
		localWAV.addActionListener(this);
		close.addActionListener(this);
		frame.show();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
		
//		PrintStream ps = null;
//		try {
//			ps = new PrintStream(new FileOutputStream("C:/Users/YUMOR/Desktop/bytes.txt"));
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.setOut(ps);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==start){
			if(start.getLabel().equals("start")){
				flag = true;
				new recordThread().start();
				start.setLabel("stop");
			}else if (start.getLabel().equals("stop")) {
				flag = false;
				start.setLabel("start");
			}
			
		}
		if(e.getSource()==localWAV){
			if(localWAV.getLabel().equals("本地wav")){
				flag = true;
				new wavthread().start();
				localWAV.setLabel("stop");
			}else if(localWAV.getLabel().equals("stop")){
				flag = false;
				localWAV.setLabel("本地wav");
			}
		}
		if(e.getSource()==close){
			flag = false;
			System.exit(0);
		}
	}

}

class recordThread extends Thread{
	//String hisIP = "192.168.23.108";//无线平板
	String hisIP = "192.168.23.110";//有线平板
	public recordThread(){
		super("录音线程");
	}
	TargetDataLine tDataLine;
	byte [] buf=new byte[1024];
	DatagramSocket socket=null;
	DatagramPacket packet=null;
	
	public void run()
	{
		try {
			socket=new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
		//audioformat(float samplerate, int samplesizeinbits, int channels,boolean signed, boolean bigendian）
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			tDataLine = (TargetDataLine) AudioSystem.getLine(info);
			tDataLine.open(format, tDataLine.getBufferSize());
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tDataLine.start();
		short[] sbyte = new short[512];
		while(Main.flag){
			int readnum = tDataLine.read(buf, 0, 1024);
			
			for(int i = 0; i<512; i++){
				int high = buf[2*i+1];
				int low = buf[2*i];
				sbyte[i] = (short) ((high << 8) + (low & 0x00ff));
			}
			
			
			try
			{
				packet=new DatagramPacket(buf,buf.length,InetAddress.getByName(hisIP),20011);
				socket.send(packet);
			}
			catch(IOException e)
			{
				
			}
			//System.out.println(buf[1]+"");
		}
		tDataLine.stop();
        tDataLine.close();
        tDataLine = null;
		
	}
	
}

class wavthread extends Thread{
	//String hisIP = "192.168.23.108";    //无线平板
	//String hisIP = "192.168.23.115";   //手机
	String hisIP = "169.254.127.111";    //有线平板
	public wavthread(){
		super("wav线程");
	}
	byte [] buf=new byte[1024];
	DatagramSocket socket=null;
	DatagramPacket packet=null;
	
	AudioInputStream ais;
	DataInputStream dis;
	
	public void run(){
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ais = AudioSystem.getAudioInputStream(new File("C:\\Users\\YUMOR\\Desktop\\sin.wav"));	
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dis = new DataInputStream(new BufferedInputStream(ais));
		
		while(Main.flag){
			try {
				int readresult = dis.read(buf,0,1024);
				if(readresult==-1){
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				packet=new DatagramPacket(buf,buf.length,InetAddress.getByName(hisIP),20011);
				socket.send(packet);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		ais = null;
		dis = null;
		
	}
}

