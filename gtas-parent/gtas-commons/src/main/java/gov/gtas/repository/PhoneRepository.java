/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Phone;

import java.util.List;

public interface PhoneRepository extends CrudRepository<Phone, Long>{
    List<Phone> findByNumber(String number);
}
