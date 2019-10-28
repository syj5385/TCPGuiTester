package Graph;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener; 

public class TcpGraph extends JFrame {
	
	protected String GraphName; 
	protected int width, height,duration;
	protected String source; 
	
	protected JFrame frame; 
	protected Container container;
	protected Graphics g; 
	protected MyAxis axis; 
	
	protected int log_port = 0; 
	
	public TcpGraph(String GraphName, int width, int height,int duration, int log_port, String source) {
		this.GraphName = GraphName; 
		this.width = width; 
		this.height = height; 
		this.duration = duration;
		this.source = source; 
		this.log_port = log_port; 
	
		frame = new JFrame(GraphName);
		container = frame.getContentPane();
		frame.setPreferredSize(new Dimension(width,height));
		frame.addComponentListener(componentListener);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
			
		
	}
	

	protected void updateGraph() {

		
	}
	
	public void setWidth(int width) {
		this.width = width ;
	}
	
	public void setHeight(int height) {
		this.height = height; 
	}
	
	
	private ComponentListener componentListener = new ComponentListener() {

		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			setWidth(arg0.getComponent().getWidth());
			setHeight(arg0.getComponent().getHeight());
			updateGraph();
			
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			setWidth(arg0.getComponent().getWidth());
			setHeight(arg0.getComponent().getHeight());
			updateGraph();
		}
		
	};


}
