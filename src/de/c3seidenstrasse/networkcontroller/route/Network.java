package de.c3seidenstrasse.networkcontroller.route;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.c3seidenstrasse.networkcontroller.communication.SssConnection;
import de.c3seidenstrasse.networkcontroller.network.AirSupplier;
import de.c3seidenstrasse.networkcontroller.network.CodeReader;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.states.IdleState;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkState;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkStateVisitor;
import de.c3seidenstrasse.networkcontroller.network.states.PreparePullState;
import de.c3seidenstrasse.networkcontroller.network.states.PreparePushState;
import de.c3seidenstrasse.networkcontroller.network.states.PullState;
import de.c3seidenstrasse.networkcontroller.network.states.PushState;
import de.c3seidenstrasse.networkcontroller.network.states.RouterTurningState;
import de.c3seidenstrasse.networkcontroller.utils.IdAlreadyExistsException;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;

public class Network implements Runnable {

	private final Thread t;

	private final SssConnection sssc;

	private final Map<Integer, NetworkComponent> idMap;

	private final AirSupplier airsupplier;

	private Network(final boolean withNetwork) {
		this.queue = new LinkedList<>();
		this.idMap = new HashMap<>();
		this.airsupplier = new AirSupplier(this);
		try {
			this.root = new CodeReader(0x7F, this);
		} catch (final IdAlreadyExistsException e) {
			throw new Error();
		}
		this.getRoot().create33c3();

		if (withNetwork) {
			this.sssc = new SssConnection(this, "/dev/ttyUSB0");
		} else {
			this.sssc = null;
		}

		this.t = new Thread(this, "NetworkWorker");
		this.t.start();
	}

	/**
	 * creates a network with an SSSConnection
	 *
	 * @return the network object
	 */
	public static Network create() {
		return new Network(true);
	}

	/**
	 * creates a network
	 *
	 * @param withNetwork
	 *            indicates if the network should have an SSS7 connection
	 * @return the network object
	 */
	public static Network create(final boolean withNetwork) {
		return new Network(withNetwork);
	}

	public void addNodeToMap(final Integer id, final NetworkComponent nc) throws IdAlreadyExistsException {
		if (this.getIdMap().containsKey(id))
			throw new IdAlreadyExistsException();
		this.getIdMap().put(id, nc);
	}

	public NetworkComponent getNodeById(final Integer id) {
		return this.getIdMap().get(id);
	}

	public Transport getCurrentTransport() throws NoCurrentTransportException {
		return this.getState().getCurrentTransport();
	}

	public CodeReader getRoot() {
		return this.root;
	}

	public void addTransport(final Transport e) {
		this.queue.add(e);
		this.awake();
	}

	public NetworkState getState() {
		return this.state;
	}

	public void setState(final NetworkState state) {
		this.state = state;
		this.state.doYourThing();
	}

	private final CodeReader root;
	private NetworkState state = IdleState.getInstance();
	private final Queue<Transport> queue;
	private boolean isNotified = false;

	synchronized public void awake() {
		this.isNotified = true;
		this.notify();
	}

	@Override
	public void run() {
		while (!this.t.isInterrupted()) {
			synchronized (this) {
				if (!this.isNotified) {
					try {
						this.wait();
					} catch (final InterruptedException e) {
						return;
					}
				} else {
					this.isNotified = false;
				}
			}

			this.getState().accept(new NetworkStateVisitor() {
				@Override
				protected void handle(final PreparePullState preparePullState) {
					if (preparePullState.isFinished()) {
						System.out.println("NETWORK: PreparePullState finished");
						final PullState ps = new PullState(preparePullState.getCurrentTransport());
						Network.this.setState(ps);
						System.out.println("NETWORK: Now in PullState");
					}
				}

				@Override
				protected void handle(final IdleState state) {
					if (!Network.this.queue.isEmpty()) {
						System.out.println("NETWORK: IdleState finished");
						final RouterTurningState pps = new PreparePullState(Network.this.queue.peek());
						Network.this.setState(pps);
						System.out.println("NETWORK: Now in PreparePullState");

					}
				}

				@Override
				protected void accept(final PullState pullState) {
					if (pullState.isFinished()) {
						System.out.println("NETWORK: PullState finished");
						final PreparePushState pps = new PreparePushState(pullState.getCurrentTransport());
						Network.this.setState(pps);
						System.out.println("NETWORK: Now in PreparePushState");
					}
				}

				@Override
				protected void accept(final PreparePushState preparePushState) {
					if (preparePushState.isFinished()) {
						System.out.println("NETWORK: preparePushState finished");
						final PushState ps = new PushState(preparePushState.getCurrentTransport());
						Network.this.setState(ps);
						System.out.println("NETWORK: Now in PushState");
					}
				}

				@Override
				protected void accept(final PushState pushState) {
					if (pushState.isFinished()) {
						System.out.println("NETWORK: pushState finished");
						Network.this.queue.remove();
						if (!Network.this.queue.isEmpty())
							Network.this.awake();
						final IdleState is = IdleState.getInstance();
						Network.this.setState(is);
						System.out.println("NETWORK: Now in idleState");
					}
				}
			});
		}
	}

	synchronized public void stop() {
		this.t.interrupt();
	}

	/**
	 * sends a message if an SSSConnection is present
	 *
	 * @param message
	 *            the message to send
	 */
	public void send(final byte[] message) {
		if (this.sssc != null)
			this.sssc.send(message);

	}

	public AirSupplier getAirsupplier() {
		return this.airsupplier;
	}
}
