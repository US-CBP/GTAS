/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Country;
import gov.gtas.repository.CountryRepository;
import gov.gtas.repository.CountryRepositoryCustom;
import gov.gtas.vo.lookup.CountryVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {

	@Resource
	private CountryRepository countryRepository;
	@Resource
	private CountryRepositoryCustom countryRepoCust;

	@Override
	@Transactional
	public CountryVo create(CountryVo country) {
		Country savedCountry = countryRepository.save(buildCountry(country));

		return buildCountryVo(savedCountry);
	}

	@Override
	@Transactional
	public CountryVo delete(Long id) {
		CountryVo countryVo = this.findById(id);

		if (countryVo != null) {
			countryRepository.delete(buildCountry(countryVo));
		}

		return countryVo;
	}

	@Override
	@Transactional
	public List<CountryVo> findAll() {
		List<Country> allCountries = (List<Country>) countryRepository.findAll();

		List<CountryVo> allCountryVos = new ArrayList<>();

		for (Country country : allCountries) {
			allCountryVos.add(buildCountryVo(country));
		}

		return allCountryVos;

	}

	@Override
	@Transactional
	public CountryVo update(CountryVo country) {
		Country savedCountry = countryRepository.save(buildCountry(country));

		return buildCountryVo(savedCountry);
	}

	@Override
	@Transactional
	public CountryVo findById(Long id) {
		Country country = countryRepository.findById(id).orElse(null);

		if (country == null) {
			return null;
		}

		return buildCountryVo(country);
	}

	@Override
	@Transactional
	public CountryVo restore(CountryVo country) {
		Country restoredCountry = countryRepoCust.restore(buildCountry(country));

		return buildCountryVo(restoredCountry);
	}

	@Override
	@Transactional
	public int restoreAll() {
		return countryRepoCust.restoreAll();
	}

	@Override
	@Transactional
	@Cacheable(value = "countryCache", key = "#country")
	public CountryVo getCountryByTwoLetterCode(String country) {
		List<Country> countries = countryRepository.getCountryByTwoLetterCode(country);

		if (countries != null && countries.size() > 0) {
			return buildCountryVo(countries.get(0));
		}

		return null;
	}

	@Override
	@Transactional
	@Cacheable(value = "countryCache", key = "#country")
	public CountryVo getCountryByThreeLetterCode(String country) {
		List<Country> countries = countryRepository.getCountryByThreeLetterCode(country);

		if (countries != null && countries.size() > 0) {
			return buildCountryVo(countries.get(0));
		}

		return null;
	}

	private CountryVo buildCountryVo(Country country) {
		return new CountryVo(country.getId(), country.getOriginId(), country.getIso2(), country.getIso3(),
				country.getName(), country.getIsoNumeric());
	}

	private Country buildCountry(CountryVo countryVo) {
		return new Country(countryVo.getId(), countryVo.getOriginId(), countryVo.getIso2(), countryVo.getIso3(),
				countryVo.getName(), countryVo.getIsoNumeric());
	}

}
