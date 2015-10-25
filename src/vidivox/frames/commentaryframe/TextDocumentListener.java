package vidivox.frames.commentaryframe;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.frames.CommentaryFrame;

/**
 * A document listner used in commentary frame.
 * This is to detect any incorrect input data, and will 
 * alert the user
 * @author andrew
 *
 */
public class TextDocumentListener implements DocumentListener{
		public void changedUpdate(DocumentEvent e) {
			warn();
		}

		public void removeUpdate(DocumentEvent e) {
			warn();
		}

		public void insertUpdate(DocumentEvent e) {
			warn();
		}
		/**
		 * Checks for a warning, and if so, will notify the user
		 * and disable any relevant buttons
		 */
		public void warn() {
			// check here if its adequate data
			String[] data = CommentaryFrame.cmFrame.TextField().getText().trim().split("\\s+", -1);
			if (data.length > 30 && CommentaryFrame.cmFrame.LblName().getText().equals("N/A")) { //Bad data, show a warning
				CommentaryFrame.cmFrame.showWarning = true;
				CommentaryFrame.cmFrame.SetWarning("Please no more than 30 words.");
				CommentaryFrame.cmFrame.btnPreview.setEnabled(false);
				CommentaryFrame.cmFrame.btnSave.setEnabled(false);
			} else if (data.length <= 1 && data[0].trim().equals("") && CommentaryFrame.cmFrame.LblName().getText().equals("N/A")) {
				CommentaryFrame.cmFrame.showWarning = true;
				CommentaryFrame.cmFrame.SetWarning("Please enter some words");
				CommentaryFrame.cmFrame.btnPreview.setEnabled(false);
				CommentaryFrame.cmFrame.btnSave.setEnabled(false);
			} else {
				CommentaryFrame.cmFrame.showWarning = false;
				CommentaryFrame.cmFrame.SetWarning("");
				CommentaryFrame.cmFrame.btnPreview.setEnabled(true);
				CommentaryFrame.cmFrame.btnSave.setEnabled(true);
			}
			CommentaryFrame.cmFrame.lblWarning.setText(CommentaryFrame.cmFrame.WarningMessage());
			CommentaryFrame.cmFrame.lblWarning.setVisible(CommentaryFrame.cmFrame.showWarning);
		}
	
}
