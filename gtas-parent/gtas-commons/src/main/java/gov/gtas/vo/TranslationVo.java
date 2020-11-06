/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import gov.gtas.model.Translation;
import java.io.IOException;

public class TranslationVo {
   private Long id;
  private String code;
  private String language;
  private String translation;

  public TranslationVo() {
  }

  public TranslationVo(Translation translation) throws IOException {
     this.id = translation.getId();
    this.code = translation.getCode();
    this.language = translation.getLanguage();
    this.translation = translation.getTranslation();
  }

   public Long getId() {
     return id;
   }
   public void setId(Long id) {
     this.id = id;
   }

  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }

  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }

  public String getTranslation() {
    return translation;
  }
  public void setTranslation(String translation) {
    this.translation = translation;
  }

}
