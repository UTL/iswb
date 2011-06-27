package engine;

/**
 * Questa classe rappresenta un singolo passo o step di simulazione.
 * <br>
 * In particolare contiene le transazioni che devono essere attivate per quel passo,
 * una per ogni macchina a stati finiti.
 * <br>
 * Pu� anche essere "singolo" ovvero, contenere transazioni solo di una macchina e non
 * dell'altra: in questo caso l'altra macchina si suppone che per quel passo non modifichi
 * il suo stato.
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class PassoSimulazione 
{
	/**
	 * 
	 */
	private Transazione prima;
	private Transazione seconda;
	
	/**
	 * Le due transazioni delle due macchine che compongono il passo di simulazione.
	 * <br>
	 * Accetta che i parametri siano "NULL". 
	 * 
	 * @param prima Una transazione
	 * @param seconda L'altra transazione
	 */
	public PassoSimulazione(Transazione prima, Transazione seconda)
	{
		this.prima=prima;
		this.seconda=seconda;
	}
	
	/**
	 * Verifica se il passo di simulazione � singolo,
	 * ovvero se contiene transazioni solo di una macchina e non dell'altra. 
	 * 
	 * @return True se il passo � singolo
	 */
	public boolean singolo()
	{
		/*
		 * ritorniamo 'vero' (cio� � un passo di simulazione singlo)
		 * se una delle due transizioni � NULL
		 */
		if (prima==null || seconda==null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Verifica che questo passo di simulazione contiene la transazione fornita come argomento.
	 * 
	 * @param transazione La transazione da verificare se � contenuta nel passo
	 * @return True se la transazione � contenuta
	 */
	public boolean contiene(Transazione transazione)
	{
		/*
		System.out.println("prima: "+prima.toString());
		System.out.println("seconda: "+seconda.toString());
		*/
		//se una delle due transizioni � uguale a quella richiesta.
		if (prima==null && seconda==null)
			return false;
		else if (prima==null)
			return this.seconda.equals(transazione);
		else if (seconda==null)
			return this.prima.equals(transazione);
		else
			return this.prima.equals(transazione) || this.seconda.equals(transazione);
		
	}
	
	/**
	 * 
	 * Questo metodo esegue il passo di simulazione, facendo scattare le sue transazioni
	 * e aggiornando lo stato delle macchine.
	 * 
	 */
	public void esegui()
	{
		if (prima!=null)
		{
			prima.getMacchina().setStatoCorrente(prima.getStatoArrivo());
		}
		if (seconda!=null)
		{
			seconda.getMacchina().setStatoCorrente(seconda.getStatoArrivo());
		}
	}
	
	/**
	 * Ritorna una stringa correttamente formattata per essere stampata, 
	 * che include una descrizione del passo di simulazione.
	 * 
	 * @return
	 */
	@Override
	public String toString()
	{
		String uscita=new String();
		uscita="Passo di simulazione contenente:\n";
		if (prima!=null)
		{
			uscita+= "   * transizione "+prima.toString()+" della macchina "+prima.getNomeMacchina()+"\n";
		}
		if (seconda!=null)
		{
			uscita+= "   * transizione "+seconda.toString()+" della macchina "+seconda.getNomeMacchina()+"\n";
		}
		
		return uscita;
	}
}
