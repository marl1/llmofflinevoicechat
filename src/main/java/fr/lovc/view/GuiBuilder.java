package fr.lovc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

public class GuiBuilder {

	public static void setUpGUI(MainWindow mainWindow) {
	    mainWindow.jFrame.setTitle("");
	    mainWindow.jFrame.setSize(800, 900);
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
	    
	    //SHEET AREA
        mainWindow.promptSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainWindow.promptTA.setWrapStyleWord(true);
        mainWindow.promptTA.setLineWrap(true);
        mainWindow.promptTA.setFont(mainWindow.promptTA.getFont().deriveFont(14f));
	    UndoRedoUtil.setUpUndoRedo(mainWindow.promptTA);

		JPanel panel = new JPanel(new MigLayout());
	    
	    JLayeredPane characterPane = new JLayeredPane();
	    characterPane.setLayout(new MigLayout());
	    characterPane.setBorder(BorderFactory.createTitledBorder("Character sheet"));
	    panel.add(characterPane, "span 2 , width 100%, height 100%, wrap");
	    characterPane.add(new JLabel("Character sheet name:"));
	    characterPane.add(mainWindow.characterSheetName, "wmin 40%, wrap");
	    characterPane.add(new JLabel("User name:"));
	    characterPane.add(mainWindow.userName, "wmin 40%, wrap");
	    characterPane.add(new JLabel("Interlocutor name:"));
	    characterPane.add(mainWindow.botName, "wmin 40%, wrap");
	    characterPane.add(mainWindow.promptSP, "span 2, width 100%, height 100%, wrap");
	    characterPane.add(new JButton("Save"));
	    characterPane.add(mainWindow.loadCharacterSheetButton, "");
	    
	    // CONVERSATION
	    JLayeredPane conversationPane = new JLayeredPane();
        mainWindow.conversationSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainWindow.conversationTA.setWrapStyleWord(true);
        mainWindow.conversationTA.setLineWrap(true);
        mainWindow.conversationTA.setFont(mainWindow.conversationTA.getFont().deriveFont(14f));
	    conversationPane.setLayout(new MigLayout());
	    conversationPane.setBorder(BorderFactory.createTitledBorder(
	                                        "Conversation"));
	    panel.add(conversationPane, "span 2 , width 100%, height 30%");
	    conversationPane.add(mainWindow.conversationSP, "width 100%, height 100%");
		DefaultCaret conversationTACaret = (DefaultCaret)mainWindow.conversationTA.getCaret();
		conversationTACaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

	    // SHEET + CONVERSATION
	    JSplitPane mainSplitPane = new JSplitPane( 
	    JSplitPane.VERTICAL_SPLIT, characterPane, conversationPane);
	    mainSplitPane.setResizeWeight( 0.33 );
	    panel.add(mainSplitPane, "span 2 2, wrap, width 100%, height 80%");
	    
	    // QUERY
	    JLayeredPane queryPane = new JLayeredPane();
	    queryPane.setLayout(new MigLayout());
	    queryPane.setBorder(BorderFactory.createTitledBorder(
	                                        "Query"));
	    panel.add(queryPane, "newline, span 2 , width 100%, wrap");

	    queryPane.add(mainWindow.listeningButton, "wrap");
	    mainWindow.cancelButton.setText("Cancel query");
	    queryPane.add(mainWindow.cancelButton, "newline");
	    queryPane.add(mainWindow.sendQueryManuallyButton, "newline");

	    //on associe le JPanel à notre fenêtre
	    mainWindow.jFrame.setContentPane(panel);
	    
	    //on affiche la fenêtre (à la fin pour que son contenu soit rafraichit
	    mainWindow.jFrame.setVisible(true);
	}
	
}
