package gov.gtas.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;

@Entity
@Table(name = "code_share_flight")
public class CodeShareFlight implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
    public CodeShareFlight() { 
    	
    }

    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    private Long id;  
  
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="operating_flight_id")
    private Flight operatingFlight;

    @Column(name="marketing_flight_id")
    private Long marketingFlightId;
    
    @Column(name = "marketing_flight_number")  
    private String marketingFlightNumber;
    
    public Long getId() {  
        return id;  
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    

    
    
    public String getMarketingFlightNumber() {
		return marketingFlightNumber;
	}

	public void setMarketingFlightNumber(String flightNumber) {
		this.marketingFlightNumber = flightNumber;
	}

	public Long getMarketingFlightId() {
		return marketingFlightId;
	}

	public void setMarketingFlightId(Long mFlightId) {
		this.marketingFlightId = mFlightId;
	}

	
	public Flight getOperatingFlight() {
		return operatingFlight;
	}

	public void setOperatingFlight(Flight oFlight) {
		this.operatingFlight = oFlight;
	}


	@Override
    public int hashCode() {
        return Objects.hash(this.getMarketingFlightNumber());
    }

    @Override
    public boolean equals(Object target) {

        if (this == target) {
            return true;
        }

        if (!(target instanceof CodeShareFlight)) {
            return false;
        }

        CodeShareFlight dataTarget = ((CodeShareFlight) target);

        return (this.getMarketingFlightNumber().equals(dataTarget.getMarketingFlightNumber()));
    }
}
