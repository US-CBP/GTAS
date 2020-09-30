/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.Translation;
import gov.gtas.services.TranslationService;
import gov.gtas.vo.TranslationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

@RestController
public class TranslationController {
  private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
  private final TranslationService service;

  @Autowired
  public TranslationController(TranslationService service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/translation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public TranslationVo save(@RequestBody @Valid TranslationVo translationVo) throws IOException {
    service.save(translationVo);
    return translationVo;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/translation")
  public List<TranslationVo> getTranslationsByLang() throws IOException {
    return service.findAll();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/translation/{language}")
  public List<TranslationVo> getTranslationsByLang(@PathVariable String language) throws IOException {
    return service.getTranslationsByLang(language);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/translation/{language}/{code}")
  public TranslationVo getTranslationByCodeAndLang(@PathVariable String language, @PathVariable String code) throws IOException {
    return service.getTranslationByCodeandLang(code, language);
  }

}
