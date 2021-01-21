/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Translation;
import gov.gtas.vo.TranslationVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface TranslationService {
  @PreAuthorize(PRIVILEGE_ADMIN)
  void save(TranslationVo translation);

  // @PreAuthorize(PRIVILEGE_ADMIN)
  // public TranslationVo delete(Long id);

  // @PreAuthorize(PRIVILEGE_ADMIN)
  // public TranslationVo deleteByLanguage(String language);

 @PreAuthorize(PRIVILEGE_ADMIN)
 List<TranslationVo> findAll() throws IOException;

  // // @PreAuthorize(PRIVILEGE_ADMIN)
  // public TranslationVo findById(Long id);

  List<TranslationVo> getTranslationsByLang(String language) throws IOException;
  
  Map<String,String> getTranslationValuesByLang(String language) throws IOException;

  @PreAuthorize(PRIVILEGE_ADMIN)
  TranslationVo getTranslationByCodeandLang(String code, String language) throws IOException;
}
