
import java.awt.*;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.awt.event.MouseAdapter;


public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
    private BufferedImage paintLayer;
    private Graphics paintLayerGraphics;
	private GridControl Control;
	FlowPanel(Terrain terrain) {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // if we moved the mouse previously draw a line from our prev point to our current position
                for(int i = e.getX()-2; i <= e.getX()+2; i++){
                    for(int j = e.getY()-2; j <= e.getY()+2; j++){

                        synchronized (land){
                            land.img.setRGB(i,j,Color.BLUE.getRGB());
                            land.control[i][j].addGridWater(3);
                        }


                    }
                }


                    repaint();
                }
                // store previous point


        });


    land=terrain;
	}
		
	// responsible for painting the terrain and water
	// as images
	@Override
    protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		  
		super.paintComponent(g);
		
		// draw the landscape in greyscale as an image
		if (land.getImage() != null){
			g.drawImage(land.getImage(), 0, 0, null);
		}
	}
	
	public void run() {	
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
	    repaint();
	}
}