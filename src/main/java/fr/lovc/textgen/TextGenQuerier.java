package fr.lovc.textgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.Main;
import fr.lovc.textgen.model.input.KoboldAiGenBody;
import fr.lovc.textgen.model.output.KoboldAiGenResponse;
import fr.lovc.tts.TextToSpeechReader;
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
	          .uri(URI.create("http://localhost:5001/api/v1/generate"))
	          .POST(BodyPublishers.ofString(bodyInString))
	          .timeout(Duration.ofSeconds(120))
	          .build();

	    HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
		    LOGGER.info("Response from KoboldAI API:" + response.body());
		    KoboldAiGenResponse koboldAiGenResponse = objectMapper.readValue(response.body(), KoboldAiGenResponse.class);
		    String responseTxtOnly = koboldAiGenResponse.getResults().get(0).getText();
		    // to remove eventual user's answer that the AI hallucinated in place of the user
		    if(responseTxtOnly.indexOf(promptManager.getUserName()+": ") > 0) {
		    	responseTxtOnly = responseTxtOnly.substring(0, responseTxtOnly.indexOf(promptManager.getUserName()+": ")).trim();
		    }
		    LOGGER.info("AI Response: \"" + responseTxtOnly + "\"");
		    promptManager.addInterlocutorLineToHistory(responseTxtOnly);
		    this.mainWindow.sendToTTS(responseTxtOnly);
		} catch (InterruptedException e) {
			LOGGER.info("Request to KoboldAI service was canceled.");
		} catch (IOException e) {
			LOGGER.error("Error connecting to KoboldAI service : \"" + bodyInString + "\"", e);
		}
		
		return null;
	}

}
