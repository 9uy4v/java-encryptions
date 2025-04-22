import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main extends JFrame {

	public void menu() {
		String[] options = { "Dijkstra's Encryption", "Dijkstra's Decryption", "SIM Encryption", "SIM Decryption",
				"Gould's Encryption", "Gould's Decryption", "Exit" };
		int response = JOptionPane.showOptionDialog(null, "Options: ",
				"Encryption Menu",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("assets/image.png"), options, options[0]);

		JFileChooser ch0 = new JFileChooser();
		int r0 = ch0.showOpenDialog(this);
		if (r0 == JFileChooser.APPROVE_OPTION) {
			File f = ch0.getSelectedFile();
			EasyEncryption ee = new EasyEncryption();

			switch (response) {
				case -1:
					System.exit(0);

				case 0:
					ee.easyEncryptFile(f); // TODO : replace with respective encryption function

					menu();
					break;

				case 1:
					ee.easyDecryptFile(f); // TODO : replace with respective decryption function

					menu();
					break;

				case 2:
					ee.easyEncryptFile(f); // TODO : replace with respective encryption function

					menu();
					break;

				case 3:
					ee.easyDecryptFile(f); // TODO : replace with respective decryption function

					menu();
					break;

				case 4:
					ee.easyEncryptFile(f); // TODO : replace with respective encryption function

					menu();
					break;

				case 5:
					ee.easyDecryptFile(f); // TODO : replace with respective decryption function

					menu();
					break;

				case 6:
					System.exit(0);

				default:
					break;
			}
		}

	}

	public static void main(String[] args) {
		Main m = new Main();
		m.menu();

	}

}
