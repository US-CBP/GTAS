import React, { useState } from "react";
import vaquita from "vaquita/dist/vaquita";
import "./utils";
import svgs from "./utils";
import "../../services/configService";
import { cypher, cypherAuth } from "../../services/configService";
import * as d3 from "d3";

const Graph = props => {
  // constructor(props) {
  //   super(props);
  // }

  const [passenger, setPassenger] = useState({});

  const _setRef = componentNode => {
    this._rootNode = componentNode;
  };
  const SvgType = 2;
  let isReloaded = true;

  cypher().then(function(result) {
    vaquita.rest.CYPHER_URL = result;
  });

  cypherAuth().then(function(result) {
    vaquita.rest.AUTHORIZATION = result;
  });

  vaquita.tools.TOGGLE_TAXONOMY = false;
  vaquita.query.USE_RELATION_DIRECTION = false;
  vaquita.tools.SAVE_GRAPH = false;
  vaquita.query.RESULTS_PAGE_SIZE = 100;
  vaquita.query.MAX_RESULTS_COUNT = 30;
  // vaquita.logger.LEVEL = vaquita.logger.LogLevels.INFO;
  vaquita.graph.link.SHOW_MARKER = false;
  vaquita.graph.node.DONUT_WIDTH = 15;
  vaquita.graph.HORIZONTAL_NODES = 1;

  vaquita.graph.setZoom(0.5, 2);

  const paxIdTag = passenger.paxIdTag;
  const paxLastName = passenger.lastName;
  const paxFlightIdTag = passenger.flightIdTag;
  const paxFullFlightNumber = (passenger.carrier + passenger.flightNumber).toUpperCase();
  const origin = passenger.embarkation;
  const destination = passenger.debarkation;

  const palette = {
    address: "#2C17B1",
    airport: "#1144AA",
    creditcard: "#007676",
    document: "#219E00",
    email: "#D77B00",
    flight: "#A90067",
    hit: "#FF0D00",
    passenger: "#5e166e",
    phone: "#D74B00"
  };

  vaquita.provider.node.Provider = {
    Address: {
      returnAttributes: ["address_line_1", "country", "city"],
      displayAttribute: "address_line_1",
      getDisplayType: node => SvgType,
      getColor: node => palette.address,
      getSVGPaths: node => [
        {
          d: svgs.getAddressPath(),
          fill: vaquita.provider.node.getColor(node)
        }
      ]
    },
    Airport: {
      returnAttributes: ["country_code", "airport_code"],
      constraintAttribute: "airport_code",
      displayAttribute: "airport_code",
      getDisplayType: function(node) {
        return SvgType;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getAirportPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getColor: function(node) {
        return palette.airport;
      },
      getIsTextDisplayed: function(node) {
        return true;
      }
    },
    Passenger: {
      returnAttributes: [
        "id_tag",
        "first_name",
        "middle_name",
        "last_name",
        "dob",
        "gender",
        "nationality",
        "last_updated_on",
        "gtas_passenger_id",
        "gtas_message_create_dtm",
        "gtas_message_id"
      ],
      constraintAttribute: "id_tag",
      displayAttribute: "last_name",
      getDisplayType: function(node) {
        return SvgType;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getPassengerPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getColor: function(node) {
        return palette.passenger;
      }
    },
    Phone: {
      returnAttributes: ["number"],
      displayAttribute: "number",
      constraintAttribute: "number",
      getDisplayType: function(node) {
        return SvgType;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getPhonePath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getColor: function(node) {
        return palette.phone;
      },
      getIsTextDisplayed: function(node) {
        return true;
      }
    },
    CreditCard: {
      returnAttributes: ["number", "exp_date", "type", "account_holder"],
      displayAttribute: "number",
      constraintAttribute: "number",
      getDisplayType: function(node) {
        return SvgType;
      },
      getColor: function(node) {
        return palette.creditcard;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getCreditCardPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getIsTextDisplayed: function(node) {
        return true;
      }
    },
    Document: {
      returnAttributes: ["number", "exp_date", "type", "issuance_country"],
      displayAttribute: "number",
      constraintAttribute: "number",
      getDisplayType: function(node) {
        return SvgType;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getDocumentPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getColor: function(node) {
        return palette.document;
      },
      getIsTextDisplayed: function(node) {
        return true;
      }
    },
    Email: {
      returnAttributes: ["address"],
      displayAttribute: "address",
      getDisplayType: function(node) {
        return SvgType;
      },
      getColor: function(node) {
        return palette.email;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getEmailPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      }
    },
    Flight: {
      returnAttributes: [
        "full_flight_number",
        "flight_id_tag",
        "eta_date",
        "full_eta_dtm",
        "destination_country",
        "gtas_message_id",
        "flight_number",
        "origin",
        "destination",
        "passenger_count",
        "full_etd_dtm",
        "origin_country",
        "gtas_message_create_dtm",
        "carrier",
        "last_updated_on",
        "etd_date",
        "direction"
      ],
      constraintAttribute: "flight_id_tag",
      displayAttribute: "full_flight_number",
      getPredefinedConstraints: function() {
        return ["$identifier.flight_id_tag is not null"];
      },
      getDisplayType: function(node) {
        return SvgType;
      },
      getColor: function(node) {
        return palette.flight; //
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getFlightPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      }
    },
    Hit: {
      returnAttributes: [
        "rule_id",
        "gtas_hit_detail_id",
        "cond_text",
        "description",
        "hit_detail_create_date",
        "title",
        "hit_type"
      ],
      displayAttribute: "hit_type",
      getDisplayType: function(node) {
        return SvgType;
      },
      getSVGPaths: function(node) {
        return [
          {
            d: svgs.getHitPath(),
            fill: vaquita.provider.node.getColor(node)
          }
        ];
      },
      getColor: function(node) {
        return palette.hit;
      },
      getIsTextDisplayed: function(node) {
        return true;
      }
    }
  };

  vaquita.provider.link.Provider = {
    getColor: function(link) {
      return palette[link.source.label.toLowerCase()];
    }
  };

  const thisPaxFlight = {
    label: "Passenger",
    rel: [
      {
        label: "flew_on",
        target: {
          label: "Flight",
          value: [
            {
              flight_id_tag: paxFlightIdTag,
              full_flight_number: paxFullFlightNumber
            }
          ]
        }
      }
    ] // rel
  };

  const paxRelations = [
    {
      label: "flew_on",
      target: {
        label: "Flight",
        value: [
          {
            flight_id_tag: paxFlightIdTag,
            full_flight_number: paxFullFlightNumber
          }
        ]
      }
    },
    {
      label: "used_document",
      target: { label: "Document" }
    },
    {
      label: "used_email",
      target: { label: "Email" }
    },
    {
      label: "used_creditcard",
      target: { label: "CreditCard" }
    },
    {
      label: "lived_at",
      target: { label: "Address" }
    },
    {
      label: "used_phone",
      target: { label: "Phone" }
    },
    {
      label: "flagged",
      target: { label: "Hit" }
    }
  ];

  const thisPax = {
    label: "Passenger",
    value: [
      {
        id_tag: paxIdTag,
        last_name: paxLastName
      }
    ]
  };

  const saves = {
    pax: {
      // this pax
      label: "Passenger",
      horiz: 1,
      value: [
        {
          id_tag: paxIdTag,
          last_name: paxLastName
        }
      ],
      rel: paxRelations
    },
    flight: {
      // this flight, all pax, ports
      label: "Flight",
      horiz: 1,
      value: [
        {
          flight_id_tag: paxFlightIdTag,
          full_flight_number: paxFullFlightNumber
        }
      ],
      rel: [
        {
          label: "flew_on",
          target: { label: "Passenger" }
        },
        {
          label: "origin_of",
          target: {
            label: "Airport",
            value: [{ airport_code: origin }]
          }
        },
        {
          label: "has_destination",
          target: {
            label: "Airport",
            value: [{ airport_code: destination }]
          }
        }
      ]
    },
    email: {
      // all emails this pax
      label: "Email",
      horiz: 2,
      rel: [
        {
          label: "used_email",
          target: thisPax
        }
      ] // rel
    },
    address: {
      //all addys this pax
      label: "Address",
      horiz: 2,
      rel: [
        {
          label: "lived_at",
          target: thisPax
        }
      ] // rel
    },
    document: {
      //all docs this pax
      label: "Document",
      horiz: 2,
      rel: [
        {
          label: "used_document",
          target: thisPax
        }
      ] // rel
    },
    creditcard: {
      //all ccards this pax
      label: "CreditCard",
      horiz: 2,
      rel: [
        {
          label: "used_creditcard",
          target: thisPax
        }
      ] // rel
    },
    phone: {
      //all phones this pax
      label: "Phone",
      horiz: 2,
      rel: [
        {
          label: "used_phone",
          target: thisPax
        }
      ] // rel
    },
    hit: {
      //  hits this pax
      label: "Passenger",
      horiz: 2,
      value: [
        {
          id_tag: paxIdTag,
          last_name: paxLastName
        }
      ],
      rel: [
        {
          label: "flagged",
          target: { label: "Hit" }
        }
      ]
    }, // rel

    emailall: {
      //all emails this flight
      label: "Email",
      horiz: 3,
      rel: [
        {
          label: "used_email",
          target: thisPaxFlight
        }
      ] // rel
    },
    addressall: {
      //all addys this flight
      label: "Address",
      horiz: 3,
      rel: [
        {
          label: "lived_at",
          target: thisPaxFlight
        }
      ] // rel
    },
    documentall: {
      //all docs this flight
      label: "Document",
      horiz: 3,
      rel: [
        {
          label: "used_document",
          target: thisPaxFlight
        }
      ] // rel
    },
    creditcardall: {
      //all ccards this flight
      label: "CreditCard",
      horiz: 3,
      rel: [
        {
          label: "used_creditcard",
          target: thisPaxFlight
        }
      ] // rel
    },
    phoneall: {
      //all phones this flight
      label: "Phone",
      horiz: 3,
      rel: [
        {
          label: "used_phone",
          target: thisPaxFlight
        }
      ] // rel
    },
    hitall: {
      // all pax with hits this flight
      label: "Hit",
      horiz: 3,
      rel: [
        {
          label: "flagged",
          target: thisPaxFlight
        }
      ] // rel
    }
  }; //saves

  const documentPath = function() {
    return svgs.getDocumentPath();
  };
  const airportPath = function() {
    return svgs.getAirportPath();
  };

  const activateGraph = function() {
    const template = saves.pax;
    vaquita.graph.HORIZONTAL_NODES = template.horiz || 1;

    // call start only when there's no rootnode
    //TODO vaquita - expose a status field on graph?
    if (vaquita.dataModel.getRootNode() === undefined) {
      vaquita.start(template);
      isReloaded = false;
    }
    // refresh graph arena if the page reloads with new pax data
    else if (isReloaded) {
      vaquita.refresh(template);
      isReloaded = false;
    }
  };

  const onClickSavedGraph = function(id) {
    // Update Graph title:
    if (!id) {
      d3.select("#save-header").text(
        d3
          .select(this)
          .select(".ppt-label")
          .text()
      );
      id = this.id;
    }

    vaquita.graph.mainLabel = saves[id];
    vaquita.graph.HORIZONTAL_NODES = saves[id].horiz || 1;
    vaquita.tools.reset();
  };

  vaquita.graph.onSave(function(graph) {});

  vaquita.result.onTotalResultCount(function(count) {
    document.getElementById("result-total-count").innerHTML = "(" + count + ")";
  });

  return <div className="line-container" ref={this._setRef.bind(this)} />;
};

export default Graph;
