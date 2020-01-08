import React from "react";
import FlightBadge from "../flightBadge/FlightBadge";
import PaxInfoRow from "./PaxInfoRow";
import { Col, Form } from "react-bootstrap";
import "./PaxInfo.css";

const PaxInfo = props => {
  const fakepax = {
    lastname: "Smitty",
    firstname: "Alex",
    middlename: "",
    age: "46",
    dob: "1/1/1964",
    gender: "M",
    nationality: "GBR",
    residence: "ERI",
    eta: "3-4-2020 05:00",
    etd: "3-4-2020 09:00",
    origin: "RUH",
    destination: "IAD",
    flightnumber: "SV0039"
  };

  const pax = props.pax || fakepax;
  const badgeprops = {
    eta: pax.eta,
    etd: pax.etd,
    origin: pax.origin,
    destination: pax.destination,
    flightnumber: pax.flightnumber
  };
  return (
    <Col>
      <FlightBadge {...badgeprops}></FlightBadge>
      <Form>
        <PaxInfoRow leftlabel="Last Name" rightlabel={pax.lastname}></PaxInfoRow>
        <PaxInfoRow leftlabel="First Name" rightlabel={pax.firstname}></PaxInfoRow>
        <PaxInfoRow leftlabel="Middle Name" rightlabel={pax.middlename}></PaxInfoRow>
        <PaxInfoRow leftlabel="Age" rightlabel={`${pax.age} (${pax.dob})`}></PaxInfoRow>
        <PaxInfoRow leftlabel="Gender" rightlabel={pax.gender}></PaxInfoRow>
        <PaxInfoRow leftlabel="Nationality" rightlabel={pax.nationality}></PaxInfoRow>
        <PaxInfoRow leftlabel="Residence" rightlabel={pax.residence}></PaxInfoRow>
        <PaxInfoRow leftlabel="Origin Airport" rightlabel={pax.origin}></PaxInfoRow>
        <PaxInfoRow
          leftlabel="Destination Airport"
          rightlabel={pax.destination}
        ></PaxInfoRow>
      </Form>
    </Col>
  );
};

export default PaxInfo;
