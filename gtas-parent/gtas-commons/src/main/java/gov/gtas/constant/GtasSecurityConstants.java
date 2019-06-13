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
    
    ///////////////////////////Role Privilege ///////////////////////////
    
	public static final String PRIVILEGE_ADMIN = "hasAuthority('Admin')";

	public static final String PRIVILEGES_ADMIN_AND_VIEW_PASSENGER = "hasAnyAuthority('Admin', 'View Passenger')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST = "hasAnyAuthority('Admin', 'Manage Watch List')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_QUERIES = "hasAnyAuthority('Admin', 'Manage Queries')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List')";

    public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries')";
    
    public static final String PRIVLEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES_AND_VIEW_PASSENGER = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries', 'View Passenger')";

    public static final String ALL_PRIVS_AND_ONE_DAY = "hasAnyAuthority('Admin', 'Manage Rules', 'Manage Watch List', 'Manage Queries','View Passenger', 'One Day Lookout')";

	public static final String PRIVILEGES_ADMIN_AND_MANAGE_RULES = "hasAnyAuthority('Admin', 'Manage Rules')";

}
