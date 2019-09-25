/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.ArrayList;
import java.util.List;

public class FlightSearchVo {

	private String userLocation;
	private boolean adminUser;
	private List<FlightDirectionVo> flightDirectionList = new ArrayList<FlightDirectionVo>();

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public boolean isAdminUser() {
		return adminUser;
	}

	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}

	public List<FlightDirectionVo> getFlightDirectionList() {
		return flightDirectionList;
	}

	public void setFlightDirectionList(List<FlightDirectionVo> flightDirectionList) {
		this.flightDirectionList = flightDirectionList;
	}

	public class FlightDirectionVo {

		private String code;
		private String description;

		public FlightDirectionVo(String code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

}
