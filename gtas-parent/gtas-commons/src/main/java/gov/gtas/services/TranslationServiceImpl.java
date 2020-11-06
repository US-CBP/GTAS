/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Translation;
import gov.gtas.repository.TranslationRepository;
import gov.gtas.vo.TranslationVo;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class TranslationServiceImpl implements TranslationService {
	private static Logger logger = LoggerFactory.getLogger(TranslationServiceImpl.class);

  @Resource
  private TranslationRepository translationRespository;

  @Override
  @Transactional
  public void save(TranslationVo translationVo) {
    Translation existing = translationRespository.getTranslationByCodeandLang(translationVo.getCode(), translationVo.getLanguage());

    if (existing != null && existing.getId() != null) {
      translationRespository.updateTranslation(existing.getId(), translationVo.getTranslation());
    }
    else {
      Translation updated = buildTranslation(translationVo);
      translationRespository.save(updated);
    }
  }

  @Override
  @Transactional
  public List<TranslationVo> findAll() throws IOException {
    List<Translation> allTranslations = (List<Translation>) translationRespository.findAll();
    List<TranslationVo> allTranslationVos = new ArrayList<>();

    for (Translation translation : allTranslations) {
      allTranslationVos.add(buildTranslationVo(translation));
    }

    return allTranslationVos;
  }

  @Override
  @Transactional
  public List<TranslationVo> getTranslationsByLang(String language) throws IOException {
    List<Translation> translations = translationRespository.getTranslationsByLang(language);
    List<TranslationVo> translationsList = new ArrayList<>();

    if (translations != null && translations.size() > 0) {
      for (Translation t : translations) {
        translationsList.add(buildTranslationVo(t));
      }
    }

    return translationsList;
  }

  @Override
  @Transactional
  public TranslationVo getTranslationByCodeandLang(String code, String language) throws IOException {
    Translation translation = translationRespository.getTranslationByCodeandLang(code, language);

      return buildTranslationVo(translation);
  }

  private TranslationVo buildTranslationVo(Translation translation) throws IOException {
    return new TranslationVo(translation);
  }

  private Translation buildTranslation(TranslationVo translationVo) {
    return new Translation(translationVo.getId(), translationVo.getCode(), translationVo.getLanguage(), translationVo.getTranslation());
  }

}
