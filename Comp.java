import java.util.*;
import java.io.*;
import java.lang.*;

public class Comp {
  public static Trie t;

  public static void main(String[] args) {
    if (args[0].equals("c"))
      compress(args[1]);
    else if (args[0].equals("d"))
      decompress(args[1]);
    else
      System.out.println("Please enter either 'c' or 'd' in your first argument");	  
  }

  public static void compress(String file) {
    IO.Compressor c;
    try {
      c = new IO.Compressor(file);
    } catch (Exception e) {
        throw new RuntimeException();  
    }
    char[] text;
    try {
      text = c.giveArray();
    } catch (Exception e) {
        throw new RuntimeException();
    }
    t = new Trie();
    StringBuilder word = new StringBuilder();
    int index = 1;
    for (int i = 0; i < text.length - 1; i++) {
      word.append(text[i]);
      System.out.println("word:" + word.toString());
      if (!t.contains(word.toString())) {
        System.out.println("new entry");
        String prefix = word.substring(0, word.length() - 1); // determines n's parent.
  //      System.out.println("prefix: " + prefix);
        Trie.Node parent = t.get(prefix);
        t.add(t.new Node(word.toString(), index), parent);
        index++;
        int prefIndex = parent.getIndex(); 
        try {
          c.encode(prefIndex, text[i]); // one transmission
          System.out.println(word.toString() + " encoded.");
        } catch (Exception e) {
          System.err.println("Y U NO ENCODE????  " + e.getMessage());
          throw new RuntimeException();
        }
        word = new StringBuilder();
      } 
    }
    try {
      c.done();
    } catch (Exception e) {
        throw new RuntimeException();
    }
  }
  
  public static void decompress(String file) {
    IO.Decompressor d;
    ArrayList<String> dic = new ArrayList<String>();
    dic.add("");
    try {
      d = new IO.Decompressor(file);
    } catch (Exception e) {
        throw new RuntimeException();  
    }
    IO.pair next = d.decode();
    while(next.valid) {
      String output = dic.get(next.index) + next.extension;
      dic.add(output);
      try {
        d.append(output); // why doesn't this shit append the last pair, even though it's decoded properly? -.-
      } catch (Exception e) {
          throw new RuntimeException();
      }
      next = d.decode();
  //    System.out.println("Next pair: (" + next.index +  "," + next.extension + ")");
  //    System.out.println("Valid?  " + next.valid);
    }
    try {
      d.done();
    } catch (Exception e) {
        throw new RuntimeException();
    }
  }
}

