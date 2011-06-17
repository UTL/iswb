package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import bindings.*;

import engine.*;

/**
 * Questa classe si occupa di fare il parsing tra i dati xml e le classi dela package "engine"
 * 
 * @author a.bonisoli
 * @author e.rizzardi
 * @author a.musatti
 *
 */
public class Parser 
{

	private File schema;
	
	/**
	 * Costruttore che usa lo schema fornito.
	 * @param schema
	 */
	public Parser (File schema)
	{
		this.schema = schema; 
	}
	
	/**
	 * Costruttore di default che usa lo schema nella posizione di default.
	 */
	public Parser ()
	{
		this(new File("docs/xsd/schema.xsd"));
	}
	
	/**
	 * Costruttore per non eseguire la verifica sintattica dello schema xml
	 * @param senzaschema
	 */
	public Parser (boolean senzaschema)
	{
		if (senzaschema) schema = null;
	}
	
	/**
	 * 
	 * @param inputXMLFile
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public Engine parseToEngine (File inputXMLFile) throws JAXBException, SAXException
	{
		//parsiamo il file
		Dati dati = parse (inputXMLFile);
		int max;
		
		//creiamo le due macchine
		MacchinaStatiFiniti macchinaUno = new MacchinaStatiFiniti(dati.getMacchina().get(0).getNome());
		MacchinaStatiFiniti macchinaDue = new MacchinaStatiFiniti(dati.getMacchina().get(1).getNome());
		
		//creaimo una lista temporanea di tutti gli stati della prima macchina. 
		List<Stato> tempListaStatiUno = new ArrayList<Stato>();
		max=dati.getMacchina().get(0).getListastati().getStato().size();
		tempListaStatiUno.add(new Stato(macchinaUno, dati.getMacchina().get(0).getListastati().getStatoiniziale().getNome()));
		for (int i =0; i<max; i++)
		{
			tempListaStatiUno.add(new Stato(macchinaUno,dati.getMacchina().get(0).getListastati().getStato().get(i).getNome()));
		}
		
		//creaimo una lista temporanea di tutti gli stati della seconda macchina. 
		List<Stato> tempListaStatiDue = new ArrayList<Stato>();
		max=dati.getMacchina().get(1).getListastati().getStato().size();
		tempListaStatiDue.add(new Stato(macchinaDue, dati.getMacchina().get(1).getListastati().getStatoiniziale().getNome()));
		for (int i =0; i<max; i++)
		{
			tempListaStatiDue.add(new Stato(macchinaDue,dati.getMacchina().get(1).getListastati().getStato().get(i).getNome()));
		}
		
		//creiamo una lista temporanea di tutte le transizioni della prima macchina
		List<Transazione> tempListaTransizioniUno = new ArrayList<Transazione>();
		max=dati.getMacchina().get(0).getListatransizioni().getTransizioni().size();
		for (int i=0; i<max;i++)
		{
			//stato temporaneo: quello finale della transazione
			Stato tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatofinale().getNome());
			//costruttore temporaneo: quello della transazione (la macchina, il suo nome, lo stato temporaneo)
			Transazione tempTransazione = new Transazione(macchinaUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getNome(),tempStato);
			//aggiungiamo alla lista temporanea
			tempListaTransizioniUno.add(tempTransazione);
			//aggiungiamo la transazione al suo stato iniziale
			tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatoiniziale().getNome());
			//	System.out.print("transizione= "+tempTransazione.toString());
			//	System.out.print("stato= "+tempStato.toString());
			
			tempStato.addTransazione(tempTransazione);
		}
		
		//creiamo una lista temporanea di tutte le transizioni della seconda macchina
		List<Transazione> tempListaTransizioniDue = new ArrayList<Transazione>();
		max=dati.getMacchina().get(1).getListatransizioni().getTransizioni().size();
		for (int i=0; i<max;i++)
		{
			Stato tempStato = cercaStato(tempListaStatiDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getStatofinale().getNome());
			Transazione tempTransazione = new Transazione(macchinaDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getNome(),tempStato);
			tempListaTransizioniDue.add(tempTransazione);
			cercaStato(tempListaStatiDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getStatoiniziale().getNome()).addTransazione(tempTransazione);
		}
		
		//settiamo lo stato iniziale
		//System.out.println("Imposto gli stati:");
		Stato inizialeUno = cercaStato(tempListaStatiUno, dati.getMacchina().get(0).getListastati().getStatoiniziale().getNome());
		Stato inizialeDue = cercaStato(tempListaStatiDue, dati.getMacchina().get(1).getListastati().getStatoiniziale().getNome());
		macchinaUno.setStatoCorrente(inizialeUno);
		macchinaDue.setStatoCorrente(inizialeDue);
		
		
		//-----DEBUG-----
		System.out.println("Dati contenuti nel file: \n");
		System.out.println("Macchina: "+macchinaUno.getNome());
		System.out.println("Lista stati:");
		for (int i=0;i<tempListaStatiUno.size();i++)
		{
			System.out.print(i+"= "+tempListaStatiUno.get(i).toString()+"; transazioni uscenti: ");
			for (int y=0;y<tempListaStatiUno.get(i).getTransazioniUscenti().size();y++)
				System.out.print(tempListaStatiUno.get(i).getTransazioniUscenti().get(y).getNome()+"; ");
			System.out.print("\n");
		}
		System.out.println("Lista transazioni:");
		for (int i=0;i<tempListaTransizioniUno.size();i++)
		{
			System.out.println(i+"= "+tempListaTransizioniUno.get(i).getNome()+"; stato di arrivo: "+tempListaTransizioniUno.get(i).getStatoArrivo().getNome());
		}
		System.out.print("\n");
		System.out.println("Macchina: "+macchinaDue.getNome());
		System.out.println("Lista stati:");
		for (int i=0;i<tempListaStatiDue.size();i++)
		{
			System.out.print(i+"= "+tempListaStatiDue.get(i).getNome()+"; transazioni uscenti: ");
			for (int y=0;y<tempListaStatiDue.get(i).getTransazioniUscenti().size();y++)
				System.out.print(tempListaStatiDue.get(i).getTransazioniUscenti().get(y).getNome()+"; ");
			System.out.print("\n");
		}
		System.out.println("Lista transazioni:");
		for (int i=0;i<tempListaTransizioniDue.size();i++)
			System.out.println(i+"= "+tempListaTransizioniDue.get(i).getNome()+"; stato di arrivo: "+tempListaTransizioniDue.get(i).getStatoArrivo().getNome());
		//-----DEBUG END-----
		
		//il motore � quasi pronto. le macchine sono impostate
		Engine motore = new Engine (macchinaUno,macchinaDue);
		
		//preparo la lista delle relazioni
		System.out.println("\nLista Relazioni:");
		ListaRelazioni tempListaRelazioni = new ListaRelazioni();
		max=dati.getListarelazioni().getRelazione().size();
		for (int i=0;i<max;i++)
		{
			Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(i);
			Transazione tempUno=null;
			Transazione tempDue=null;
			//se la prima transizione appartiene alla prima macchina e la seconda alla seconda
			if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				//cerco la prima transazione nella prima lista (e la seconda nella seconda)
				tempUno=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(1).getNome());
			}
			//altrimenti il contrario
			else if (tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaDue.getNome()))
			{
				tempUno=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(1).getNome());
			}
			//in base al tipo aggiungo la relazione
			if (tempRelazione.getTipo().equals(Tipotransizione.ASINCRONA))
			{
				System.out.println("Imposto relazione ASINCRONA tra "+tempUno.toString()+" e "+tempDue.toString()+".");
				tempListaRelazioni.addRelazione(new Asincrone());
			}
			else if (tempRelazione.getTipo().equals(Tipotransizione.SINCRONA))
			{
				System.out.println("Imposto relazione SINCRONA tra "+tempUno.toString()+" e "+tempDue.toString()+".");
				tempListaRelazioni.addRelazione(new Sincrone(tempUno,tempDue));
			}
			else if (tempRelazione.getTipo().equals(Tipotransizione.MUTEX))
			{
				System.out.println("Imposto relazione MUTEX tra "+tempUno.toString()+" e "+tempDue.toString()+".");
				tempListaRelazioni.addRelazione(new MutuamenteEsclusive(tempUno,tempDue));
			}
		}
		
		//
		motore.setRelazioni(tempListaRelazioni);
		
		return motore;
		
	}

	/**
	 * 
	 * @param inputXMLFile
	 * @return
	 * @throws JAXBException 
	 * @throws SAXException 
	 */
	private Dati parse(File inputXMLFile) throws JAXBException, SAXException
	{
		//context necessario per l'unmarschaller
		JAXBContext jc = JAXBContext.newInstance( "bindings" );
		Unmarshaller u = jc.createUnmarshaller();
		
		//impostiamo lo schema
		Schema mySchema;
		SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
		mySchema = sf.newSchema(schema );
		u.setSchema(mySchema);
		
		//eseguiamo l'unmarshal dei dati
		Dati dati= (Dati) u.unmarshal(inputXMLFile);
		
		//-----
		List<Macchinatype> listaMacchine = dati.getMacchina();
		
		//verifichiamo che le macchine abbiano nomi validi
		if (listaMacchine.get(0).getNome().equals("") || listaMacchine.get(1).getNome().equals(""))
			throw new JAXBException("Errore! Le macchine hanno nomi non validi!");
		//verifichiamo che le macchine abbiano un nome diverso
		if (listaMacchine.get(0).getNome().equals(listaMacchine.get(1).getNome()))
			throw new JAXBException("Errore! Le macchine hanno lo stesso nome!");
		
		//----- verifichiamo le liste della prima macchina
		Macchinatype macchinaUno = listaMacchine.get(0);
		
		Listastatitype listaStatiUno = macchinaUno.getListastati();
		Listatransizionitype listaTransizioniUno = macchinaUno.getListatransizioni();
		
		verificaListe(listaStatiUno,listaTransizioniUno);
		
		//----- verifichiamo le liste della seconda macchina
		Macchinatype macchinaDue = listaMacchine.get(1);
		
		Listastatitype listaStatiDue = macchinaDue.getListastati();
		Listatransizionitype listaTransizioniDue = macchinaDue.getListatransizioni();
		
		verificaListe(listaStatiDue,listaTransizioniDue);
		
		//-----verifichiamo la lista delle relazioni
		List<Relazionetype> tempListaRelazioni = dati.getListarelazioni().getRelazione();
		
		//per ogni relazione...
		for (int i=0;i<tempListaRelazioni.size();i++)
		{
			Relazionetype.Transizione transizioneUno = tempListaRelazioni.get(i).getTransizione().get(0);
			Relazionetype.Transizione transizioneDue = tempListaRelazioni.get(i).getTransizione().get(1);
			
			//verificahiamo che la prima transizione sia della prima macchina
			//e la seconda della seconda
			if (transizioneUno.getMacchina().equals(macchinaUno.getNome()) 
					&& transizioneDue.getMacchina().equals(macchinaDue.getNome()))
			{
				//allora la prima macchina deve conterene la prima transizione, e la seconda macchina la seconda
				if (!contieneTransizione(listaTransizioniUno.getTransizioni(), transizioneUno)
						&& !contieneTransizione(listaTransizioniDue.getTransizioni(), transizioneDue))
				{
					throw new JAXBException("Errore! Una relazione si riferisce ad una transizione non esistente!");
				}
			}
			//oppure il contrario
			//
			else if (transizioneUno.getMacchina().equals(macchinaDue.getNome()) 
					&& transizioneDue.getMacchina().equals(macchinaUno.getNome()))
			{
				if (!contieneTransizione(listaTransizioniDue.getTransizioni(), transizioneUno)
						&& !contieneTransizione(listaTransizioniUno.getTransizioni(), transizioneDue))
				{
					throw new JAXBException("Errore! Una relazione si riferisce ad una transizione non esistente!");
				}
			}
			else
				//se no una delle macchine non coincide con i nomi.
				throw new JAXBException("Errore! Una relazione fa riferimento ad una macchina non esistente!");
		}
		
		
		
		
		
		return dati;
	}
	
