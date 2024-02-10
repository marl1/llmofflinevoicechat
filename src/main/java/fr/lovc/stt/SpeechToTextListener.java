package fr.lovc.stt;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.stt.model.VoskText;
import fr.lovc.textgen.TextGenQuerier;
import fr.lovc.view.MainWindow;

public class SpeechToTextListener extends SwingWorker<Void, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpeechToTextListener.class);	

	TextGenQuerier textGenQuerier;
	MainWindow mainWindow;
	
	public SpeechToTextListener(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	protected Void doInBackground() throws Exception {
	    LibVosk.setLogLevel(LogLevel.DEBUG);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);
	    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	    TargetDataLine microphone = null;
	    Model model;
		try {
			model = new Model(Paths.get("").toAbsolutePath().resolve("model/vosk-model-small-en-us-0.15").toAbsolutePath().toString());
		    try (Recognizer recognizer = new Recognizer(model, 16000)) {
				microphone = (TargetDataLine)AudioSystem.getLine(info);
				microphone.open(format);
				microphone.start();
	
				int numBytesRead;
				int CHUNK_SIZE = 4096;
				int bytesRead = 0;
	
				byte[] b = new byte[4096];
	
				while(!isCancelled()){
				    numBytesRead = microphone.read(b, 0, CHUNK_SIZE);
	
				    bytesRead += numBytesRead;
	
				    if(recognizer.acceptWaveForm(b, numBytesRead)){
				    	String query = objectMapper.readValue(recognizer.getResult(), VoskText.class).getText();
				    	LOGGER.info("Detected: \"" + query + "\"");
				    	if (!query.isEmpty()) {
				    		this.mainWindow.sendToQuerier(query);
				    	}
				    }
				}
	
			} catch (LineUnavailableException e) {
				LOGGER.error("Cannot use microphone.", e);
			} finally {
				if (microphone != null) {
					microphone.close();
				}
			}
		} catch (IOException e) {
	    	LOGGER.error("Cannot find speech model.", e);
		}
		return null;
	}

}
