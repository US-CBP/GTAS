package gov.gtas.repository;

import java.util.List;

public class PvlCriteraResult {
	Long id;
	Long counts;
	
	public PvlCriteraResult() {
	}

	
	public PvlCriteraResult(Long id, Long counts) {
		super();
		this.id = id;
		this.counts = counts;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the counts
	 */
	public Long getCounts() {
		return counts;
	}

	/**
	 * @param counts the counts to set
	 */
	public void setCounts(Long counts) {
		this.counts = counts;
	}

	
}
