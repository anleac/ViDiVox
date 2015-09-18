package vidiVox;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CommentaryFrame extends JFrame {

	private JPanel contentPane;
	public static CommentaryFrame cmFrame = new CommentaryFrame();
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CommentaryFrame frame = new CommentaryFrame();
					frame.setVisible(true);
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		textField = new JTextField();
		contentPane.add(textField, BorderLayout.NORTH);
		textField.setColumns(10);
		
		//Preview button plays text through festival
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Preview button clicked
				
			}
		});
		contentPane.add(btnPreview, BorderLayout.CENTER);
		
		//Button returns to mainFrame without saving
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Back button clicked
				
			}
		});
		contentPane.add(btnBack, BorderLayout.SOUTH);
		
		//Button for saving synth speech as MP3 file
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Save button clicked
				
			}
		});
		contentPane.add(btnSave, BorderLayout.WEST);
	}

}
