package tr;

// Generated May 24, 2012 11:06:45 PM by Hibernate Tools 3.4.0.CR1

/**
 * MmProvisionserver generated by hbm2java
 */
public class MmProvisionserver implements java.io.Serializable {

	private MmProvisionserverId id;
	private MmConfigcoll mmConfigcoll;
	private MmServer mmServer;

	public MmProvisionserver() {
	}

	public MmProvisionserver(MmProvisionserverId id, MmConfigcoll mmConfigcoll,
			MmServer mmServer) {
		this.id = id;
		this.mmConfigcoll = mmConfigcoll;
		this.mmServer = mmServer;
	}

	public MmProvisionserverId getId() {
		return this.id;
	}

	public void setId(MmProvisionserverId id) {
		this.id = id;
	}

	public MmConfigcoll getMmConfigcoll() {
		return this.mmConfigcoll;
	}

	public void setMmConfigcoll(MmConfigcoll mmConfigcoll) {
		this.mmConfigcoll = mmConfigcoll;
	}

	public MmServer getMmServer() {
		return this.mmServer;
	}

	public void setMmServer(MmServer mmServer) {
		this.mmServer = mmServer;
	}

}
