import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;


public class SubSampleQuan {
    
    static int IMAGE_WIDTH = 352;
    static int IMAGE_HEIGHT = 288;
    static int Y;
    static int U;
    static int V;
    static int Q;
    //initial two arrays one for rgb data the other for yuv data
    static int imageCopy[] = new int[3*IMAGE_HEIGHT*IMAGE_WIDTH];
    static double yuvImage[] = new double[3*IMAGE_HEIGHT*IMAGE_WIDTH];
  
   public static void main(String[] args) 
   {
    //read filename Y U V Q 5 parameters
	String fileName = args[0];
   	Y = Integer.parseInt(args[1]);
    U = Integer.parseInt(args[2]);
    V = Integer.parseInt(args[3]);
    Q = Integer.parseInt(args[4]);
	
    //output two images, one original image at left, the other result image at right
    BufferedImage imgOriginal = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
       
    try {
	    File file = new File(args[0]);
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
		 
				byte a = 0;
                //read r value from original image and store it in imageCopy[]
                //note here byte value in Java is from -128 to 127
                //we need transform it to 0-255 for further yuv calculation
				byte r = bytes[ind];
                if(r<0) {
                    imageCopy[(y*IMAGE_WIDTH + x)*3] = r+256;
                } else {
                    imageCopy[(y*IMAGE_WIDTH + x)*3] = r;
                }
                
				byte g = bytes[ind+IMAGE_HEIGHT*IMAGE_WIDTH];
                if(g<0) {
                    imageCopy[(y*IMAGE_WIDTH + x)*3 + 1] = g+256;
                } else {
                    imageCopy[(y*IMAGE_WIDTH + x)*3 + 1] = g;
                }
                
				byte b = bytes[ind+IMAGE_HEIGHT*IMAGE_WIDTH*2];
                if(b<0) {
                    imageCopy[(y*IMAGE_WIDTH + x)*3 + 2] = b+256;
                } else {
                    imageCopy[(y*IMAGE_WIDTH + x)*3 + 2] = b;
                }
                
                //calculate yuv value and store them in yuvImage[]
                double changedY = RGBtoY(imageCopy[(y*IMAGE_WIDTH + x)*3], imageCopy[(y*IMAGE_WIDTH + x)*3 + 1], imageCopy[(y*IMAGE_WIDTH + x)*3 + 2]);
                yuvImage[(y*IMAGE_WIDTH + x)*3] = changedY;
                
                double changedU = RGBtoU(imageCopy[(y*IMAGE_WIDTH + x)*3], imageCopy[(y*IMAGE_WIDTH + x)*3 + 1], imageCopy[(y*IMAGE_WIDTH + x)*3 + 2]);
                yuvImage[(y*IMAGE_WIDTH + x)*3+1] = changedU;
                
                double changedV = RGBtoV(imageCopy[(y*IMAGE_WIDTH + x)*3], imageCopy[(y*IMAGE_WIDTH + x)*3 + 1], imageCopy[(y*IMAGE_WIDTH + x)*3 + 2]);
                yuvImage[(y*IMAGE_WIDTH + x)*3+2] = changedV;
                
                //set pixel for display original image
				int pixOriginal = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                imgOriginal.setRGB(x,y,pixOriginal);
				ind++;
			}
		}
		
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    //do sub sampling here
    subSample();
    
