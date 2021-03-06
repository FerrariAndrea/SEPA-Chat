package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import it.unibo.arces.wot.sepa.apps.chat.ChatAggregator;
import it.unibo.arces.wot.sepa.apps.chat.Receiver;
import it.unibo.arces.wot.sepa.apps.ichat.IMessageHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;

class ReceiverRoom extends ChatAggregator {
	protected final IMessageHandler handler;
	protected String userUri;
	protected String room;
	public ReceiverRoom(String userUri,String room,IMessageHandler handler)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		super("SENT", "SET_RECEIVED");

		this.setSubscribeBindingValue("receiver", new RDFTermURI(userUri));
		this.setSubscribeBindingValue("room", new RDFTermURI(room));	
		this.room=room;
		this.handler = handler;
		this.userUri = userUri;
	}
	
	@Override
	public void onBrokenConnection(ErrorResponse err) {
		logger.error(err);
		handler.onReceiverBrokenConnection(userUri);
	}

	@Override
	public void onAddedResults(BindingsResults results) {
		super.onAddedResults(results);
		
		logger.debug("onAddedResults");

		for (Bindings bindings : results.getBindings()) {
			logger.debug("SENT " + bindings.getValue("message"));
			
			handler.onMessageReceived(userUri, bindings.getValue("message"), bindings.getValue("name"), bindings.getValue("text"),bindings.getValue("time"));
		
			try {
				this.setUpdateBindingValue("room",  new RDFTermURI(this.room));
				this.setUpdateBindingValue("message", new RDFTermURI(bindings.getValue("message")));
				update();
				
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	@Override
	public void onRemovedResults(BindingsResults results) {
		super.onRemovedResults(results);
		
		logger.debug("onRemovedResults");

		for (Bindings bindings : results.getBindings()) {
			logger.debug("REMOVED " + bindings.getValue("message"));
			
			handler.onMessageRemoved(userUri, bindings.getValue("message"), bindings.getValue("name"), bindings.getValue("text"),bindings.getValue("time"));
		}
	}
}
