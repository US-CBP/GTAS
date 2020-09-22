/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Translation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranslationRepository extends CrudRepository<Translation, String> {

	@Query("select t from Translation t where t.code = :code and t.language = :language")
	Translation getTranslationByCodeandLang(@Param("code") String code, @Param("language") String language);

	@Query( "select t from Translation t where t.language = :language" )
	List<Translation> getAllTranslationsByLang(@Param("language") String language);

	@Query("select t from Translation")
	List<Translation> getAllTranslations();
}