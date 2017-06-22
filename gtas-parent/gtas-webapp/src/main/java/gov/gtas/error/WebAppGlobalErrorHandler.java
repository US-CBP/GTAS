/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import gov.gtas.constants.ErrorConstants;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.services.ErrorPersistenceService;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WebAppGlobalErrorHandler {
    @Autowired
    ErrorPersistenceService errorService;
    /*
     * The logger for the Webapp Global Error Handler
     */
//  private static final Logger logger = LoggerFactory
//          .getLogger(WebAppGlobalErrorHandler.class);
    
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommonServiceException.class)
    public @ResponseBody JsonServiceResponse handleError(CommonServiceException ex) {
        ErrorDetailInfo err = null;
        if(ex.isLogable()){
            err = generateErrorInfoAndLogError(ex);
        } else{
            err= ErrorHandlerFactory.getErrorHandler().processError(ex);
        }
        return new JsonServiceResponse(err.getErrorCode(),
                err.getErrorDescription(), null);
    }
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public @ResponseBody JsonServiceResponse handleError(HttpMessageNotReadableException ex) {
        return new JsonServiceResponse(WebappErrorConstants.MALFORMED_JSON_ERROR_CODE,
                String.format(WebappErrorConstants.MALFORMED_JSON_ERROR_MESSAGE, ex.getMessage()), null);       
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JpaSystemException.class)
    public @ResponseBody JsonServiceResponse handleError(JpaSystemException ex) {
        JsonServiceResponse resp = GlobalErrorHandlerHelper.createDbErrorResponse(errorService, ex);
            return resp;
    }
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public @ResponseBody JsonServiceResponse handleError(TypeMismatchException ex) {
        ex.printStackTrace();
        return new JsonServiceResponse(ErrorConstants.INVALID_PATH_VARIABLE_ERROR_CODE,
        "The REST path variable could not be parsed:"
                + ex.getMessage(), null);
    }
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public @ResponseBody JsonServiceResponse handleError(HttpRequestMethodNotSupportedException ex) {
        ex.printStackTrace();
        return new JsonServiceResponse( ErrorConstants.HTTP_METHOD_NOT_SUPPORTED_ERROR_CODE,
        "The URL could not be dispatched:"
                + ex.getMessage(), null);
    }
   
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody JsonServiceResponse handleError(Exception ex) {
        return new JsonServiceResponse(generateErrorInfoAndLogError(ex));           
    }
    
    private ErrorDetailInfo generateErrorInfoAndLogError(Exception ex){
        ErrorDetailInfo errorDetails = ErrorHandlerFactory.createErrorDetails(ex);
        try{
            errorDetails = errorService.create(errorDetails); //add the saved ID
        } catch (Exception exception){
            //possibly DB is down
            exception.printStackTrace();
        }
        return  errorDetails;   
    }
}
