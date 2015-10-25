package vidivox.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import vidivox.frames.commentaryframe.LimitDocumentFilter;
import vidivox.frames.commentaryframe.TextDocumentListener;
import vidivox.projects.CustomAudio;
import vidivox.tools.BashTools;
import vidivox.tools.FileTools;

import javax.swing.JRadioButton;

/**
 * A JFrame which handles the GUI components of adding mp3s to the video (this
 * supports generation of wav files, and adding pre-existing mp3s).
 * 
 * @author alea644
 * 
 */
@SuppressWarnings("serial")
public class CommentaryFrame extends JFrame {
	/*
	 * 
	 * Frame used to add/create 'commentary' (text) into a wave file and add to
	 * the video loaded. This JFrame represents the GUI for it.
	 */
	private JPanel contentPane;
	public static CommentaryFrame cmFrame = new CommentaryFrame();
	private JTextArea textField; //holds the info to convert to text
	public JTextArea TextField() { return textField; }
	private final JTextField mField, sField;
	public boolean showWarning = false; // whether or not to show the warning
											// message.
	private String warningMessage = ""; // shows an error to the user
	public String WarningMessage() {return warningMessage;}
	public void SetWarning(String set) {warningMessage = set;}
	private String selectedFile = "";
	private final JLabel lblName = new JLabel("N/A"); // name of a file loaded (if applicable)
	public JLabel LblName() {return lblName; }
	public 	final JLabel lblWarning = new JLabel("");

	final JRadioButton rdbtnRobotic = new JRadioButton("Robotic"), rdbtnBritish = new JRadioButton("British"), rdbtnNewZealander = new JRadioButton("Kiwi");

	public final JButton btnSave = new JButton("Apply"), btnPreview = new JButton("Preview");
	//for saving/applying text
	

	/**
	 * Resets any changed GUI components to original state
	 */
	public void clearCurrent() {
		textField.setText("");
		lblName.setText("N/A");
		selectedFile = "";
		warningMessage = ""; // reset any previous warnings
	}

	/**
	 * Returns which string they selected
	 * 
	 * @return
	 */
	public String getVoice() {
		if (rdbtnRobotic.isSelected()) {
			return "(voice_kal_diphone)";
		} else if (rdbtnBritish.isSelected()) {
			return "(voice_rab_diphone)";
		} else { // british
			return "(voice_akl_nz_jdt_diphone)";
		}
	}

