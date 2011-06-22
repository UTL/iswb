package engine;

/**
 * Questa classe rappresenta una macchina a stati fininiti.
 * <br>
 * Mette a disposizione tutti i metodi per poter gestire una macchina a stati finiti,
 * purch� le istanze degli stati e delle transizioni siano stati correttamente istanziati.
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class MacchinaStatiFiniti 
{
	/**
	 * I parametri privati della classe.
	 */
	private String nome;
	private Stato corrente;
	
	/**
	 * Il costruttore della classe richiede come parametro il nome della macchina.
	 * 
	 * @param nome Il nome della macchina
	 */
	public MacchinaStatiFiniti(String nome)
	{
		this.nome=nome;
		corrente=null;
	}
	
	/**
	 * Ritorna il nome della macchina.
	 * 
	 * @return Il nome della macchina
	 */
	public String getNome()
	{
		return nome;
	}
	
	/**
	 * Verifica l'uguaglianza di due macchine sulla base del nome.
	 * <br>
	 * Le due macchine saranno considerate uguali se hanno lo stesso nome.
	 * 
	 * @param altra La macchina da comparare
	 * @return True se hanno lo stesso nome
	 */
	public boolean equals(MacchinaStatiFiniti altra)
	{
		//consideraimo le macchine uguali se hanno lo stesso nome.
		if (altra==null)
			return false;
		else 
			return altra.getNome().equals(this.nome);
	}
	
	/**
	 * Ritorna lo stato attuale della macchina.
	 * 
	 * @return Lo stato corrente della macchina
	 */
	public Stato getStatoCorrente()
	{
		return corrente;
	}
	
	/**
	 * Imposta un nuovo stato corrente della macchina.
	 * 
	 * @param prossimo
	 */
	public void setStatoCorrente(Stato prossimo)
	{
		if(!(corrente==null))
			for(int i=0;i<corrente.getTransazioniUscenti().size();i++)
			{
				//System.out.println("Imposto NON attiva la "+corrente.getTransazioniUscenti().get(i));
				corrente.getTransazioniUscenti().get(i).setAttiva(false);
			}
		
		//System.out.println("Imposto lo stato corrente: "+prossimo.toString());
		corrente=prossimo;
		if(!(corrente==null))
		{
			//System.out.println("Attivo le transazioni "+corrente.getTransazioniUscenti().size());
			for(int i=0;i<corrente.getTransazioniUscenti().size();i++)
			{
				//System.out.println("Imposto ATTIVA la "+corrente.getTransazioniUscenti().get(i));
				corrente.getTransazioniUscenti().get(i).setAttiva(true);
			}
		}
	}

	/**
	 * Ritorna una stringa formattata correttamente per essere stampata
	 * con il nome della macchina e il suo stato.
	 * 
	 * @return
	 */
	@Override
	public String toString()
	{
		return "Macchina a stati finiti di nome "+nome+", nello stato "+corrente.getNome()+".";
	}
}
