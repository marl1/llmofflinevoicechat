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
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

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
    JScrollPane promptSP = new JScrollPane(promptTA);
    JCheckBox listeningButton = new JCheckBox("Listen to me");
    JEditorPane conversationEP = new JEditorPane();
    JScrollPane conversationSP = new JScrollPane(conversationEP);
    JTextArea lastQueryTA = new JTextArea();
    JScrollPane lastQuerySP = new JScrollPane(lastQueryTA);
    JButton cancelButton = new JButton();
    JMenuItem mnuOpenFile = new JMenuItem( "Open character sheet..." );
    JButton loadCharacterSheetButton = new JButton("Load");
	
	public MainWindow() {
		promptManager = new PromptManager(this);

		GuiBuilder.setUpGUI(this);
	    
	    setUpListeners();
	}

	private void setUpListeners() {
		// listening checkbox
		addListenButtonEventListener();
		
		addCancelEventListener();
	    
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
	            	promptTA.setEditable(false); //we block the prompt...
	            	promptTA.setOpaque(false); //...from being edited
		        	speechToTextListener = new SpeechToTextListener(this); //create a new swing worker
		        	speechToTextListener.execute();
	        	}
	        } else {
	        	speechToTextListener.cancel(true); //if the swing worker responsible for listening has been cancelled...
	        	speechToTextListener = null; //...we delete it, cannot reuse swing anyway.
	        	promptTA.setEditable(true); //we also reactivate the prompt editing.
	        	promptTA.setOpaque(true);
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
	 * To cancel an ongoing query. For instance, if the user regret what he had just say, he can clic the cancel
	 * button to withdraw it from history.
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
	    		promptManager.goBackToPreviousPrompt();
	    	}
	    	if (this.textToSpeechReader != null) {
	    		cancelButton.setEnabled(false);
	    		textToSpeechReader.cancel(true);
	    		promptManager.goBackToPreviousPrompt();
	    	}
	    });
	}

	// called by the PromptManager when his prompt is updated
	public void updatePromptText(String newPrompt) {
		promptTA.setText(newPrompt);
	}
	
	// called after the query was deduced from the voice 
    public void sendToQuerier(String query) {
    	cancelButton.setEnabled(true);
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum());
    	System.out.println("La query est là !!" + query);
    	lastQueryTA.setText(query);
    	lastQuerySP.getVerticalScrollBar().setValue(lastQuerySP.getVerticalScrollBar().getMaximum());
    	textGenQuerier = new TextGenQuerier(this, query, promptManager);
    	textGenQuerier.execute();
    }
    
    public void sendToTTS(String botAnswerToReadOutLoud) {
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum()+10);
    	this.textToSpeechReader = new TextToSpeechReader(this, botAnswerToReadOutLoud);
    	textToSpeechReader.execute();
    }

}
