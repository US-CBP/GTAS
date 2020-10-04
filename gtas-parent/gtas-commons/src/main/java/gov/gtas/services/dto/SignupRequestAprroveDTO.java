package gov.gtas.services.dto;

import java.util.Set;

import gov.gtas.services.security.RoleData;

public class SignupRequestAprroveDTO { 
	private Long requestId;
	private Set<RoleData> roles;
	
	public Long getRequestId() {
		return requestId;
	}
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	public Set<RoleData> getRoles() {
		return roles;
	}
	public void setRoles(Set<RoleData> roles) {
		this.roles = roles;
	}
	
	

}
