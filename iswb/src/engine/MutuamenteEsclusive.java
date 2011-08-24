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
	private Transizione prima;
	private Transizione seconda;
	
	/**
	 * Il costruttore richiede le due transazioni che devono essere impostate come sincrone.
	 * 
	 * @param prima Una transizione
	 * @param seconda L'altra transizione
	 */
	public MutuamenteEsclusive(Transizione prima, Transizione seconda)
	{
		this.prima=prima;
		this.seconda=seconda;
	}
	
	public boolean risultaAmmissibile(PassoSimulazione passo) 
	{
		boolean uscita;
		/*
		 * se il passo si applica (ovvero contiene le due transizioni che devono essere
		 * mutuiamente esclusive)  e non � un singlo ritorniamo false,
		 * altrimenti ritorniamo true. 
		 */
		if (passo.contiene(prima) && passo.contiene(seconda) && ! passo.singolo())
		{
			uscita= false;
			System.out.println("rilevato false in mutex");
		}
		else
		{
			uscita= true;
			System.out.println("rilevato true in mutex");

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
