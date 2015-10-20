import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import javax.swing.*;

public class VideoPlayer {
  static final int WIDTH = 352;
  static final int HEIGHT = 288;

  public static void main(String[] args) {
    String fileFolder = args[0];
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
  
    //get video name
	StringBuilder reverseS = new StringBuilder(fileFolder).reverse();
	String filename = fileFolder.substring(fileFolder.length()-reverseS.toString().indexOf('/'), fileFolder.length());
//	String audioFilename = fileFolder + "/" + filename + ".wav";
	
//	System.out.println("Start loading video file contents.");
    try {
      //every video in db has 600 frames
//      for(int i=1; i<=600; i++) {
//    	  String fileNum = "00";
//    	  if(i < 100 && i > 9) {
//    		  fileNum = "0";
//    	  } else if(i > 99) {
//    		  fileNum = "";
//    	  }
    	  String fullName = fileFolder + "/" + filename + "001.rgb";
    	  
    	  File file = new File(fullName);
    	  InputStream is = new FileInputStream(file);

   	      long len = file.length();
	      byte[] bytes = new byte[(int)len];
	      int offset = 0;
          int numRead = 0;
          while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
              offset += numRead;
          }
          System.out.println("Start loading frame: " + fullName);
    	  int index = 0;
          BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
          for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
   				byte r = bytes[index];
   				byte g = bytes[index+HEIGHT*WIDTH];
   				byte b = bytes[index+HEIGHT*WIDTH*2]; 
   				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    			image.setRGB(x,y,pix);
    			index++;
    		}
    	  }
          images.add(image);
          is.close();
          System.out.println("End loading frame: " + fullName);
//      }//end for
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
//    System.out.println("End loading video contents.");

//	FileInputStream audioStream;
//	try {
//		audioStream = new FileInputStream(audioFilename);
//	} catch (FileNotFoundException e) {
//	    e.printStackTrace();
//	    return;
//	}

	// initializes the playSound Object
//	PlaySound playSound = new PlaySound(audioStream);

	// plays the sound
//	try {
//	    playSound.play();
//	} catch (PlayWaveException e) {
//	    e.printStackTrace();
//	    return;
//	}
	
	VideoQueryUI ui = new VideoQueryUI(images);
	ui.showUI();

  }

}