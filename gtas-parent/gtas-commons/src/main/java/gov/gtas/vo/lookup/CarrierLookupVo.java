package gov.gtas.vo.lookup;

import java.io.Serializable;

public class CarrierLookupVo implements Serializable {

  private String name;
  private String iata;

  public CarrierLookupVo(String name, String iata) {
    this.name = name;
    this.iata = iata;
  }

  public CarrierLookupVo() {
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
