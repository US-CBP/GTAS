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
