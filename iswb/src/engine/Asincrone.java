/**
 * 
 */
package engine;

/**
 * Questa classe implementa l'interfaccia Relazione per un tipo di relazione asincrona. 
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Asincrone implements Relazione 
{

	public boolean risultaAmmissibile(PassoSimulazione passo) 
	{
		/*
		 * in realt� ci sono tre casi distinti:
		 * se il passo si applica ed � un singolo, allora ritorniamo true;
		 * se il passo si applica e non � un singolo, allora ritorniamo true;
		 * se il passo non si applica, allora ritorniamo indeterminato (ovvero true).
		 */
		return true;
	}

}
