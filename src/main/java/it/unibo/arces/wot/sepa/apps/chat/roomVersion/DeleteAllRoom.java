package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.JSAPProvider;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.Producer;

/**
 * Delete all the registered users and messages. Message logs are not delete as they belong to a different graph.
 * */
public class DeleteAllRoom extends Producer {
	private static final Logger logger = LogManager.getLogger();
	
	public DeleteAllRoom() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
		super(new JSAPProvider().getJsap(), "DELETE_ROOM",new JSAPProvider().getSecurityManager());
	}
	
	public void clean(ArrayList<String> rooms) {
		for (String room : rooms) {
			logger.info("Delete room " + room);
			try {
				this.setUpdateBindingValue("room",  new RDFTermURI(room));
				update();
			} catch (SEPASecurityException | SEPAPropertiesException | SEPABindingsException | SEPAProtocolException e) {
				logger.error(e.getMessage());
			}
		}
		
	}
}
