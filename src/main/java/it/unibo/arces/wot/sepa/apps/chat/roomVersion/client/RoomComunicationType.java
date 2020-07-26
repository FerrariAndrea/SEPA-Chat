package it.unibo.arces.wot.sepa.apps.chat.roomVersion.client;

public class RoomComunicationType {
	private String room;
	private String roomUri=null;
	private String receiver;
	private int priv;
	private String receiverName;
	private String sender;
	public RoomComunicationType(String sender, String room, String receiver,String receiverName) {
		super();
		this.room = room;
		this.sender = sender;
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
	public RoomComunicationType(String sender,String room) {
		this(sender,room,null,null);
	}
	public String getRoom() {
		return room;
	}
	public String getSender() {
		return sender;
	}
	public void setRoomUri(String r) {
		 this.roomUri=r;
	}
	public String getRoomUri() {
		if(roomUri==null) {
			throw new NullPointerException("Room uri not set yet.");
		}
		return roomUri;
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
	
	public boolean isFreeRoom() {
		return priv==0;
	}
	
}
