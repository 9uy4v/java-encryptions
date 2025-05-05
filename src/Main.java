import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import dijkstra.DijkstraEncryption;
import SIM.SimEnryption;
import gould.GouldEncryption;

public class Main extends JFrame {

	public void menu() {
		String[] options = { "Dijkstra's Encryption", "Dijkstra's Decryption", "SIM Encryption", "SIM Decryption",
				"Gould's Encryption", "Gould's Decryption", "Exit" };
		int response = JOptionPane.showOptionDialog(null, "Options: ",
				"Encryption Menu",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("assets/image.png"), options, options[0]);

		if (response < 0 || response > 5)
			System.exit(0);

		JFileChooser ch0 = new JFileChooser();
		int r0 = ch0.showOpenDialog(this);
		if (r0 == JFileChooser.APPROVE_OPTION) {
			File f = ch0.getSelectedFile();

			switch (response) {
				case -1:
					System.exit(0);

				case 0:
					DijkstraEncryption.encrypt(f);
					break;

				case 1:
					DijkstraEncryption.decrypt(f);
					break;

				case 2:
					SimEnryption.encrypt(f);
					break;

				case 3:
					SimEnryption.decrypt(f);
					break;

				case 4:
					GouldEncryption.encrypt(f);
					break;

				case 5:
					GouldEncryption.decrypt(f);
					break;

				default:
					break;
			}

		}

		menu();
	}

	public static void main(String[] args) {
		Main m = new Main();
		m.menu();

	}

}
