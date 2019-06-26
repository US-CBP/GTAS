package gov.gtas.parsers.vo;

import gov.gtas.model.Bag;
import gov.gtas.model.BagMeasurements;
import gov.gtas.parsers.pnrgov.segment.TBD_BagTagDetails;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BagVo {

    private UUID bagVoUUID = UUID.randomUUID();
    private UUID passengerId;
    private Set<UUID> flightVoId = new HashSet<>();
    private BagMeasurementsVo bagMeasurementsVo;
    private UUID bagMeasurementUUID;
    private String bagId;
    private String data_source;
    private String destinationAirport;
    private String airline;
    private String bagTagIssuerCode;
    private boolean headPool = false;
    private boolean memberPool = false;
    private boolean primeFlight = false;
    private String consecutiveTagNumber;

    public BagVo() {
    }

    public static BagVo fromTbdBagTagDetails(TBD_BagTagDetails bagTagSegment) {
        BagVo bagVo = new BagVo();
        if (bagTagSegment == null || StringUtils.isBlank(bagTagSegment.getBagIdNumber())) {
            return bagVo;
        }
        bagVo.setBagId(bagTagSegment.getBagIdNumber());
        bagVo.setDestinationAirport(bagTagSegment.getThreeLetterCityCode());
        bagVo.setAirline(bagTagSegment.getCompanyId());
        bagVo.setBagTagIssuerCode(bagTagSegment.getCompIdNumber());

        return bagVo;
    }


    public boolean isHeadPool() {
        return headPool;
    }

    public void setHeadPool(boolean headPool) {
        this.headPool = headPool;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public String getBagTagIssuerCode() {
        return bagTagIssuerCode;
    }

    public void setBagTagIssuerCode(String bagTagIssuerCode) {
        this.bagTagIssuerCode = bagTagIssuerCode;
    }

    public boolean isPrimeFlight() {
        return primeFlight;
    }

    public void setPrimeFlight(boolean primeFlight) {
        this.primeFlight = primeFlight;
    }

    public String getConsecutiveTagNumber() {
        return consecutiveTagNumber;
    }

    public void setConsecutiveTagNumber(String consecutiveTagNumber) {
        this.consecutiveTagNumber = consecutiveTagNumber;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(UUID passengerId) {
        this.passengerId = passengerId;
    }

    public Set<UUID> getFlightVoId() {
        return flightVoId;
    }

    public void setFlightVoId(Set<UUID> flightVoId) {
        this.flightVoId = flightVoId;
    }

    public UUID getBagMeasurementUUID() {
        return bagMeasurementUUID;
    }

    public void setBagMeasurementUUID(UUID bagMeasurementUUID) {
        this.bagMeasurementUUID = bagMeasurementUUID;
    }

    public BagMeasurementsVo getBagMeasurementsVo() {
        return bagMeasurementsVo;
    }

    public void setBagMeasurementsVo(BagMeasurementsVo bagMeasurementsVo) {
        this.bagMeasurementsVo = bagMeasurementsVo;
    }

    public UUID getBagVoUUID() {
        return bagVoUUID;
    }

    public void setBagVoUUID(UUID bagVoUUID) {
        this.bagVoUUID = bagVoUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BagVo)) return false;
        BagVo bagVo = (BagVo) o;
        return getBagVoUUID().equals(bagVo.getBagVoUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBagVoUUID());
    }

    public boolean isMemberPool() {
        return memberPool;
    }

    public void setMemberPool(boolean memberPool) {
        this.memberPool = memberPool;
    }

    public boolean hasSameBagInfo(Bag bag) {
        BagMeasurements bagMeasurements = bag.getBagMeasurements();
        BagMeasurementsVo VoMeasurements = this.getBagMeasurementsVo();
        if (!objectsAreBothNullOrNotNull(bagMeasurements, VoMeasurements)) return false;

        boolean sameCount = true;
        boolean sameWeight = true;
        if (bagMeasurements != null) {
            Integer bagCount = bagMeasurements.getBagCount();
            Integer voCount = VoMeasurements.getQuantity();
            sameCount = Objects.equals(bagCount, voCount);

            Double bagWeight = bagMeasurements.getWeight();
            Double voWeight = VoMeasurements.getWeightInKilos();
            if (voWeight != null) {
                long rounded = Math.round(bagMeasurementsVo.getWeightInKilos());
                voWeight = (double) rounded;
            }
            sameWeight = Objects.equals(bagWeight, voWeight);

        }

        String voAirline = this.getAirline();
        String bagAirline = bag.getAirline();
        boolean sameAirline = Objects.equals(voAirline, bagAirline);

        String voDestinationAirport = this.getDestinationAirport();
        String bagDestinationAirport = bag.getDestinationAirport();
        boolean sameDestination = Objects.equals(voDestinationAirport, bagDestinationAirport);

        String bagId = bag.getBagId();
        String voId = this.getBagId();
        boolean sameId = Objects.equals(bagId, voId);

        String bagConseq = bag.getBagSerialCount();
        String voConseq = this.getConsecutiveTagNumber();
        boolean sameConseqId = Objects.equals(bagConseq, voConseq);

        return sameCount
                && sameWeight
                && sameAirline
                && sameDestination
                && sameId
                && sameConseqId;
    }

    private boolean objectsAreBothNullOrNotNull(Object o1, Object o2) {
        return !((o1 == null && o2 != null) || (o2 == null && o1 != null));
    }
}
