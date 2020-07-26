package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import it.unibo.arces.wot.sepa.apps.chat.ChatAggregator;
import it.unibo.arces.wot.sepa.apps.ichat.IMessageHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;

class RemoverRoom extends ChatAggregator {	
	private String userUri;
	private final IMessageHandler handler;
	private String room;
	
	public RemoverRoom(String userUri,String room, IMessageHandler handler) throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, SEPABindingsException {
		super("RECEIVED", "REMOVE");
		
		this.setSubscribeBindingValue("sender", new RDFTermURI(userUri));
		this.setSubscribeBindingValue("room", new RDFTermURI(room));
		this.userUri = userUri;
		this.handler = handler;
		this.room=room;
	}

	@Override
	public void onAddedResults(BindingsResults results) {
		super.onAddedResults(results);
		
		for (Bindings bindings : results.getBindings()) {
			logger.debug("RECEIVED: "+bindings.getValue("message"));
			
			handler.onMessageSent(userUri,bindings.getValue("message"),bindings.getValue("time"));
					
			try {//REMOVE
				this.setUpdateBindingValue("message", bindings.getRDFTerm("message"));
				this.setUpdateBindingValue("room", new RDFTermURI(this.room));
				update();
				
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	@Override
	public void onBrokenConnection(ErrorResponse err) {
		logger.error(err);
		handler.onRemoverBrokenConnection(userUri);
	}
}
