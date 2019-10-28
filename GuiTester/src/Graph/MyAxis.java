package Graph;

import javax.swing.*; 
import java.awt.*; 

public class MyAxis extends JPanel{
	protected int width,height, duration;
	
	public MyAxis(int width, int height,int duration) {
		this.width = width; 
		this.height = height;
		this.duration = duration; 
		System.out.println("Width : " + width +"\tHeight : " + height);
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		// draw Outer Line
		
		g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,0));
		g2.drawRect(width/10,height/10,width*8/10, height*7/10);
		
		int interval = duration / 10; 
	
		Font current = g2.getFont();
		Font newFont = current.deriveFont(Font.BOLD|Font.CENTER_BASELINE, height/30);
		g2.setFont(newFont);
		for(int i=0; i<11; i++) {
			FontMetrics fm = g2.getFontMetrics();
			g2.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,0));
			g2.setColor(Color.GRAY);
			g2.drawLine(width/10 + (width*8/10)*i/10, height/10,width/10 + (width*8/10)*i/10, height*8/10);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font(g2.getFont().getPSName(), Font.PLAIN, 10));
			g2.drawString(String.valueOf(i*interval),width/10 + (width*8/10)*i/10-fm.stringWidth(String.valueOf(i*interval))/2, height*8/10 + (int)(g.getFont().getSize()*1.2));
		}

	}
	
	public void Restart() {
		removeAll();
		repaint();
	}
	
	public void setSize(int width, int height) {
		this.width = width; 
		this.height = height;
	}
	

	

}
