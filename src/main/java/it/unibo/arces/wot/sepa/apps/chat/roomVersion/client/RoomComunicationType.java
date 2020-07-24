package it.unibo.arces.wot.sepa.apps.chat.roomVersion.client;

public class RoomComunicationType {
	private String room;
	private String receiver;
	private int priv;
	private String receiverName;
	private String sender;
	public RoomComunicationType(String room, String receiver,String receiverName) {
		super();
		this.room = room;
		if(receiver==null) {
			this.receiverName ="ALL";
			this.priv =0;
			this.receiver = "";
		}else {
			this.receiverName=receiverName;
			this.receiver = receiver;
			this.priv=1;
		}
	}
	public RoomComunicationType(String room) {
		new RoomComunicationType(room,null,null);
	}
	public String getRoom() {
		return room;
	}

	public String getReceiver() {
		return receiver;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public int getPriv() {
		return priv;
	}
	

	
}
