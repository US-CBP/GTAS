/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.Filter;

import java.util.List;

public interface FilterService {
    public FilterData create(FilterData filterData);

    public void delete(String userId);

    public List<FilterData> findAll();

    public FilterData update(FilterData filterData);

    public FilterData findById(String userId);

}
