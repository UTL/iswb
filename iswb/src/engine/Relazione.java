
package engine;

/**
 * Questa interfaccia specifica i metodi che tutte le relazioni devono implementare. 
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public interface Relazione 
{
	
	/**
	 * Questo Metodo valuta un passo di simulazione rispetto alla relazione,
	 * ritornando false nel caso in cui la relazione impedisca l'esecuzione del
	 * passo stesso.
	 * @param passo Il passo di simulazione da valutare
	 * @return False nel caso in cui questa relazione impedisca il passo di slumlazione, true altrimenti
	 */
	public boolean risultaAmmissibile(PassoSimulazione passo);

}
