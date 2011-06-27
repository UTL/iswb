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
			Relazionetype.TempTransizione transizioneUno = tempListaRelazioni.get(i).getTransizione().get(0);
			Relazionetype.TempTransizione transizioneDue = tempListaRelazioni.get(i).getTransizione().get(1);
			
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
	 * Questo metodo verifica alcuni parametri delle liste affinche` siano valide.
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
		
		//verifichiamo l'unicita` del nome di ogni stato...
		//...per prima cosa che lo stato iniziale non sia vuoto
		if (statoIniziale.getNome().equals(""))
			throw new JAXBException("Errore! Non ci possono essere stati con il nome vuoto!");
		for (int i=0; i<max; i++)
		{
			//...verifichiamo l'unicita` sia che sia quello iniziale
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
		//verifichiamo l'unicita` del nome di ogni transizione e...
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
	private boolean contieneTransizione(List<Transizionitype> listaTransizioni, Relazionetype.TempTransizione transizione)
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
	private Transizione cercaTransizione(List<Transizione> listaTransazioni, String nome)
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
