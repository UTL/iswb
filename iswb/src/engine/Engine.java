package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa è la classe principale e si occupa di simulare le due macchine.
 * <br>
 * Essa matinene tutti gli oggetti necessari alla simulazione, computa
 * i passi di simulazione eseguibili (anche sulla base delle relazioni 
 * tra le due macchine) e si occupa di eseguire il passo scelto. 
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Engine 
{
	/**
	 * 
	 */
	private ListaRelazioni matriceRelazioni;
	private MacchinaStatiFiniti macchinaUno;
	private MacchinaStatiFiniti macchinaDue;
	
	/**
	 * Costruttore della classe richiede come parametri le due macchine
	 * e si occupa di inizializzare il motore.
	 * 
	 * @param uno Una macchina a stati finiti
	 * @param due L'altra macchina a stati finiti
	 */
	public Engine (MacchinaStatiFiniti uno, MacchinaStatiFiniti due)
	{
		macchinaUno=uno;
		macchinaDue=due;
		matriceRelazioni=null;
	}
	
	/**
	 * Imposta la matrice delle relazioni tra le due macchine che
	 * dovrà essere usata per computare i passi di simulazione validi.
	 * 
	 * @param relazioni L'oggetto che contiene tutte le relazioni tra le due macchine
	 */
	public void setRelazioni(ListaRelazioni relazioni)
	{
		matriceRelazioni=relazioni;
	}
	
	/**
	 * Questo metodo restituisce tutti i passi di simulazione che possono
	 * essere eseguiti dalle due macchine in base al loro stato attuale.
	 * <br>
	 * In particolare quindi conterrà tutte le transazioni attive che non
	 * violano nessuna relazione tra le due macchine.
	 * 
	 * @return La lista dei passi di simulazioni eseguibili
	 */
	public List<PassoSimulazione> getPassiAttivi()
	{
		//le transazioni attive delle due macchine.
		List<Transazione> uno = macchinaUno.getStatoCorrente().getTransazioniUscenti();
		List<Transazione> due = macchinaDue.getStatoCorrente().getTransazioniUscenti();
		List<PassoSimulazione> lista = new ArrayList<PassoSimulazione>();
		
		//computa tutte le coppie di transizioni eseguibili come passi di simulazione
		int maxi = uno.size();
		int maxy = due.size();
		for (int i =0; i < maxi; i++ )
		{
			lista.add(new PassoSimulazione(uno.get(i),null));
		}
		for (int y=0; y<maxy; y++)
		{
			lista.add(new PassoSimulazione(due.get(y),null));
			for (int i=0; i< maxi; i++)
			{
				lista.add(new PassoSimulazione(uno.get(i),due.get(y)));
			}
		}
		
		//se non è stata impostata la matrice di relazioni ritorniamo la lista
		//altrimenti prima filtriamo quelle non ammissibli con la matrice
		if (matriceRelazioni==null)
			return lista;
		else
			return matriceRelazioni.computaAmmissibili(lista);
		
	}
	
	/**
	 * Questo metodo esegue un passo di simulazione, aggiornando di conseguenza lo stato delle macchine.
	 * 
	 * @param passo Il passo di simulazione da eseguire
	 */
	public void esegui(PassoSimulazione passo)
	{
		passo.esegui();
	}
	
	/**
	 * Questo metodo ritorna una stringa formatata correttamente per la stampa
	 * contenente lo stato attuale delle macchine.
	 * 
	 * @return 
	 */
	public String statiCorrentiToString()
	{
		return macchinaUno.toString()+"\n"+macchinaDue.toString();
	}

}
