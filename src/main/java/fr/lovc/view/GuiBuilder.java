package fr.lovc.view;

import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

public class GuiBuilder {

	public static void setUpGUI(MainWindow mainWindow) {
	    mainWindow.jFrame.setTitle("");
	    mainWindow.jFrame.setSize(600, 500);
	        mainWindow.jFrame.setLocationRelativeTo(null);

	    // Terminate app if this window is closed
	    mainWindow.jFrame.setDefaultCloseOperation(mainWindow.jFrame.EXIT_ON_CLOSE);
	    
	    // menu bar
	    JMenuBar menuBar = new JMenuBar();
	    JMenu mnuFile = new JMenu( "File" );
        mnuFile.setMnemonic( 'F' );
        
        mainWindow.mnuOpenFile.setMnemonic( 'O' );
        mainWindow.mnuOpenFile.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK) );
        mnuFile.add(mainWindow.mnuOpenFile);
        menuBar.add(mnuFile);
        mainWindow.jFrame.setJMenuBar(menuBar);
	    
	    
	    
	    //PROMPT AREA
        mainWindow.promptSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainWindow.promptTA.setWrapStyleWord(true);
        mainWindow.promptTA.setLineWrap(true);
	    UndoRedoUtil.setUpUndoRedo(mainWindow.promptTA);
		DefaultCaret caret = (DefaultCaret)mainWindow.promptTA.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		mainWindow.lastQuerySP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainWindow.lastQueryTA.setWrapStyleWord(true);
		mainWindow.lastQueryTA.setLineWrap(true);
		mainWindow.lastQueryTA.setEditable(false);
		mainWindow.lastQueryTA.setFocusable(true);
		mainWindow.lastQueryTA.setOpaque(false);
	   
	   	    
	    JPanel panel = new JPanel(new MigLayout());
	    panel.add(new JLabel("User name:"));
	    panel.add(new JTextField("Bob"), "wmin 40%, wrap");
	    panel.add(new JLabel("Interlocutor name:"));
	    panel.add(new JTextField("Liza"), "wmin 40%, wrap");
	    panel.add(new JLabel("Prompt:"), "wrap");
	    panel.add(mainWindow.promptSP, "span 3, wmin 90%, height 100%, wrap");
	    panel.add(mainWindow.listeningButton, "wrap");
	    panel.add(new JLabel("Last query:"),"wrap");
	    panel.add(mainWindow.lastQuerySP, "span 2, wmin 60%, height 60%");
	    panel.add(mainWindow.cancelButton, "span 2, wrap");
	    

	    //on associe le JPanel à notre fenêtre
	    mainWindow.jFrame.setContentPane(panel);
	    
	    //on affiche la fenêtre (à la fin pour que son contenu soit rafraichit
	    mainWindow.jFrame.setVisible(true);
	}
	
}
