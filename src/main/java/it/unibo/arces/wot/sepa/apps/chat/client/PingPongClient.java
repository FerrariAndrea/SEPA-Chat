package it.unibo.arces.wot.sepa.apps.chat.client;

import java.io.IOException;

import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class PingPongClient extends BasicClient {
	private int index = 0;
	
	public PingPongClient(String userURI, Users users,ChatMonitor monitor)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException, InterruptedException {
		super(userURI, users,1,monitor);
	}
	
	@Override
	public void onMessageReceived(String userUri, String messageUri, String user, String message,String time) {
		super.onMessageReceived(userUri,messageUri,user,message,time);
		sendMessage(user, "Reply #" + index++);
	}

}
