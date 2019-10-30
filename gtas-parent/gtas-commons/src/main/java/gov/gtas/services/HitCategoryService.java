/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.HitCategory;

public interface HitCategoryService {

	HitCategory findById(Long id);

	Iterable<HitCategory> findAll();

	void create(HitCategory hitCategory);
}
