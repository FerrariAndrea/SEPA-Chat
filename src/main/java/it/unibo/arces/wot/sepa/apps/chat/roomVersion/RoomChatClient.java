package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.ichat.IMessageHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;


/**
 * A chat client is composed by three SEPA clients:
 * 1) Sender : it sends a message to a client
 * 2) Receiver: it receives notifications about messages that have been sent or removed. It marks messages that have been sent
 * to the client as received.
 * 3) Remover: it removes messages that have been received 
 * */
public abstract class RoomChatClient implements Runnable,IMessageHandler {
	protected static final Logger logger = LogManager.getLogger();
	
	protected SenderRoom sender;
	private ReceiverRoom receiver;
	private RemoverRoom remover;
	
	public RoomChatClient(String userURI,String room) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException, InterruptedException {
		sender = new SenderRoom(userURI,this);
		receiver = new ReceiverRoom(userURI,room,this);
		remover = new RemoverRoom(userURI,room,this);
		
		do {
			logger.info(userURI + " joining the chat...");
			try {
				joinChat();
			} catch (SEPASecurityException | IOException | SEPAPropertiesException | SEPAProtocolException
					| InterruptedException | SEPABindingsException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					return;
				}
				continue;
			}
			break;
		} while (true);

		logger.info(userURI + " chat joined!");
	}
	
	public void joinChat() throws SEPASecurityException, IOException, SEPAPropertiesException, SEPAProtocolException, InterruptedException, SEPABindingsException {
		remover.joinChat();
		receiver.joinChat();
	}

	public void leaveChat() throws SEPASecurityException, IOException, SEPAPropertiesException, SEPAProtocolException, InterruptedException {
		remover.leaveChat();
		receiver.leaveChat();
	}

	public boolean sendMessage(String receiverURI,String room,String message) {
		return sender.sendMessage(receiverURI,room,message);
	}
	public boolean sendPublicMessage(String receiverURI,String room,String message) {
		return sender.sendPublicMessage( receiverURI, room, message);
	}
}
