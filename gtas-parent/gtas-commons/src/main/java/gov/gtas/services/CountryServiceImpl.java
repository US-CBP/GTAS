/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Country;
import gov.gtas.repository.CountryRepository;
import gov.gtas.repository.CountryRepositoryCustom;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

    @Resource
    private CountryRepository countryRepository;
    @Resource
    private CountryRepositoryCustom countryRepoCust;

    @Override
    @Transactional
    public Country create(Country country) {
        return countryRepository.save(country);
    }

    @Override
    @Transactional
    public Country delete(Long id) {
        Country country = this.findById(id);
        if(country != null){
            countryRepository.delete(country);
        }
        return country;
    }


    @Override
    @Transactional
    public List<Country> findAll() {
        return (List<Country>) countryRepository.findAll();
    }

    @Override
    @Transactional
    public Country update(Country country) {
      return countryRepository.save(country);
    }

    @Override
    @Transactional
    public Country findById(Long id) {
      return countryRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Country restore(Country country) {
        return countryRepoCust.restore(country);
    }

    @Override
    @Transactional
    public int restoreAll() {
        return countryRepoCust.restoreAll();
    }

    @Override
    @Transactional
    @Cacheable(value = "countryCache", key = "#country")
    public Country getCountryByTwoLetterCode(String country) {
        Country ctry = null;
        List<Country> countries = (List<Country>)countryRepository.getCountryByTwoLetterCode(country);
        if(countries != null && countries.size() >0)
            ctry=countries.get(0);
        return ctry;
    }

    @Override
    @Transactional
    @Cacheable(value = "countryCache", key = "#country")
    public Country getCountryByThreeLetterCode(String country) {
        Country ctry = null;
        List<Country> countries = (List<Country>)countryRepository.getCountryByThreeLetterCode(country);
        if(countries != null && countries.size() >0)
            ctry=countries.get(0);
        return ctry;
    }

}
