package fr.lovc.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoRedoUtil {

	public static void setUpUndoRedo(JTextArea promptTA) {
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
	}
	
}
