/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.lookup;

import java.io.Serializable;

public class AirportLookupVo implements Serializable {

  private String name;
  private String iata;

  public AirportLookupVo(String name, String iata) {
    this.name = name;
    this.iata = iata;
  }

  public AirportLookupVo() {
  }

  public String getName() {
    return name;
  }

  public void setName(String data) {
    this.name = data;
  }

  public String getIata() {
    return iata;
  }

  public void setIata(String data) {
    this.iata = data;
  }

}
