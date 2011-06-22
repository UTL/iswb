package main;

import it.unibs.fp.mylib.InputDati;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import engine.Engine;
import engine.PassoSimulazione;


/**
 * Classe principale del programma, condiene il metodo main che deve essere invocato per avviare.
 * <br>
 * Fornisce  una semplice interfaccia a linea di comando per l'interazione testuale.
 * <br>
 * Si appoggia sugli altri package per eseguire le funzionalit�.
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 * 
 *
 */
public class Main 
{
	/**
	 * Metodo standard per avviare un programma java.
	 * 
	 * @param args
	 * @throws FileNotFoundException  
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		//// -------- Copiato in view printStart
		System.out.println("===== Program Start =====");
		//// -------- Copiato in view printStart END

	
		// creiamo un parser standard con in file di schema fornito;
		// se lo schema non � leggibile, terminamo il programma.
		Parser parser = new Parser(); 
		// -------- Copiato in view printFileStatus
		if (!parser.canReadSchemaFile()) 
			{
			System.out.println("Errore! Non posso leggere il file dello schema!");
			System.out.println("Ho cercato il file: "+parser.getSchemaPath());
			System.exit(-1);
			} // -------- Copiato in view printFileStatus END
		
		//carichiamo il file xml contenente i dati delle macchine,
		//alternativamente possiamo farlo come parametro di invocazione del metodo
		//oppure inserendo esplicitamente il percorso,
		File xml = null;
		
		if (args.length!=1)
		{
			// -------- Copiato in view askInputFile
			String nome= InputDati.leggiStringa("Per favore scrivi il nome del file di input: "); 
			// -------- Copiato in view askInputFile END
			//controlliamo che sia un percoso relativo
			
			if (!nome.startsWith(File.separator))
				nome=File.separator+nome;
			xml = new File (System.getProperty("user.dir")+nome);
			
		}
		
		else
		{
			//controlliamo che sia un percorso relativo
			if (!args[0].startsWith(File.separator))
				args[0]=File.separator+args[0];
			xml = new File(System.getProperty("user.dir") + args[0]);
		}
		
		//System.out.println("Attendi mentre provo a leggere il file...");
		//verifichiamo di leggere correttamente il file.
		
		if (!xml.canRead()) 
		{
			// -------- Copiato in view	printFileStatus
			System.out.println("\nErrore! Non posso leggere il file XML!");
			System.out.println("Ho cercato il file: "+xml.getAbsolutePath());

			System.exit(-1);

		}
		else
		{
			System.out.println("Il file risulta correttamente leggibile.");
		}
		// -------- Copiato in view printFileStatus END 
		
		//eseguiamo il parsing dei dati dal file xml alle classi del package "engine"
		
		// -------- Copiato in view waitForParsing
		System.out.println("Attendi mentre eseguo il parsing del file...");
		// -------- Copiato in view waitForParsing END
		
		Engine motore=null;

		try {
			motore= parser.parseToEngine(xml);
		}
		//attenzione alle eccezioni!!
		// -------- Copiato in view printParseError
		catch (JAXBException e) 
		{
			System.out.println("Si e' verificato un'errore ed il programma ora verra' terminato.");
			System.out.print("Dettagli dell'errore: "+e.getMessage());
			if (e.getCause()!=null)
				System.out.print("; "+e.getCause().getMessage());
			System.exit(-1);
			e.printStackTrace();
		} catch (SAXException e) 
		{
			System.out.println("Si e' verificato un'errore ed il programma ora verr� terminato.");
			System.out.println("Dettagli dell'errore: "+e.getMessage()+"; "+e.getLocalizedMessage()+".");
			System.exit(-1);
			e.printStackTrace();
		}
		// -------- Copiato in view printParseError END
		
		//iniziamo la simulazione vera e propria
		if (motore!=null)
		{
			// -------- Copiato in view printStartSimulation
			System.out.println("=====Inizio simulazione=====");
			// -------- Copiato in view printStartSimulation END
			
			int letto; //passo da simulare alla prossima iterazione
			List<PassoSimulazione> passi;
			
			
			//per ogni ciclo di simulazione eseguo:
			do
			{
				// -------- Copiato in view printCurrentState
				//mostro lo stato corrente
				System.out.println("=====");
				System.out.println("Stato corrente delle macchine:");
				System.out.println(motore.statiCorrentiToString());
				System.out.println("=====");
				// -------- Copiato in view printCurrentState END
				
				//mostro i passi attivi e chiedo quale eseguire
				// -------- Copiato in view printActiveTransitions MANCA LOGICA
				passi = motore.getPassiAttivi();
				if (passi.size() == 0)
				{
					System.out.println("Non ci sono altre transazioni attive per le macchine!");
					System.out.println("Il programma verr� terminato.");
					letto=0;
				}
				else
				{
					System.out.println("Scegli un passo di simulazione da eseguire (zero per terminare):\n");
					for (int i=0; i<passi.size();i++)
					{
						System.out.println((i+1)+": "+passi.get(i).toString());
					}
					letto=InputDati.leggiIntero("Scegli un valore: ", 0, passi.size());
				}
				// -------- Copiato in view printActiveTransitions END
				//eseguo il passo
				if (letto!=0)
					// -------- Copiato in view runNextStep
				{
					System.out.println("=====");
					System.out.println("Eseguo:");
					System.out.print(passi.get(letto-1).toString());
					
					motore.esegui(passi.get(letto-1));
					// -------- Copiato in view runNextStep END MANCA LOGICA
				}
			}
			while (letto!=0); //termino solo se viene inserito zero.
		}
		
		// -------- Copiato in view printEnd
		System.out.println("===== Program  End  =====");
		// -------- Copiato in view printEnd END
	}
	
	
}
