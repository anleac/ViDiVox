package frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

import tools.FileTools;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import java.awt.Label;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import jtools.LimitDocumentFilter;
import javax.swing.SwingConstants;
/**
 * A GUI for adding audio to a specific time in the video
 * @author alea644
 *
 */
public class AudioFrame extends JFrame {

	private JPanel contentPane;
	public static AudioFrame fFrame = new AudioFrame();
	private final JLabel lblSelected = new JLabel("N/A"); //used to display the selected file
	private String selectedFile = ""; //to store the path to the selected file
	private final JTextField mText, sField;
	/**
	 * Create the frame.
	 */
	public AudioFrame() {
		setResizable(false);
		setTitle("Add Audio");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 353, 218);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSelectAAudio = new JButton("Select an audio file:");
		btnSelectAAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = FileTools.openMP3File(); //get an mp3 file
				if (f == null) return;
				AutoLoadAudio(f.getAbsolutePath()); //load in the path
			}
		});
		btnSelectAAudio.setBackground(Color.WHITE);
		btnSelectAAudio.setBounds(23, 44, 233, 25);
		contentPane.add(btnSelectAAudio);
		
		JLabel lblAddAAudio = new JLabel("Add a audio file to a specific time:");
		lblAddAAudio.setBounds(23, 12, 247, 15);
		contentPane.add(lblAddAAudio);
		
		JLabel lblSelectedFile = new JLabel("Selected file:");
		lblSelectedFile.setBounds(23, 81, 93, 15);
		contentPane.add(lblSelectedFile);
		
		lblSelected.setBounds(128, 81, 212, 15);
		contentPane.add(lblSelected);
		
		JButton btnApply = new JButton("Apply");
		btnApply.setBackground(Color.WHITE);
		btnApply.setBounds(20, 146, 90, 25);
		contentPane.add(btnApply);
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.setBackground(Color.WHITE);
		btnPreview.setBounds(122, 146, 113, 25);
		contentPane.add(btnPreview);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				lblSelected.setText("N/A"); //unselect any files
				selectedFile = "";
				MainFrame.mFrame.setVisible(true);
			}
		});
		btnBack.setBackground(Color.WHITE);
		btnBack.setBounds(247, 146, 93, 25);
		contentPane.add(btnBack);
		
		JLabel lblTimeInVideo = new JLabel("Time in video:");
		lblTimeInVideo.setToolTipText("Time to add the audio into the video");
		lblTimeInVideo.setBounds(23, 114, 105, 15);
		contentPane.add(lblTimeInVideo);
		
		mText = new JTextField();
		mText.setHorizontalAlignment(SwingConstants.RIGHT);
		((AbstractDocument)mText.getDocument()).setDocumentFilter(new LimitDocumentFilter(2));
		mText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) { //Make sure it isnt empty
				mText.setText((mText.getText().trim().equals(""))? "0" : mText.getText());
			}
		});
		mText.setToolTipText("Minutes in the video");
		mText.setText("0");
		mText.setBounds(128, 115, 22, 19);
		contentPane.add(mText);
		mText.setColumns(10);
		
		Label label = new Label("m");
		label.setBounds(150, 114, 15, 21);
		contentPane.add(label);
		
		sField = new JTextField();
		sField.setHorizontalAlignment(SwingConstants.RIGHT);
		((AbstractDocument)sField.getDocument()).setDocumentFilter(new LimitDocumentFilter(2));
		sField.setToolTipText("Minutes in the video");
		sField.setText("0");
		sField.setColumns(10);
		sField.setBounds(167, 115, 23, 19);
		sField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) { //Make sure it isnt empty
				sField.setText((sField.getText().trim().equals(""))? "0" : sField.getText());
			}
		});
		contentPane.add(sField);
		
		Label label_1 = new Label("s");
		label_1.setBounds(190, 114, 19, 21);
		contentPane.add(label_1);
		
		JButton btnCurrentTime = new JButton("Current time");
		btnCurrentTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sField.setText(MainFrame.mFrame.getCurrentTime().split(":")[1].replaceFirst("^0+(?!$)", "")); //set the current time into the labels
				mText.setText(MainFrame.mFrame.getCurrentTime().split(":")[0].replaceFirst("^0+(?!$)", ""));
			}
		});
		btnCurrentTime.setToolTipText("Inserts the current time into the boxes to the left");
		btnCurrentTime.setBackground(Color.WHITE);
		btnCurrentTime.setBounds(209, 112, 131, 25);
		contentPane.add(btnCurrentTime);
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(null);
	}
	
	/**
	 * Automatically loads a given mp3 path; into the gui
	 * @param path
	 */
	public void AutoLoadAudio(String path){
		if ((new File(path).exists())){ //check the file exists
			selectedFile = path;
			lblSelected.setText(path.split(File.separator)[path.split(File.separator).length - 1]); //only display the name of the file
		}else{
			FileTools.displayError("Supplied file does not exist"); //ensure the file exists
		}
	}
}
