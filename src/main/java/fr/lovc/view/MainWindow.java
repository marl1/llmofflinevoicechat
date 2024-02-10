package fr.lovc.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import fr.lovc.stt.SpeechToTextListener;
import fr.lovc.textgen.PromptManager;
import fr.lovc.textgen.TextGenQuerier;
import fr.lovc.tts.TextToSpeechReader;
import net.miginfocom.swing.MigLayout;

public class MainWindow {
		
	PromptManager promptManager;

	SpeechToTextListener speechToTextListener;
	
	TextGenQuerier textGenQuerier;
	
	TextToSpeechReader textToSpeechReader;
	
    JTextArea promptTA = new JTextArea();
    JScrollPane promptSP = new JScrollPane(promptTA);
    JCheckBox listeningButton = new JCheckBox("Listen to me");
    JTextArea lastQueryTA = new JTextArea();
    JScrollPane lastQuerySP = new JScrollPane(lastQueryTA);
    JButton cancelButton = new JButton("Cancel query");
	
	public MainWindow() {
		setUpGUI();
	    
	    setUpListeners();
	}

	private void setUpListeners() {
		listeningButton.addItemListener((itemEvent) -> {
	        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
	        	if (speechToTextListener == null) {
		        	speechToTextListener = new SpeechToTextListener(this);
		        	speechToTextListener.execute();
	        	}
	        } else {
	        	speechToTextListener.cancel(true);
	        	speechToTextListener = null;
	        	promptTA.setEditable(true);
	        	promptTA.setOpaque(true);
		    	if (this.textGenQuerier != null) {
		    		textGenQuerier.cancel(true);
		    	}
		    	if (this.textToSpeechReader != null) {
		    		textToSpeechReader.cancel(true);
		    	}
	        }
	    });
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

	private void setUpGUI() {
		promptManager = new PromptManager(this);
	    JFrame jFrame=new JFrame();
	    jFrame.setTitle("");
	    jFrame.setSize(600, 500);
	        jFrame.setLocationRelativeTo(null);

	    // Terminate app if this window is closed
	    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    promptSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    promptTA.setWrapStyleWord(true);
	    promptTA.setLineWrap(true);
	    UndoManager undoManager = new UndoManager();
	    promptTA.getDocument().addUndoableEditListener(undoManager);
	    
	    InputMap im = promptTA.getInputMap(JComponent.WHEN_FOCUSED);
	    ActionMap am = promptTA.getActionMap();

	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

	    am.put("Undo", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                if (undoManager.canUndo()) {
	                    undoManager.undo();
	                }
	            } catch (CannotUndoException exp) {
	                exp.printStackTrace();
	            }
	        }});
	    am.put("Redo", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                if (undoManager.canRedo()) {
	                    undoManager.redo();
	                }
	            } catch (CannotUndoException exp) {
	                exp.printStackTrace();
	            }
	        }});


	    lastQuerySP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    lastQueryTA.setWrapStyleWord(true);
        lastQueryTA.setLineWrap(true);
        lastQueryTA.setEditable(false);
        lastQueryTA.setFocusable(true);
        lastQueryTA.setOpaque(false);
	   
	   	    
	    JPanel panel = new JPanel(new MigLayout());
	    panel.add(new JLabel("User name:"));
	    panel.add(new JTextField("Bob"), "wmin 40%, wrap");
	    panel.add(new JLabel("Interlocutor name:"));
	    panel.add(new JTextField("Liza"), "wmin 40%, wrap");
	    panel.add(new JLabel("Prompt:"), "wrap");
	    panel.add(promptSP, "span 3, wmin 90%, height 100%, wrap");
	    panel.add(listeningButton, "wrap");
	    panel.add(new JLabel("Last query:"),"wrap");
	    panel.add(lastQuerySP, "span 2, wmin 60%, height 60%");
	    panel.add(cancelButton, "span 2, wrap");
	    

	    //on associe le JPanel à notre fenêtre
	    jFrame.setContentPane(panel);
	    
	    //on affiche la fenêtre (à la fin pour que son contenu soit rafraichit
	    jFrame.setVisible(true);
	}

	// called by the PromptManager when his prompt is updated
	public void updatePromptText(String newPrompt) {
		promptTA.setText(newPrompt);
	}
	
	// called after the query was deduced from the voice 
    public void sendToQuerier(String query) {
    	cancelButton.setEnabled(true);
    	promptTA.setEditable(false);
    	promptTA.setOpaque(false);
    	promptSP.getVerticalScrollBar().setValue(promptSP.getVerticalScrollBar().getMaximum());
    	System.out.println("La query est là !!" + query);
    	lastQueryTA.setText(query);
    	lastQuerySP.getVerticalScrollBar().setValue(lastQuerySP.getVerticalScrollBar().getMaximum());
    	textGenQuerier = new TextGenQuerier(this, query, promptManager);
    	textGenQuerier.execute();
    }
    
    public void sendToTTS(String botAnswerToReadOutLoud) {
    	this.textToSpeechReader = new TextToSpeechReader(this, botAnswerToReadOutLoud);
    	textToSpeechReader.execute();
    }

}
