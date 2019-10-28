
import Graph.CwndGraph;
import Graph.RttGraph;
import Graph.ThroughputGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Tester Start");
		
		int duration = Integer.parseInt(args[0]);
		System.out.println("Duration = " + duration);
		
		ThroughputGraph throughput = new ThroughputGraph("Test", 600, 400,duration,Integer.parseInt(args[1]), null);
		RttGraph rtt = new RttGraph("Test",600, 400,duration,Integer.parseInt(args[1]), null);
		CwndGraph cwnd = new CwndGraph("test",600,400,duration,Integer.parseInt(args[1]),null);
		
	}
	
	private class TCPflow {
		
		private static final int TCP_CONGESTION = 13; 
		
		private String cca;
		private String dst_ip; 
		private int dst_port; 
		
		private Socket socket; 
		private OutputStream outputstream;
		
		private byte[] data = new byte[1024]; 
	
		
		public TCPflow(String dst_ip, int dst_port, String cca ) {
			this.dst_ip = dst_ip; 
			this.dst_port = dst_port; 
			this.cca = cca;
			
		}
		
		private class flowThread extends Thread{
			public flowThread () throws IOException {
				socket = new Socket();
				socket.connect(new InetSocketAddress(dst_ip, dst_port));
				outputstream = socket.getOutputStream();
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
			}

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return cca; 
			}
			
			
		}
	}
	
	

}



