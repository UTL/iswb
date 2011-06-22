package main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import bindings.Dati;
import bindings.Listastatitype;
import bindings.Listatransizionitype;
import bindings.Macchinatype;
import bindings.Relazionetype;
import bindings.Statotype;
import bindings.Tipotransizione;
import bindings.Transizionitype;

import engine.Asincrone;
import engine.Engine;
import engine.ListaRelazioni;
import engine.MacchinaStatiFiniti;
import engine.MutuamenteEsclusive;
import engine.PassoSimulazione;
import engine.Sincrone;
import engine.Stato;
import engine.Transazione;
public class Model {
	private File schema;
	private File xml;
	private Engine motore;
	private Dati dati;
	private MacchinaStatiFiniti macchinaUno;
	private MacchinaStatiFiniti macchinaDue;
	List<Stato> tempListaStatiUno = new ArrayList<Stato>();
	List<Stato> tempListaStatiDue = new ArrayList<Stato>();
 	private List<Transazione> tempListaTransizioniUno = new ArrayList<Transazione>();
	private List<Transazione> tempListaTransizioniDue = new ArrayList<Transazione>();


	/*public MacchinaStatiFiniti getMacchina(int i){
		String nomeMacchina = "";
		
		if (i == 1)
			nomeMacchina = getNomeMacchinaUno();
		else if (i == 2)
			nomeMacchina = getNomeMacchinaDue();
		MacchinaStatiFiniti macchina = null;
		
		macchina = 
		
	}*/
	
	public Model(String path){
		schema = new File(path);	
	}
	
	public boolean canReadSchemaFile()
	{
		return schema.canRead();
	}
	
	public boolean canReadXmlFile()
	{
		return xml.canRead();
	}
	
	public void setXml(File fileXml){
		xml = fileXml;
	}

