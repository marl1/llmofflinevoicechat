package fr.lovc.internaldata;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lovc.internaldata.model.CharacterSheet;

public class CharacterSheetLoader {

	public static CharacterSheet load(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		CharacterSheet characterSheet = objectMapper.readValue(json, CharacterSheet.class);
		
		return characterSheet;
	}
}
