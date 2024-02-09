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
import fr.lovc.textgen.model.KoboldAiGenBody;

public class TextGenQuerier {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);	

	public void query(String query) throws IOException, InterruptedException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		KoboldAiGenBody koboldAiGenBody = new KoboldAiGenBody();
		koboldAiGenBody.setPrompt(query);
		String bodyInString = objectMapper.writeValueAsString(koboldAiGenBody);
		
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create("http://localhost:5001/api/v1/generate"))
	          .POST(BodyPublishers.ofString(bodyInString))
	          .build();

	    HttpResponse<String> response =
	          client.send(request, BodyHandlers.ofString());

	    System.out.println(response.body());
	}
}
