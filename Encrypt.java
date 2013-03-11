import java.util.*;
import java.io.*; 

public class Encrypt {

  public static void main(String[] args) {
    String encryptee = args[0];
    String key = args[1]; 
    encrypt(encryptee, key);
  } 

  public static void encrypt(String e, String k) { 
    try {
      File encryptee = new File(e);
      InputStream input = new BufferedInputStream(new FileInputStream(encryptee));
      File key = new File(k);
      Scanner kScan = new Scanner(key);
      long modder = kScan.nextLong();
      long publicKey = kScan.nextLong();     
      File encrypted = new File("encrypted");
      OutputStream out = new BufferedOutputStream(new FileOutputStream(encrypted));  
      if (!encrypted.exists()) 
        encrypted.createNewFile();
      byte[] blocks = new byte[3]; 
      byte[] newBlocks; 
      long fileIndex = 0; 
      while (fileIndex < encryptee.length()) {		
        input.read(blocks);
        blocks = removeBad(blocks);
        fileIndex += 3;
        if (blocks.length != 0) { // skip the writing if the code only read null/newline chars for this iteration
          newBlocks = convert(blocks, modder, publicKey);
          for (int a = 0; a < newBlocks.length; a++)
            out.write(newBlocks[a]);
    //      System.out.println("Blocks written");
        } 
        blocks = new byte[3];	 	  
      } 
      out.close();
    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }	  	
  }

  public static byte[] convert(byte[] b, long n, long e) {
    byte[] eBlocks = new byte[4];
    long number = 0;
    for (int i = 0; i < b.length; i++)   
      number = (number << 8) + (b[i] & 0xFF); 
    number = number << (8 * (3 - b.length)); // will pad the number if it's less than 3 bytes
    long encrypted = 1;
    for (int i = getNumBits(e) - 1; i >= 0; i--) {
      long flag = (e >>> i) & 1;
      if (flag == 0)
        encrypted = (encrypted * encrypted) % n;
      else
        encrypted = (((encrypted * encrypted) % n) * number) % n;
    } 
    for (int x = 0; x < 4; x++) 
      eBlocks[3-x] = (byte) ((encrypted >> (x*8)) & 0xFF); 
    return eBlocks;
  }

  public static byte[] removeBad(byte[] b) {
    ArrayList<Byte> dummy = new ArrayList<Byte>();
    for(int i = 0; i < b.length; i++){
      if (b[i] != 0)
        dummy.add(b[i]);
    }
    byte[] result = new byte[dummy.size()];
    int index = 0;
    for (Byte elem : dummy) {
      result[index] = elem;
      index++;
    }
    return result;
  }   

  public static int getNumBits(long l) { 
    int result = 0; // only one bit of this guy should be set to 1
    while (l != 0) {
      l /= 2;
      result++;
    }
    return result; // max: 32
  }

// For debugging purposes only
  public static void printBytes(byte[] b) {
    System.out.println("You have read:");
    for (int i = 0; i < b.length; i++)
      System.out.println("Byte " + (i+1) + ": " + b[i]); 
    System.out.println(); 
  }

    // For debugging purposes only
  public static void printWrittenBytes(byte[] b) {
    System.out.println("You have written: ");
    for (int i = 0; i < b.length; i++)
      System.out.println("Byte " + (i+1) + ": " + b[i]); 
    System.out.println(); 
  }    
}
  
