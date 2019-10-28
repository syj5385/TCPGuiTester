package Graph;

import javax.swing.*; 
import java.io.File;
import java.awt.Graphics;



public class MyGraph extends MyAxis{
	
	
	
	protected int width; 
	protected int height; 
	protected String source; 
	
	protected String FilePath = "/home/jjun/Desktop/";
	
	
	public MyGraph (int width, int height, int duration, String source ) {
		super(width,  height, duration);
		
		this.source = source; 
	
		
	
	}
	
	public void paint(Graphics g) {
		

	}
}
