package main;
import it.unibs.fp.mylib.InputDati;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import bindings.Tipotransizione;

import engine.Asincrone;
import engine.ListaRelazioni;
import engine.MacchinaStatiFiniti;
import engine.MutuamenteEsclusive;
import engine.PassoSimulazione;
import engine.Sincrone;
import engine.Stato;
import engine.Transazione;


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
	
	//se restituisce -1 non ci sono più transizioni
	//se restituisce 0 l'utente vuole terminare
	//se n > 1 fa scattare la transizione
	public int printActiveTransitions(List<PassoSimulazione> passi){
		int letto = -1;
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
		letto=InputDati.leggiIntero("Digita un valore: ", 0, passi.size());
		}
		return letto;
	}
	
	//bisogna passare direttamente il passo che viene dalla List<passosimulazione> passi
	public void runNextStep(PassoSimulazione passo){
		System.out.println("=====");
		System.out.println("Eseguo:");
		System.out.print(passo.toString());
	}
	
	public void printFileContent(List<Stato> tempListaStatiUno,List<Stato> tempListaStatiDue, MacchinaStatiFiniti macchinaUno,MacchinaStatiFiniti macchinaDue, List<Transazione> tempListaTransizioniUno, List<Transazione> tempListaTransizioniDue){
		
		System.out.println("Dati contenuti nel file: \n");
		
		System.out.println("Macchina: "+macchinaUno.getNome());
		printFSMInfo(tempListaStatiUno);
		printTransitionsInfo(tempListaTransizioniUno);
		
		System.out.println("Macchina: "+macchinaDue.getNome());
		printFSMInfo(tempListaStatiDue);
		printTransitionsInfo(tempListaTransizioniDue);

	}
	
	private void printFSMInfo(List<Stato> tempListaStati){
		System.out.println("Lista stati:");
		
		for (int i=0;i<tempListaStati.size();i++)
		{
			System.out.print(i+"= "+tempListaStati.get(i).toString()+"; transazioni uscenti: ");
			for (int y=0;y<tempListaStati.get(i).getTransazioniUscenti().size();y++)
				System.out.print(tempListaStati.get(i).getTransazioniUscenti().get(y).getNome()+"; ");
			System.out.print("\n");
		}
	}
	
	private void printTransitionsInfo(List<Transazione> tempListaTransizioni){
		System.out.println("Lista transizioni:");
		for (int i=0;i<tempListaTransizioni.size();i++)
		{
			System.out.println(i+"= "+tempListaTransizioni.get(i).getNome()+"; stato di arrivo: "+tempListaTransizioni.get(i).getStatoArrivo().getNome());
		}
		System.out.print("\n");
	}
	
	public void printRelazione(Transazione tUno,Transazione tDue,String tipoRelazione){
			System.out.println("Imposto relazione "+tipoRelazione+" tra "+tUno.toString()+" e "+tDue.toString()+".");
	}
	
}







