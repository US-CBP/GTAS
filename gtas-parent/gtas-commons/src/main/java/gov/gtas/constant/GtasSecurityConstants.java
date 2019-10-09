/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

public class GtasSecurityConstants {
	private GtasSecurityConstants() {
	}

	public static final String GTAS_APPLICATION_USERID = "gtas";

	/*
	 * This is the error code that indicates that the user does not have
	 * authorization for the operation invoked.
	 */
	public static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED";

	/*
	 * This is the error message that indicates that the user does not have
	 * authorization for the operation invoked.
	 */
	public static final String UNAUTHORIZED_ERROR_MESSAGE = "User %s is not authorized to perform %s.";

	/////////////////////////// Role Privilege ///////////////////////////
	
	/*
	 * Admin has every permission in the entire system, this means that rather than double parameters for every Role section, we'll combine each individual permission with Admin
	 * There will no longer be multiple non-admin roles, bundled together.
	 */

	public static final String PRIVILEGE_ADMIN = "hasAuthority('Admin')";

	public static final String PRIVILEGES_ADMIN_AND_VIEW_PASSENGER = "hasAnyAuthority('Admin', 'View Passenger')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST = "hasAnyAuthority('Admin', 'Manage Watch List')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_QUERIES = "hasAnyAuthority('Admin', 'Manage Queries')";
	
	public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES = "hasAnyAuthority('Admin', 'Manage Rules')";
	
	public static final String PRIVILEGES_ADMIN_AND_MANAGE_HITS = "hasAnyAuthority('Admin', 'Manage Hits')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_CASES = "hasAnyAuthority('Admin', 'Manage Cases')";

	//This combo role is generated because of a specific edge case: Manage queries can see list of passengers in nearly all facets EXCEPT for after flight grid selection, this makes little sense
	//This enables the list of pax after a flight is selected to be displayed for users with the permission Manage Queries
	public static final String PRIVILEGES_ADMIN_AND_VIEW_PASSENGER_AND_MANAGE_QUERIES = "hasAnyAuthority('Admin','View Passenger','Manage Queries')";
	
	//These below are now defunct excepting ALL_PRIVS_AND_ONE_DAY
	
	public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries')";

	public static final String PRIVLEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES_AND_VIEW_PASSENGER = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries', 'View Passenger')";

	public static final String ALL_PRIVS_AND_ONE_DAY = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries','View Passenger', 'One Day Lookout')";

}