	/**
	 * Questo metodo verifica alcuni parametri delle liste affinch� siano valide.
	 * @param listaStati
	 * @param listaTransizioni
	 * @return
	 */
	private void verificaListe(Listastatitype listaStati, Listatransizionitype listaTransizioni)
		throws JAXBException
	{
		
		Statotype statoIniziale = listaStati.getStatoiniziale();
		List<Statotype> tempListaStati = listaStati.getStato();
		List<Transizionitype> tempListaTransizioni = listaTransizioni.getTransizioni();
		int max = tempListaStati.size();
		
		//verifichiamo l'unicit� del nome di ogni stato...
		//...per prima cosa che lo stato iniziale non sia vuoto
		if (statoIniziale.getNome().equals(""))
			throw new JAXBException("Errore! Non ci possono essere stati con il nome vuoto!");
		for (int i=0; i<max; i++)
		{
			//...verifichiamo l'unicit� sia che sia quello iniziale
			if (tempListaStati.get(i).getNome().equals(statoIniziale.getNome()))
				throw new JAXBException("Errore! Non ci possono essere due stati con lo stesso nome!");
			//...sia che non sia vuoto
			if (tempListaStati.get(i).getNome().equals(""))
				throw new JAXBException("Errore! Non ci possono essere stati con il nome vuoto!");
			for (int y=0; y<max;y++)
			{
				//...sia che sia contenuto nella lista
				if (i!=y && tempListaStati.get(i).getNome().equals(tempListaStati.get(y).getNome()) )
					throw new JAXBException("Errore! Non ci possono essere due stati con lo stesso nome!");
					
			}
		}
		max= tempListaTransizioni.size();
		//verifichiamo l'unicit� del nome di ogni transizione e...
		for (int i=0; i<max; i++)
		{
			//...e che non sia vuoto e che...
			if (tempListaTransizioni.get(i).getNome().equals(""))
				throw new JAXBException("Errore! Non ci possono essere transizioni con il nome vuoto!");
			for (int y=0; y<max;y++)
			{
				if (i!=y && tempListaTransizioni.get(i).getNome().equals(tempListaTransizioni.get(y).getNome()) )
					throw new JAXBException("Errore! Non ci possono essere due transizioni con lo stesso nome!");
					
			}
			//...che gli stati iniziali e finali siano nella lista degli stati
			// (o che siano quello iniziale)
			if (!contieneStato(tempListaStati,tempListaTransizioni.get(i).getStatoiniziale()) &&
					! statoIniziale.getNome().equals(tempListaTransizioni.get(i).getStatoiniziale().getNome()))
				throw new JAXBException("Errore! Una transizione fa riferimento ad uno stato non esistente: "+statoIniziale.getNome()+" != "+ tempListaTransizioni.get(i).getStatoiniziale().getNome()+"!");
			if (!contieneStato(tempListaStati,tempListaTransizioni.get(i).getStatofinale()) &&
					! statoIniziale.getNome().equals(tempListaTransizioni.get(i).getStatofinale().getNome()))
				throw new JAXBException("Errore! Una transizione fa riferimento ad uno stato non esistente!");
		}
	}
	
