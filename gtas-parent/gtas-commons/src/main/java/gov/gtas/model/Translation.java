/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

@Cacheable
@Entity
@Table(name = "translation", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "code", "language"})}, indexes = { @Index(columnList = "language", name = "translation_lang_index")}
)

  public class Translation extends BaseEntityAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String code;
	private String language;
	private String translation;

  @Column(name = "default_text", length = 255)
	private String defaultText;

	public Translation() {
	}

	public Translation(Long id, String code, String language, String translation, String defaultText) {
		this.id = id;
		this.code = code;
		this.language = language;
		this.translation = translation;
		this.defaultText = defaultText;
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

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.code, this.language, this.translation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;

    final Translation other = (Translation) obj;
    return Objects.equals(this.code, other.code) && Objects.equals(this.language, other.language)
        && Objects.equals(this.translation, other.translation);
  }

	@Override
	public String toString() {
		return this.translation;
	}
}