package it.unibo.arces.wot.sepa.apps.chat;

import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class ChatMonitor {
	protected static final Logger logger = LogManager.getLogger();

	

	private HashMap<String, UserMonitor> messageMap = new HashMap<>();

	public ChatMonitor(Set<String> users, int messages) throws SEPAProtocolException, SEPAPropertiesException,
			SEPASecurityException, SEPABindingsException, InterruptedException {

		for (String user : users)
			messageMap.put(user, new UserMonitor(user, messages * (users.size() - 1)));
		
		new Thread() {
			public void run() {			
				while(true) {
					printStatus();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}.start();
	}

	public void printStatus() {
		logger.info("****************************");
		for (UserMonitor mon : messageMap.values()) {
			logger.info(mon);
		}
	}
	public synchronized void monitor() throws InterruptedException {
		boolean allDone;
		do {
			allDone = true;
			for (UserMonitor mon : messageMap.values()) {
				allDone = allDone && mon.allDone();
				if (!allDone) {
					wait();
					break;
				}
			}
		} while(!allDone);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			return;
		}
		printStatus();
	}

	public synchronized void brokenConnectionReceiver(String user) {
		messageMap.get(user).brokenConnectionReceiver = true;
		notify();
	}

	public synchronized void messageSent(String user) {
		messageMap.get(user).sent++;
		notify();
	}

	public synchronized void messageReceived(String user) {
		messageMap.get(user).received++;
		notify();
	}

	public synchronized void messageRemoved(String user) {
		messageMap.get(user).removed++;
		notify();
	}

	public void brokenConnectionRemover(String user) {
		messageMap.get(user).brokenConnectionRemover = true;
		notify();
		
	}
}
