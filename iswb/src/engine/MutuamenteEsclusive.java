package engine;

/**
 * Questa classe specifica il comportamento di una relazione mutuamente esclusiva tra due transazioni.
 * <br>
 * Implementa l'interficcia Relazione.
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class MutuamenteEsclusive implements Relazione 
{

	/**
	 * 
	 */
	private Transazione prima;
	private Transazione seconda;
	
	/**
	 * Il costruttore richiede le due transazioni che devono essere impostate come sincrone.
	 * 
	 * @param prima Una transazione
	 * @param seconda L'altra transazione
	 */
	public MutuamenteEsclusive(Transazione prima, Transazione seconda)
	{
		this.prima=prima;
		this.seconda=seconda;
	}
	
	public boolean risultaAmmissibile(PassoSimulazione passo) 
	{
		boolean uscita;
		/*
		 * se il passo si applica (ovvero contiene le due transizioni che devono essere
		 * mutuiamente esclusive)  e non è un singlo ritorniamo false,
		 * altrimenti ritorniamo true. 
		 */
		if (passo.contiene(prima) && passo.contiene(seconda) && ! passo.singolo())
		{
			uscita= false;
		}
		else
		{
			uscita= true;
		}
		/*
		System.out.println("--");
		System.out.println("La mutua esclusione tra "+prima.toString()+" e "+seconda.toString()+" ritiene che:");
		System.out.print("il "+passo.toString());
		System.out.println("sia da valutare: "+uscita);
		System.out.println("--");
		*/
		return uscita;
	}

}
