import React, { useRef } from "react";
import RQueryBuilder from "react-querybuilder";
import "./QueryBuilder.scss";

const QueryBuilder = props => {
  function logQuery(query) {
    console.log(query);

    console.log(document.getElementsByClassName("rule"));
  }

  const entities = [
    { name: "ADDRESS", label: "ADDRESS", fields: "addressFields" },
    { name: "BAG", label: "BAG", fields: "addressFields" },
    { name: "CREDIT CARD", label: "CARD", fields: "addressFields" },
    { name: "DOCUMENT", label: "DOCUMENT", fields: "addressFields" },
    { name: "EMAIL", label: "EMAIL", fields: "addressFields" },
    { name: "FLIGHT", label: "FLIGHT", fields: "addressFields" },
    { name: "FLIGHT LEG", label: "LEG", fields: "addressFields" },
    { name: "FREQUENT FLYER", label: "FLYER", fields: "addressFields" },
    { name: "PASSENGER", label: "PASSENGER", fields: "addressFields" },
    { name: "PHONE", label: "PHONE", fields: "addressFields" },
    { name: "FORM OF PAYMENT", label: "PAYMENT", fields: "addressFields" },
    { name: "PNR", label: "PNR", fields: "addressFields" },
    { name: "DWELL TIME", label: "TIME", fields: "addressFields" },
    { name: "TRAVEL AGENCY", label: "AGENCY", fields: "addressFields" }
  ];

  const addressFields = [
    { name: "", label: "Select" },
    { name: "city", label: "City" },
    { name: "country", label: "Country" },
    { name: "postalCode", label: "Postal Code" },
    { name: "address", label: "Address" },
    { name: "phone", label: "Phone" },
    { name: "email", label: "Email" },
    { name: "twitter", label: "Twitter" },
    { name: "isDev", label: "Is a Developer?", value: false }
  ];

  const bagFields = [
    { name: "", label: "Select" },
    { name: "airline", label: "Airline" },
    { name: "bagId", label: "Bag ID" },
    { name: "dataSource", label: "Data Source" }
  ];

  const documentFields = [
    { name: "", label: "Select" },
    { name: "issuanceCountry", label: "Issuance Country" },
    { name: "expirationDate", label: "Expiration Date" },
    { name: "issuanceDate", label: "Issuance Date" }
  ];

  return <RQueryBuilder fields={entities} onQueryChange={logQuery}></RQueryBuilder>;
};

export default QueryBuilder;
