package de.c3seidenstrasse.networkcontroller.network;

import com.google.gson.annotations.Expose;

public final class IndexedNetworkComponent {
	@Override
	public String toString() {
		return "[nc=" + this.nc + ", i=" + this.i + ", transDur=" + this.transferDuration + "]";
	}

	@Expose
	private final NetworkComponent nc;
	@Expose
	private final Integer i;
	@Expose
	private final Integer transferDuration;

	public IndexedNetworkComponent(final NetworkComponent nc, final Integer i, final int transferDuration) {
		this.nc = nc;
		this.i = i;
		this.transferDuration = transferDuration;
	}

	final public Integer getI() {
		return this.i;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.i == null ? 0 : this.i.hashCode());
		result = prime * result + (this.nc == null ? 0 : this.nc.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final IndexedNetworkComponent other = (IndexedNetworkComponent) obj;
		if (this.i == null) {
			if (other.i != null)
				return false;
		} else if (!this.i.equals(other.i))
			return false;
		if (this.nc == null) {
			if (other.nc != null)
				return false;
		} else if (!this.nc.equals(other.nc))
			return false;
		return true;
	}

	final public NetworkComponent getNc() {
		return this.nc;
	}

	public Integer getTransferDuration() {
		return this.transferDuration;
	}
}
