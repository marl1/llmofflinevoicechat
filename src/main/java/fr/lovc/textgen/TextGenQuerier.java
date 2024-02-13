package fr.lovc.textgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.Main;
import fr.lovc.textgen.model.input.KoboldAiGenBody;
import fr.lovc.textgen.model.output.see.KoboldAiData;
import fr.lovc.view.MainWindow;

public class TextGenQuerier extends SwingWorker<Void, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	PromptManager promptManager;
	MainWindow mainWindow;
	String query = "";
	String fullPrompt = "";

	public TextGenQuerier(MainWindow mainWindow, String query, String fullPrompt, PromptManager promptManager) {
		this.mainWindow = mainWindow;
		this.query = query;
		this.promptManager = promptManager;
		this.fullPrompt = fullPrompt;
	}
	
	@Override
	protected Void doInBackground() {
		if (query != null) { // distinguish between full prompt sending and query. Not good. To be refactored when I'll have individual dialog lines.
			promptManager.addUserLineToHistory(query);
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		KoboldAiGenBody koboldAiGenBody = new KoboldAiGenBody();
		koboldAiGenBody.setPrompt(promptManager.getFullPrompt());
		String bodyInString;
		try {
			bodyInString = objectMapper.writeValueAsString(koboldAiGenBody);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in creating the input body for KoboldAI.", e);
			throw new RuntimeException(e);
		}
		LOGGER.info("Sending to the AI : \"" + bodyInString + "\"");
		
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create("http://localhost:5001/api/extra/generate/stream"))
	          .POST(BodyPublishers.ofString(bodyInString))
	          .timeout(Duration.ofSeconds(120))
	          .build();

	    Stream<String> linesInResponse;
		try {
			mainWindow.resetProgressBar();
			StringBuilder linesConcatenation = new StringBuilder();
			linesInResponse = client.send(request, BodyHandlers.ofLines()).body();
			linesInResponse
			.filter(line -> line.startsWith("data: {"))
			.map((line) -> {
					line = line.substring("data: {".length()-1, line.length());
					try {
						LOGGER.info("Response from KoboldAI API:" + line);
						KoboldAiData koboldAiData = objectMapper.readValue(line, KoboldAiData.class);
						linesConcatenation.append(koboldAiData.token());
						this.mainWindow.progressBar.setValue(this.mainWindow.progressBar.getValue()+1);
						this.mainWindow.progressBar.setStringPainted(true);
						this.mainWindow.progressBar.setString(this.mainWindow.progressBar.getValue() + "/300"); //fixme
						LOGGER.info("Concatened response from KoboldAI API:" + linesConcatenation);

					} catch (JsonProcessingException e) {
						LOGGER.info("Couldn't read response from KoboldAI API:", e);
					}
				return linesConcatenation;
			})
			.anyMatch(sb -> {
				if(this.isCancelled()) {
					linesConcatenation.delete(0, linesConcatenation.length());
					return true;
				}
				// we stop if the bot starts to hallucinate the user response
				int hallucinatedUserAnswerPosition = linesConcatenation.indexOf("\n" + promptManager.getUserPrefix());
				hallucinatedUserAnswerPosition = hallucinatedUserAnswerPosition<0?linesConcatenation.indexOf("\n" + promptManager.getUserPrefix()+" : "):hallucinatedUserAnswerPosition;
			    if( hallucinatedUserAnswerPosition >= 0) { 
			    	 linesConcatenation.delete(hallucinatedUserAnswerPosition, linesConcatenation.length());
			    	 return true;
			    }
			    return false;
			});
			this.stopGen();
			mainWindow.resetProgressBar();
		    LOGGER.info("AI full response: \"" + linesConcatenation + "\"");
		    promptManager.addInterlocutorLineToHistory(linesConcatenation.toString());
		    if (linesConcatenation.toString() != "") {
		    	this.mainWindow.sendToTTS(linesConcatenation.toString());
		    }

		} catch (InterruptedException e) {
			LOGGER.info("Request to KoboldAI service was canceled.");
		} catch (IOException e) {
			LOGGER.error("Error connecting to KoboldAI service : \"" + bodyInString + "\"", e);
		}
		
		return null;
	}

	private void stopGen() {
		try {
			HttpClient.newHttpClient().send(
  	  HttpRequest.newBuilder()
			 .uri(URI.create("http://localhost:5001/api/extra/abort"))
			 .POST(BodyPublishers.ofString("{\"genkey\": \"lovc\"}"))
			 .timeout(Duration.ofSeconds(120))
			 .build()
			 , BodyHandlers.ofString()).body();
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Exception when trying to cancel the ongoing request", e);
		} 
	}
}
