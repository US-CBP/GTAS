package gov.gtas.bo.match;

public class PnrDwellTimeLink extends PnrAttributeLink {

	private static final long serialVersionUID = 1L;

	public PnrDwellTimeLink(long pnrId, long dwellId) {
		super(pnrId, dwellId);
	}

	public long getDwellId() {
		return super.getLinkAttributeId();
	}

}
