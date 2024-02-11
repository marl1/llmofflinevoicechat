package fr.lovc.textgen;

import java.beans.PropertyChangeSupport;

import fr.lovc.view.MainWindow;

public class PromptManager {

	private MainWindow mainWindow;
	
	private PropertyChangeSupport support;
	
	String userName = "User";
	String botName= "Assistant";
	String initialPrompt =
			"""
			This is a conversation between the user and an helpful A.I. assistant.
            The assistant will help Bob with all his requests.
			
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
		this.dialog += this.userName + ": " + dialogLine + System.lineSeparator() + this.botName + ": "; // adding current time date would be cool
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		mainWindow.updateUserName(this.userName);
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
		mainWindow.updateCharacterName(this.botName);
	}

	public String getCurrentPrompt() {
		return currentPrompt;
	}	

}
