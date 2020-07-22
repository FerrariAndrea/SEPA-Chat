package it.unibo.arces.wot.sepa.apps.chat;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class UserRegistration extends Producer {
	private static final Logger logger = LogManager.getLogger();
	
	public UserRegistration() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
		super(new JSAPProvider().getJsap(), "REGISTER_USER",new JSAPProvider().getSecurityManager());
	}
	
	public void register(String userName) {
		logger.debug("Register: "+userName);
		
		try {
			this.setUpdateBindingValue("userName", new RDFTermLiteral(userName));
			
			update();
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
	}
	
	//se si vuole specificare la stanza
	public void register(String userName, String room) {
		logger.debug("Register: "+userName);
		
		try {
			this.setUpdateBindingValue("userName", new RDFTermLiteral(userName));
			this.setUpdateBindingValue("room", new RDFTermURI(room));
			
			update();
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
	}
}
