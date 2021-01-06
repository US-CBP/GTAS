package gov.gtas.bo.match;

import java.io.Serializable;

public class PnrSegmentLink extends  PnrAttributeLink implements Serializable {
    private static final long serialVersionUID = -784332;

    public PnrSegmentLink(final long pnrId, final long segmentId) {
        super(pnrId, segmentId);
    }

    public long getLinkAttributeId() {
        return super.getLinkAttributeId();
    }
}
