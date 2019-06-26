package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name="attachment")
public class Attachment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;

	@Column(name="name")
	private String name;

	@Column(name="description")
	private String description;

	@Column(name="filename")
	private String filename;

	@Column(name="content", length=2000000)
	@Lob
	private Blob content;
	
	@Column(name="content_type")
	private String contentType;
	
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)  
	private Date created;

	@ManyToOne(fetch = FetchType.LAZY)
        private Passenger passenger;

        @ManyToMany(
        mappedBy = "attachmentSet",
        targetEntity = HitsDispositionComments.class, cascade = CascadeType.ALL
        )
        private Set<HitsDispositionComments> hitsDispositionComments = new HashSet<>();
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@JsonIgnore
	public Blob getContent() {
		return content;
	}

	public void setContent(Blob content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
    public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

    public Set<HitsDispositionComments> getHitsDispositionComments() {
        return hitsDispositionComments;
    }

    public void setHitsDispositionComments(Set<HitsDispositionComments> hitsDispositionComments) {
        this.hitsDispositionComments = hitsDispositionComments;
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
  
        Attachment other = (Attachment) object;  
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) {  
            return false;  
        }  
        return true;  
    }  

}
