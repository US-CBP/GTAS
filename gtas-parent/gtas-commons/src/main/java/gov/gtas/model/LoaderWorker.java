/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "loader_worker")
public class LoaderWorker extends BaseEntityAudit {
	private static final long serialVersionUID = 1L;
	
	@Column(name="worker_name", unique=true)
	private String workerName;
	
	@Column(name="bucketCount")
	private int bucketCount = 0;

	@Override
	public int hashCode() {
		return Objects.hash(workerName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LoaderWorker)) {
			return false;
		}
		LoaderWorker other = (LoaderWorker) obj;
		return Objects.equals(workerName, other.workerName);
	}

	public LoaderWorker() {
	}

	/**
	 * @return the workerName
	 */
	public String getWorkerName() {
		return workerName;
	}

	/**
	 * @param workerName the workerName to set
	 */
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	/**
	 * @return the bucketCount
	 */
	public int getBucketCount() {
		return bucketCount;
	}

	/**
	 * @param bucketCount the bucketCount to set
	 */
	public void setBucketCount(int bucketCount) {
		this.bucketCount = bucketCount;
	}
	
	
}
