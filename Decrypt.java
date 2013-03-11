import java.util.*;
import java.io.*;

public class Decrypt {
  public static void main (String[] args) {
    String encrypted = args[0];
    String key = args[1];
    decrypt(encrypted, key);
  }
  
  public static void decrypt(String e, String k) {
    File en = new File(e);
    File key = new File(k);
    try {
      Scanner kScan = new Scanner(key);
      long modder = kScan.nextLong();
      long garbage = kScan.nextLong(); // not used during decryption
      long privateKey = kScan.nextLong();
      InputStream input = new BufferedInputStream(new FileInputStream(en)); 
      File decrypted = new File("decrypted");
      OutputStream out = new BufferedOutputStream(new FileOutputStream(decrypted));  
      if (!decrypted.exists())  
        decrypted.createNewFile();
      byte[] blocks = new byte[4]; 
      byte[] newBlocks;  
      long fileIndex = 0; 
      while (fileIndex < en.length()) {		
        input.read(blocks);
        fileIndex += 4;
        newBlocks = convert(blocks, modder, privateKey);
        for (int a = 0; a < newBlocks.length; a++)
          out.write(newBlocks[a]);
        blocks = new byte[4];	 	  
      } 
      out.close();
    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }	  	
  }
  
  public static byte[] convert(byte[] b, long n, long d) {
    byte[] dBlocks = new byte[3];
    long number = 0;
    for (int i = 0; i < b.length; i++) {  
      number = (number << 8) + (b[i] & 0xFF); // change b[i] to an unsigned byte
    }
    long decrypted = 1;
    for (int i = getNumBits(d) - 1; i >= 0; i--) {
      long flag = (d >>> i) & 1;
      if (flag == 0)
        decrypted = (decrypted * decrypted) % n;
      else
        decrypted = (((decrypted * decrypted) % n) * number) % n;
    } 
    for (int x = 0; x < 3; x++)
      dBlocks[2-x] = (byte) ((decrypted >> (x*8)) & 0xFF);
    dBlocks = unpad(dBlocks); 
    return dBlocks;
  } 

// Remove any 0 bytes at the end of the decryption process
   public static byte[] unpad(byte[] b) {
    int zerosToRemove = 0;
    for(int i = b.length - 1; i >= 0; i--){
      if (b[i] != 0) 
        break;
      else
        zerosToRemove++;
    }
    byte[] result = new byte[b.length - zerosToRemove];
    for (int i = 0; i < result.length; i++) 
      result[i] = b[i];
    return result;
  }

  public static int getNumBits(long l) { 
    int result = 0;
    while (l != 0) {
      l /= 2;
      result++;
    }
    return result; // max: 32
  }

  // For debugging purposes only
  public static void printBytes(byte[] b) {
    System.out.println("You have read: ");
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
