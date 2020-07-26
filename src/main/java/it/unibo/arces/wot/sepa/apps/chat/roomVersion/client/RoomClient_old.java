package it.unibo.arces.wot.sepa.apps.chat.roomVersion.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.ChatClient;
import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
/*
public class RoomClient extends RoomChatClient {

	private static final Logger logger = LogManager.getLogger();

	protected String user;
	private RoomComunicationType rct;
	private int messages = 10;
	private ChatMonitor monitor;
	
	public RoomClient(String userURI, RoomComunicationType rct,int messages,ChatMonitor monitor)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException, InterruptedException {
		super(userURI,rct.getRoomUri(),messages,monitor);

		this.user = userURI;
		this.rct = rct;
		this.messages = messages;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		int n = 0;
		
		for (int i = 0; i < messages; i++) {	
			try {
				sendMessage(this.rct.getReceiver(),this.rct.getRoomUri(), "MSG #" + i);
				
			}catch(Exception e) {
				System.out.println("Error send: "+ e.getMessage()+ ". "
						+ " \n\t{reciver: "+this.rct.getReceiver()+" "
								+ " \n\troom: "+this.rct.getRoomUri()+"}");
			}
			logger.debug(this.rct.getReceiverName() + " SEND MESSAGE (" + i + "/" + messages+ ") at room: " + this.rct.getRoom());
		}

		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}

	public String getMonitorId() {
		return user+"-"+rct.getReceiverName()+"-"+rct.getRoom();
	}
	@Override
	public void onMessageReceived(String userUri, String messageUri, String name, String message,String time) {
		monitor.messageReceived(getMonitorId());
	}

	@Override
	public void onMessageRemoved(String userUri, String messageUri, String name, String message, String time) {
		monitor.messageRemoved(getMonitorId());
	}

	@Override
	public void onMessageSent(String userUri, String messageUri, String time) {
		monitor.messageSent(getMonitorId());
	}

	@Override
	public void onRemoverBrokenConnection(String userUri) {
		monitor.brokenConnectionRemover(getMonitorId());
	}

	@Override
	public void onReceiverBrokenConnection(String userUri) {
		monitor.brokenConnectionReceiver(getMonitorId());		
	}

}
*/