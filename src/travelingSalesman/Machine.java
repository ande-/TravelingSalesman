package travelingSalesman;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * This is the main class for traveling salesman. The program picks a number of cities from those 
 * listed in a file and calculates (brute-force, through all permutations) the shortest route, 
 * starting and ending with Albany.  The GUI is updated with a picture of the shortest route 
 * found so far and the progress bar with the progress towards verifying the shortest route.
 * The user can stop the calculation at any time or get a different set of random cities.
 * 
 * Machine is the GUI and also has the canvas and calculation thread as inner classes.
 * @author andreahoug
 *
 */
public class Machine extends JFrame {
	private static final long serialVersionUID = 1L;

	//for picking the cities
	private static List<City> cities;
	private static final Read read = new Read("./cities.txt");;
	private static final int numCities = 10; //this is the number of cities.  Don't make this much higher than 10!
	private static final Random random = new Random();

	//for finding shortest rout
	private Thread thread;
	private City[] shortestRoute;
	private static volatile boolean stopRequested = false;

	//gui-related fields
	private DrawCanvas canvas;
	private JProgressBar progressBar;
	private JTextField field = new JTextField();
	private String message;

	public static synchronized void requestStop(){
		stopRequested = true;
	}
	
	public static synchronized boolean stopRequested(){
		return stopRequested;
	}
	public static void main(String[] args) {
		//cities must be picked before the GUI is set up
		cities = pickCities(numCities-1); 
		Machine gui = new Machine();	
		gui.setVisible(true);
	}

	/**
	 * Randomly picks the appropriate length list of cities from the long list in Read
	 * @param numberOfDestinations, which is one less the number of cities since Albany 
	 * is not considered a destination
	 * @return the list of cities to be used
	 */
	public static List<City> pickCities(int numberOfDestinations) {
		List<City> all = read.getCities();
		List<City> chosen = new ArrayList<City>();

		chosen.add(all.get(0)); //always add Albany in index 0
		List<Integer> numbers = new ArrayList<Integer>();
		while(numbers.size() < numberOfDestinations) {
			int index = random.nextInt(all.size());
			if(!numbers.contains(index) && index !=0) {
				numbers.add(index);
				chosen.add(all.get(index));
			}
		}
		return chosen;
	}

	/**
	 * Constructor sets up the GUI
	 */
	public Machine() {
		super("Traveling Salesman Route Calculator");

		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		canvas = new DrawCanvas();
		canvas.setBackground(Color.WHITE);
		contentPane.add(canvas);
		canvas.repaint();  

		//exit
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JPanel bottom = new JPanel();
		contentPane.add(bottom);
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");
		JButton newCitiesButton = new JButton("New cities");
		bottom.add(startButton);
		bottom.add(stopButton);
		bottom.add(newCitiesButton);
		progressBar = new JProgressBar();
		bottom.add(progressBar);
		contentPane.add(field);
		field.setText(message);

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopRequested = false;
				
				
				if(thread == null || !thread.isAlive()) {
					thread = new Thread(new Pathfinder());
					thread.start();
				}

				field.setText("calculating");
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				if(thread!= null) {
					stopRequested = true;
					field.setText("Terminated");
					progressBar.setValue(0);

				}
			}
		});

		newCitiesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(thread!= null) {
					stopRequested = true;
					field.setText("");
					progressBar.setValue(0);

				}
				cities = pickCities(numCities-1);
				shortestRoute = null;
				canvas.repaint();
			}
		});

		pack();
	}

	/**
	 * An inner class for a runnable object that will do the analysis.
	 * The run method is where the permutations are generated and the shortest is found
	 *
	 */
	public class Pathfinder implements Runnable {
		private PermutationGenerator gen = new PermutationGenerator(numCities);

		@Override
		public void run() {
			progressBar.setMaximum(gen.getTotal().intValue());
			//length is the number of permutations starting with Albany, which are always at the beginning
			int length = (gen.getTotal().intValue())/numCities;  

			int progress = 0;
			double shortestDistance = Double.POSITIVE_INFINITY;
			for(int i = 0; i<length; i++) {
				if(stopRequested) break;
				double totalDistance = 0;
				City[] route = new City[numCities+1];
				int[] perm = gen.getNext();
				for(int index : perm) {
					if(stopRequested) break;
					route[index] = cities.get(perm[index]);
				}
				route[route.length-1] = cities.get(0); //end up at Albany. 

				for(int city = 0; city<route.length-1; city++) {
					if(stopRequested) break;
					City from = route[city];
					City to = route[city+1];
					totalDistance += from.calculateDistance(to);
					progress += 1;
					progressBar.setValue(progress);		
				}

				if(totalDistance < shortestDistance) {
					shortestDistance = totalDistance;
					shortestRoute = route;
				}
				canvas.repaint();

			}
			if(!stopRequested) {
				message = "Best route:";
				for(City city : shortestRoute) {
					message += " "+city.getName()+" ";
				}
			}
			field.setText(message);
		}
	}

	/**
	 * An inner class for the canvas where the map is drawn
	 *
	 */
	public class DrawCanvas extends Canvas {

		private static final long serialVersionUID = 1L;

		public DrawCanvas() {
			setSize(1500,680);
		}

		public void paint(Graphics g) {
			Graphics2D graphic = (Graphics2D) g;
			graphic.setColor(Color.BLUE);

			for(City city : cities) {
				int y = city.getypos(); 
				int x = city.getxpos(); 
				graphic.drawString(city.getName(), x, y); 
			}

			if(shortestRoute != null) {
				graphic.setColor(Color.MAGENTA);
				for(int city = 0; city < shortestRoute.length-1; city++) {
					City from = shortestRoute[city];
					City to = shortestRoute[city+1];
					graphic.drawLine(from.getxpos(), from.getypos(), to.getxpos(),  to.getypos());
				}
			}
		}
	}

}
