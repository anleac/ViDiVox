package vidivox.frames;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
/**
 * 
 * @author alea644
 * A simple JFrame which pops up whenever an intensive task is being preformed
 * to let the user know so; opposed to is just freezing.
 *
 */
@SuppressWarnings("serial")
public class ProgressBarFrame extends JFrame {

	private JPanel contentPane;

	public static ProgressBarFrame pbFrame = new ProgressBarFrame();
	JProgressBar progressBar = new JProgressBar();
	//the progress bar to show the progress to the user
	JLabel lblGiveUsA = new JLabel("");
	//informs the user what its currently doing

	/**
	 * Create the frame.
	 */
	public ProgressBarFrame() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		contentPane.setLayout(null);
		progressBar.setBounds(35, 39, 163, 20);
		progressBar.setValue(0);
		progressBar.setMaximum(100); //goes form 0-100%
		progressBar.setVisible(true);
		contentPane.add(progressBar);
		setTitle("Creating new video");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 242, 141);
		
		lblGiveUsA.setBounds(12, 12, 208, 15);
		lblGiveUsA.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblGiveUsA);
		setContentPane(contentPane);
	}
	
	/**
	 * This method is called via the swingworker class, and will update
	 * the gui depending on which task (represented by the integer 'code'
	 * its up to
	 * and progress represents how far through it is
	 * @param code
	 */
	public void Update(int code, int progress){
		switch (code){
			case 0: //Stripping audio
				lblGiveUsA.setText("Stripping audio from video");
			break;
			case 1: //Creating audio
				lblGiveUsA.setText("Creating required audio files");
			break;
			case 2: //Merging audio
				lblGiveUsA.setText("Merging audio to video");
			break;
			case 3: //Finishing up
				lblGiveUsA.setText("Finishing up video");
			break;
		}
		progressBar.setValue(progress); //update the progress
	}
	
	/**
	 * Resets the GUI components to its initial state
	 */
	public void reset(){
		progressBar.setValue(0); //go back to the start!
		lblGiveUsA.setText("Creating required audio files"); //default text
	}
}

