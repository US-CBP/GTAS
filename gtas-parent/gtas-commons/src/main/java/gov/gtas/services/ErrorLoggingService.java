/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.InvalidObjectInfo;
import java.util.List;

public interface ErrorLoggingService {

    public InvalidObjectInfo create(InvalidObjectInfo invalidObjectInfo);
    public InvalidObjectInfo delete(Long id);
    public List<InvalidObjectInfo> findAll();
    public InvalidObjectInfo update(InvalidObjectInfo port) ;
    public InvalidObjectInfo findById(Long id);
}