    //process output image
    for(int y = 0; y < IMAGE_HEIGHT; y++){
           
        for(int x = 0; x < IMAGE_WIDTH; x++){
            
            int i = y*IMAGE_WIDTH + x;
            
            byte r, g, b;
            //calculate rgb value from yuv value
            //take round
            //then do quantization
            //then eliminate data that smaller than 0 or larger than 255
            imageCopy[3*i] = (int)Math.rint(YUVtoR(yuvImage[3*i], yuvImage[3*i+1], yuvImage[3*i+2]));
            imageCopy[3*i] = quantization(imageCopy[3*i]);
            if(imageCopy[3*i] < 0) {
                imageCopy[3*i] = 0;
            } else if(imageCopy[3*i] > 255) {
                imageCopy[3*i] = 255;
            }
            
            imageCopy[3*i +1] = (int)Math.rint(YUVtoG(yuvImage[3*i], yuvImage[3*i+1], yuvImage[3*i+2]));
            imageCopy[3*i + 1] = quantization(imageCopy[3*i +1]);
            if(imageCopy[3*i + 1] < 0) {
                imageCopy[3*i + 1] = 0;
            } else if(imageCopy[3*i + 1] > 255) {
                imageCopy[3*i + 1] = 255;
            }
            
            imageCopy[3*i +2] = (int)Math.rint(YUVtoB(yuvImage[3*i], yuvImage[3*i+1], yuvImage[3*i+2]));
            imageCopy[3*i + 2] = quantization(imageCopy[3*i + 2]);
            if(imageCopy[3*i + 2] < 0) {
                imageCopy[3*i + 2] = 0;
            } else if(imageCopy[3*i + 2] > 255) {
                imageCopy[3*i + 2] = 255;
            }
    
            //note here we need transform int data 0-255
            //to byte data which is -128 to 127
            if(imageCopy[3*i] > 127) {
                r = (byte)(imageCopy[3*i] - 256);
            } else {
                r = (byte)imageCopy[3*i];
            }
            
            if(imageCopy[3*i + 1] > 127) {
                g = (byte)(imageCopy[3*i + 1] - 256);
            } else {
                g = (byte)imageCopy[3*i + 1];
            }
            
            if(imageCopy[3*i + 2] > 127) {
                b = (byte)(imageCopy[3*i + 2] - 256);
            } else {
                b = (byte)imageCopy[3*i + 2];
            }
            
            //set pixel for display output image
            int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
            img.setRGB(x,y,pix);
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
    
    //method for sub sample
    public static void subSample() {
        for(int y = 0; y < IMAGE_HEIGHT; y++){
            if(Y > 1) {
                int x;
                for(x = 0; x < IMAGE_WIDTH - Y; x+=Y){
                    //set values between x to x+Y to be the average of two
                    //remained values
                    for(int z=x+1; z<x+Y; z++) {
                        yuvImage[3*(IMAGE_WIDTH*y + z)] = (yuvImage[3*(IMAGE_WIDTH*y + x)] + yuvImage[3*(IMAGE_WIDTH*y + x+Y)])/2;
                    }
                }
                x++;
                //note here for the last period of values
                //we set them to be the start remained value
                for(;x<IMAGE_WIDTH; x++) {
                    yuvImage[3*(IMAGE_WIDTH*y + x)] = yuvImage[3*(IMAGE_WIDTH*y + IMAGE_WIDTH - Y)];
                }
            }
            
            if(U > 1) {
                int x;
                for(x = 0; x < IMAGE_WIDTH - U; x+=U){
                    for(int z=x+1; z<x+U; z++) {
                        yuvImage[3*(IMAGE_WIDTH*y + z) + 1] = (yuvImage[3*(IMAGE_WIDTH*y + x) + 1] + yuvImage[3*(IMAGE_WIDTH*y + x+U) + 1])/2;
                    }
                }
                x++;
                for(;x<IMAGE_WIDTH; x++) {
                    yuvImage[3*(IMAGE_WIDTH*y + x) + 1] = yuvImage[3*(IMAGE_WIDTH*y + IMAGE_WIDTH - U) + 1];
                }
            }
            
            if(Y > 1) {
                int x;
                for(x = 0; x < IMAGE_WIDTH - V; x+=V){
                    for(int z=x+1; z<x+V; z++) {
                        yuvImage[3*(IMAGE_WIDTH*y + z) + 2] = (yuvImage[3*(IMAGE_WIDTH*y + x) + 2] + yuvImage[3*(IMAGE_WIDTH*y + x+V) + 2])/2;
                    }
                }
                x++;
                for(;x<IMAGE_WIDTH; x++) {
                    yuvImage[3*(IMAGE_WIDTH*y + x) + 2] = yuvImage[3*(IMAGE_WIDTH*y + IMAGE_WIDTH - V) + 2];
                }
            }
        }
    }
    
    //do quantization
    //note here we need to set all inputs that fall in last level
    //to be the number of start of last level
    public static int quantization(int input) {
        if(Q < 256 && Q > 0) {
            int span = (256/Q);
            if(input > (255-span)) {
                return (255-span);
            }
            int level = (int)Math.rint((input+1.0)/span);
            return span*level-1;
        }
        return input;
    }
    
   /*
    * Multiply RGB with 0.299 0.587 0.114
    * Return Y value
    */
   public static double RGBtoY(int r, int g, int b) {

       return (0.299*r + 0.587*g + 0.114*b);
   }
  
    /*
     * Multiply RGB with -0.147 -0.289 0.436
     * Return U value
     */
    public static double RGBtoU(int r, int g, int b) {
        return (0.436*b - (0.147*r) - (0.289*g));
    }
    
    /*
     * Multiply RGB with 0.615 -0.515 -0.100
     * Return V value
     */
    public static double RGBtoV(int r, int g, int b) {

        return (0.615*r - (0.515*g) - (0.100*b));
    }
    
    /*
     * Multiply YUV with 0.999 0.000 -0.581
     * Return R value
     */
    public static double YUVtoR(double y, double u, double v){
        return (0.999*y + 1.140*v);
    }
    
    /*
     * Multiply YUV with 1.000 -0.395 -0.581
     * Return G value
     */
    public static double YUVtoG(double y, double u, double v) {
        return (y - 0.395*u - 0.581*v);
    }
    
    /*
     * Multiply YUV with 1.000 2.032 -0.000
     * Return B value
     */
    public static double YUVtoB(double y, double u, double v) {
        return (y + 2.032*u);
    }
}