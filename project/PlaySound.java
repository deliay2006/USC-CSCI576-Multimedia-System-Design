
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;
    
    private Clip dataClip = null;
    
    private int pause = 0;

    private String filename;
    /**
     * CONSTRUCTOR
     */
    public PlaySound(String audioFilename) {
    	this.filename = audioFilename;
    }

    public void play() throws PlayWaveException {

    try {
		this.waveStream = new FileInputStream(this.filename);
	} catch (FileNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	AudioInputStream audioInputStream = null;
	try {
		//add buffer for mark/reset support, modified by Jian
		InputStream bufferedIn = new BufferedInputStream(this.waveStream);
	    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		
	} catch (UnsupportedAudioFileException e1) {
	    throw new PlayWaveException(e1);
	} catch (IOException e1) {
	    throw new PlayWaveException(e1);
	}

	try {
		dataClip = AudioSystem.getClip();
	} catch (LineUnavailableException e1) {
	    throw new PlayWaveException(e1);
	}

	try {
		// Starts the music :P
		dataClip.open(audioInputStream);
		dataClip.setFramePosition(pause);  // Must always rewind!
		dataClip.loop(pause);
		dataClip.start();
	} catch (LineUnavailableException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
    public void pause() {
    	pause = dataClip.getFramePosition();
    	dataClip.stop();
    }
    
    public void loop(){
    	dataClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stop(){
    	pause = 0;
    	dataClip.stop();
    }
}
