package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe rappresenta uno stato di una macchina a stati finiti.
 * <br>
 * Dispone di un nome e di una lista di transazioni uscenti dallo stato. Deve fare riferimento ad una mecchina.  
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Stato 
{
	/**
	 * 
	 */
	private String nome;
	private MacchinaStatiFiniti macchina;
	private List<Transazione> uscenti;
	
	/**
	 * Il costruttore della classe richiede la macchina a stati finiti a cui appartiene lo stato
	 * ed un nome.
	 * 
	 * @param macchina La macchina a stati finiti
	 * @param nome Il nome dello stato
	 */
	public Stato(MacchinaStatiFiniti macchina, String nome)
	{
		this.macchina=macchina;
		this.nome=nome;
		uscenti=new ArrayList<Transazione>();
	}
	
	/**
	 * Ritorna il nome dello stato.
	 * 
	 * @return Il nome dello stato
	 */
	public String getNome()
	{
		return nome;
	}
	
	/**
	 * Ritorna la macchina a cui lo stato appartiene.
	 * 
	 * @return La macchina a stati finiti
	 */
	public MacchinaStatiFiniti getMacchina()
	{
		return macchina;
	}
	
	/**
	 *  Verifica l'uguaglianza di due stati sulla base del nome.
	 * <br>
	 * I due stati saranno considerati uguali se appartengono alla stessa macchina
	 * ed hanno lo stesso nome.
	 * 
	 * @param altro Lo stato da confrontare
	 * @return True se i due stati hanno lo stesso nome e appartengono alla stessa macchina
	 */
	public boolean equals(Stato altro)
	{
		//consideriamo gli stati uguali se appartengono alla stessa macchina e hanno lo stesso nome.
		if (altro==null)
			return false;
		else
			return macchina.equals(altro.getMacchina()) && altro.getNome().equals(this.nome);
	}

	/**
	 * Aggiunge una transazione uscente dallo stato.
	 * 
	 * @param nuova La transazione da aggiungere
	 */
	public void addTransazione(Transazione nuova)
	{
		uscenti.add(nuova);
	}
	
	/**
	 * ritorna la lista delle transazioni uscenti d aquesto stato.
	 * 
	 * @return La lista delle trensazione uscenti
	 */
	public List<Transazione> getTransazioniUscenti()
	{
		return uscenti;
	}
	
	/**
	 * Ritorna una stringa formattata per la stampa contenente il nome dello stato. 
	 * 
	 * @return
	 * 
	 */
	@Override
	public String toString()
	{
		return "stato "+nome+" della macchina "+macchina.getNome();
	}
}
