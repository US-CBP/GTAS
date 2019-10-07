package gov.gtas.parser.pentaho.bean;

/**
 * This class holds the service string information of a UNA message
 *
 */
public class UNA {

	public String serviceStringAdvice;
	public String componentDataElementSep;
	public String dataElementSep;
	public String decimalMark;
	public String releaseCharacter;
	public String repetitionSep;
	public String segmentTerminator;

	public String getServiceStringAdvice() {
		return serviceStringAdvice;
	}

	public void setServiceStringAdvice(String serviceStringAdvice) {
		this.serviceStringAdvice = serviceStringAdvice;
	}

	public String getComponentDataElementSep() {
		return componentDataElementSep;
	}

	public void setComponentDataElementSep(String componentDataElementSep) {
		this.componentDataElementSep = componentDataElementSep;
	}

	public String getDataElementSep() {
		return dataElementSep;
	}

	public void setDataElementSep(String dataElementSep) {
		this.dataElementSep = dataElementSep;
	}

	public String getDecimalMark() {
		return decimalMark;
	}

	public void setDecimalMark(String decimalMark) {
		this.decimalMark = decimalMark;
	}

	public String getReleaseCharacter() {
		return releaseCharacter;
	}

	public void setReleaseCharacter(String releaseCharacter) {
		this.releaseCharacter = releaseCharacter;
	}

	public String getRepetitionSep() {
		return repetitionSep;
	}

	public void setRepetitionSep(String repetitionSep) {
		this.repetitionSep = repetitionSep;
	}

	public String getSegmentTerminator() {
		return segmentTerminator;
	}

	public void setSegmentTerminator(String segmentTerminator) {
		this.segmentTerminator = segmentTerminator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((componentDataElementSep == null) ? 0 : componentDataElementSep.hashCode());
		result = prime * result + ((dataElementSep == null) ? 0 : dataElementSep.hashCode());
		result = prime * result + ((decimalMark == null) ? 0 : decimalMark.hashCode());
		result = prime * result + ((releaseCharacter == null) ? 0 : releaseCharacter.hashCode());
		result = prime * result + ((repetitionSep == null) ? 0 : repetitionSep.hashCode());
		result = prime * result + ((segmentTerminator == null) ? 0 : segmentTerminator.hashCode());
		result = prime * result + ((serviceStringAdvice == null) ? 0 : serviceStringAdvice.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UNA other = (UNA) obj;
		if (componentDataElementSep == null) {
			if (other.componentDataElementSep != null)
				return false;
		} else if (!componentDataElementSep.equals(other.componentDataElementSep))
			return false;
		if (dataElementSep == null) {
			if (other.dataElementSep != null)
				return false;
		} else if (!dataElementSep.equals(other.dataElementSep))
			return false;
		if (decimalMark == null) {
			if (other.decimalMark != null)
				return false;
		} else if (!decimalMark.equals(other.decimalMark))
			return false;
		if (releaseCharacter == null) {
			if (other.releaseCharacter != null)
				return false;
		} else if (!releaseCharacter.equals(other.releaseCharacter))
			return false;
		if (repetitionSep == null) {
			if (other.repetitionSep != null)
				return false;
		} else if (!repetitionSep.equals(other.repetitionSep))
			return false;
		if (segmentTerminator == null) {
			if (other.segmentTerminator != null)
				return false;
		} else if (!segmentTerminator.equals(other.segmentTerminator))
			return false;
		if (serviceStringAdvice == null) {
			if (other.serviceStringAdvice != null)
				return false;
		} else if (!serviceStringAdvice.equals(other.serviceStringAdvice))
			return false;
		return true;
	}

}
