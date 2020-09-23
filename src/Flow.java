

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.Dimension;

import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class Flow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static SimulationPlay[] simThread;
	static volatile boolean paused;
    static int count;
    static JLabel timeStep;
	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	public static void setupGUI(int frameX,int frameY,Terrain landdata) {
		count=0;
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Waterflow"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);

	    

         timeStep = new JLabel("Time Step: ");
        //count  = new JLabel(Integer.toString(SimulationPlay.c);

        JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		JButton endButton = new JButton("End");;
		// add the listener to the jbutton to handle the "pressed" event
		endButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do ask threads to stop
				frame.dispose();
				System.exit(0);
			}
		});
				panel.add(endButton);



        //Reset Button
        JButton resetB = new JButton("Reset");
        resetB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused){
                    paused = true;
                }


                synchronized (landdata.control){
                    for (int i = 0; i < landdata.getDimY() ; i++) {
                        for (int j = 0; j < landdata.getDimY(); j++) {
                            landdata.control[j][i].resetGridWater();
                            landdata.resetPixel(j,i);
                        }
                    }
                }


                for (int i = 0; i < 4; i++) {
                    simThread[i].running = false;

                }



                }

                //make new threads



        });

        //Pause Button
        JButton pauseB = new JButton("Pause");
        pauseB.addActionListener(e ->{
            //stop all threads

            paused = true;
            for (int i = 0; i < 4; i++) {
                simThread[i].running = false;

            }

        });

        //play button
        JButton playB = new JButton("Play");
        playB.addActionListener(e -> {
            paused= false;
            getTimeStep();



       });




        //add the buttons
        panel.add(resetB);
        panel.add(pauseB);
        panel.add(playB);
        panel.add(endButton);
        g.add(panel);
        g.add(timeStep);

        g.add(panel);
    	
		frame.setSize(frameX, frameY+80);	// a little extra space at the bottom for buttons
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        Thread fpt = new Thread(fp);
        fpt.start();
	}

	public static void getTimeStep() {


        if (!paused) {
            //    for (int i = 1; i <= 4; i++) {

            synchronized (simThread) {
                int incSize = fp.land.permute.size();
                int low = 0;
                int high = incSize;
                for (int i = 1; i <= 4; i++) {
                    simThread[i - 1] = new SimulationPlay(incSize, low, fp.land,count);
                    low += incSize;
                    high += incSize;
                    simThread[i - 1].start();
                    count+= simThread[i-1].getCount();
                    timeStep.setText("Time Step: "+ count   );

                }


            }
        }
    }

	public static void main(String[] args) {
		Terrain landdata = new Terrain();
		
		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
				
		// landscape information from file supplied as argument
		//
		simThread = new SimulationPlay[4];

		landdata.readData(args[0]);
		paused= false;
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata));
		
		// to do: initialise and start simulation
	}
}
