/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Flight;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class FlightGridVo {
  private static Logger logger = LoggerFactory.getLogger(FlightGridVo.class);
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
  static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

  protected Long id;
  private String fullFlightNumber;
  private String origin;
  private String destination;
  private String direction;
  private Date etd;
  private Date eta;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date countdown;
  private Integer passengerCount = 0;
  private Integer ruleHitCount;
  private Integer listHitCount;
  private Integer graphHitCount;
  private Integer fuzzyHitCount;
  private Integer externalHitCount;
  private Integer manualHitCount;
  private Integer lowPrioHitCount;
  private Integer medPrioHitCount;
  private Integer highPrioHitCount;

  public static FlightGridVo from(Flight flight) {
    FlightGridVo fgvo = new FlightGridVo();
    try {
      BeanUtils.copyProperties(flight, fgvo);
      BeanUtils.copyProperties(flight.getMutableFlightDetails(), fgvo);
      fgvo.setId(flight.getId());
    } catch (Exception e) {
      logger.error("failure to copy proeprties", e);
    }
    return fgvo;
  }

  public static FlightGridVo from(BookingDetail bookingDetail) {
    FlightGridVo fgvo = new FlightGridVo();
    fgvo.setDestination(bookingDetail.getDestination());
    fgvo.setEta(bookingDetail.getEta());
    fgvo.setOrigin(bookingDetail.getOrigin());
    fgvo.setEtd(bookingDetail.getEtd());
    fgvo.setFullFlightNumber(bookingDetail.getFullFlightNumber());
    return fgvo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getFullFlightNumber() {
    return fullFlightNumber;
  }

  public void setFullFlightNumber(String fullFlightNumber) {
    this.fullFlightNumber = fullFlightNumber;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public Integer getPassengerCount() {
    return passengerCount;
  }

  public void setPassengerCount(Integer passengerCount) {
    this.passengerCount = passengerCount;
  }

  public Integer getRuleHitCount() {
    return ruleHitCount;
  }

  public void setRuleHitCount(Integer ruleHitCount) {
    this.ruleHitCount = ruleHitCount;
  }

  public Integer getListHitCount() {
    return listHitCount;
  }

  public void setListHitCount(Integer listHitCount) {
    this.listHitCount = listHitCount;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  public Integer getGraphHitCount() {
    return graphHitCount;
  }

  public void setGraphHitCount(Integer graphHitcount) {
    this.graphHitCount = graphHitcount;
  }

  public Integer getFuzzyHitCount() {
    return fuzzyHitCount;
  }

  public void setFuzzyHitCount(Integer fuzzyHitcount) {
    this.fuzzyHitCount = fuzzyHitcount;
  }

  public Integer getExternalHitCount() { return externalHitCount; }

  public void setExternalHitCount(Integer externalHitCount) { this.externalHitCount = externalHitCount; }

  public Integer getManualHitCount() { return manualHitCount; }

  public void setManualHitCount(Integer manualHitCount) { this.manualHitCount = manualHitCount; }

  public Date getEtd() {
    return etd;
  }

  public void setEtd(Date etd) {
    this.etd = etd;
  }

  public Date getEta() {
    return eta;
  }

  public void setEta(Date eta) {
    this.eta = eta;
  }

  public Date getCountdown() {
    return countdown;
  }

  public void setCountdown(Date countdown) {
    this.countdown = countdown;
  }

  public Integer getLowPrioHitCount() { return lowPrioHitCount; }

  public void setLowPrioHitCount(Integer lowPrioHitCount) { this.lowPrioHitCount = lowPrioHitCount; }

  public Integer getMedPrioHitCount() { return medPrioHitCount; }

  public void setMedPrioHitCount(Integer medPrioHitCount) { this.medPrioHitCount = medPrioHitCount; }

  public Integer getHighPrioHitCount() { return highPrioHitCount; }

  public void setHighPrioHitCount(Integer highPrioHitCount) { this.highPrioHitCount = highPrioHitCount; }
}