	public void engine(){
		try {
			dati = parse (xml);
			
			macchinaUno = new MacchinaStatiFiniti(dati.getMacchina().get(0).getNome());
			macchinaDue = new MacchinaStatiFiniti(dati.getMacchina().get(1).getNome());
			motore = parseToEngine();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Engine parseToEngine () throws JAXBException, SAXException
	{
		
		//parsiamo il file
		//Dati dati = parse (xml);
		int max;
		
		//creiamo le due macchine
		//MacchinaStatiFiniti macchinaUno = new MacchinaStatiFiniti(dati.getMacchina().get(0).getNome());
		//MacchinaStatiFiniti macchinaDue = new MacchinaStatiFiniti(dati.getMacchina().get(1).getNome());
		
		//creaimo una lista temporanea di tutti gli stati della prima macchina. 
		//List<Stato> tempListaStatiUno = new ArrayList<Stato>();
		max=dati.getMacchina().get(0).getListastati().getStato().size();
		tempListaStatiUno.add(new Stato(macchinaUno, dati.getMacchina().get(0).getListastati().getStatoiniziale().getNome()));
		for (int i =0; i<max; i++)
		{
			tempListaStatiUno.add(new Stato(macchinaUno,dati.getMacchina().get(0).getListastati().getStato().get(i).getNome()));
		}
		
		//creaimo una lista temporanea di tutti gli stati della seconda macchina. 
		max=dati.getMacchina().get(1).getListastati().getStato().size();
		tempListaStatiDue.add(new Stato(macchinaDue, dati.getMacchina().get(1).getListastati().getStatoiniziale().getNome()));
		for (int i =0; i<max; i++)
		{
			tempListaStatiDue.add(new Stato(macchinaDue,dati.getMacchina().get(1).getListastati().getStato().get(i).getNome()));
		}
		
		//creiamo una lista temporanea di tutte le transizioni della prima macchina
		//List<Transazione> tempListaTransizioniUno = new ArrayList<Transazione>();
		max=dati.getMacchina().get(0).getListatransizioni().getTransizioni().size();
		for (int i=0; i<max;i++)
		{
			//stato temporaneo: quello finale della transazione
			Stato tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatofinale().getNome());
			//costruttore temporaneo: quello della transazione (la macchina, il suo nome, lo stato temporaneo)
			Transazione tempTransizione = new Transazione(macchinaUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getNome(),tempStato);
			//aggiungiamo alla lista temporanea
			tempListaTransizioniUno.add(tempTransizione);
			//aggiungiamo la transazione al suo stato iniziale
			tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatoiniziale().getNome());
			
			tempStato.addTransazione(tempTransizione);
		}
		
		//creiamo una lista temporanea di tutte le transizioni della seconda macchina
		//List<Transazione> tempListaTransizioniDue = new ArrayList<Transazione>();
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
		// -------- Copiato in view printFileContent
		/*System.out.println("Dati contenuti nel file: \n");
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
		// -------- Copiato in view END printFileContent
		//-----DEBUG END-----
		*/
		//il motore � quasi pronto. le macchine sono impostate
//////////////////////////
		Engine motore = new Engine (macchinaUno,macchinaDue);
		//preparo la lista delle relazioni
		//System.out.println("\nLista Relazioni:");
//////////////////////////
		ListaRelazioni tempListaRelazioni = new ListaRelazioni();
//////////////////////////
		max=dati.getListarelazioni().getRelazione().size();
//////////////////////////
		/*
		for (int i=0;i<max;i++)
		{
			Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(i);
			Transazione tempUno=null;
			Transazione tempDue=null;
			//se la prima transizione appartiene alla prima macchina e la seconda alla seconda
			if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				//if(tempListaTransizioniUno==null)System.out.println("templista null");
				//else if(tempListaTransizioniDue==null)System.out.println("templista2 null");
				//else if(tempRelazione==null)System.out.println("temprelazione null");
				System.out.println(tempRelazione.getTransizione().get(0).getNome());

				//cerco la prima transazione nella prima lista (e la seconda nella seconda)
				tempUno=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(0).getNome());
				System.out.println(tempUno.toString());

				tempDue=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(1).getNome());
				System.out.println(tempDue.toString());
				
				 
			}
			//altrimenti il contrario
			//DANGER QUA NON SO CHE FA E NON L'HO MESSO!!
			else if (tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaDue.getNome()))
			{
				//System.out.println("Cond due");
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
		}*/
		
		//
//////////////////////////
		motore.setRelazioni(tempListaRelazioni);
		
		return motore;
		
	}
	
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
	
	public String getNomeMacchinaUno(){
		return macchinaUno.getNome();
	}
	
	public String getNomeMacchinaDue(){
		return macchinaDue.getNome();
	}
	
	public String statiCorrenti(){
		return motore.statiCorrentiToString();
	} 
	
	public List<PassoSimulazione> getPassiAttivi(){
		return motore.getPassiAttivi();
	} 
	
	public String getNomeStato(int numeroStato, int numeroMacchina){
		String nomeStato = "";
		if (numeroMacchina == 1)
			nomeStato = tempListaStatiUno.get(numeroStato).toString();
		else nomeStato = tempListaStatiDue.get(numeroStato).toString();
		return nomeStato;
	}
	
	public int getNumeroStati(int numeroMacchina){
		int numeroStati = 0;
		if (numeroMacchina == 1)
			numeroStati = tempListaStatiUno.size();
		else
			numeroStati = tempListaStatiDue.size();
		return numeroStati;

	}
	
	// tempListaStati.get(i).getTransazioniUscenti().size()
	
	public int getNumeroTransizioniUscenti(int statoNumero, int numeroMacchina){
		int numeroTransizioniUscenti = 0;
		if (numeroMacchina == 1)
			numeroTransizioniUscenti = tempListaStatiUno.get(statoNumero).getTransazioniUscenti().size();
		else
			numeroTransizioniUscenti = tempListaStatiDue.get(statoNumero).getTransazioniUscenti().size();
		return numeroTransizioniUscenti;
	}
	
