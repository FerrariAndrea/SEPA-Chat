package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import java.util.HashMap;

import it.unibo.arces.wot.sepa.apps.chat.JSAPProvider;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class Room extends Producer  {

	private HashMap<String,RDFTermURI> _roomList = new HashMap<String,RDFTermURI>();
	private String _roomPrefixName;
	private EnterRoom _enterRoomProducer = new EnterRoom();
	
	public Room(String roomPrefixName)throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(new JSAPProvider().getJsap(), "CREATE_ROOM", new JSAPProvider().getSecurityManager());
		this._roomPrefixName= roomPrefixName;
	}
	public Room()throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException{
		super(new JSAPProvider().getJsap(), "CREATE_ROOM", new JSAPProvider().getSecurityManager());
		this._roomPrefixName= "http://wot.arces.unibo.it/chat/";
	}
	
	public void create(String roomName) {
		logger.debug("Create room: "+roomName);
		
		try {
			RDFTermURI room = new RDFTermURI(_roomPrefixName + roomName +"/");
			this.setUpdateBindingValue("room",room);			
			update();
			_roomList.put(roomName,room);
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	public RDFTermURI getRoomByName(String roomName) {
		return _roomList.get(roomName);
	}
	public void enter(String roomName) {
		_enterRoomProducer.enter(this.getRoomByName(roomName));
	}

}


class EnterRoom extends Producer  {

	
	public EnterRoom()throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(new JSAPProvider().getJsap(), "ENTER_ROOM", new JSAPProvider().getSecurityManager());
	}
	
	public void enter(RDFTermURI room) {
		logger.debug("Enter room: "+room);		
		try {
			this.setUpdateBindingValue("room",room);			
			update();
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
		
	}
	


}
