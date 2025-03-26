import java.io.*;

public class EasyEncryption
{
	private final int SIZE=8;
	public int[]   vec=new int[SIZE];
		
	public EasyEncryption()
	{
		for( int i=0; i<vec.length; i++){
		vec[i]=i;
		}
	}
	
	public int switchBit(int data, int bitNum)
	{
		return data ^ (1<<bitNum);
	}
	
	public void convertFile(File inFile,String s)
	{
		try{
			int temp, index=0;
			FileInputStream in;
			FileOutputStream out;
			//The first argument creates the new file
			// in the same directory on inFIle:
			File outFile=new File(inFile.getParentFile(),s + inFile.getName());
			in= new FileInputStream(inFile);
			out= new FileOutputStream(outFile);
			while((temp=in.read()) != -1){
				out.write(switchBit(temp, vec[index]));
				index++;
				if(index == SIZE)
					index=0;
			}	
			in.close();
			out.close();
		
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	public void easyEncryptFile(File inFile)
	{
		convertFile(inFile,"easyEncrypt");
	}
	
	public void easyDecryptFile(File inFile)
	{
		convertFile(inFile,"easyDecrypt");
	}
	
}
