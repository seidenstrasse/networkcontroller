package de.c3seidenstrasse.networkcontroller.utils;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;

public interface Observer {
	
	/**Operation that each observer needs to implement in 
	 * order to receive notifications from observee.
	 */
	public void update(NetworkComponent nc);
}
