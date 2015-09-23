package frames;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ProgressBarFrame extends JFrame {

	private JPanel contentPane;

	public static ProgressBarFrame pbFrame = new ProgressBarFrame();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					pbFrame.setLocationRelativeTo(null);
					pbFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ProgressBarFrame() {
		contentPane = new JPanel();
contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		contentPane.setLayout(null);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(38, 61, 163, 20);
		progressBar.setIndeterminate(true);
		progressBar.setValue(1);
		progressBar.setVisible(true);
		contentPane.add(progressBar);
		setTitle("Working...");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 263, 176);
		
		
		
		
		JLabel lblGiveUsA = new JLabel("Give us a few seconds");
		lblGiveUsA.setBounds(38, 22, 250, 15);
		contentPane.add(lblGiveUsA);
		setContentPane(contentPane);
	}
}

