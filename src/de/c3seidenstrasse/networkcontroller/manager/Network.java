package de.c3seidenstrasse.networkcontroller.manager;

import java.util.LinkedList;
import java.util.Queue;

import de.c3seidenstrasse.networkcontroller.network.CodeReader;
import de.c3seidenstrasse.networkcontroller.network.states.IdleState;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkState;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkStateVisitor;
import de.c3seidenstrasse.networkcontroller.network.states.PreparePullState;
import de.c3seidenstrasse.networkcontroller.network.states.PreparePushState;
import de.c3seidenstrasse.networkcontroller.network.states.PullState;
import de.c3seidenstrasse.networkcontroller.network.states.PushState;
import de.c3seidenstrasse.networkcontroller.network.states.RouterTurningState;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;

public class Network implements Runnable {

	public static final int ROUTERMAXWAIT = 10000;
	public static final int DETECTIONMAXWAIT = 5000;

	boolean isStopped = false;
	private final Thread t;

	public Network() {
		this.queue = new LinkedList<>();
		this.root = new CodeReader(this);

		this.getRoot().create33c3();
		this.t = new Thread(this, "NetworkWorker");
		this.t.start();
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

	public void startPushAirflow() {
		System.out.println("Saubsauger �berdruck wurde gestartet!");
	}

	public void startPullAirflow() {
		System.out.println("Saubsauger Unterdruck wurde gestartet!");
	}

	public void stopAirflow() {
		System.out.println("Luftfluss wurde gestoppt!");
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
		while (!this.isStopped) {
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
						System.out.println("Kapsel wird jetzt gezogen!");
						final PullState ps = new PullState(preparePullState.getCurrentTransport());
						Network.this.setState(ps);
					}
				}

				@Override
				protected void handle(final IdleState state) {
					if (!Network.this.queue.isEmpty()) {
						System.out.println("Router werden f�r das Saugen eingestellt! (PreparePull)");
						final RouterTurningState pps = new PreparePullState(Network.this.queue.poll());
						Network.this.setState(pps);

					}
				}

				@Override
				protected void accept(final PullState pullState) {
					if (pullState.isFinished()) {
						System.out.println("Router werden f�r das Pusten eingestellt (PreparePush)!");
						final PreparePushState pps = new PreparePushState(pullState.getCurrentTransport());
						Network.this.setState(pps);
					}
				}

				@Override
				protected void accept(final PreparePushState preparePushState) {
					if (preparePushState.isFinished()) {
						System.out.println("Es wird gepustet! (Push)");
						final PushState ps = new PushState(preparePushState.getCurrentTransport());
						Network.this.setState(ps);
					}
				}

				@Override
				protected void accept(final PushState pushState) {
					if (pushState.isFinished()) {
						System.out.println("Transport abgeschlossen!");
						if (!Network.this.queue.isEmpty())
							Network.this.awake();
						final IdleState is = IdleState.getInstance();
						Network.this.setState(is);
					}
				}
			});
		}
	}

	synchronized public void stop() {
		this.t.interrupt();
	}

}