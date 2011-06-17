package main;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import engine.PassoSimulazione;


public class View {
	public void printStart(){
		System.out.println("===== Avvio programma =====");
	}
	
	public void printEnd(){
		System.out.println("===== Fine programma =====");
	}
	
	public void printFileStatus(boolean isReadable, boolean isSchema, String path){
		if (!isSchema) System.out.print("Il file schema ");
		else System.out.print("Il file XML ");
		
		System.out.print("che si trova in "+ path+ " ");
		
		if (!isReadable) System.out.println("non è leggibile.");
		else System.out.println("è stato caricato correttamente.");
		
	}
	
	public void askInputFile(){
		System.out.println("Digita il nome del file di input: ");
	}
	
	public void waitForParsing(){
		System.out.println("Attendi mentre eseguo il parsing del file...");
	}
	
	public void printParseError(JAXBException e){
		System.out.println("Si e' verificato un'errore ed il programma ora verra' terminato.");
		System.out.print("Dettagli dell'errore: "+e.getMessage());
		if (e.getCause()!=null)
			System.out.print("; "+e.getCause().getMessage());
		e.printStackTrace();
	}
	
	public void printParseError(SAXException e){
		System.out.println("Si e' verificato un'errore ed il programma ora verra' terminato.");
		System.out.print("Dettagli dell'errore: "+e.getMessage());
		if (e.getCause()!=null)
			System.out.print("; "+e.getCause().getMessage());
		e.printStackTrace();
	}
	
	//
	public void printStartSimulation(){
		System.out.println("=====Inizio simulazione=====");
	}
	
	//
	public void printCurrentState(String statiCorrenti){
		System.out.println("=====");
		System.out.println("Stato corrente delle macchine:");
		System.out.println(statiCorrenti);
		System.out.println("=====");
	}
	
	//chiede anche il nuovo passo
	public void printActiveTransitions(List<PassoSimulazione> passi){
		if (passi.size() == 0)
		{
			System.out.println("Non ci sono altre transazioni attive per le macchine!");
			System.out.println("Il programma verra' terminato.");
		}
		else {
			for (int i=0; i<passi.size();i++)
			{
				System.out.println((i+1)+": "+passi.get(i).toString());
			}
			System.out.println("Scegli un passo di simulazione da eseguire (zero per terminare):\n");
		}
	}
	
	//bisogna passare direttamente il passo che viene dalla List<passosimulazione> passi
	public void runNextStep(PassoSimulazione passo){
		System.out.println("=====");
		System.out.println("Eseguo:");
		System.out.print(passo.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}







