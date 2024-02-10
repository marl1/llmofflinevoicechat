package fr.lovc.textgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.Main;
import fr.lovc.textgen.model.input.KoboldAiGenBody;
import fr.lovc.textgen.model.output.KoboldAiGenResponse;
import fr.lovc.tts.TextToSpeechReader;

public class TextGenQuerier {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	PromptBuilder promptBuilder = new PromptBuilder();


	public void query(String query) throws IOException, InterruptedException {
		
		promptBuilder.addUserLineToHistory(query);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		KoboldAiGenBody koboldAiGenBody = new KoboldAiGenBody();
		koboldAiGenBody.setPrompt(promptBuilder.build());
		String bodyInString = objectMapper.writeValueAsString(koboldAiGenBody);
		LOGGER.info("Sending to the AI : \"" + bodyInString + "\"");
		
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create("http://localhost:5001/api/v1/generate"))
	          .POST(BodyPublishers.ofString(bodyInString))
	          .build();

	    HttpResponse<String> response =
	          client.send(request, BodyHandlers.ofString());

	    LOGGER.info("Response from KoboldAI API:" + response.body());
	    KoboldAiGenResponse koboldAiGenResponse = objectMapper.readValue(response.body(), KoboldAiGenResponse.class);
	    String responseTxtOnly = koboldAiGenResponse.getResults().get(0).getText();
	    // to remove eventual user's answer that the AI hallucinated in place of the user
	    if(responseTxtOnly.indexOf(promptBuilder.getUserName()+": ") > 0) {
	    	responseTxtOnly = responseTxtOnly.substring(0, responseTxtOnly.indexOf(promptBuilder.getUserName()+": ")).trim();
	    }
	    LOGGER.info("AI Response: \"" + responseTxtOnly + "\"");
	    promptBuilder.addInterlocutorLineToHistory(responseTxtOnly);
	    new TextToSpeechReader().read(responseTxtOnly);
	}
}
