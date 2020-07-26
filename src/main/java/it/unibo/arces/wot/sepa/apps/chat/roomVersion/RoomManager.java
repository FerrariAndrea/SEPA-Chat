package it.unibo.arces.wot.sepa.apps.chat.roomVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.JSAPProvider;
import it.unibo.arces.wot.sepa.apps.chat.roomVersion.client.RoomComunicationType;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class RoomManager extends Producer  {

	private HashMap<String,String> _roomList = new HashMap<String,String>();
	public String getRoomPrefixName() {
		return _roomPrefixName;
	}
	private String _roomPrefixName;
	private EnterRoom _enterRoomProducer = new EnterRoom();
	private DeleteAllRoom _deleteRoom = new DeleteAllRoom();
	
	public RoomManager(String roomPrefixName)throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(new JSAPProvider().getJsap(), "CREATE_ROOM", new JSAPProvider().getSecurityManager());
		this._roomPrefixName= roomPrefixName;
	}
	public RoomManager()throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException{
		super(new JSAPProvider().getJsap(), "CREATE_ROOM", new JSAPProvider().getSecurityManager());
		this._roomPrefixName= "http://wot.arces.unibo.it/chat/";
	}
	
	public String create(RoomComunicationType roomRCT) {
		logger.debug("Create room: "+roomRCT.getRoom());
		String room = _roomPrefixName + roomRCT.getRoom()  ;
		try {			
			this.setUpdateBindingValue("room",new RDFTermURI(room));			
			update();
			_roomList.put(roomRCT.getRoom(),room);
		
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
		return room;
	}
	public void deleteAllRoom() {
		_deleteRoom.clean();
	}
	
	public String getRoomByName(String roomName) {
		return _roomList.get(roomName);
	}
	public void enter(String roomUri) {
		try {
			//perche devo sempre creare un nuovo producer? (la close è automomatica)
			new EnterRoom().enter(new RDFTermURI(roomUri));
		} catch (SEPAProtocolException | SEPASecurityException | SEPAPropertiesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeAll() throws IOException {
		_enterRoomProducer.close();
		_deleteRoom.close();
		this.close();
	}

}


class EnterRoom extends Producer  {

	
	public EnterRoom()throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(new JSAPProvider().getJsap(), "ENTER_ROOM", new JSAPProvider().getSecurityManager());
	}
	
	public void enter(RDFTermURI room) {
		//System.out.print("-->"+room);
		logger.debug("Enter room: "+room);		
		try {
			this.setUpdateBindingValue("room",room);			
			update();
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
			logger.error(e.getMessage());
		}
		
	}
	


}

class DeleteAllRoom extends Producer {
	private static final Logger logger = LogManager.getLogger();
	
	public DeleteAllRoom() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException {
		super(new JSAPProvider().getJsap(), "DELETE_ROOM",new JSAPProvider().getSecurityManager());
	}
	
	public void clean() {
			try {
				update();
			} catch (SEPASecurityException | SEPAPropertiesException | SEPABindingsException | SEPAProtocolException e) {
				logger.error(e.getMessage());
			}		
		
	}
}