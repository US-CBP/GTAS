/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;

public interface MessageRepository<T extends Message> extends CrudRepository<T, Long> {
    List<T> findByStatus(MessageStatus status);
    T findByHashCode(String hashCode);
}