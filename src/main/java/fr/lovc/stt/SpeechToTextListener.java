package fr.lovc.stt;

import java.io.IOException;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.stt.model.VoskText;
import fr.lovc.textgen.TextGenQuerier;

public class SpeechToTextListener {
	TextGenQuerier textGenQuerier = new TextGenQuerier();
	public SpeechToTextListener() throws IOException, LineUnavailableException, InterruptedException {
	    LibVosk.setLogLevel(LogLevel.DEBUG);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);
	    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	    TargetDataLine microphone;
	    Model model = new Model(Paths.get("").toAbsolutePath().resolve("model/vosk-model-small-en-us-0.15").toAbsolutePath().toString());
	    Recognizer recognizer = new Recognizer(model, 16000);

	    microphone = (TargetDataLine)AudioSystem.getLine(info);
	    microphone.open(format);
	    microphone.start();

	    int numBytesRead;
	    int CHUNK_SIZE = 4096;
	    int bytesRead = 0;

	    byte[] b = new byte[4096];

	    while(bytesRead<=100000000){
	        numBytesRead = microphone.read(b, 0, CHUNK_SIZE);

	        bytesRead += numBytesRead;

	        if(recognizer.acceptWaveForm(b, numBytesRead)){
	            System.out.println(recognizer.getResult());
	            //textGenQuerier.query(objectMapper.readValue(recognizer.getResult(), VoskText.class).getText());
	        }
	    }

	    System.out.println(recognizer.getFinalResult());
	    
	    microphone.close();
	}
}
