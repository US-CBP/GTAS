package gov.gtas.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ticket_fare")
public class TicketFare implements Serializable{

	private static final long serialVersionUID = 1L;

	public TicketFare(){
		
	}
	
	@Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    private Long id; 
    
    @Column(name = "payment_amount")
    private String paymentAmount;

    @Column(name= "currency_code")
    private String currencyCode;
    
    @Column(name= "ticket_number")
    private String ticketNumber="0";
    
    @Column(name= "ticket_type")
    private String ticketType;
    
    @Column(name= "number_booklets")
    private String numberOfBooklets;
    
    @Column(name= "ticketless")
    private boolean ticketless;
    
	@ManyToOne
	@JoinColumn(nullable = false)
	private Passenger passenger;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public String getNumberOfBooklets() {
		return numberOfBooklets;
	}

	public void setNumberOfBooklets(String numberOfBooklets) {
		this.numberOfBooklets = numberOfBooklets;
	}

	public boolean isTicketless() {
		return ticketless;
	}

	public void setTicketless(boolean ticketless) {
		this.ticketless = ticketless;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@Override  
    public int hashCode() {  
        int hash = 0;  
        hash += (this.getId() != null ? this.getId().hashCode() : 0);  
  
        return hash;  
    }  
  
    @Override  
    public boolean equals(Object object) {  
    if (this == object)  
            return true;  
        if (object == null)  
            return false;  
        if (getClass() != object.getClass())  
            return false;  
  
        TicketFare other = (TicketFare) object;  

        if(this.getTicketNumber().equals(other.getTicketNumber())){
        	return true;
        }
        return false;  
    }  
}
