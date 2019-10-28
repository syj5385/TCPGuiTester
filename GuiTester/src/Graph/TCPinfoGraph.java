package Graph;

import Graph.MyGraph;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader; 
import java.io.IOException;
import java.io.InputStream;

import Graph.Define;


public class TCPinfoGraph extends TcpGraph {
	
	protected File sourceFile;
	protected String FilePath = "/home/jjun/Desktop/";
	protected String content = "Throughput";
	
	protected GraphPanel graph; 
	protected int  offset_x = Define.REL_TIME;
	protected int  offset_y1 = Define.THROUGHPUT;
	protected int  offset_y2 = Define.STHROUGHPUT;

	protected ArrayList<String> flow = new ArrayList<>();
	protected ArrayList<ArrayList<Throu_unit>> value = new ArrayList<>();
	protected int numberOfFlow = 0; 
	
	protected double maximumY = 0; 
	protected int rangeY= 0; 
	
	protected File log; 
	protected String destination = "";
	protected FileOutputStream fos; 
	
	public TCPinfoGraph(String GraphName, int width, int height, int duration, int log_port, String source){
		super(GraphName, width, height,duration, log_port, source);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputStream is = null; 
				char temp;
				String readLine; 
				Socket tcpinfo = null;
				try {
					tcpinfo = new Socket();
					tcpinfo.setSoTimeout(3000);
					System.out.println("connect to tcpinfo server : " + log_port);
					tcpinfo.connect(new InetSocketAddress("127.0.0.1",log_port));
					System.out.println("TCPprobe is connected");
					is = tcpinfo.getInputStream();
					
					while(true) {
						readLine = "";
						while((temp = (char)is.read()) != '\n') 
							readLine += temp;
 
						addNewPoint(readLine);
//						updateGraph();
						readLine = ""; 
					}
				}
				catch(SocketTimeoutException e) {
					try {
						if(tcpinfo != null)
							tcpinfo.close();
					}
					catch(IOException e2) {
						e2.printStackTrace();
					}
					
				}
				catch(IOException e1) {
					e1.printStackTrace();
					try{
						tcpinfo.close();
						is.close();
					}catch (IOException e2){
						e2.printStackTrace(); 
					}
				}
		
			}
			
		}).start();
	}
	
	protected void addNewPoint(String line) {
//		System.out.println(line);
		String[] result = line.split(" ");
		if(result.length != 16)
			return;
		String flow_id = result[Define.SRC_PORT] + "/" + result[Define.DEST_PORT];
		if(!flow.contains(flow_id)) {
			flow.add(flow_id);
			ArrayList<Throu_unit> newArray = new ArrayList<>();
			value.add(newArray);
		}
		else {
			int index = flow.indexOf(flow_id);
			try{
				value.get(index).add(new Throu_unit(Double.parseDouble(result[offset_x]),Double.parseDouble(result[offset_y2]) ));
				
			}catch(NumberFormatException e){
				return; 
			}
		}
	}
	
	protected class GraphPanel extends MyAxis{
		public GraphPanel(int width, int height, int duration,String source) {
			super(width, height,duration);
			// TODO Auto-generated constructor stub
			
			
		}
		
		public void paint(Graphics g){
			super.paint(g);
			
			if(width == 0 || height == 0) {
				System.out.println("zero");
				return; 
			}
//			System.out.println("Draw in paint method\n");
			Graphics2D g2 = (Graphics2D)g;
			g2.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,0));
			
		
			
			for(int j=0; j<value.size();j++) {
				ArrayList<Throu_unit> a = value.get(j);
				g2.setColor(Define.color[j]);
				for(int i=0; i<a.size(); i++) {
					
					double unit[] = a.get(i).getUnit();
					double x = width/10 + unit[0]/duration*(width*8/10); 
					double y = unit[1]; 
					if(y >  maximumY) {
						maximumY = y; 
						rangeY = (int)(maximumY *1.5); 
						super.paintComponent(g);
//						super.paintComponents(g2);
						updateGraph();
					}
					y = height*8/10 - (height*7*unit[1]/10/rangeY);
					//if(i==0)
					//	g2.drawLine(width/10, height*8/10, (int)x,(int)y);
					if(i!=0){
						double[] prev = a.get(i-1).getUnit();
						g2.drawLine((int)(width/10+prev[0]/duration*(width*8/10)),(int)( height*8/10 - (height*7*prev[1]/10/rangeY)), (int)x, (int)y);
						
					}
					
				}
			}
			
		
			
			g2.setFont(new Font(g2.getFont().getPSName(), Font.PLAIN, height/30));
			FontMetrics fm = g2.getFontMetrics();
			g2.setColor(Color.black);
			// legend
			int i=0; 
			for(i=0; i<flow.size(); i++) {
				
				g2.setColor(Color.black);
				g2.drawString(flow.get(i), (int)(width*9/10-fm.stringWidth(flow.get(i))*1.2),(int)(height/10 + g2.getFont().getSize()*(i+1)*1.5));
				g2.setColor(Define.color[i]);
				g2.drawLine((int)(width*9/10-fm.stringWidth(flow.get(i))*1.5), (int)(height/10 + g2.getFont().getSize()*(i+1)*1.5) - g2.getFont().getSize()/2,
						(int)(width*9/10-fm.stringWidth(flow.get(i))*1.2) - width/10, (int)(height/10 + g2.getFont().getSize()*(i+1)*1.5) - g2.getFont().getSize()/2);
			}
			
//			g2.setColor(Color.black)
//			g2.setFont(new Font(g2.getFont().getPSName(), Font.PLAIN, 10));
//			float interval_y = (float) (maximumY*1.5)/10;
//			FontMetrics fm = g2.getFontMetrics();
//			for(int i=0; i<11; i++) {
//				String thik = String.format("%.2f",interval_y*i);
//				g2.drawString(thik, (int)( width/10 - fm.stringWidth(thik)*1.2), (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10)+g2.getFont().getSize()/2);
//				g2.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,0));
//				g2.drawLine(width/10, (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10), width*9/10, (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10));
//			}
//			g2.setFont(new Font(g2.getFont().getPSName(), Font.BOLD, 20));
//			
//			g2.drawString(content, width/2-fm.stringWidth(content)/2,height/10-g2.getFont().getSize()/2);

		}
		
		
		
	}

	public void setContent(String content) {
		this.content = content; 
	}
	
	private class Throu_unit{
		private double time, throu;
		public Throu_unit(Double time, Double throu) {
			this.time = time;
			this.throu = throu;
		}
		
		public double[] getUnit() {
			return new double[] {
					time,throu
			};
		}
	}
	
	protected void updateGraph() {
		if(graph != null) {
			graph.setSize(width, height);
		}
		container.repaint();
		
	}
	
	protected GraphPanel getGraphPanel() {
		return graph;
	}
	
	
}
