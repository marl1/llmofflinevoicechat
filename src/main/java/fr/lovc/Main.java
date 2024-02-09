package fr.lovc;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lovc.stt.SpeechToTextListener;
import fr.lovc.view.MainWindow;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);	

	public static void main(String[] args) throws IOException, LineUnavailableException, InterruptedException {

		LOGGER.info("App starting.");
		new MainWindow();
		System.out.println("lol");
		new SpeechToTextListener();
	}
}
