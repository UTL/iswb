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
import engine.Transizione;


public class View {
	
	public void printStart(){
		System.out.println("===== Avvio programma =====");
	}
	
	public void printEnd(){
		System.out.println("===== Fine programma =====");
	}
	
	public void printFileStatus(boolean isReadable, boolean isSchema, String path){
		if (isSchema) System.out.print("Il file schema ");
		else System.out.print("Il file XML ");
		
		System.out.print("che si trova in "+ path+ " ");
		
		if (!isReadable) System.out.println("non esiste o non e' leggibile.");
		else System.out.println("è stato caricato correttamente.");
		
	}
	
	public void askInputFile(){
		System.out.println("Digita il nome del file di input, oppure 0 per terminare: ");
		System.out.println("Esempio: testcase/primo.xml");
		System.out.println("Esempio: testcase/secondo.xml");
		System.out.println("Esempio: testcase/terzo.xml");
		System.out.println("Esempio: testcase/quarto.xml");
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
	
	public void printFileContent(Model modello){
		
		System.out.println("Dati contenuti nel file: \n");
		
		System.out.println("Macchina: "+modello.getNomeMacchinaUno());
		printFSMInfo(modello, 1);
		//printInfoTransizioni(modello, 1);
		
		System.out.println("Macchina: "+modello.getNomeMacchinaDue());
		printFSMInfo(modello, 2);
		//printInfoTransizioni(modello, 2);


	}
	
	private void printFSMInfo(Model modello, int numeroMacchina){
		
		//if (i == 1) 
		System.out.println("Lista stati:");
		
		
		for (int i=0;i<modello.getNumeroStati(numeroMacchina);i++)
		{
			int nTransizioni= modello.getNumeroTransizioniUscenti(i, numeroMacchina);
			System.out.println(i+"= "+ modello.getNomeStato(i, numeroMacchina)+";");
			
			if(nTransizioni<=0){
				System.out.println("nessuna transizione uscente.\n");
				continue;
			}
			//tempListaStati.get(i).toString() metodo
			System.out.println("transazioni uscenti: ");	//tempListaStati.get(i).toString()+"; transazioni uscenti: ");
			for (int y=0;y< nTransizioni;y++){//tempListaStati.get(i).getTransazioniUscenti().size();y++)
				System.out.print("\t"+modello.getNomeTransizioneUscente(i, y, numeroMacchina) +", ");//tempListaStati.get(i).getTransazioniUscenti().get(y).getNome()+"; ");
				System.out.println("il cui stato di arrivo e':"+modello.getNomeStatoArrivo(i, y, numeroMacchina));
			}
			System.out.print("\n");
		}
	}
	
	/*private void printInfoTransizioni(Model modello, int numeroMacchina){
		System.out.println("Lista transizioni:");
		for (int i=0;i<tempListaTransizioni.size();i++)
		{
			System.out.println(i+"= "+tempListaTransizioni.get(i).getNome()+"; stato di arrivo: "+tempListaTransizioni.get(i).getStatoArrivo().getNome());
		}
		System.out.print("\n");
	}
	
	public void printRelazione(Transizione tUno,Transizione tDue,String tipoRelazione){
			System.out.println("Imposto relazione "+tipoRelazione+" tra "+tUno.toString()+" e "+tDue.toString()+".");
	}*/
	
	public void printRelazioni(Model modello) throws JAXBException{
		for(int i =0; i < modello.getNumeroTotaleTransizioni(); i++){
			System.out.print("'"+modello.getNomeTransizione(i)+"'");
			System.out.print("  relazione ");
			if(modello.getTipoRelazione(i).equals(Tipotransizione.ASINCRONA))
				System.out.println("ASINCRONA");
			else if(modello.getTipoRelazione(i).equals(Tipotransizione.SINCRONA))
				System.out.println("SINCRONA");
			else System.out.println("MUTEX");
		}
		
	}
	
	public void printEseguo(){
		System.out.println("=====");
		System.out.println("Eseguo:");
	}

	public void printRelError() {
		System.out.println("Errore, non ci possono essere stati senza transizioni");
		
	}

	
}







