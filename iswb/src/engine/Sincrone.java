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
	private Transizione prima;
	private Transizione seconda;
	
	/**
	 * Il costruttore richiede le due transazioni che devono essere impostate come sincrone.
	 * 
	 * @param prima Una transizione
	 * @param seconda L'altra transizione
	 */
	public Sincrone (Transizione prima, Transizione seconda)
	{
		this.prima=prima;
		this.seconda=seconda;
	}
	
	public boolean risultaAmmissibile(PassoSimulazione passo) 
	{
		boolean uscita;
		/*
		 * se il passo di simulazione � un singlo e contiene una delle due
		 * transizioni sincorone allora ritorniamo falso
		 * (un passo di simulazione non pu� mai essere singlo con una transizione sincrona)
		 * in caso contrario ritorniamo vero. 
		 * NOTA: verifichiamo per� che tutte e due le transazioni siano attive!!
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
		
		
//		System.out.println("La sincronia tra "+prima.toString()+" che � "+prima.isAttiva()+" e "+seconda.toString()+" che � "+seconda.isAttiva()+" ritiene che:");
//		System.out.print("il "+passo.toString());
//		System.out.println("sia da valutare: "+uscita);
//		System.out.println("--");
//		
		return uscita;
		
	}

}
