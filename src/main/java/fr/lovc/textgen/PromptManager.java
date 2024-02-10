package fr.lovc.textgen;

import java.beans.PropertyChangeSupport;

public class PromptManager {

	private PropertyChangeSupport support;
	
	String userName = "Bob";
	String interlocutorName= "Liza";
	String initialPrompt =
			"""
			This is a conversation between Bob and Liza.
			Liza is a funny, flirty, curious, surprising, forward, playful, open-minded, captivating woman in her twenties. 
			She's happy to spend time with the user. She's aware she lives in a computer, but she feels real.
			
			Prior conversation: 
					"""
			;
	String convHistory = "";
	String currentPrompt = initialPrompt;
	
	public PromptManager() {
		support = new PropertyChangeSupport(this);
	}
	
	public String updatePrompt() {
		String newPrompt = initialPrompt + convHistory;
		support.firePropertyChange("prompt", currentPrompt, newPrompt);
		this.currentPrompt = newPrompt;
		return newPrompt;
	}
	
	public String addUserLineToHistory(String txt) {
		return this.convHistory += ("\n"+this.userName + ": " + txt + "\n"+this.interlocutorName + ": "); // adding current time date would be cool
	}
	
	public String addInterlocutorLineToHistory(String txt) {
		return this.convHistory += (txt);
	}
	
	
	public String getInitialPrompt() {
		return initialPrompt;
	}
	public void setInitialPrompt(String initialPrompt) {
		this.initialPrompt = initialPrompt;
	}
	public String getConvHistory() {
		return convHistory;
	}
	public void setConvHistory(String convHistory) {
		this.convHistory = convHistory;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInterlocutor() {
		return interlocutorName;
	}

	public void setInterlocutor(String interlocutor) {
		this.interlocutorName = interlocutor;
	}	
	
}
