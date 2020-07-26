package it.unibo.arces.wot.sepa.apps.chat;

public class UserMonitor {
	private String user;
	private int messages;

	public UserMonitor(String user, int messages) {
		this.user = user;
		this.messages = messages;
	}

	public int sent = 0;
	public int received = 0;
	public int removed = 0;
	public boolean brokenConnectionRemover = false;
	public boolean brokenConnectionReceiver = false;
	
	public String toString() {
		return user + "|" + messages + "|" + received + "|" + sent + "|" + removed + "|" + brokenConnectionReceiver + "|" + brokenConnectionRemover;
	}

	public boolean allDone() {
		return brokenConnectionRemover || brokenConnectionReceiver || ((sent == messages) && (received == messages) && (removed == messages));
	}
}