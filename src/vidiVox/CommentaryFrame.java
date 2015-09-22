package vidiVox;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CommentaryFrame extends JFrame {

	private JPanel contentPane;
	public static CommentaryFrame cmFrame = new CommentaryFrame();
	private JTextArea textField;
	public static JCheckBox chckbxApplyThisSpeech;
	public static int currentFestID = 0;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cmFrame.setVisible(true);
					cmFrame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CommentaryFrame() {
		setTitle("Add Commentary");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 460, 288);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextArea();
		textField.setLineWrap(true);
		textField.setBounds(12, 22, 432, 134);
		contentPane.add(textField);

		// Preview button plays text through festival
		JButton btnPreview = new JButton("Preview");
		btnPreview.setBounds(15, 168, 133, 25);
		btnPreview.setBackground(Color.WHITE);
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Preview button clicked
				String textToPreview = textField.getText();
				int numWords = textToPreview.split(" ").length;
				// Brief says to limit max number of words to between 20-40
				if (numWords > 30) {
					// Too many words
					Tools.displayError("Number of words should be less than 30");
				} else {
					currentFestID = Tools.speakFestival(textToPreview);
				}

			}
		});
		contentPane.add(btnPreview);

		// Button returns to mainFrame without saving
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(311, 168, 133, 25);
		btnBack.setBackground(Color.WHITE);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Back button clicked
				// MainFrame was never set to invisible
				// Simply hide this current frame
				cmFrame.setVisible(false);
				MainFrame.mFrame.requestFocus();

			}
		});
		contentPane.add(btnBack);

		// Button for saving synth speech as MP3 file
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(160, 170, 139, 21);
		btnSave.setBackground(Color.WHITE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save button clicked
				String textToSave = textField.getText();
				Tools.saveFestToMP3(textToSave);
			}
		});
		contentPane.add(btnSave);

		JLabel lblWriteTextBelow = new JLabel("Write text below to add to your video (30 word limit)");
		lblWriteTextBelow.setBounds(12, 0, 378, 15);
		contentPane.add(lblWriteTextBelow);

		JButton btnStop = new JButton("Stop Preview");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop button clicked
				Tools.killAllFestProc(currentFestID);
			}
		});
		btnStop.setBackground(Color.WHITE);
		btnStop.setBounds(15, 205, 133, 25);
		contentPane.add(btnStop);
		
		chckbxApplyThisSpeech = new JCheckBox("Apply audio to a new video");
		chckbxApplyThisSpeech.setBounds(160, 196, 290, 23);
		contentPane.add(chckbxApplyThisSpeech);
		
		JCheckBox chckbxLoadNewVideo = new JCheckBox("Load new video after completion");
		chckbxLoadNewVideo.setBounds(160, 216, 290, 23);
		contentPane.add(chckbxLoadNewVideo);
	}
}
