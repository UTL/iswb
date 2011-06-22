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
		
		if (args.length!=1)
		{
			vista.askInputFile();
			nome= InputDati.leggiStringa(); 
			
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
			System.exit(-1);
		}
		
		modello.engine();
		System.out.println("DEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUG");

		vista.printFileContent(modello);
		
		vista.printRelazioni(modello);
		System.out.println("DEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUG");

		//TODO: togliere i print da parsetoengine
		//TODO: aggiungere attributi a model per robaccia in parsetoengine
		
		
		vista.printCurrentState(modello.statiCorrenti());
		vista.printActiveTransitions(modello.getPassiAttivi());
		InputDati.leggiIntero("Digita un valore: ", 0, modello.getPassiAttivi().size());
	}
		
	//lInputDati.leggiIntero("Digita un valore: ", 0, passi.size());

}
