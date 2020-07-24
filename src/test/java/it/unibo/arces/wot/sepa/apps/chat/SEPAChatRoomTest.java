package it.unibo.arces.wot.sepa.apps.chat;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.arces.wot.sepa.apps.chat.client.BasicClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomChatClient;
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
	private static ArrayList<RoomComunicationType> rooms;
	private static Users users;
	private static List<RoomChatClient> clients = new ArrayList<RoomChatClient>();
	private static RoomManager rm;
	private static ChatMonitor monitor;

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
		users.joinChat();
		
		//creazione logica di scambio messaggi con le room 
		//(tramite RoomComonucationType che definisce 1 comunicazione appoggiata a una room)		
		rooms = new ArrayList<RoomComunicationType>();	
		//String prefixRoom = "http://wot.arces.unibo.it/chat/";
		//una room per ogni coppia di utenti
		String temp[] = new String[users.getUsers().size()];
		int k = 0;
		for (Iterator<String> it = users.getUsers().iterator(); it.hasNext(); ) {
			temp[k] = it.next();
			k++;
		}
	
		for(int x=0;x<users.getUsers().size();x++) {
			for(int y=0;y<users.getUsers().size();y++) {
				if(x!=y) {
					String shortName = "room_"+x+"_"+ "y";
					rooms.add(new RoomComunicationType(shortName, temp[x],x+"."+y));
				}				
			}
		}
		//room di gruppo
		for(int x =0;x< FREE_ROOMS;x++) {
			rooms.add(new RoomComunicationType("FreeRoom"+ x));
		}		
		createRooms();	
	}

	@Test // (timeout = 5000)
	public void chatRoomsTest() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException,
			InterruptedException, IOException, SEPABindingsException {
		/*
		 * Ogni coppia di clienti ha una stanza in cui si scambiano dei messaggi 
		 */
	
		
		//da modificare anche il monitor, il numero di MESSAGES non è lo stesso per ogni room-utente, le freeroom ne hanno di più
		Set<String> monitorUsers = new HashSet<String>();
		
	
		String temp[] = new String[users.getUsers().size()];
		int k = 0;
		for (Iterator<String> it = users.getUsers().iterator(); it.hasNext(); ) {
			temp[k] = it.next();
			k++;
		}
	
		int index = 0;
		for(int x=0;x<users.getUsers().size();x++) {
			for(int y=0;y<users.getUsers().size();y++) {
				if(x!=y) {			
					RoomClient rc1=new RoomClient(temp[x], rooms.get(index), MESSAGES,monitor);
					clients.add(rc1);
					monitorUsers.add(rc1.getMonitorId());
					rm.enter(rooms.get(index).getRoom());//sorta di registrazione nella room (è solo un contatore)
					
					RoomClient rc2=new RoomClient(temp[y], rooms.get(index), MESSAGES,monitor);
					clients.add(rc2);
					monitorUsers.add(rc2.getMonitorId());
					rm.enter(rooms.get(index).getRoom());//sorta di registrazione nella room (è solo un contatore)
					index++;
				}				
			}
		}
		//room di gruppo
		for(int x =0;x< FREE_ROOMS;x++) {
			for(int z=0;z<users.getUsers().size();z++) {
					RoomClient rc=new RoomClient(temp[z], rooms.get(index), MESSAGES,monitor);
					clients.add(rc);
					monitorUsers.add(rc.getMonitorId());	
					rm.enter(rooms.get(index).getRoom());//sorta di registrazione nella room (è solo un contatore)					
			}
			index++;		
		}	
		
		monitor = new ChatMonitor(monitorUsers, MESSAGES);
		
		
		
		//start chatting
		for (RoomChatClient client : clients) {
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
		//rm.deleteAllRoom(rooms);	 room null point	
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
	private static void createRooms() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
		System.out.println("Create rooms...");
		for (RoomComunicationType r : rooms) {
			rm.create(r);
		}
		try {
			rm.closeAll();
		} catch (IOException e) {
			assertFalse("createRooms", true);
		}
		System.out.println("End create rooms");
	}
}
