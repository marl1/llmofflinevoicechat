package fr.lovc.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow {
	
	public MainWindow() {
	    JFrame jFrame=new JFrame();

	    
	    jFrame.setTitle("");
	    jFrame.setSize(400, 300);
	        jFrame.setLocationRelativeTo(null);

	    //Termine le programme si on ferme
	    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	        //Il faut créer un JPanelpour manipuler des éléments à l'intérieur
	    JPanel panneau=new JPanel();

	    //on associe le JPanel à notre fenêtre
	    jFrame.setContentPane(panneau);

	    //on affiche la fenêtre (à la fin pour que son contenu soit rafraichit
	    jFrame.setVisible(true);
	}

}
