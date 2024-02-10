package fr.lovc.tts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lovc.Main;

public class TextToSpeechReader  {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);	

	public void read(String txt) {
		Path sndPath = generateSound(txt);
		

		
		playSound(sndPath); 
	}
	
	public Path generateSound(String txt) {
		Path piperPath = Paths.get("").toAbsolutePath().resolve("piper").toAbsolutePath();
		Path sndPath = piperPath.resolve("tmp/output.wav");
		txt = txt.replace("\n", "").replace("\r", ""); // removing eventual line breaks because would not work in the cmd line otherwise
		try {
			Files.deleteIfExists(sndPath);
		} catch (IOException e) {
			LOGGER.error("Cannot delete " + sndPath, e);
		}
		String piperCmd = "echo " + txt + " | " +
							piperPath.resolve("piper.exe") +
							" --model " +
							piperPath.resolve("voices/en_US-amy-low.onnx") +
							" --output_file " +
							piperPath.resolve("tmp/output.wav");
		LOGGER.info("Text sent to tts: \"" + piperCmd + "\"");
		
	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", piperCmd);

        processBuilder.redirectInput();
        processBuilder.redirectOutput();
        processBuilder.redirectError();
        processBuilder.redirectErrorStream(true);
        Process process;
		try {
			process = processBuilder.start();
	        String line = "";
	        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
		        while ((line = reader.readLine()) != null) {
		            LOGGER.info("[piper] {}", line);
		        }
		        process.waitFor();
	    	} catch (Exception e) {
				LOGGER.error("Error reading piper output", e);
			}
		} catch (IOException e1) {
			LOGGER.error("Cannot launch the command: " + piperCmd, e1);
		}
		LOGGER.info("returning" + sndPath);
		return sndPath;
	}
	
	public void playSound(Path sndPath) {
		LOGGER.info("Trying to open the audio file " + sndPath);
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(sndPath.toFile()))){
	        SourceDataLine sourceDataLine = null;
	        AudioInputStream audioStream = null;
	        try {
	        	audioStream = AudioSystem.getAudioInputStream(inputStream);
	        	AudioFormat audioFormat = audioStream.getFormat();
	        	sourceDataLine = (SourceDataLine) AudioSystem.getSourceDataLine(audioFormat);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();
				int BUFFER_SIZE = 4096;
				byte[] bufferBytes = new byte[BUFFER_SIZE];
				int readBytes = -1;
				while ((readBytes = audioStream.read(bufferBytes)) != -1) {
				    sourceDataLine.write(bufferBytes, 0, readBytes);
				}
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
				LOGGER.error("An error has occured trying to read " + sndPath,e);
			} finally {
				if (sourceDataLine != null) {
					sourceDataLine.drain();
					sourceDataLine.close();
				}
				if (audioStream != null) {
					try {
						audioStream.close();
					} catch (IOException e) {
						LOGGER.error("An error has occured trying to close the audio" + sndPath,e);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("An error has occured trying to open " + sndPath, e);
		} 

         
	}

}
