package vidiVox;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class ProgressBarFrame extends JFrame {

	private JPanel contentPane;
	public static ProgressBarFrame pbFrame = new ProgressBarFrame();
	public static JProgressBar progressBar = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
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
		setTitle("Working...");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 204, 73);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 29, 180, 20);
		
		contentPane.add(progressBar);
		progressBar.setIndeterminate(true);
		
		JLabel lblGiveUsA = new JLabel("Give us a sec g");
		lblGiveUsA.setBounds(12, 2, 132, 15);
		contentPane.add(lblGiveUsA);
		
	}
}
