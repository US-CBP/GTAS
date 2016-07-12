/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
}
