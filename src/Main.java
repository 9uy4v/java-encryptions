import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main extends JFrame 
{
	
	public void menu()
	{
		String[] options =  {"Easy Encryption 1", "Easy Decryption 1", "Exit"};
		int response = JOptionPane.showOptionDialog(null, "Options: ", 
				"Encryption Menu",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("assets/image.png"), options, options[0]);
		
		switch( response)
		{
			case -1:
				System.exit(0);
			
			case 0:
				JFileChooser ch0 = new JFileChooser();
			    int r0 = ch0.showOpenDialog(this);
			    if (r0 == JFileChooser.APPROVE_OPTION) 
			    {
			    	File f = ch0.getSelectedFile();
			    	EasyEncryption ee = new EasyEncryption();
				    ee.easyEncryptFile(f);  	
			    }
			    menu();
				break;
				
			case 1:
				 JFileChooser ch1 = new JFileChooser();
			     int r1 = ch1.showOpenDialog(this);
			     if (r1 == JFileChooser.APPROVE_OPTION) 
			     {
			    	File f = ch1.getSelectedFile();
			    	EasyEncryption ee = new EasyEncryption();
				    ee.easyDecryptFile(f);    	
			     } 	
			     menu();
				break;
				
			
			case  2:
				System.exit(0);
				
			default:
				break;		
		}
	}
	
	
	public static void main(String[] args) 
	{
		Main m = new Main();
		m.menu();

	}

}
