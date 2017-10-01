package application;

import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HTMLManual extends Stage {
	WebView webView = new WebView();
	
	public HTMLManual() {
		super();
		
		//so wird eine Seite aus Web geladen
		//webView.getEngine().load("http://www.hs-augsburg.de");
		
		// so wird eine lokale Seite geladen
		URL manual = getClass().getResource("/resources/text.html");
		webView.getEngine().load(manual.toExternalForm());


		initModality(Modality.NONE);
		
		Scene scene = new Scene(webView, 600, 400);
		this.setTitle("User Manual");
		this.setScene(scene);
	}
     
}
