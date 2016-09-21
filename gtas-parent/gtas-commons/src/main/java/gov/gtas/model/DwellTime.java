package gov.gtas.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "pnr_dwell_time")
public class DwellTime  {

	 private static final long serialVersionUID = 1L;  
	    public DwellTime() { }
	    
	    public DwellTime(Date arrival,Date departure,String airport,Pnr pnr ) {
	    	this.arrivalTime=arrival;
	    	this.departureTime=departure;
	    	this.location=airport;
	    	this.pnr=pnr;
	    	long diff = this.departureTime.getTime() - this.arrivalTime.getTime(); 
	    	int minutes=(int)TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
	    	DecimalFormat df = new DecimalFormat("#.##");      
	    	this.dwellTime = Double.valueOf(df.format((double) minutes / 60));
	    }
	    
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id")
	    private Integer id;
	    
	    @Column(name ="arrival_time")
	    @Temporal(TemporalType.TIMESTAMP)
	    private Date arrivalTime;
	    
	    @Column(name ="departure_at")
	    @Temporal(TemporalType.TIMESTAMP)
	    private Date departureTime;
	    
	    @Column(name = "arrival_airport", length = 3)
	    private String location;
	    
	    @ManyToOne
	    @JoinColumn(name = "pnr_id", nullable = false)
	    private Pnr pnr;
	    
	    @Column(name ="dwell_time")
	    private Double dwellTime;
	    
	    @Column(name="flying_from")
	    private String flyingFrom;
	    
	    @Column(name="flying_to")
	    private String flyingTo;
	        
	    
	    public String getFlyingFrom() {
			return flyingFrom;
		}

		public void setFlyingFrom(String flyingFrom) {
			this.flyingFrom = flyingFrom;
		}

		public String getFlyingTo() {
			return flyingTo;
		}

		public void setFlyingTo(String flyingTo) {
			this.flyingTo = flyingTo;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Date getArrivalTime() {
			return arrivalTime;
		}

		public void setArrivalTime(Date arrivalTime) {
			this.arrivalTime = arrivalTime;
		}

		public Date getDepartureTime() {
			return departureTime;
		}

		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public Pnr getPnr() {
			return pnr;
		}

		public void setPnr(Pnr pnr) {
			this.pnr = pnr;
		}

		public Double getDwellTime() {
			return dwellTime;
		}

		public void setDwellTime(Double dwellTime) {
			this.dwellTime = dwellTime;
		}

		@Override
	    public int hashCode() {
	       return Objects.hash(this.id, this.pnr.getId());
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (!(obj instanceof DwellTime))
	            return false;
	        final DwellTime other = (DwellTime)obj;
	        return Objects.equals(this.id, other.id);
	    }    
}
