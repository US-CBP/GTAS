package gov.gtas.vo.lookup;

public class CarrierVo {

    private Long id;
    private Long originId;
    private String name;
    private String iata;
    private String icao;

    public CarrierVo(Long id, Long originId, String name, String iata, String icao) {
        this.id = id;
        this.originId = originId;
        this.name = name;
        this.iata = iata;
        this.icao = icao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long data) {
        this.originId = data;
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

    public String getIcao() {
        return icao;
    }

    public void setIcao(String data) {
        this.icao = data;
    }

}
