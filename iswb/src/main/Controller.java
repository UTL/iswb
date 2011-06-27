package main;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import it.unibs.fp.mylib.InputDati;

public class Controller {
	
	static String path = "docs/xsd/schema.xsd";
	
	public static void main(String[] args){
		View vista = new View();
		Model modello = new Model(path);
		
		vista.printStart();
		
		boolean schemaLeggibile= modello.canReadSchemaFile();
		
		//secondo argomento true se e' un file schema, false se e' xml
		vista.printFileStatus(schemaLeggibile, true, path);
		
		String nome="";
		while (true) {
			if (args.length < 1)
			{
				vista.askInputFile();
				nome= InputDati.leggiStringa();
				if(nome.equals("0"))
					System.exit(0);
				
				if (!nome.startsWith(File.separator))
					nome=File.separator+nome;
				//modello.setXml(new File (System.getProperty("user.dir")+nome));
				
			}
			else
			{
				//controlliamo che sia un percorso relativo
				if (!args[0].startsWith(File.separator))
					nome=File.separator+args[0];
			}	
			
			modello.setXml(new File(System.getProperty("user.dir") + nome));
	
			boolean xmlLeggibile= modello.canReadXmlFile();
			
			//secondo argomento true se e' un file schema, false se e' xml
			vista.printFileStatus(xmlLeggibile, false, nome);
			if(!xmlLeggibile || !schemaLeggibile){
				if(args.length < 1)
					continue;
				System.exit(-1);
			}
			else 
				break;
		}
			
		modello.engine();
		System.out.println("START DEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUG");
		
		
		vista.printFileContent(modello);
		
		
		try {
			vista.printRelazioni(modello);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			vista.printRelError();
			System.exit(-1);
		}
		System.out.println("END DEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUG");

		//TODO: togliere i print da parsetoengine
		//TODO: aggiungere attributi a model per robaccia in parsetoengine
		int letto = 0;
		
		do{
			vista.printCurrentState(modello.statiCorrenti());
			vista.printActiveTransitions(modello.getPassiAttivi());
			
			if(modello.getPassiAttivi().size() == 0)
				letto=0;
			else
				letto=InputDati.leggiIntero("Digita un valore (0 per terminare): ", 0, modello.getPassiAttivi().size());
			
			if (letto != 0){
				vista.printEseguo();
				System.out.print(modello.getPassiAttivi().get(letto-1).toString());
				modello.eseguiPasso(letto);
			}
		}
		while(letto!=0);
	}
		
	//lInputDati.leggiIntero("Digita un valore: ", 0, passi.size());

}
