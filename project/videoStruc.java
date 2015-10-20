import java.lang.String;
import java.lang.Object;
import java.lang.Math;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;


public class videoStruc {
	public String videoname;
	public int type;
	public int sound;
	public int[][] imgbytes;
	public int framenum;
	public double[][] rgbCount; //0 r, 1 g, 2 b
	public byte[][] colorbytes;
	
	public videoStruc(int f, int t){
		sound = 0;
		framenum = f;
		type = t;
		imgbytes = new int[framenum][99];
		colorbytes = new byte[framenum][];
		rgbCount = new double[framenum][3];
		
	}
	
	public void readAndextractSound() throws PlayWaveException, FileNotFoundException, IOException{
		String soundFileName;
		if(type == 0)
			soundFileName= "/Users/delia/Desktop/videoQuery/db/" + videoname + "/" + videoname + ".wav";
		else
			soundFileName= "/Users/delia/Desktop/videoQuery/query/" + videoname + "/" + videoname  + ".wav";
		try {
			File soundFile = new File(soundFileName);
			InputStream sis = new FileInputStream(soundFile);
			
			AudioInputStream audioInputStream = null;
			try {
			    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (UnsupportedAudioFileException e1) {
			    new PlayWaveException(e1);
			} catch (IOException e1) {
			    new PlayWaveException(e1);
			}

			int count = 0;
			int readBytes = 0;
			byte[] audioBuffer = new byte[524288];
			
			
			try {
			    if (readBytes != -1) {
			    
				readBytes = audioInputStream.read(audioBuffer, 0,
					audioBuffer.length);
				
					if (readBytes >= 0){
						sound = calculateRMSLevel(audioBuffer);
						//System.out.println(sound);
						/*System.out.println(count++);*/
					}
			    }
			}catch (IOException e){ e.printStackTrace();}
			
			
			
		}catch (FileNotFoundException e) {
		      e.printStackTrace();
		} catch (IOException e) {
		      e.printStackTrace();
		}
	}
	
	public void readAndextractVideo() {
		for(int i = 0;i < framenum;i++) {
			//System.out.println("line" + i);
			int[] bits = new int[99];
			String num  = "";
			int k = i + 1;
			num += Integer.toString(k / 100);
			k = k -  (k / 100) * 100;
			num += Integer.toString(k / 10);
			k = k - (k / 10) * 10;
			num += Integer.toString(k);
			String imgpath;
			if(type == 0)
				imgpath= "/Users/delia/Desktop/videoQuery/db/" + videoname + "/" + videoname + num + ".rgb";
			else
				imgpath= "/Users/delia/Desktop/videoQuery/query/" + videoname + "/" + videoname + "_" + num + ".rgb";
			readImagergb(imgpath, i);
			bits = cvtComputeBits(cvtGrayscale(resizePixels(colorbytes[i])));
			for(int j = 0;j < 99;j++) {
				imgbytes[i][j] = bits[j];
			}
		}
	}
	
	public void extractColor() {
		//colorV[0] = colorV[1] = colorV[2] = 0.0;
		for(int i = 0;i < framenum;i++) {
			rgbCount[i][0] = rgbCount[i][1] = rgbCount[i][2] = 0.0;
		for(int ind = 0;ind < 352 * 288;ind++) {
			
			int r = colorbytes[i][ind] < 0?colorbytes[i][ind] + 256:colorbytes[i][ind];
			int g = colorbytes[i][ind + 352 * 288] < 0?colorbytes[i][ind + 352 * 288] + 256:colorbytes[i][ind + 352 * 288];
			int b = colorbytes[i][ind + 352 * 288 * 2] < 0?colorbytes[i][ind + 352 * 288 * 2] + 256:colorbytes[i][ind + 352 * 288 * 2];
			
			float hsb[] = null;
			hsb = Color.RGBtoHSB(r, g, b, null);
			if(hsb[0] < 0.17 || hsb[0] >= 0.83) {
				rgbCount[i][0] += 1;
			}
			else if(hsb[0] >= 0.17 && hsb[0] < 0.5) {
				rgbCount[i][1] += 1;
			}
			else if(hsb[0] >= 0.5 && hsb[0] < 0.83) {
				rgbCount[i][2] += 1;
			}
		}
		
		normalizeVector(i);
		/*for(int k = 0;k < 3;k++) {
			colorV[k] += rgbCount[i][k];
		}*/
		
		}
		/*for(int k = 0;k < 3;k++) {
			colorV[k] /= framenum;
		}
		System.out.println(colorV[0] + " " + colorV[1]+ " " + colorV[2]);*/
	}
	
	private void normalizeVector(int k) {
		for(int i = 0;i < 3;i++) {
			rgbCount[k][i] /= 352 * 288;
		}
		
		double rgbProd = rgbCount[k][0] * rgbCount[k][0]
					  + rgbCount[k][1] * rgbCount[k][1]
					  + rgbCount[k][2] * rgbCount[k][2];
		rgbProd = Math.sqrt(rgbProd);
		
		for(int i = 0;i < 3;i++) {
			rgbCount[k][i] /= rgbProd;
		}
	}
	
	private void readImagergb(String imgpath, int i){
		//create a new buffered image
		
		
		File file = new File(imgpath);
		long len = file.length();
	    colorbytes[i] = new byte[(int)len];
		//read image from file path
		try {
			
		    InputStream is = new FileInputStream(file);
		    
		    int offset = 0;
	        int numRead = 0;
	        while (offset < colorbytes.length && (numRead=is.read(colorbytes[i], offset, colorbytes[i].length-offset)) >= 0) {
	            offset += numRead;
	        }
	        
	        
		}catch (FileNotFoundException e) {
		      e.printStackTrace();
		} catch (IOException e) {
		      e.printStackTrace();
		}
		
		
	}
	
	private byte[] resizePixels(byte[] pixels) {
		byte[] temp = new byte[11*9*3] ;
	    int ratio = 32;
	    int p; 
	    for(int ind = 0;ind < 99;ind++){
	            p = ind * ratio;
	            //temp[(i*11)+j] = pixels[py*352+px];
	            temp[ind] = pixels[p];
	            temp[ind + 99] = pixels[p + 352 * 288];
	            temp[ind + 99 * 2] = pixels[p + 352 * 288 * 2];
	            ind++;
	    }
	    return temp;
	}
	
	private byte[] cvtGrayscale(byte[] bytes) {
		byte[] graybytes = new byte[11 * 9];
		int ind = 0;
		for(int y = 0; y < 9; y++){
	
			for(int x = 0; x < 11; x++){
				byte r = bytes[ind];
				byte g = bytes[ind+99];
				byte b = bytes[ind+99*2]; 
				graybytes[y * 11 + x] = (byte)(0.21 * r + 0.72 * g + 0.07 * b);
				ind++;
				
			}
		}
		return graybytes;
	}
	
	private int[] cvtComputeBits(byte[] graybytes) {
		int[] bits = new int[99];
		double average = 0, sum = 0;
		for(int i = 0;i < 99;i++) {
			sum += graybytes[i];
		}
		average = sum / 99.0;
		for(int i = 0;i < 99;i++) {
			if(graybytes[i] > average) {
				bits[i] = 1;
			}
			else {
				bits[i] = 0;
			}
			//System.out.print(bits[i]);
		}
		//System.out.println(" division");
		return bits;
	}
	
	private int calculateRMSLevel(byte[] audioData)
    { 
        long lSum = 0;
        for(int i=0; i<audioData.length; i++)
            lSum = lSum + audioData[i];
 
        double dAvg = lSum / audioData.length;
 
        double sumMeanSquare = 0d;
        for(int j=0; j<audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);
 
        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }
}
