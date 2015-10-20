import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

public class VectorQuan {
    
    static int IMAGE_WIDTH = 352;
    static int IMAGE_HEIGHT = 288;
    static int N;
    static Cluster[] clusters;
  
   public static void main(String[] args) {
    //read filename and N 2 parameters
	String fileName = args[0];
   	N = Integer.parseInt(args[1]);
	
    //output two images, one original image at left, the other result image at right
    BufferedImage imgOriginal = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    
    try {
	    File file = new File(fileName);
	    InputStream is = new FileInputStream(file);

	    long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    
	    int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        
    	int ind = 0;
		for(int y = 0; y < IMAGE_HEIGHT; y++){
			for(int x = 0; x < IMAGE_WIDTH; x++){
				//for reading .raw image to show as a rgb image
				byte r = bytes[ind];
				byte g = bytes[ind];
				byte b = bytes[ind];

                //set pixel for display original image
				int pixOriginal = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                imgOriginal.setRGB(x,y,pixOriginal);
				ind++;
			}
		}
		is.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    int[] vectorSpace = calculateVectorSpace(imgOriginal);
    
    //quantization
    for (int y=0; y < IMAGE_HEIGHT;y++) {
        for (int x=0; x < IMAGE_WIDTH;x++) {
            int clusterId = vectorSpace[IMAGE_WIDTH*y+x];
            img.setRGB(x, y, clusters[clusterId].getPixel());
        }
    }
    
    // Use a panel and label to display the image
    JPanel  panel = new JPanel ();
    panel.add (new JLabel (new ImageIcon (imgOriginal)));
    panel.add (new JLabel (new ImageIcon (img)));
    
    JFrame frame = new JFrame("Display images");
    
    frame.getContentPane().add (panel);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
   }
   
   public static int[] calculateVectorSpace(BufferedImage image) {
	   clusters = createClusters(image);
       int[] vectorSpace = new int[IMAGE_WIDTH*IMAGE_HEIGHT];
       Arrays.fill(vectorSpace, -1);

       boolean refineNeeded = true;
       int loops = 0;
       while (refineNeeded) {
    	   refineNeeded = false;
           loops++;
           
           for (int y=0; y < IMAGE_HEIGHT; y++) {
               for (int x=0; x < IMAGE_WIDTH; x++) {
                   int pixel = image.getRGB(x, y);
                   Cluster cluster = getMinCluster(pixel);
                   
                   if (vectorSpace[IMAGE_WIDTH*y+x] != cluster.getId()) {
                       if (vectorSpace[IMAGE_WIDTH*y+x] != -1) {
                           clusters[vectorSpace[IMAGE_WIDTH*y+x]].removePixel(pixel);
                       }
                       cluster.addPixel(pixel);
                       refineNeeded = true;
                       vectorSpace[IMAGE_WIDTH*y+x] = cluster.getId();
                   }
               }
           }
       }
       
       System.out.println("Took "+ loops +" loops.");
       return vectorSpace;
   }
   
   //find the min distance cluster
   public static Cluster getMinCluster(int pixel) {
       Cluster cluster = null;
       int min = Integer.MAX_VALUE;
       for(int i = 0;i < clusters.length;i++) {
           int distance = clusters[i].distance(pixel);
           if (distance < min) {
               min = distance;
               cluster = clusters[i];
           }
       }
       return cluster;
   }
   
   //create N numbers of clusters
   public static Cluster[] createClusters(BufferedImage image) {
       Cluster[] result = new Cluster[N];
       int x = 0; 
       int y = 0;
       int dx = IMAGE_WIDTH/N;
       int dy = IMAGE_HEIGHT/N;
       
       for(int i=0;i < N;i++) {
           result[i] = new Cluster(i,image.getRGB(x, y));
           x += dx; 
           y += dy;
       }
       return result;
   }
}