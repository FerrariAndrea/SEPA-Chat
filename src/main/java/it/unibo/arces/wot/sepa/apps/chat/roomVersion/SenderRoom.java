package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.Sender;
import it.unibo.arces.wot.sepa.apps.ichat.IMessageHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;

public class SenderRoom extends Sender {
	protected static final Logger logger = LogManager.getLogger();

	private final String userUri;
	
	public SenderRoom(String userUri,IMessageHandler handler)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		super(userUri,handler);

		this.setUpdateBindingValue("sender", new RDFTermURI(userUri));
		
		this.userUri = userUri;
	}
	
	public boolean sendMessage(String receiverURI,String room, String text) {
		logger.debug("SEND To: " + receiverURI + " Message: " + text);

		int retry = 5;

		boolean ret = false;
		while (!ret && retry > 0) {
			try {
				this.setUpdateBindingValue("receiver", new RDFTermURI(receiverURI));
				this.setUpdateBindingValue("text", new RDFTermLiteral(text));
				this.setUpdateBindingValue("room", new RDFTermURI(room));
				ret = update().isUpdateResponse();
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException
					| SEPABindingsException e) {
				logger.error(e.getMessage());
				ret = false;
			}
			retry--;
		}
		
		if (!ret) logger.error("UPDATE FAILED sender: "+userUri+" receiver: "+receiverURI+" text: "+text + " room: "+room );

		return ret;
	}
	
	public boolean sendPublicMessage(String receiverURI,String room, String text) {
		logger.debug("SEND To: " + receiverURI + " Message: " + text);

		int retry = 5;

		boolean ret = false;
		while (!ret && retry > 0) {
			try {
				this.setUpdateBindingValue("receiver", new RDFTermURI(receiverURI));
				this.setUpdateBindingValue("text", new RDFTermLiteral(text));
				this.setUpdateBindingValue("room", new RDFTermURI(room));
				this.setUpdateBindingValue("private", new RDFTermLiteral("0"));
				ret = update().isUpdateResponse();
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException
					| SEPABindingsException e) {
				logger.error(e.getMessage());
				ret = false;
			}
			retry--;
		}
		
		if (!ret) logger.error("UPDATE FAILED sender: "+userUri+" receiver: "+receiverURI+" text: "+text + " room: "+room );

		return ret;
	}
}
