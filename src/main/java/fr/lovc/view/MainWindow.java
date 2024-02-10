package fr.lovc.view;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import fr.lovc.stt.SpeechToTextListener;
import fr.lovc.textgen.PromptManager;
import fr.lovc.textgen.TextGenQuerier;
import net.miginfocom.swing.MigLayout;

public class MainWindow implements PropertyChangeListener  {
	
	ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	Future res;
	
	Thread t;
	
	SwingWorker worker;
	
	SpeechToTextListener speechToTextListener;
	
	TextGenQuerier textGenQuerier;
	
	PromptManager promptManager;
	
	public MainWindow() {
	    JFrame jFrame=new JFrame();

	    
	    jFrame.setTitle("");
	    jFrame.setSize(600, 500);
	        jFrame.setLocationRelativeTo(null);

	    //Termine le programme si on ferme
	    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JTextArea promptTA = new JTextArea();
	    JScrollPane promptSP = new JScrollPane(promptTA);
	    JCheckBox listeningButton = new JCheckBox("Listen to me");
	    
	    listeningButton.addItemListener((itemEvent) -> {
	        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
	        	if (speechToTextListener == null) {
		        	speechToTextListener = new SpeechToTextListener();
		        	speechToTextListener.execute();
	        	}
	        } else {
	        	speechToTextListener.cancel(true);
	        	speechToTextListener = null;
	        }
	    });
	    
	    
	    JPanel panel = new JPanel(new MigLayout());
	    panel.add(new JLabel("User name:"));
	    panel.add(new JTextField("Bob"), "wmin 40%, wrap");
	    panel.add(new JLabel("Interlocutor name:"));
	    panel.add(new JTextField("Liza"), "wmin 40%, wrap");
	    panel.add(new JLabel("Prompt:"), "wrap");
	    panel.add(promptSP, "span 2, wmin 90%, height 100%, wrap");
	    panel.add(listeningButton, "wrap");

	    //on associe le JPanel à notre fenêtre
	    jFrame.setContentPane(panel);
	    
	    //on affiche la fenêtre (à la fin pour que son contenu soit rafraichit
	    jFrame.setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	

}
