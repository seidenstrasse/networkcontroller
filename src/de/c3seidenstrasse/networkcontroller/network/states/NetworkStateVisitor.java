package de.c3seidenstrasse.networkcontroller.network.states;

public abstract class NetworkStateVisitor {
	protected abstract void handle(IdleState state);

	protected abstract void handle(PreparePullState state);

	protected abstract void accept(PullState pullState);

	protected abstract void accept(PreparePushState preparePushState);

	protected abstract void accept(PushState pushState);
}
