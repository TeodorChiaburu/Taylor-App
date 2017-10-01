package application.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Funktion {
	
	// Signatur der Funktion: Vorschrift (name)
	//						  Definitions- und Wertebereich (gc)
	private String name;
	private GraphicsContext gc;
	private double skalFunk;
	private double skalArg;
	private double plusArg;
	private double plusFunk;
	
	// Konstanten
	private static final String SQRT = "sqrt";
	private static final String EXP  = "exp";
	private static final String LN   = "ln";
	private static final String SIN  = "sin";
	private static final String COS  = "cos";
	private static final double xMitte = 350; 
	private static final double yMitte = 250;
	private static final double skalEinheit = 50;
	private static final double dicke = 1.0;
	private static final Color funkFarbe = Color.GREEN;
	private static final Color polyFarbe = Color.RED;
	
	
	// Konstruktor
	public Funktion (String name, GraphicsContext gc, double skalFunk, double skalArg, double plusArg, double plusFunk) {
		this.name = name;
		this.gc = gc;
		this.skalFunk = skalFunk;
		this.skalArg  = skalArg;
		this.plusArg  = plusArg;
		this.plusFunk = plusFunk;
	}
	
	
	/*** Methoden ***/
	
	// Funktionswert f(x)
	public double bewerteFunktion (double x) {
		double funkWert;
		
		switch (this.name) {
		
		case SQRT :
			funkWert = skalFunk * Math.sqrt( skalArg * x + plusArg ) + plusFunk;
			break ;
			
		case EXP :
			funkWert = skalFunk * Math.exp( skalArg * x + plusArg ) + plusFunk;
			break ;
			
		case LN :
			funkWert = skalFunk * Math.log( skalArg * x + plusArg ) + plusFunk;
			break ;
			
		case SIN :
			funkWert = skalFunk * Math.sin( skalArg * x + plusArg ) + plusFunk;
			break ;
			
		case COS :
			funkWert = skalFunk * Math.cos( skalArg * x + plusArg ) + plusFunk;
			break ;
			
		default:
			funkWert = 0;
			break;
		
		}
		
		return funkWert;
	}
	
	
	// malt den Graph der Funktion
	public void zeichneFunktion (double xStart, double xEnd, double entwPunkt) {
		
		// Dicke der Kurve
		gc.setLineWidth(dicke);

		// Farbe der Kurve
		gc.setStroke(funkFarbe);
		
		// Graph der Funktion
		gc.strokeLine(xMitte + skalEinheit * (xStart - entwPunkt), 
				      yMitte - skalEinheit * (bewerteFunktion(xStart) - bewerteFunktion(entwPunkt)),
					  xMitte + skalEinheit * (xEnd	- entwPunkt), 
					  yMitte - skalEinheit * (bewerteFunktion(xEnd)   - bewerteFunktion(entwPunkt)));
	}
	
	
	// Polynomwert p(x)
	public double bewertePolynom (double entwPunkt, int grad, double xStelle) {
		
		double polyWert = bewerteFunktion(entwPunkt);
		
		for (int k = 1; k <= grad; k++) {
		
			switch (this.name) {
			
			case SQRT :		
				polyWert += ablWurz(entwPunkt, k) * powFakult(entwPunkt, xStelle, k);			
				break ;
				
			case EXP :
				polyWert += skalFunk * Math.pow(skalArg, grad) * 
							Math.exp(skalArg * entwPunkt + plusArg) * powFakult(entwPunkt, xStelle, k);
				break ;
				
			case LN :			
				polyWert += ablLog(entwPunkt, k) * powFakult(entwPunkt, xStelle, k);
				break ;
							
			case SIN :			
				polyWert += monomTrig(SIN, entwPunkt, xStelle, k);     
				break ;
				
			case COS :			
				polyWert += monomTrig(COS, entwPunkt, xStelle, k);
				break ;
				
			default:
				break;
			
			}
		
		} 
		
		return polyWert;
	}
	
	
	// malt den Graph des Polynoms
	public void zeichnePolynom (double xStart, double xEnd, double entwPunkt, int grad) {
		
		// Dicke der Kurve
		gc.setLineWidth(dicke);
		
		// Farbe der Kurve
		gc.setStroke(polyFarbe);
			
		// Graph des Polynoms
		gc.strokeLine(xMitte + skalEinheit * (xStart - entwPunkt), 
					  yMitte - skalEinheit * (bewertePolynom(entwPunkt, grad, xStart) - bewerteFunktion(entwPunkt)),				  
				      xMitte + skalEinheit * (xEnd	- entwPunkt), 
				      yMitte - skalEinheit * (bewertePolynom(entwPunkt, grad, xEnd)   - bewerteFunktion(entwPunkt)));	
	}
	
	
	// Getters und Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public double getSkalFunk() {
		return skalFunk;
	}

	public void setSkalFunk(double skalFunk) {
		this.skalFunk = skalFunk;
	}

	public double getSkalArg() {
		return skalArg;
	}

	public void setSkalArg(double skalArg) {
		this.skalArg = skalArg;
	}

	public double getPlusArg() {
		return plusArg;
	}

	public void setPlusArg(double plusArg) {
		this.plusArg = plusArg;
	}

	public double getPlusFunk() {
		return plusFunk;
	}

	public void setPlusFunk(double plusFunk) {
		this.plusFunk = plusFunk;
	}

	
	
	/*** HILFSFUNKTIONEN ***/
	
	// Fakultaet-Funktion
	public long fakultaet (int n) {
		long fak = 1;
		if ( n == 0 || n == 1 )
			return fak;
		
		for ( int i = 2; i <= n; ++i )
			fak *= i;
		return fak;
	}
	
	// Funktion fuer den gemeinsamen Teil aller Monome in Taylor
	public double powFakult (double punkt, double x, int grad) {
		
		return Math.pow(x - punkt, grad) / fakultaet(grad);
	}
	
	// Berechnung der Monome in der Approximation von Sinus oder Cosinus
	public double monomTrig (String funk, double punkt, double x, int grad) {
		
		// gemeinsamer Teil
		double powFakult = powFakult(punkt, x, grad);
		
		if ( funk == SIN )
			// grad ist gerade => Ableitung ist sin
			if ( grad % 2 == 0 )
				// Vorzeichen beachten!
				if ( grad == 2 || grad == 6 || grad == 10 )
					return -1 * skalFunk * Math.pow(skalArg, grad) * Math.sin(skalArg * punkt + plusArg) * powFakult;
				else
					return      skalFunk * Math.pow(skalArg, grad) * Math.sin(skalArg * punkt + plusArg) * powFakult;
			// grad ist ungerade => Ableitung ist cos
			else
				if ( grad == 3 || grad == 7 )
					return -1 * skalFunk * Math.pow(skalArg, grad) * Math.cos(skalArg * punkt + plusArg) * powFakult;
				else
					return 		skalFunk * Math.pow(skalArg, grad) * Math.cos(skalArg * punkt + plusArg) * powFakult;
		
		else if ( funk == COS )
			// grad ist gerade => Ableitung ist cos
			if ( grad % 2 == 0 )
				if ( grad == 2 || grad == 6 || grad == 10 )
					return -1 * skalFunk * Math.pow(skalArg, grad) * Math.cos(skalArg * punkt + plusArg) * powFakult;
				else 
					return 		skalFunk * Math.pow(skalArg, grad) * Math.cos(skalArg * punkt + plusArg) * powFakult;
			// grad ist ungerade => Ableitung ist sin
			else
				if ( grad == 1 || grad == 5 || grad == 9 )
					return -1 * skalFunk * Math.pow(skalArg, grad) * Math.sin(skalArg * punkt + plusArg) * powFakult;
				else
					return 		skalFunk * Math.pow(skalArg, grad) * Math.sin(skalArg * punkt + plusArg) * powFakult;
		else
			return 0;
	}
	
	// Ableitung der Wurzel
	// n-te Ableitung: (-1)^(n+1) * ( Produkt(2k+1) von k=0 bis n-2 ) / 2^n * x ^-(2n-1)/2
	public double ablWurz (double x, int ord) {
		
		if ( ord == 1 )
			return 0.5 * skalFunk * skalArg * Math.pow(skalArg * x + plusArg, -0.5);
		else {
			double faktor;
			double produkt = 1;
			for ( int k = 0; k <= ord - 2; k++ ) {
				faktor = 2*k + 1;
				produkt *= faktor;
			}
			
			return Math.pow(-1, ord+1) * produkt * skalFunk * Math.pow(skalArg, ord) / 
				   Math.pow(2, ord) * Math.pow(skalArg * x + plusArg, 0.5-ord);
		}			
	}
	
	// Ableitung von Logarithmus
	// n-te Ableitung: (-1)^(n+1) * (n-1)! * x^-n
	public double ablLog (double x, int ord) {
		
		return Math.pow(-1, ord+1) * fakultaet(ord-1) * skalFunk * Math.pow(skalArg, ord) / Math.pow(skalArg * x + plusArg, ord);
	}

}
