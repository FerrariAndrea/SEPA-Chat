package it.unibo.arces.wot.sepa.apps.benchmarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import it.unibo.arces.wot.sepa.apps.chat.DeleteAll;
import it.unibo.arces.wot.sepa.apps.chat.UserRegistration;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomManager;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomClientWrapper;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomComunicationType;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class BenchmarkerSeparateGraph implements IBenchmarker{

	private int privateRoomCount=0;
	private int freeRoomCount;
	private int clientCount;
	private int messaggeCount;
	
	
	/*
	 * privateRoomCount 	-->numero di comunicazioni 1-1 (ognina genera un nuovo grafo)
	 * freeRoomCount 		-->numero di comunicazioni N-N (ognuna genera un nuovo grafo)
	 * clientCount			-->numero di clienti totali, un untente partecipa sia ad una freeRoom che PrivateRoom
	 * messaggeCount		-->numero di messaggi inviati da ogni utente in ogni stanza
	 */
	
	private static Users users;
	private static List<RoomClient> roomClients = new ArrayList<RoomClient>();
	private static HashMap<String,RoomClientWrapper> cleints =new  HashMap<String,RoomClientWrapper>();
	private static RoomManager rm;
	private static RoomChatMonitor monitor;
	//private static JSAPProvider cfg;
	
	public BenchmarkerSeparateGraph( int freeRoomCount, int clientCount,int messaggeCount) {
		super();
		this.freeRoomCount = freeRoomCount;
		this.messaggeCount = messaggeCount;		
		this.clientCount =clientCount;

		System.out.println("BenchmarkerSeparateGraph[client->"+clientCount+"; msg->"+messaggeCount+"; free->"+freeRoomCount+"]");
		
	}

	
	@Override
	public boolean init() {
		try {
			//cfg = new JSAPProvider();
			rm= new RoomManager();
			clear();
			registerClients();	
			users = new Users();
			users.joinChat();
			HashMap<String, RoomComunicationType>  monitorUsers = new HashMap<String, RoomComunicationType> ();
			
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
						
						RoomClient rc1=new RoomClient(rct1,users, messaggeCount);
						roomClients.add(rc1);
						monitorUsers.put(rc1.getMonitorId(),rct1);
						rm.enter(rct1.getRoomUri());//sorta di registrazione nella room (è solo un contatore)
						
						RoomClient rc2=new RoomClient(rct2,users, messaggeCount);
						roomClients.add(rc2);
						monitorUsers.put(rc2.getMonitorId(),rct2);
						rm.enter(rct2.getRoomUri());						
					}				
				}
			}
			privateRoomCount=roomClients.size();
			//room di gruppo
			
			for(int x =0;x< freeRoomCount;x++) {
					for(int z=0;z<users.getUsers().size();z++) {
							RoomComunicationType freerct=new RoomComunicationType(temp[z],"roomFree_"+ x);
							freerct.setRoomUri(rm.create(freerct));
							RoomClient rc=new RoomClient(freerct,users, messaggeCount);
							roomClients.add(rc);						
							monitorUsers.put(rc.getMonitorId(),freerct);
							rm.enter(freerct.getRoomUri());//sorta di registrazione nella room (è solo un contatore)					
					}	
				
			}		
			rm.closeAll();
			
			monitor = new RoomChatMonitor(monitorUsers, messaggeCount, users.getUsers().size());		
			for (RoomClient c : roomClients) {
				c.setMonitor(monitor);
			}
			//per la nuova versione conl stesso numero di thread su entrambi i test
			for (RoomClient rc : roomClients) {
				if(cleints.containsKey(rc.getUser())) {
					RoomClientWrapper temprcw =cleints.get(rc.getUser());
					temprcw.add(rc);
					cleints.put(rc.getUser(),temprcw);
				}else {
					cleints.put(rc.getUser(),new RoomClientWrapper(rc));
				}
			}
			

			System.out.println("N-thread: "+ cleints.size());
			return true;
		}catch (Exception e) {
			System.out.println("Init-Error: "+ e.getMessage());
			return false;
		}


		
	}

	@Override	
	public boolean runTest() {
		//old version (numero thread diverso tra i 2 test)
		/*
		try {
			//start chatting
			for (RoomClient client : roomClients) {
				Thread th = new Thread(client);
				th.start();
			}
			
			monitor.monitor();
			return true;
		}catch (Exception e) {
			System.out.println("Run-Error: "+ e.getMessage());
			return false;
		}
		*/
		try {
			//start chatting
			for (RoomClientWrapper client : cleints.values()) {
				Thread th = new Thread(client);
				th.start();
			}			
			monitor.monitor();
			return true;
		}catch (Exception e) {
			System.out.println("Run-Error: "+ e.getMessage());
			return false;
		}
	}


	
	//----------------------------------------------------------------------
	private  void clear() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		System.out.println("Start clear...");
		DeleteAll clients = new DeleteAll();
		clients.clean();
		clients.close();
		rm.deleteAllRoom();	
		System.out.println("End clear");
	}

	private  void registerClients() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
	
		System.out.println("Register bots...");
		UserRegistration registration = new UserRegistration();
		for (int i = 0; i < clientCount; i++) {
			registration.register("ChatBot" + i);
		}
		registration.close();
		System.out.println("End register bots");
	}
	
	
}
