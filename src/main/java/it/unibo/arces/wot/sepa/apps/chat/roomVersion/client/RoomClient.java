package it.unibo.arces.wot.sepa.apps.chat.roomVersion.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.ChatClient;
import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.Sender;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.apps.chat.client.BasicClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomChatClient;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.RoomChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.SenderRoom;
import it.unibo.arces.wot.sepa.apps.ichat.IMessageHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;


public class RoomClient extends RoomChatClient {
	private static final Logger logger = LogManager.getLogger();

	protected String user;
	protected Users users;
	protected int messages = 10;
	protected RoomChatMonitor monitor;
	private RoomComunicationType rct;
	public RoomClient(RoomComunicationType rct, Users users,int messages)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException, InterruptedException {
		super(rct.getSender(),rct.getRoomUri());
		
		this.rct=rct;
		this.user = rct.getSender();
		this.users = users;
		this.messages = messages;
		
	}
	
	public void setMonitor(RoomChatMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() {

		int n = 0;
		
		for (int i = 0; i < messages; i++) {
			if(rct.isFreeRoom()) {
				logger.debug(users.getUserName(user) +"@"+rct.getRoomUri()+ " SEND MESSAGE (" + n + "/" +messages+ messages  +")");
				sendMessage("http://wot.arces.unibo.it/chat/ALL",rct.getRoomUri(), "MSG #" + n);
			}else {
				logger.debug(users.getUserName(user) +"@"+rct.getRoomUri()+ " SEND MESSAGE (" + n + "/"  +messages+ messages +")");
				sendMessage(rct.getReceiver(),rct.getRoomUri(), "MSG #" + n);
			}
			n++;
		}

		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void onMessageReceived(String userUri, String messageUri, String name, String message,String time) {
		if(this.monitor!=null) {
			monitor.messageReceived(this.getMonitorId());
		}
	}

	@Override
	public void onMessageRemoved(String userUri, String messageUri, String name, String message, String time) {
		if(this.monitor!=null) {
			monitor.messageRemoved(this.getMonitorId());
		}
	}

	@Override
	public void onMessageSent(String userUri, String messageUri, String time) {
		if(this.monitor!=null) {
			monitor.messageSent(this.getMonitorId());
		}
	}

	@Override
	public void onRemoverBrokenConnection(String userUri) {
		if(this.monitor!=null) {
			monitor.brokenConnectionRemover(this.getMonitorId());
		}
	}

	@Override
	public void onReceiverBrokenConnection(String userUri) {
		if(this.monitor!=null) {
			monitor.brokenConnectionReceiver(this.getMonitorId());
		}
		
	}
	public String getMonitorId() {
		return user+"-"+rct.getReceiverName()+"-"+rct.getRoom();
	}
}
