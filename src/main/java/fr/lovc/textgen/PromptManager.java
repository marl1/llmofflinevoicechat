package fr.lovc.textgen;

import java.beans.PropertyChangeSupport;

import fr.lovc.view.MainWindow;

public class PromptManager {

	private MainWindow mainWindow;
	
	private PropertyChangeSupport support;
	
	String userPrefix = "User";
	String botPrefix= "Assistant";
	String initialPrompt =
			"""
			This is a conversation between the user and an helpful A.I. assistant.
            The assistant will help the user with all his requests.
			
			Prior conversation: 
					""";
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
	
	public String getFullPrompt() {
			return currentPrompt + dialog;
	}
	
	public void goBackToPreviousDialogLine() {
		this.dialog = previousDialogState;
		mainWindow.updateConversationText(dialog);
	}
	
	public void addUserLineToHistory(String dialogLine) {
		previousDialogState = this.dialog;
		if (!this.dialog.isBlank()) { this.dialog += System.lineSeparator(); }
		this.dialog += this.userPrefix + dialogLine + System.lineSeparator() + this.botPrefix; // adding current time date would be cool
		mainWindow.updateConversationText(dialog);

	}
	
	public void addInterlocutorLineToHistory(String dialogLine) {
		this.dialog += (dialogLine);
		mainWindow.updateConversationText(dialog);
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
	public void setDialog(String dialog) {
		this.dialog = dialog;
	}

	public String getUserPrefix() {
		return userPrefix;
	}

	public void setUserPrefix(String userName) {
		this.userPrefix = userName;
		mainWindow.updateUserName(this.userPrefix);
	}

	public String getBotPrefix() {
		return botPrefix;
	}

	public void setBotPrefix(String botName) {
		this.botPrefix = botName;
		mainWindow.updateCharacterName(this.botPrefix);
	}

	public String getCurrentPrompt() {
		return currentPrompt;
	}	

}
