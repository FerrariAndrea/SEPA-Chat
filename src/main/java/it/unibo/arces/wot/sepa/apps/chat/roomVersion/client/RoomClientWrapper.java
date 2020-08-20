package it.unibo.arces.wot.sepa.apps.chat.roomVersion.client;

import java.util.ArrayList;

public class RoomClientWrapper implements Runnable {

	private ArrayList<RoomClient> rcs;
	private int messages;
	private String sender;
	
	
	public RoomClientWrapper(RoomClient rc) {
		this.rcs = new  ArrayList<RoomClient>();
		rcs.add(rc);
		this.messages =rc.messages;
		this.sender =rc.getUser();
		
	}
	
	public void add(RoomClient rc) throws Exception {
		if(rc.messages!=this.messages) {
			throw new Exception("All RoomClient need the same number of message.");				
		}
		if(rc.getUser()!=this.sender) {
			throw new Exception("All RoomClient need the same Sender.");				
		}
		rcs.add(rc);
	}
	

	public ArrayList<RoomClient> getRcs() {
		return rcs;
	}

	public void setRcs(ArrayList<RoomClient> rcs) {
		this.rcs = rcs;
	}

	@Override
	public void run() {
		for (int i = 0; i < messages; i++) {
			for (RoomClient roomClient : rcs) {
				roomClient.sendNextMessage();
			}
		}

		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}

}