	/**
	 * Siccome il metodo <code>contains</code> dell'interfaccia <code>List</code> verifica l'uguaglianza
	 * stretta degli oggetti, mentre noi dobbiamo solo verificare l'uguaglianza dei nomi, useremo questo
	 * metodo per verificare che uno stato sia presente in una lista.
	 * @param listaStati
	 * @param stato
	 * @return
	 */
	private boolean contieneStato(List<Statotype> listaStati, Statotype stato)
	{
		boolean risultato = false;
		for (int i = 0; i<listaStati.size();i++)
		{
			if (listaStati.get(i).getNome().equals(stato.getNome()))
				risultato=true;
			//DEBUG: System.out.println(listaStati.get(i).getNome()+" == "+stato.getNome());
		}
		
		return risultato;		
	}
	
	/**
	 * Come per il metodo <code>contieneStato</code>, ma per le transazioni
	 * @param listaTransizioni
	 * @param transizione
	 * @return
	 */
	private boolean contieneTransizione(List<Transizionitype> listaTransizioni, Relazionetype.Transizione transizione)
	{
		boolean risultato=false;
		for (int i = 0; i<listaTransizioni.size();i++)
		{
			if (listaTransizioni.get(i).getNome().equals(transizione.getNome()))
				risultato=true;
		}
		
		return risultato;
	}

	/**
	 * 
	 * @param tempListaStatiUno
	 * @param nome
	 * @return
	 */
	private Stato cercaStato(List<Stato> listaStati, String nome) 
	{
		for(int i=0;i<listaStati.size();i++)
		{
			//DEBUG: System.out.println(listaStati.get(i).getNome()+" == "+nome);
			if (listaStati.get(i).getNome().equals(nome))
				return listaStati.get(i);
		}
		return null;
	}
	
	/**
	 * 
	 * @param listaTransazioni
	 * @param nome
	 * @return
	 */
	private Transazione cercaTransizione(List<Transazione> listaTransazioni, String nome)
	{
		for(int i=0;i<listaTransazioni.size();i++)
		{
			if (listaTransazioni.get(i).getNome().equals(nome))
				return listaTransazioni.get(i);
		}
		return null;
	}
	
	/**
	 * Metodo di debug per verificare che il file contenente lo schema sia effettivamente leggibile.
	 * @return
	 */
	public boolean canReadSchemaFile()
	{
		return schema.canRead();
	}

	/**
	 * 
	 * @return
	 */
	public String getSchemaPath() 
	{
		return schema.getAbsolutePath();
	}

}
