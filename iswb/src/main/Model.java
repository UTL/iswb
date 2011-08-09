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

import engine.Engine;
import engine.ListaRelazioni;
import engine.MacchinaStatiFiniti;
import engine.PassoSimulazione;
import engine.Stato;
import engine.Transizione;
public class Model {
	private File schema;
	private File xml;
	private Engine motore;
	private Dati dati;
	private MacchinaStatiFiniti macchinaUno;
	private MacchinaStatiFiniti macchinaDue;
	List<Stato> tempListaStatiUno = new ArrayList<Stato>();
	List<Stato> tempListaStatiDue = new ArrayList<Stato>();
 	private List<Transizione> tempListaTransizioniUno = new ArrayList<Transizione>();
	private List<Transizione> tempListaTransizioniDue = new ArrayList<Transizione>();


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

	public void engine() throws JAXBException, SAXException{
		//try {
			dati = parse (xml);
			
			macchinaUno = new MacchinaStatiFiniti(dati.getMacchina().get(0).getNome());
			macchinaDue = new MacchinaStatiFiniti(dati.getMacchina().get(1).getNome());
			motore = parseToEngine();
		/*} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public Engine parseToEngine () throws JAXBException, SAXException
	{
		
		int max;
		
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
		
		max=dati.getMacchina().get(0).getListatransizioni().getTransizioni().size();
		for (int i=0; i<max;i++)
		{
			//stato temporaneo: quello finale della transizione
			Stato tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatofinale().getNome());
			//costruttore temporaneo: quello della transizione (la macchina, il suo nome, lo stato temporaneo)
			Transizione tempTransizione = new Transizione(macchinaUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getNome(),tempStato);
			//aggiungiamo alla lista temporanea
			tempListaTransizioniUno.add(tempTransizione);
			//aggiungiamo la transizione al suo stato iniziale
			tempStato = cercaStato(tempListaStatiUno,dati.getMacchina().get(0).getListatransizioni().getTransizioni().get(i).getStatoiniziale().getNome());
			
			tempStato.addTransizione(tempTransizione);
		}
		
		
		max=dati.getMacchina().get(1).getListatransizioni().getTransizioni().size();
		for (int i=0; i<max;i++)
		{
			Stato tempStato = cercaStato(tempListaStatiDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getStatofinale().getNome());
			Transizione tempTransizione = new Transizione(macchinaDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getNome(),tempStato);
			tempListaTransizioniDue.add(tempTransizione);
			cercaStato(tempListaStatiDue,dati.getMacchina().get(1).getListatransizioni().getTransizioni().get(i).getStatoiniziale().getNome()).addTransizione(tempTransizione);
		}
		
		//settiamo lo stato iniziale
		Stato inizialeUno = cercaStato(tempListaStatiUno, dati.getMacchina().get(0).getListastati().getStatoiniziale().getNome());
		Stato inizialeDue = cercaStato(tempListaStatiDue, dati.getMacchina().get(1).getListastati().getStatoiniziale().getNome());
		
		
		macchinaUno.setStatoCorrente(inizialeUno);
		macchinaDue.setStatoCorrente(inizialeDue);
		
		Engine motore = new Engine (macchinaUno,macchinaDue);

		ListaRelazioni tempListaRelazioni = new ListaRelazioni();

		max=dati.getListarelazioni().getRelazione().size();

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
	
	public void verificaListe(Listastatitype listaStati, Listatransizionitype listaTransizioni)
	throws JAXBException
{
	
	Statotype statoIniziale = listaStati.getStatoiniziale();
	List<Statotype> tempListaStati = listaStati.getStato();
	List<Transizionitype> tempListaTransizioni = listaTransizioni.getTransizioni();
	
	checkStati(statoIniziale, tempListaStati);
	checkTransizioni(statoIniziale, tempListaStati, tempListaTransizioni);
	//checkStatoIniziale(statoIniziale, true);
	//checkStatoIniziale(statoIniziale, false);
	
}
/*
	private void checkStatoIniziale(Stato statoIniziale, boolean statoMacchinaUno) {
		List<Transizione>tempListaTransizioni;
		if(statoMacchinaUno) tempListaTransizioni = tempListaTransizioniUno;
		else tempListaTransizioni = tempListaTransizioniDue;
		int max = tempListaTransizioni.size();
		
		statoIniziale.getTransazioniUscenti();
		
		for (int i=0; i<max; i++)
		{
			//if(!tempListaTransizioni.get(i).ge  getStatoiniziale().equals(statoIniziale));
		}
		
		//statoIniziale.
	}*/

	private void checkStati(Statotype statoIniziale,
			List<Statotype> tempListaStati) throws JAXBException {
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
	}

	private void checkTransizioni(Statotype statoIniziale,
			List<Statotype> tempListaStati,
			List<Transizionitype> tempListaTransizioni) throws JAXBException {
		int max;
		max= tempListaTransizioni.size();
		//verifichiamo l'unicit� del nome di ogni transizione e...
		boolean statoInizIsolato = true;
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
			if (!tempListaTransizioni.get(i).getStatoiniziale().equals(statoIniziale)){
				System.out.println("stato isolato, i="+i);
				statoInizIsolato = false;
				}
		}
		if (statoInizIsolato)
			throw new JAXBException("Errore! Lo stato iniziale e` isolato!");
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
	private Transizione cercaTransizione(List<Transizione> listaTransazioni, String nome)
	{
		for(int i=0;i<listaTransazioni.size();i++)
		{
			if (listaTransazioni.get(i).getNome().equals(nome))
				return listaTransazioni.get(i);
		}
		return null;
	}
	
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
	
		
		public String getNomeTransizione(int numeroTransizione) throws JAXBException{
			Relazionetype tempRelazione = dati.getListarelazioni().getRelazione().get(numeroTransizione);
			Transizione tempUno=null;
			Transizione tempDue=null;
			String output = tempRelazione.getTransizione().get(0).getNome();

			/*if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				if(tempListaTransizioniUno==null)System.out.println("templista null");
				else if(tempListaTransizioniDue==null)System.out.println("templista2 null");
				else if(tempRelazione==null)System.out.println("temprelazione null");
				output = tempRelazione.getTransizione().get(0).getNome();

				//cerco la prima transizione nella prima lista (e la seconda nella seconda)
				*/
				
			
				//output += "\n"+getTransizione(tempRelazione, true).toString();
				output += " - ";
				output += getTransizione(tempRelazione, false).toString();
				//}
			return output;
		}
		
		private Transizione getTransizione(Relazionetype tempRelazione, boolean transizioneUno) throws JAXBException{
			Transizione tempUno=null;
			Transizione tempDue=null;
			Transizione output= null;
			if (tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaDue.getNome()) )
			{
				tempUno=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(1).getNome());
				

			}
			else if (tempRelazione.getTransizione().get(1).getMacchina().equals(macchinaUno.getNome())
					&& tempRelazione.getTransizione().get(0).getMacchina().equals(macchinaDue.getNome()))
			{
				//System.out.println("Cond due");
				tempUno=cercaTransizione(tempListaTransizioniDue, tempRelazione.getTransizione().get(0).getNome());
				tempDue=cercaTransizione(tempListaTransizioniUno, tempRelazione.getTransizione().get(1).getNome());
			}
			else 
				throw new JAXBException("Errore! Non possono esistere stati senza transizioni!");
			
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
	
		public void eseguiPasso(int letto){
			List<PassoSimulazione> passi;
			passi = motore.getPassiAttivi();
			motore.esegui(passi.get(letto-1));
			//motore.esegui(passi.get(letto-1));

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
