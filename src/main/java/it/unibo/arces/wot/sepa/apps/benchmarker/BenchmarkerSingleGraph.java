package it.unibo.arces.wot.sepa.apps.benchmarker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.arces.wot.sepa.apps.chat.ChatClient;
import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.DeleteAll;
import it.unibo.arces.wot.sepa.apps.chat.UserRegistration;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.apps.chat.client.BasicClient;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class BenchmarkerSingleGraph implements IBenchmarker{


	private int clientCount;
	private int messaggeCount;
	
	

	private static Users users;
	private static List<ChatClient> clients = new ArrayList<ChatClient>();
	private static ChatMonitor monitor;
	
	public BenchmarkerSingleGraph( int clientCount,int messaggeCount) {
		super();
		this.messaggeCount = messaggeCount;		
		this.clientCount =clientCount;		
	}

	
	@Override
	public boolean init() {
		try {
			deleteAllClients();
			registerClients();
			users = new Users();
			users.joinChat();			
			monitor = new ChatMonitor(users.getUsers(), messaggeCount);			
			for (String user : users.getUsers()) {
				ChatClient client = new BasicClient(user, users, messaggeCount,monitor);
				clients.add(client);
			}
			
			return true;
		}catch (Exception e) {
			System.out.println("Init-Error: "+ e.getMessage());
			return false;
		}


		
	}

	
	@Override	
	public boolean runTest() {
		try {
				//start chatting
				for (ChatClient client : clients) {
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

	private  void deleteAllClients() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		DeleteAll client = new DeleteAll();
		client.clean();
		client.close();
	}

	private  void registerClients() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		// Register chat BOTS
		UserRegistration registration = new UserRegistration();
		for (int i = 0; i < clientCount; i++) {
			registration.register("ChatBot" + i);
		}
		registration.close();
	}
	
}