	/**
	 * Create the frame.
	 */
	public CommentaryFrame() {
		setResizable(false);
		setTitle("Add Audio");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 414, 381);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextArea();
		textField.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textField.setLineWrap(true);
		textField.setBounds(25, 39, 193, 112);
		contentPane.add(textField);
		// Preview button plays text through festival
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lblName.getText().equals("N/A")) // no file selected
					BashTools.speakFestival(textField.getText(), getVoice()); // speak the preview
				else {
					BashTools.speakMp3(selectedFile); // speak the mp3 file
				}
			}
		});
		btnPreview.setEnabled(false);
		btnPreview.setBounds(25, 303, 102, 25);
		btnPreview.setBackground(Color.WHITE);
		contentPane.add(btnPreview);

		// Button returns to mainFrame without saving
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(267, 303, 110, 25);
		btnBack.setBackground(Color.WHITE);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Back button clicked
				// MainFrame was never set to invisible
				// Simply hide this current frame
				textField.setText(""); // clear the text
				cmFrame.setVisible(false);
				MainFrame.mFrame.requestFocus();

			}
		});
		contentPane.add(btnBack);
		
		// Button for saving synth speech as MP3 file
		btnSave.setEnabled(false);
		btnSave.setBounds(139, 303, 116, 25);
		btnSave.setBackground(Color.WHITE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save button clicked
				String textToSave = textField.getText();
				boolean usedFile = !lblName.getText().equals("N/A");
				if (textToSave.equals("") && !usedFile) { // Error, they entered no text
					FileTools.displayError("Please enter some valid text or select a file");
					return;
				}

				double length = -1;
				if (!usedFile) length = BashTools.saveFestToMP3(textToSave, getVoice(), false);
				// sadly unable to get length of an mp3 without additional libs
				if (length > 0 || usedFile) {
					// Add the custom audio file to the project
					if (!usedFile) selectedFile = getVoice();
					MainFrame.mFrame.VProject().AddAudio(new CustomAudio((!usedFile) ? textToSave : lblName.getText(),
							FileTools.TimeToLong(mField.getText() + ":"+ sField.getText()), length, selectedFile));
					MainFrame.mFrame.resetVideo();
					AudioFrame.aFrame.updateAudio();
					// Update the GUi components
				}
				setVisible(false);
			}
		});
		contentPane.add(btnSave);

		JLabel lblSpeakingOptions = new JLabel("Speaking Options"), lblWriteTextBelow = new JLabel("Write text below to add to your video");
		lblWriteTextBelow.setBounds(21, 12, 378, 15);
		contentPane.add(lblWriteTextBelow);

		lblSpeakingOptions.setBounds(236, 39, 141, 15);
		contentPane.add(lblSpeakingOptions);

		JLabel label = new JLabel("Time in video:");
		label.setToolTipText("Time to add the audio into the video");
		label.setBounds(25, 268, 105, 15);
		contentPane.add(label);

		// These hold the minutes and seconds in the video
		mField = new JTextField();
		((AbstractDocument) mField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(2));
		mField.addKeyListener(new KeyAdapter() { // Called whenever new data is
			// entered onto the text
			// field
			@Override
			public void keyReleased(KeyEvent e) {
				showWarning = false;
				btnSave.setEnabled(true);
				try {
					if ((Long.parseLong(sField.getText()) + (Long
							.parseLong(mField.getText()) * 60)) * 1000 >= MainFrame.mFrame.Video()
							.getMediaMeta().getLength()) {
						showWarning = true;
						warningMessage = "Time is too long";
						btnSave.setEnabled(false);
					}
				} catch (Exception eb) { // they entered a letter.
					showWarning = true;
					warningMessage = "Invalid time";
					btnSave.setEnabled(false);
				}
				lblWarning.setVisible(showWarning);
				lblWarning.setText(warningMessage);
			}
		});
		mField.setToolTipText("Minutes in the video");
		mField.setText("0");
		mField.setHorizontalAlignment(SwingConstants.RIGHT);
		mField.setColumns(10);
		mField.setBounds(130, 269, 22, 19);
		contentPane.add(mField);

		sField = new JTextField();
		((AbstractDocument) sField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(2));
		sField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				showWarning = false;
				try {
					if ((Long.parseLong(sField.getText()) + (Long
							.parseLong(mField.getText()) * 60)) * 1000 >= MainFrame.mFrame.Video()
							.getMediaMeta().getLength()) {
						showWarning = true;
						warningMessage = "Time is too long";
						btnSave.setEnabled(false);
					}
				} catch (Exception eb) { // they entered a letter.
					showWarning = true;
					warningMessage = "Invalid time";
					btnSave.setEnabled(false);
				}
				lblWarning.setVisible(showWarning);
				lblWarning.setText(warningMessage);
			}
		});
		sField.setToolTipText("Seconds in the video");
		sField.setText("0");
		sField.setHorizontalAlignment(SwingConstants.RIGHT);
		sField.setColumns(10);
		sField.setBounds(169, 269, 23, 19);
		contentPane.add(sField);

		JButton button = new JButton("Current time");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] currentTime = FileTools.LongToTime(
						MainFrame.mFrame.TimeSlider().getValue() * 100)
						.split(":");
				sField.setText(currentTime[1]); // Get the current time of the
												// video
				mField.setText(currentTime[0]); // Then split it into
												// minutes/seconds and applt to
												// GUI
			}
		});
		button.setToolTipText("Inserts the current time into the boxes to the left");
		button.setBackground(Color.WHITE);
		button.setBounds(216, 263, 161, 25);
		contentPane.add(button);

		Label mLabel = new Label("m"), sLabel = new Label("s");
		mLabel.setBounds(152, 268, 15, 21);
		sLabel.setBounds(192, 268, 19, 21);
		contentPane.add(mLabel);
		contentPane.add(sLabel);

		JLabel lblOrAddAn = new JLabel(
				"Or add an existing file (takes priority)");
		lblOrAddAn.setBounds(25, 201, 286, 15);
		contentPane.add(lblOrAddAn);
		lblWarning.setForeground(Color.RED);
		// to select an MP3 file to add
		JButton btnSelectFile = new JButton("Select file:");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File mp3 = FileTools.openMP3File(); // select an mp3 file to
													// add.
				if (mp3 != null) { // make sure isnt null!
					lblName.setText(mp3.getAbsolutePath().split(File.separator)[mp3
							.getAbsolutePath().split(File.separator).length - 1]);
					// update name
					btnPreview.setEnabled(true);
					btnSave.setEnabled(true);
					lblWarning.setVisible(false);
					selectedFile = mp3.getAbsolutePath();
				}
			}
		});
		btnSelectFile
				.setToolTipText("Inserts the current time into the boxes to the left");
		btnSelectFile.setBackground(Color.WHITE);
		btnSelectFile.setBounds(25, 221, 161, 25);
		contentPane.add(btnSelectFile);
		lblName.setBounds(192, 228, 185, 15);
		contentPane.add(lblName);
		lblWarning.setFont(new Font("Dialog", Font.BOLD, 12));
		lblWarning.setBounds(25, 183, 352, 15);
		contentPane.add(lblWarning);

		ButtonGroup group = new ButtonGroup();
		// add them all to a group so that only one can be checked at a
		// given time
		rdbtnRobotic.setSelected(true);
		rdbtnRobotic.setBounds(228, 62, 149, 23);
		contentPane.add(rdbtnRobotic);
		group.add(rdbtnRobotic);

		rdbtnBritish.setBounds(228, 89, 149, 23);
		contentPane.add(rdbtnBritish);
		group.add(rdbtnBritish);

		rdbtnNewZealander.setBounds(228, 116, 149, 23);
		contentPane.add(rdbtnNewZealander);
		group.add(rdbtnNewZealander);

		JLabel lblCount = new JLabel("");
		lblCount.setForeground(Color.RED);
		lblCount.setFont(new Font("Dialog", Font.BOLD, 12));
		lblCount.setBounds(25, 159, 352, 15);
		contentPane.add(lblCount);
		// an action listener which is called upon new text
		// nbeing added, we can check here for adequate text input.
		textField.getDocument().addDocumentListener(new TextDocumentListener());

	}
}
