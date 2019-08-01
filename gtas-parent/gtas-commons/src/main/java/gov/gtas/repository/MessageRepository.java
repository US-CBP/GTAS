/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import gov.gtas.model.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository<T extends Message> extends CrudRepository<T , Long> {
    Message findByHashCode(String hashCode);
    List<T> findTop500ByOrderByIdDesc();
}
