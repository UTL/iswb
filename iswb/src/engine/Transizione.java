package engine;

/**
 * Questa classe rappresenta la transazione tra due stati di una macchina a stati finiti.
 * <br>
 * Deve avere un nome, una macchina alla quale appartiene ed uno stato di arrivo.
 * 
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Transizione 
{
	/**
	 * 
	 */
	private String nome;
	private MacchinaStatiFiniti macchina;
	private Stato arrivo;
	private boolean attiva;
	
	/**
	 * il costruttore della classe richiede la macchina a cui la transazione appartiene,
	 * un nome della transazione ed uno stato di arrivo.
	 * 
	 * 
	 * @param macchina La macchina a stati finiti a cui la transazione appartiene
	 * @param nome Il suo nome
	 * @param arrivo Lo stato di arrivo della transazione
	 */
	public Transizione(MacchinaStatiFiniti macchina, String nome, Stato arrivo)
	{
		this.macchina=macchina;
		this.nome=nome;
		this.arrivo=arrivo;
		this.attiva=false;
	}
	

	/**
	 * Ritorna il nome della transazione.
	 * 
	 * @return Il nome della transazione
	 */
	public String getNome()
	{
		return nome;
	}
	
	/**
	 * Ritorna la macchina a stati finiti a cui appartiene la transazione.
	 * 
	 * @return La macchina a cui appartiene
	 */
	public MacchinaStatiFiniti getMacchina()
	{
		return macchina;
	}

	/**
	 *  Verifica l'uguaglianza di due transazioni sulla base del nome.
	 * <br>
	 * Le due transazioni saranno considerate uguali se appartengono alla stessa macchina ed
	 * hanno lo stesso nome.
	 * 
	 * @param altra La transazione da confrontare
	 * @return True se hanno lo stesso nome e appartengono alla stessa macchina
	 */
	public boolean equals(Transizione altra)
	{
		//consideriamo le transazioni uguali se appartengono alla stessa macchina e hanno lo stesso nome.
		if (altra==null)
			return false;
		else
			return macchina.equals(altra.getMacchina()) && altra.getNome().equals(this.nome);
	}
	
	/**
	 * Questo metodo segala se la transazione � attiva, ovvero se lo stato 
	 * da cui la transazione � uscente � lo stato corrente della macchina.
	 * 
	 * @return True se la transazione � attiva
	 */
	public boolean isAttiva()
	{
		return attiva;
	}
	
	/**
	 * Imposta la condizione di essere attiva o meno per la transazione.
	 * 
	 * @param valore Valore booleano in base alla condizione
	 */
	public void setAttiva(boolean valore)
	{
		attiva=valore;
	}
	
	/**
	 * Ritorna lo stato di arrivo della transazione.
	 * 
	 * @return Lo stato di arrivo
	 */
	public Stato getStatoArrivo()
	{
		return arrivo;
	}
	
	/**
	 * Ritorna una stringa formattata correttamente per la stampa con il nome della transazione.
	 * 
	 * @return
	 */
	@Override
	public String toString()
	{
		//return "transizione "+nome+" della macchina "+macchina.getNome();
		return nome;
	}
	
	public String getNomeMacchina(){
		return macchina.getNome();
	}

}
