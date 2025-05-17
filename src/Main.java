import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import dijkstra.DijkstraEncryption;
import SIM.SimEnryption;
import gould.GouldEncryption;

public class Main extends JFrame {

	public static void menu() {
		String[] options = { "Dijkstra's Encryption", "Dijkstra's Decryption", "SIM Encryption", "SIM Decryption",
				"Gould's Encryption", "Gould's Decryption", "Exit" };
		int response = JOptionPane.showOptionDialog(null, "Options: ",
				"Encryption Menu",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("assets/image.png"), options, options[0]);

		if (response < 0 || response > 5)
			System.exit(0);

		JFileChooser ch0 = new JFileChooser();
		int r0 = ch0.showOpenDialog(null);
		if (r0 == JFileChooser.APPROVE_OPTION) {
			File f = ch0.getSelectedFile();

			boolean success = false;

			switch (response) {

				case 0:
					success = DijkstraEncryption.encrypt(f);
					break;

				case 1:
					success = DijkstraEncryption.decrypt(f);
					break;

				case 2:
					success = SimEnryption.encrypt(f);
					break;

				case 3:
					success = SimEnryption.decrypt(f);
					break;

				case 4:
					success = GouldEncryption.encrypt(f);
					break;

				case 5:
					success = GouldEncryption.decrypt(f);
					break;

				default:
					System.exit(0);
					break;
			}

			if (success) {
				showSucess();
			} else {
				showError("Encryption/Decryption Failed");
			}

		}

		menu();
	}

	public static void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		menu();
	}

	public static void showSucess() {
		JOptionPane.showMessageDialog(null, "Encryption/ Decryption successful", "Success",
				JOptionPane.INFORMATION_MESSAGE);
		menu();
	}

	public static void main(String[] args) {
		menu();
	}

}
