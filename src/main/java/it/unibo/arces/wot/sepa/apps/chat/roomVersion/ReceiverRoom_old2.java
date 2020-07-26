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

class ReceiverRoom_old2 extends Receiver {
	private String room;
	
	public ReceiverRoom_old2(String userUri,String room,IMessageHandler handler)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		super(userUri, handler);

		this.setSubscribeBindingValue("room", new RDFTermURI(room));		
		this.room =room;
	}
	
	

	@Override
	public void onAddedResults(BindingsResults results) {
		super.onAddedResults(results);
		
		logger.debug("onAddedResults");

		for (Bindings bindings : results.getBindings()) {//FOR EACH SENT notify
			logger.debug("SENT " + bindings.getValue("message"));
			//System.out.println("--> "+  bindings.getValue("name"));
			//System.out.println("--> "+  bindings.getValue("text"));
			//System.out.println("--> "+ 	bindings.getValue("time") );
			handler.onMessageReceived(
					userUri, 
					bindings.getValue("message"),
					bindings.getValue("name"),
					bindings.getValue("text"),
					bindings.getValue("time")
				);
		//System.out.println("ok");
			//SET_RECEIVED 
			try {
				this.setUpdateBindingValue("message", new RDFTermURI(bindings.getValue("message")));
				this.setUpdateBindingValue("room",  new RDFTermURI(this.room));
				update();
				
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
}
