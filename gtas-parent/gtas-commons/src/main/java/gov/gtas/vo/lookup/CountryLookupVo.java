/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.lookup;

import java.io.Serializable;

public class CountryLookupVo implements Serializable {
  private String iso3;
  private String name;

  public CountryLookupVo(String iso3, String name) {
    this.iso3 = iso3;
    this.name = name;
  }

  public CountryLookupVo() {
  }

  public String getIso3() {
    return iso3;
  }

  public void setIso3(String data) {
    this.iso3 = data;
  }

  public String getName() {
    return name;
  }

  public void setName(String data) {
    this.name = data;
  }
}
