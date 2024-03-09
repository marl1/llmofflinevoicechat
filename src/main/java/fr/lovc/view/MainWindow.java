package fr.lovc.view;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lovc.internaldata.CharacterSheetLoader;
import fr.lovc.internaldata.model.CharacterSheet;
import fr.lovc.stt.SpeechToTextListener;
import fr.lovc.textgen.PromptManager;
import fr.lovc.textgen.TextGenQuerier;
import fr.lovc.tts.TextToSpeechReader;

/**
 * @author Aka
 *
 */
public class MainWindow {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);	

	PromptManager promptManager;

	SpeechToTextListener speechToTextListener;
	
	TextGenQuerier textGenQuerier;
	
	TextToSpeechReader textToSpeechReader;
	
    JFrame jFrame=new JFrame();
    JTextArea promptTA = new JTextArea();
    JTextField characterSheetName = new JTextField("Todo");
    JTextField userPrefix = new JTextField("User");
    JTextField botPrefix = new JTextField("Bot");
    JTextField botVoice = new JTextField("en_US-amy-low.onnx");
    JScrollPane promptSP = new JScrollPane(promptTA);
    JCheckBox listeningButton = new JCheckBox("Listen to me");
    JTextArea conversationTA = new JTextArea();
    JScrollPane conversationSP = new JScrollPane(conversationTA);
    JButton cancelButton = new JButton();
    JMenuItem mnuOpenFile = new JMenuItem( "Open character sheet..." );
    JButton loadCharacterSheetButton = new JButton("Load");
    JButton loadConvButton = new JButton("Load");
    JButton saveConvButton = new JButton("Save");
    JButton sendQueryManuallyButton = new JButton("Send query manually");
    public JProgressBar progressBar = new JProgressBar();

	public MainWindow() {
		promptManager = new PromptManager(this);

		GuiBuilder.setUpGUI(this);
	    
	    setUpListeners();
	}

	private void setUpListeners() {
		// listening checkbox
		addListenButtonEventListener();
		
		addCancelEventListener();
	    
		sendQueryManuallyButton.addActionListener((actionEvent) -> { 
				this.promptManager.updateCurrentPrompt(promptTA.getText()); //we update with a new prompt
        		this.promptManager.setDialog(conversationTA.getText());
        		this.sendFullPromptToQuerier(promptManager.getFullPrompt());
				});

	    // open file
	    ActionListener loadAction = (actionEvent) -> {
	    	final JFileChooser fc = new JFileChooser(Paths.get("").toAbsolutePath().toString());
	    	int returnVal = fc.showOpenDialog(jFrame);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            CharacterSheet characterSheet = null;
	        	try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));) {
		        	String line = "";
		        	String textFileContent = "";
		        	while ((line = bufferedReader.readLine()) != null) {
		        		textFileContent += line + System.lineSeparator();
		        	}
		        	characterSheet = CharacterSheetLoader.load(textFileContent);
	            } catch (IOException e) {
	            	LOGGER.error(String.format("Cannot read file \"%s\"", file), e);
				}
	        	if (characterSheet != null) {
	        		this.promptManager.updateCurrentPrompt(characterSheet.description());
	        		this.promptManager.setUserPrefix(characterSheet.userPrefix());
	        		this.promptManager.setBotPrefix(characterSheet.botPrefix());
	        		this.botVoice.setText(characterSheet.botVoice());
	        	}
	        }
	    };
	    mnuOpenFile.addActionListener(loadAction);
	    loadCharacterSheetButton.addActionListener(loadAction);
	}

	/**
	 * A method to activate/deactivate the microphone speech recgonition.
	 * Should also keep from editing the prompt because it is send at the end of this method
	 * and we don't want a mismacth between what is showed, what is sent.
	 */
	private void addListenButtonEventListener() {
		listeningButton.addItemListener((itemEvent) -> {
	        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
	        	if (speechToTextListener == null) {
	        		// if we check the box and don't have any tts listener already
	        		this.promptManager.updateCurrentPrompt(promptTA.getText()); //we update with a new prompt
	        		this.promptManager.setDialog(conversationTA.getText());
	            	promptTA.setEditable(false); //we block the prompt...
	            	promptTA.setOpaque(false); //...from being edited
	            	conversationTA.setEditable(false); //same for the conversation
	            	conversationTA.setOpaque(false);
		        	speechToTextListener = new SpeechToTextListener(this); //create a new swing worker
		        	speechToTextListener.execute();
	        	}
	        } else {
	        	speechToTextListener.cancel(true); //if the swing worker responsible for listening has been cancelled...
	        	speechToTextListener = null; //...we delete it, cannot reuse swing anyway.
	        	promptTA.setEditable(true); //we also reactivate the prompt editing.
	        	promptTA.setOpaque(true);
            	conversationTA.setEditable(true); //same for the conversation
            	conversationTA.setOpaque(true);
		    	if (this.textGenQuerier != null) {
		    		textGenQuerier.cancel(true); //we also cancel other swing workers such as text query...
		    	}
		    	if (this.textToSpeechReader != null) {
		    		textToSpeechReader.cancel(true); //...and sound reading.
		    	}
	        }
	    });
	}

	
	/**
	 * To cancel an ongoing query. For instance, if the user regret what he had just say, he can click the
	 * cancel button to withdraw it from history.
	 * It should also cancel the query and sound player.
	 */
	private void addCancelEventListener() {
		// cancel query button
	    cancelButton.setEnabled(false);
	    cancelButton.addActionListener((actionEvent) -> 
	    {
	    	if (this.textGenQuerier != null) {
	    		cancelButton.setEnabled(false);
	    		textGenQuerier.cancel(true);
	    		promptManager.goBackToPreviousDialogLine();
	    	}
	    	if (this.textToSpeechReader != null) {
	    		cancelButton.setEnabled(false);
	    		textToSpeechReader.cancel(true);
	    		promptManager.goBackToPreviousDialogLine();
	    	}
	    });
	}
	
	
	/**
	 * To send the query to KoboldCpp backend. Called just after the query string was deduced 
	 * from the voice.
	 */
    public void sendQueryToQuerier(String query) {
    	cancelButton.setEnabled(true);
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum());
    	System.out.println("La query est là !!" + query);
    	if (this.textGenQuerier == null || this.textGenQuerier.isCancelled() ||  this.textGenQuerier.isDone()) {
    		textGenQuerier = new TextGenQuerier(this, query, null, promptManager);
    		textGenQuerier.execute();
    	}
    }
    
	/**
	 * To send a FULL PROMPT to to KoboldCpp backend. To be refactored, a bit ridiculous to have
	 * two methods so similar. The have sendQueryToQuerier will suffice when I'll have individual dialog lines.
	 */
    public void sendFullPromptToQuerier(String fullPrompt) {
    	cancelButton.setEnabled(true);
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum());
    	if (this.textGenQuerier == null || this.textGenQuerier.isCancelled() ||  this.textGenQuerier.isDone()) {
    		textGenQuerier = new TextGenQuerier(this, null, fullPrompt, promptManager);
    		textGenQuerier.execute();
    	}
    }
    public void sendToTTS(String botAnswerToReadOutLoud) {
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum()+10);
    	this.textToSpeechReader = new TextToSpeechReader(this, botAnswerToReadOutLoud, this.botVoice.getText());
    	textToSpeechReader.execute();
    }

	// called by the PromptManager when his prompt is updated
	public void updatePromptText(String newPrompt) {
		promptTA.setText(newPrompt);
	}
	
	// called by the PromptManager when his prompt is updated
	public void updateUserName(String newName) {
		userPrefix.setText(newName);
	}
	
	// called by the PromptManager when his prompt is updated
	public void updateCharacterName(String newName) {
		botPrefix.setText(newName);
	}
	
	// called by the PromptManager whenthe dialog is updated
	public void updateConversationText(String newConversationLine) {
		conversationTA.setText(newConversationLine);
    	conversationSP.getVerticalScrollBar().setValue(conversationSP.getVerticalScrollBar().getMaximum());
	}
	
	public void resetProgressBar() {
		this.progressBar.setMaximum(300); // todo fixme
		this.progressBar.setMinimum(0);
		this.progressBar.setValue(0);
		this.progressBar.setString(" ");
	}

}
