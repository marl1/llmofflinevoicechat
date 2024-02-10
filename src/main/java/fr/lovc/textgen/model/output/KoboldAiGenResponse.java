package fr.lovc.textgen.model.output;

import java.util.List;

public class KoboldAiGenResponse {
	private List<KoboldAiGenResult> results ;

	public List<KoboldAiGenResult> getResults() {
		return results;
	}

	public void setResults(List<KoboldAiGenResult> results) {
		this.results = results;
	}
	
}
