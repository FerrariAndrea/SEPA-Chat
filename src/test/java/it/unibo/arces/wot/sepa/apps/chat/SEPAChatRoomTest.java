package it.unibo.arces.wot.sepa.apps.chat;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.arces.wot.sepa.apps.chat.client.BasicClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomManager;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomComunicationType;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class SEPAChatRoomTest {
	private static final Logger logger = LogManager.getLogger();
	

	private static int N_CLIENTS = 10;
	private static int BASE = 0;
	private static int MESSAGES = 10;
	private static int FREE_ROOMS = 2; //room non private, di gruppo
	private static Users users;
	private static List<RoomClient> clients = new ArrayList<RoomClient>();
	private static RoomManager rm;
	private static RoomChatMonitor monitor;

	private static JSAPProvider cfg;

	@BeforeClass
	public static void init() throws SEPASecurityException, SEPAPropertiesException, SEPAProtocolException, IOException, InterruptedException, SEPABindingsException {
		cfg = new JSAPProvider();

		BASE = cfg.getJsap().getExtendedData().get("base").getAsInt();
		N_CLIENTS = cfg.getJsap().getExtendedData().get("clients").getAsInt();
		MESSAGES = cfg.getJsap().getExtendedData().get("messages").getAsInt();
		FREE_ROOMS = cfg.getJsap().getExtendedData().get("free_rooms").getAsInt();
		
	
		rm= new RoomManager();
		clear();
		registerClients();	
		users = new Users();


		
	}

	@Test // (timeout = 5000)
	public void chatRoomsTest() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException,
			InterruptedException, IOException, SEPABindingsException {
		/*
		 * Ogni coppia di clienti ha una stanza in cui si scambiano dei messaggi 
		 */
	
		users.joinChat();
		
		//da modificare anche il monitor, il numero di MESSAGES non è lo stesso per ogni room-utente, le freeroom ne hanno di più
		HashMap<String, RoomComunicationType>  monitorUsers = new HashMap<String, RoomComunicationType> ();
		
		
		//creazione logica di scambio messaggi con le room 
		//(tramite RoomComonucationType che definisce 1 comunicazione appoggiata a una room)		
	
		//String prefixRoom = "http://wot.arces.unibo.it/chat/";
		//una room per ogni coppia di utenti
		String temp[] = new String[users.getUsers().size()];
		int k = 0;
		for (Iterator<String> it = users.getUsers().iterator(); it.hasNext(); ) {
			temp[k] = it.next();
			k++;
		}
		Set<String> tempKey = new HashSet<String>();
		for(int x=0;x<users.getUsers().size();x++) {
			for(int y=0;y<users.getUsers().size();y++) {
				if(x!=y && !tempKey.contains(x+"-"+y)) {
					String shortName = "room_"+x+"_"+ y;
					RoomComunicationType rct1=new RoomComunicationType(temp[x],shortName, temp[y],users.getUserName( temp[y]));
					rct1.setRoomUri(rm.create(rct1));
					RoomComunicationType rct2=new RoomComunicationType(temp[y],shortName, temp[x],users.getUserName( temp[x]));
					rct2.setRoomUri(rm.create(rct2));
					
					tempKey.add(x+"-"+y);
					tempKey.add(y+"-"+x);
					
					RoomClient rc1=new RoomClient(rct1,users, MESSAGES);
					clients.add(rc1);
					monitorUsers.put(rc1.getMonitorId(),rct1);
					rm.enter(rct1.getRoomUri());//sorta di registrazione nella room (è solo un contatore)
					
					RoomClient rc2=new RoomClient(rct2,users, MESSAGES);
					clients.add(rc2);
					monitorUsers.put(rc2.getMonitorId(),rct2);
					rm.enter(rct2.getRoomUri());
				}				
			}
		}
		//room di gruppo
		
		for(int x =0;x< FREE_ROOMS;x++) {
				for(int z=0;z<users.getUsers().size();z++) {
						RoomComunicationType freerct=new RoomComunicationType(temp[z],"roomFree_"+ x);
						freerct.setRoomUri(rm.create(freerct));
						RoomClient rc=new RoomClient(freerct,users, MESSAGES);
						clients.add(rc);						
						monitorUsers.put(rc.getMonitorId(),freerct);
						rm.enter(freerct.getRoomUri());//sorta di registrazione nella room (è solo un contatore)					
				}	
			
		}		
		
		//for (RoomComunicationType r : rooms) {
		//	System.out.println(r.getRoom());
		//}
		
		
		try {
			rm.closeAll();
		} catch (IOException e) {
			assertFalse("createRooms", true);
		}
		
		

	
		System.out.println("User count : "+users.getUsers().size());			
		System.out.println("Room count : "+clients.size());		
		
		monitor = new RoomChatMonitor(monitorUsers, MESSAGES,users.getUsers().size());		
		for (RoomClient c : clients) {
			c.setMonitor(monitor);
		}
		
		//start chatting
		for (RoomClient client : clients) {
			Thread th = new Thread(client);
			th.start();
		}
		
		monitor.monitor();
		
	}

	private static void clear() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
		System.out.println("Start clear...");
		DeleteAll clients = new DeleteAll();
		clients.clean();
		try {
			clients.close();
		} catch (IOException e) {
			assertFalse("deleteAllClients", true);
		}
		rm.deleteAllRoom();	
		System.out.println("End clear");
	}

	private static void registerClients() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
	
		System.out.println("Register bots...");
		UserRegistration registration = new UserRegistration();
		for (int i = BASE; i < BASE + N_CLIENTS -1; i++) {
			logger.info("Register client: "+"ChatBot" + i);
			registration.register("ChatBot" + i);
		}
		try {
			registration.close();
		} catch (IOException e) {
			assertFalse("registerClients", true);
		}
		System.out.println("End register bots");
	}
	
}