	public String getNomeTransizioneUscente(int numeroStato, int numeroTransizione, int numeroMacchina){
		String nomeTransizione = "";
		if (numeroMacchina == 1)
			nomeTransizione = tempListaStatiUno.get(numeroStato).getTransazioniUscenti().get(numeroTransizione).getNome();
		else
			nomeTransizione = tempListaStatiDue.get(numeroStato).getTransazioniUscenti().get(numeroTransizione).getNome();
		return nomeTransizione;
	}

	public String getNomeStatoArrivo(int numeroStato, int numeroTransizione, int numeroMacchina){
		String nomeStatoArrivo = "";
		if (numeroMacchina == 1)
			nomeStatoArrivo = tempListaStatiUno.get(numeroStato).getTransazioniUscenti().get(numeroTransizione).getStatoArrivo().getNome();
		else
			nomeStatoArrivo = tempListaStatiDue.get(numeroStato).getTransazioniUscenti().get(numeroTransizione).getStatoArrivo().getNome();
		return nomeStatoArrivo;
	}
	
	public int getNumeroTotaleTransizioni(){
		return dati.getListarelazioni().getRelazione().size();
	}
	
	/*Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(i);
	Transazione tempUno=null;
	Transazione tempDue=null;
	//se la prima transizione appartiene alla prima macchina e la seconda alla seconda
	if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
			&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
	{
		if(tempListaTransizioniUno==null)System.out.println("templista null");
		else if(tempListaTransizioniDue==null)System.out.println("templista2 null");
		else if(tempRelazione==null)System.out.println("temprelazione null");
		System.out.println(tempRelazione.getTransizione().get(0).getNome());

		//cerco la prima transazione nella prima lista (e la seconda nella seconda)
		tempUno=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(0).getNome());
		System.out.println(tempUno.toString());

		tempDue=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(1).getNome());
		System.out.println(tempDue.toString());*/
		
		public String getNomeTransizione(int numeroTransizione){
			Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(numeroTransizione);
			Transazione tempUno=null;
			Transazione tempDue=null;
			String output = tempRelazione.getTransizione().get(0).getNome();

			/*if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				if(tempListaTransizioniUno==null)System.out.println("templista null");
				else if(tempListaTransizioniDue==null)System.out.println("templista2 null");
				else if(tempRelazione==null)System.out.println("temprelazione null");
				output = tempRelazione.getTransizione().get(0).getNome();

				//cerco la prima transazione nella prima lista (e la seconda nella seconda)
				*/
				
			
				//output += "\n"+getTransizione(tempRelazione, true).toString();
				output += " - ";
				output += getTransizione(tempRelazione, false).toString();
				//}
			return output;
		}
		
		private Transazione getTransizione(Relazionetype tempRelazione, boolean transizioneUno){
			Transazione tempUno=null;
			Transazione tempDue=null;
			Transazione output= null;
			if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				tempUno=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(1).getNome());
			}
			else 
			{
				//System.out.println("Cond due");
				tempUno=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(1).getNome());
			}
			
			if (transizioneUno)
				output=tempUno;
			else output=tempDue;
			
			return output;
		}
		
		//public String getNomeMacchinaTransizione(int numeroTransizione){
			//Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(numeroTransizione);	
			//return tempRelazione.getNo;
		//}
		
		public Tipotransizione getTipoRelazione(int numeroTransizione){
			Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(numeroTransizione);	
			
			return tempRelazione.getTipo();
		}
	
	/*public String getNomeTransizioneUscente(){
		tempListaTransizioniUno.get(i).getStatoArrivo().getNome());	
	}*/
	
	/*DA FINIRE
	public int getListaStatiSize(int numeroMacchina){
		int dimensione = 0;
		return tempListaStatiDue.get(i).getTransazioniUscenti().size();
	}*/
}