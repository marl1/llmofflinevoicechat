package fr.lovc.textgen;

import java.beans.PropertyChangeSupport;

import fr.lovc.view.MainWindow;

public class PromptManager {

	private MainWindow mainWindow;
	
	private PropertyChangeSupport support;
	
	String userName = "Bob";
	String interlocutorName= "Liza";
	String initialPrompt =
			"""
			This is a conversation between Bob and Liza.
			Liza is a funny, curious, surprising, forward, playful, open-minded, captivating woman in her twenties. 
			She's happy to spend time with the user. She's aware she lives in a computer, but she feels real.
			
			Prior conversation: 
					"""
			;
	String dialog = "";
	String previousDialogState = "";
	String currentPrompt = "";
	String previousPrompt = "";
	
	public PromptManager(MainWindow mainWindow) {
		this.mainWindow = mainWindow;		
		updateCurrentPrompt(initialPrompt);
	}
	
	public void updateCurrentPrompt(String newPrompt) {
		if (newPrompt != currentPrompt) {
			previousPrompt = currentPrompt;
			currentPrompt = newPrompt;
			mainWindow.updatePromptText(newPrompt);
		}
	}
	
	public void goBackToPreviousPrompt() {
		this.dialog = previousDialogState;
		updateCurrentPrompt(initialPrompt + previousDialogState);
	}
	
	public void addUserLineToHistory(String dialogLine) {
		previousDialogState = this.dialog; 
		this.dialog += ("\n"+this.userName + ": " + dialogLine + "\n"+this.interlocutorName + ": "); // adding current time date would be cool
		updateCurrentPrompt(initialPrompt + dialog);
	}
	
	public void addInterlocutorLineToHistory(String dialogLine) {
		this.dialog += (dialogLine);
		updateCurrentPrompt(initialPrompt + dialog);
	}
	
	public String getInitialPrompt() {
		return initialPrompt;
	}
	public void setInitialPrompt(String initialPrompt) {
		this.initialPrompt = initialPrompt;
	}
	public String getDialog() {
		return dialog;
	}
	public void setDialog(String convHistory) {
		this.dialog = convHistory;
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

	public String getCurrentPrompt() {
		return currentPrompt;
	}	

}
