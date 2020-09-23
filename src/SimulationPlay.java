import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class SimulationPlay extends java.lang.Thread {
    private int upper;
    private int lower;
    private Terrain land;
    volatile boolean running;
    int count;
    public SimulationPlay(int upper, int lower, Terrain land,int count){
        this.upper= upper;
        this.lower=lower;
        this.land= land;
        this.running= true;
        this.count=count;
    }
    @Override
    public void run() {
        while (running) {
            for (int i = lower; i < upper; i++) {
                int arr[] = new int[2];
                land.getPermute(i, arr);
                int x = arr[0];
                int y = arr[1];
                GridControl lowfind = land.control[x][y];
                GridControl lowest = null;
                if ((lowfind.getGridWater() > 0) && (x >0) && (x < land.getDimX()-1) && (y >0) && (y <land.getDimY()-1)) {
                    for (int k = y - 1; k <= y + 1; k++) {

                        //iterate the immediate columns
                        for (int j = x - 1; j <= x + 1; j++) {
                            // check if this thing is the lowest
                            if ((land.control[j][k].getWaterSurface() < lowfind.getWaterSurface()) && (j < 512) && (k < 512)) {
                                lowest = land.control[j][k];
                            }


                        }
                    }
                        if (lowest != null) {
                            synchronized (lowfind) {
                                synchronized (lowest) {

                                    lowfind.deductGridWater(1);
                                    lowest.addGridWater(1);


                                    land.img.setRGB(lowest.getGridCol(), lowest.getGridRow(),Color.BLUE.getRGB());



                                    if (lowfind.getGridWater() == 0) {
                                         land.resetPixel(lowfind.getGridCol(), lowfind.getGridRow());
                                    } else {
                                        //reshade the current pixel
                                      //  float val1 = (lowfind.getGridWater() - land.minh) / (land.maxh - land.minh);
                                        //Color col1 = new Color(0, val1, val1, 1.0f);
                                        land.img.setRGB(lowfind.getGridCol(), lowfind.getGridRow(), Color.BLUE.getRGB());
                                    }

                                    //repaint the panel

                                    Flow.fp.repaint();

                                }
                            }// end sync block

                        }



                } else {
                    synchronized (lowfind) {
                        lowfind.resetGridWater();
                        land.resetPixel(lowfind.getGridCol(), lowfind.getGridRow());

                    }
                }
                this.count=i;

            }


        }
    }

    public int getCount() {
        return count;
    }
}
