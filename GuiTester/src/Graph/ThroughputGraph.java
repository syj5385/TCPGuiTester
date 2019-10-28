package Graph;

import Graph.MyGraph;
import Graph.TCPinfoGraph.GraphPanel;

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
import java.io.FileReader; 
import java.io.IOException;
import java.io.InputStream;

import Graph.Define;


public class ThroughputGraph extends TCPinfoGraph {
	
	
	public ThroughputGraph(String GraphName, int width, int height, int duration, int log_port,String source){
		super(GraphName, width, height,duration,log_port, source);
		frame.setLocation(100,200);
		GraphName += "_Throughput";
		frame.setTitle(GraphName);
		
		setContent("Throughput");
		offset_y2 = Define.STHROUGHPUT;
		
		graph = new GraphPanel(width, height,duration,source) {

			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.black);
				g2.setFont(new Font(g2.getFont().getPSName(), Font.PLAIN, 10));
				float interval_y = (float) (maximumY*1.5)/10;
				FontMetrics fm = g2.getFontMetrics();
				for(int i=0; i<11; i++) {
					String thik = String.format("%.2f",interval_y*i);
					g2.drawString(thik, (int)( width/10 - fm.stringWidth(thik)*1.2), (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10)+g2.getFont().getSize()/2);
					g2.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,0));
					g2.drawLine(width/10, (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10), width*9/10, (int)(height*8/10-(interval_y*i)/(1.5*maximumY)*height*7/10));
				}
				
				g2.setFont(new Font(g2.getFont().getPSName(), Font.BOLD, height/15));
				fm = g2.getFontMetrics();
				g2.drawString(content, width/2-fm.stringWidth(content)/2,height/10-g2.getFont().getSize()/2);
			}
			
		};
		graph.repaint();
		container.add(graph,BorderLayout.CENTER);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					
					updateGraph();
					try {
						Thread.sleep(20);
					}catch(InterruptedException e) {
						
					}
				}
			}
			
		}).start();
		
	}
	
	
	
}
