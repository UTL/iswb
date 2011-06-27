package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe rappresenta un'insieme di relazioni tra due macchine a stati finiti.
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class ListaRelazioni 
{
	/**
	 * 
	 */
	private List<Relazione> relazioni;
	
	/**
	 * Questo costruttore istanzia la classe a partire da una lista di relazioni. 
	 * @param lista
	 */
	public ListaRelazioni (List<Relazione> lista)
	{
		relazioni=lista;
	}
	
	/**
	 * Questo costruttore instanzia la classe con una lista inizialmente vuota.
	 * 
	 */
	public ListaRelazioni ()
	{
		relazioni=new ArrayList<Relazione>();
	}
	
	/**
	 * Questo metodo aggiunge una relazione alla lista.
	 * 
	 * @param nuova La relazione da aggiungere
	 */
	public void addRelazione(Relazione nuova)
	{
		relazioni.add(nuova);
	}
	
	/**
	 * Questo metodo, a partire da una lista di passi di silumazione, ritorna la lista di passi
	 * che risultano ammissibili dalle relazioni della lista.
	 * 
	 * @param listaPassi La lista di passi da verificare
	 * @return La lista di passi che sono ammissibili
	 */
	public List<PassoSimulazione> computaAmmissibili(List<PassoSimulazione> listaPassi)
	{
		boolean uscita = true;
		List<PassoSimulazione> daRimuovere= new ArrayList<PassoSimulazione>();
		//per ogni elemento della listaPassi
		for (int i =0; i < listaPassi.size(); i++ )
		{
			uscita = true;
			//per ogni elemento della lista relazioni
			for (int y=0; y<relazioni.size(); y++)
				{
				//verifico che sia ammissibile
				uscita = relazioni.get(y).risultaAmmissibile(listaPassi.get(i));
				/*
				System.out.println("--");
				System.out.println("valuto: il passo: "+listaPassi.get(i).toString());
				System.out.println("risultato: "+uscita);
				System.out.println("--");
				*/
				//se non lo e` lo aggiungo alla lista dei passi da rimuovere
				if (!uscita)
					daRimuovere.add(listaPassi.get(i));
				}
			
		}
		
		//rimuovo i passi non ammessi.
		listaPassi.removeAll(daRimuovere);
		return listaPassi;
	}

}
