/**
 * 
 */
package engine;

/**
 * Questa classe implementa l'interfaccia Relazione per un tipo di relazione sincrona. 
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Sincrone implements Relazione 
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
	public Sincrone (Transazione prima, Transazione seconda)
	{
		this.prima=prima;
		this.seconda=seconda;
	}
	
	public boolean risultaAmmissibile(PassoSimulazione passo) 
	{
		boolean uscita;
		/*
		 * se il passo di simulazione è un singlo e contiene una delle due
		 * transizioni sincorone allora ritorniamo falso
		 * (un passo di simulazione non può mai essere singlo con una transazione sincrona)
		 * in caso contrario ritorniamo vero. 
		 * NOTA: verifichiamo però che tutte e due le transazioni siano attive!!
		 */
		//System.out.println("--");
		
		if (passo.singolo())
		{
			if(prima.isAttiva() && seconda.isAttiva())
			{
				if (passo.contiene(prima) || passo.contiene(seconda))
				{
					//System.out.println("singolo, attive e contenenti");
					uscita=false;
				}
				else
				{
					//System.out.println("singolo, attive e NON contenenti");
					uscita=true;
				}
			}
			else
			{
				//System.out.println("singolo e NON attive");
				uscita=true;
			}
		}
		else
		{
			if (passo.contiene(prima) && passo.contiene(seconda))
			{
				//System.out.println("NON singolo e contenenti");
				uscita=true;
			}
			else
			{
				//System.out.println("NON singolo e NON contenenti");
				uscita=true;
			}
		}
		
		
//		System.out.println("La sincronia tra "+prima.toString()+" che è "+prima.isAttiva()+" e "+seconda.toString()+" che è "+seconda.isAttiva()+" ritiene che:");
//		System.out.print("il "+passo.toString());
//		System.out.println("sia da valutare: "+uscita);
//		System.out.println("--");
//		
		return uscita;
		
	}

}
