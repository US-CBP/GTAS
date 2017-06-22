/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP)
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.pnrgov.PnrUtils;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * TBD: TRAVELER BAGGAGE DETAILS/Electronic Ticketing
 * <p>
 * To specify the baggage details, including number of bags and serial numbers.
 * This segment is for the checked in baggage and not for excess bag details
 * EXAMPLE
 * bags, weight 84 kilos, Head of Pool, tags 4074902824, 3 in sequence to MSP
 * TBD++3:84:700++HP+KL:4074902824:3:MSP'
 * Bag pool members with Head of Pool ticket.
 * TBD+++MP:0741234123456'
 * Total 5 bags, weight 155 pounds, 2 checked to MSP, 3 short
 * checked to JFK
 * TBD++5:155:701+++KL: 8074902824:2:MSP+ KL: 8074902826:3:JFK’
 * 
 * 700 unit qualifier is for KGS
 * 701 unit qualifier is for pounds
 * 702 No Unit
 */
public class TBD extends Segment {
    private Integer numBags;
    private Double baggageWeight;
    private String unitQualifier;
    
    public class BagDetails {
        private String airline;
        private String tagNumber;
        private List<String> bagIds = new ArrayList<>();
        private Integer numConsecutiveTags;
        private String destAirport;
        private boolean memberPool=false;
        private boolean isCarryOn=false;
        
        
		public boolean isCarryOn() {
			return isCarryOn;
		}
		public void setCarryOn(boolean isCarryOn) {
			this.isCarryOn = isCarryOn;
		}
		public boolean isMemberPool() {
			return memberPool;
		}
		public void setMemberPool(boolean pool) {
			this.memberPool = pool;
		}
		public List<String> getBagIds() {
			return bagIds;
		}
		public void setBagIds(List<String> bagIds) {
			this.bagIds = bagIds;
		}
		public String getAirline() {
            return airline;
        }
        public void setAirline(String airline) {
            this.airline = airline;
        }
        public String getTagNumber() {
            return tagNumber;
        }
        public void setTagNumber(String tagNumber) {
            this.tagNumber = tagNumber;
        }
        public Integer getNumConsecutiveTags() {
            return numConsecutiveTags;
        }
        public void setNumConsecutiveTags(Integer numConsecutiveTags) {
            this.numConsecutiveTags = numConsecutiveTags;
        }
        public String getDestAirport() {
            return destAirport;
        }
        public void setDestAirport(String destAirport) {
            this.destAirport = destAirport;
        }
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
        }
    }
    
    private List<BagDetails> bagDetails;

    public TBD(List<Composite> composites) {
        super(TBD.class.getSimpleName(), composites);
        Composite c = getComposite(1);
        this.bagDetails = new ArrayList<>();
        if (c != null && StringUtils.isNotBlank(c.getElement(0))) {
             this.numBags = ParseUtils.returnNumberOrNull(c.getElement(0));
            if(StringUtils.isNotBlank(c.getElement(1))){
            	this.baggageWeight = Double.valueOf(c.getElement(1));
            }
            if(StringUtils.isNotBlank(c.getElement(2))){
            	if("700".equals(c.getElement(2))){
            		this.unitQualifier="kgs";
            	}
            	else if("701".equals(c.getElement(2))){
            		this.unitQualifier="lbs";
            	}
            }
          //CASE-2:
            //TBD++03:278:701+++DL:80070001::GRU:006+DL::80070002::GRU:006+DL::80070002::GRU:006'
          //CASE-3:
            //TBD++5:155:701+++KL: 8074902824:2:MSP+ KL: 8074902826:3:JFK'
            //TBD++3:84:700++HP+KL:4074902824:3:MSP'
            for (int i=3; i<numComposites(); i++) {
            	c = getComposite(i);
                if (c != null && c.numElements() > 2) {
                	if(StringUtils.isBlank(c.getElement(2))){
                		BagDetails bag = new BagDetails();
                		bag.setAirline(c.getElement(0));
                        bag.setTagNumber(c.getElement(1));
                        bag.setDestAirport(c.getElement(3));
                        bagDetails.add(bag);
                	}else{
                		int noBags=Integer.valueOf(c.getElement(2));
                		String airline=c.getElement(0);
                		String dest=c.getElement(3);
                		
                		for(int j=0;j<noBags;j++){
                       		BagDetails bag = new BagDetails();
                    		bag.setAirline(airline);
                            bag.setTagNumber(PnrUtils.getBagTagFromElement(c.getElement(1),j));
                            bag.setDestAirport(dest);
                            bagDetails.add(bag);
                		}
                	}
                }
            }
        }else{
            //CASE-1:
            //TBD+++MP:0741234123456'
        	Composite mpc = getComposite(2);
        	if(mpc != null && mpc.numElements() >1){
        		BagDetails bag = new BagDetails();
        		bag.setTagNumber(mpc.getElement(1));
        		bag.setMemberPool(true);
        		this.numBags=1;
        		this.baggageWeight = 0.0;
        		this.unitQualifier="NONE";
        		bagDetails.add(bag);
        	}
        	
        }
    }

    public Integer getNumBags() {
        return numBags;
    }

    public List<BagDetails> getBagDetails() {
        return bagDetails;
    }

	public Double getBaggageWeight() {
		return baggageWeight;
	}

	public void setBaggageWeight(Double bWeight) {
		this.baggageWeight = bWeight;
	}

	public String getUnitQualifier() {
		return unitQualifier;
	}

	public void setUnitQualifier(String unitQualifier) {
		this.unitQualifier = unitQualifier;
	}

	public void setNumBags(Integer numBags) {
		this.numBags = numBags;
	}

	public void setBagDetails(List<BagDetails> bagDetails) {
		this.bagDetails = bagDetails;
	}
   
}
