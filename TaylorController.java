package application.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import application.HTMLManual;
import application.model.Funktion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TaylorController {

	/*** ATTRIBUTE ***/
	
	@FXML private Canvas canvas;
	@FXML private ToolBar toolBar;
	@FXML private ComboBox<String> funktionBox;
	@FXML private Label funktionLabel;
	@FXML private Label punktLabel;
	@FXML private Label gradLabel;
	@FXML private Label xStellenLabel;
	@FXML private Label fehlerLabel;
	@FXML private Label skalFunkLabel;
	@FXML private Label skalArgLabel;
	@FXML private Label plusArgLabel;
	@FXML private Label plusFunkLabel;
	@FXML private TextField skalFunkField;
	@FXML private TextField skalArgField;
	@FXML private TextField plusArgField;
	@FXML private TextField plusFunkField;
	@FXML private TextField punktField;
	@FXML private TextField xStellenField;
	@FXML private TextField fehlerField;
	@FXML private Slider gradSlider;
	@FXML private Button okButton;	
	@FXML private Button helpButton;
	@FXML private Button saveButton;
	
	private GraphicsContext gc;
	private final static Color HINTERGRUND = Color.WHITE;
	// zum Speichern
	private Stage primaryStage;
	
	
	// initialisiere Canvas mit Koordinatensystem im Ursprung
	@FXML private void initialize() {
		
		// initialisiere Funktionenliste
		List<String> funktionenListe = new ArrayList<String>();
    	
    	funktionenListe.add("sqrt");
    	funktionenListe.add("exp");
    	funktionenListe.add("ln");
    	funktionenListe.add("sin");
		funktionenListe.add("cos");
		
		ObservableList<String> obList = FXCollections.observableList(funktionenListe);
		funktionBox.setItems(obList);
		
		// lege Hintergrund und KoordSystem
	    zeichneKoord(0, 0);	
	    
	    // Default-Werte
	    skalFunkField.setText("1");
	    skalArgField.setText("1");
	    plusArgField.setText("0");
	    plusFunkField.setText("0");
	    punktField.setText("0");
	    xStellenField.setText("0");
	    
	    // Fehler-Feld disabled
	    fehlerField.setDisable(true);
	}
	
	
	// zeichne Koordinatensystem
	public void zeichneKoord (double xMitte, double yMitte) {
		
		// initialisiere Canvas
		gc = canvas.getGraphicsContext2D();
		gc.setFill(HINTERGRUND);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setStroke(Color.BLACK);
		
		/*** Koordinatenebene ***/
		// x-Richtung
		// Breite Fenster = 700 (es werden aber Achsen bis 800 gemalt)
		for (double x = 0; x < 800; x += 50) {
			// Hauptachse wird dicker gezeichnet
			if ( x == 350 - 50 * (int)xMitte )  
				gc.setLineWidth(2.3); 			
			else 
				gc.setLineWidth(0.3); 		
				
			// ganze x-Werte auf der Achse
			gc.strokeText( String.format("%.0f", x/50 - 7 + (int)xMitte),  x - 50 * (xMitte - (int)xMitte) + 3, 495);
			
			// vertikales Gitter
            gc.strokeLine(x - 50 * (xMitte - (int)xMitte), 0,
      			  		  x - 50 * (xMitte - (int)xMitte), 500);
		}   
		
		// y-Richtung
		// Hoehe Fenster = 600 (es werden aber Achsen bis 700 gemalt)
		for (double y = 0; y < 600; y += 50) {	
			// Hauptachse wird dicker gezeichnet
			if ( y == 250 + 50 * (int)yMitte )  
				gc.setLineWidth(2.3);
			else 
				gc.setLineWidth(0.3);
				
			// ganze y-Werte auf der Achse
			if ( y < 500 )
			gc.strokeText(String.format("%.0f", 5 - y/50 + (int)yMitte), 5, y + 50 * (yMitte - (int)yMitte) - 3);
			
			// horizontales Gitter
            gc.strokeLine(0, y + 50 * (yMitte - (int)yMitte), 
            			700, y + 50 * (yMitte - (int)yMitte));
        }
		
		// verdeutliche Entwicklungspunkt
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(0.7);
		gc.strokeLine(350, 250, 350, 250 + 50 * yMitte);
		gc.strokeLine(350, 250, 350 - 50 * xMitte, 250);
		
		
		// schreibe Koordinaten des Entwicklungspunkts hin - Ausnahme: (0, 0)
		if ( !(xMitte == 0 && yMitte == 0) )
			gc.strokeText("( " + String.format("%.3f", xMitte) + ", " + String.format("%.3f", yMitte) + " )", 355, 255);
		
		
		// Richtungspfeile der Hauptachsen
		gc.setFill(Color.BLACK);
		// x-Pfeil
		gc.fillPolygon(new double[]{685, 700, 685}, 
		        	   new double[]{240 + 50 * yMitte, 250 + 50 * yMitte, 260 + 50 * yMitte}, 3);
		
		// y-Pfeil
		gc.fillPolygon(new double[]{350 - 50 * xMitte, 360 - 50 * xMitte, 340 - 50 * xMitte}, 
	        	   	   new double[]{0, 15, 15}, 3);
	}
	
	
	// zeichne Funktion und approximierendes Polynom beim Klicken von OK
	@FXML public void handleOKButton () {
		
		// finde die ausgewaehlte Funktion
		String comboFunktion = funktionBox.getSelectionModel().getSelectedItem();
		
		// Skalierung des Funktionswerts
		Double skalFunk = Double.parseDouble(skalFunkField.getText());
		
		// Skalierung des Arguments
		Double skalArg = Double.parseDouble(skalArgField.getText());
		
		// Verschiebung im Argument
		Double plusArg = Double.parseDouble(plusArgField.getText());
		
		// Verschiebung des Funktionswerts
		Double plusFunk = Double.parseDouble(plusFunkField.getText());
		
		// erzeuge Objekt vom Typ Funktion
		Funktion myFunction = new Funktion(comboFunktion, gc, skalFunk, skalArg, plusArg, plusFunk);
		
		// finde Grad des Polynoms
		Integer grad = (int)gradSlider.getValue();
		
		// finde Entwicklungspunkt
		Double entwPunkt = Double.parseDouble(punktField.getText()); 
		
		// finde x-Stelle fuer den Fehler
		Double xFehler = Double.parseDouble(xStellenField.getText()); 
		
		
		// Fehlermeldungen für unzulässige Eingaben
		if ( funktionBox.getSelectionModel().isEmpty() ) 
			fehlerAlert("Fehler", "Keine Funktion", "Bitte wählen Sie eine Funktion aus!");
		
		else if ( comboFunktion == "sqrt" && (entwPunkt < 0 || xFehler < 0 )) 						
			fehlerAlert("Fehler", "Ungültiger Definitionsbereich", "Die Wurzelfunktion nimmt keine negativen Argumente an!");
				
		else if ( comboFunktion == "sqrt" && entwPunkt == 0 )
			fehlerAlert("Fehler", "Nicht differenzierbar", "Die Wurzelfunktion kann in 0 nicht approximiert werden!");
			
		else if ( comboFunktion == "ln"   && (entwPunkt <= 0 || xFehler < 0 )) 
			fehlerAlert("Fehler", "Ungültiger Definitionsbereich", "Der Logarithmus nimmt nur positive Argumente an!");
		
		else {
			// verschiebe Koordinatensystem in den Entwicklungspunkt
			zeichneKoord(entwPunkt, myFunction.bewerteFunktion(entwPunkt));
			
			// Zeichne Funktion und Polynom
			for (double i = -7 + entwPunkt; i < 7 + entwPunkt; i += 0.001) {	
				
				// Schrittweite fuer die Zeichnung eines Graphs
				double j = i + 0.001;
				
				myFunction.zeichneFunktion(i, j, entwPunkt);
				
				myFunction.zeichnePolynom (i, j, entwPunkt, grad);
			}
			
			// Fehlerausgabe
			fehlerField.setText(String.format("%.3f", myFunction.bewertePolynom(entwPunkt, grad, xFehler) - myFunction.bewerteFunktion(xFehler)));
		}
		
		// Fehler-Feld enabled aber nicht editierbar
		fehlerField.setDisable(false);
		fehlerField.setEditable(false);		
	}
	

	// saveButton
	@FXML private void handleSaveButton() {
		FileChooser fileChooser = new FileChooser();
		
		// setze Filter fuer Datei-Erweiterung
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		
		// zeige Dialog zum Sichern von Dateien
		File file = fileChooser.showSaveDialog(primaryStage);
		
		if( file != null ) {
			WritableImage writableImage = canvas.snapshot(null, null);
			// schreibe in PNG Format
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	// Benutzerhandbuch
    // eine Webseite mit Bedienungsanleitung
    @FXML public void handleHandbuch() {
    	HTMLManual manual = new HTMLManual();
    	manual.showAndWait();
    }
	
	// Programm laeuft auch beim Druecken von Enter
	@FXML public void handleEnter(KeyEvent event) {
		switch (event.getCode()) {
		case ENTER:
			if ( saveButton.isFocused() )
				handleSaveButton();
			else if ( helpButton.isFocused() )
				handleHandbuch();
			else
				handleOKButton();
			break;
		case ESCAPE:
			Stage sb = (Stage)okButton.getScene().getWindow(); // use any object
	        sb.close();
			break;
		default:
			break;
		}
	}
	
	
	// Fehlermeldungen
	public void fehlerAlert (String title, String header, String content) {
		Alert fail= new Alert(AlertType.ERROR);
		fail.setTitle(title);
		fail.setHeaderText(header);
        fail.setContentText(content);
        fail.showAndWait();
	}

}
