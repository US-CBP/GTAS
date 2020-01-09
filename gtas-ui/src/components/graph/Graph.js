import React, { useState } from "react";
import "../../services/configService";
import { cypher, cypherAuth } from "../../services/configService";
import * as d3 from "d3";
import { provider, paxRelations, saves, palette } from "./structure";
import "./Graph.css";
import "../../../node_modules/vaquita/css/vaquita-svg.css";
const vaquita = require("vaquita");

class Graph extends React.Component {
  constructor(props) {
    super(props);

    this.onClickSavedGraph = this.onClickSavedGraph.bind(this);
    this.activateGraph = this.activateGraph.bind(this);

    this.pax1 = {
      dob: "1967-05-22",
      updatedAt: "2019-07-16T17:12:31.561000000",
      paxId: 23,
      idTag: "c957ff6158dbd9d8145971f44c3ea66156338261",
      firstName: "HIEU",
      middleName: "EV?EN",
      lastName: "KORANDAK",
      id: 70456,
      createdAt: 1575796218000,
      createdBy: "SYSTEM",
      updatedBy: null,
      suffix: null,
      gender: "M",
      nationality: "ERI",
      passengerType: "P",
      seat: null,
      flightId: "1",
      flightNumber: "0037",
      fullFlightNumber: "UA0861",
      carrier: "UA",
      etd: 1576032600000,
      eta: 1576084800000,
      flightOrigin: "IAD",
      flightDestination: "GRU",
      onRuleHitList: true,
      onGraphHitList: false,
      onWatchList: false,
      onWatchListDoc: false,
      onWatchListLink: false,
      documents: [
        {
          documentType: "P",
          documentNumber: "374330695",
          expirationDate: "2025-03-02",
          issuanceDate: null,
          issuanceCountry: "ERI",
          firstName: null,
          lastName: null
        }
      ]
    };

    this.setRef = componentNode => {
      Node.getRootNode = componentNode;
    };

    const SvgType = 2;

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

    vaquita.provider.node.Provider = provider(vaquita, SvgType);

    vaquita.provider.link.Provider = {
      getColor: function(link) {
        return palette[link.source.label.toLowerCase()];
      }
    };

    vaquita.result.onTotalResultCount(function(count) {
      document.getElementById("result-total-count").innerHTML = "(" + count + ")";
    });

    this.state = {
      pax1: this.pax1,
      svgType: 2,
      isReloaded: true,
      palette: palette,
      paxIdTag: this.pax1.paxIdTag,
      paxLastName: this.pax1.lastName,
      paxFlightIdTag: this.pax1.flightIdTag,
      paxFullFlightNumber: this.pax1.carrier + this.pax1.flightNumber,
      origin: this.pax1.embarkation,
      destination: this.pax1.debarkation,
      vaquita: vaquita,
      // thisPaxFlight: thisPaxFlight,
      paxRelations: paxRelations(
        this.pax1.flightIdTag,
        this.pax1.carrier + this.pax1.flightNumber
      ),
      saves: saves(this.pax1)
    };
  }

  componentDidMount() {
    cypher.get().then(function(result) {
      vaquita.rest.CYPHER_URL = "http://localhost:7474/db/data/transaction/commit"; //result;
    });

    cypherAuth.get().then(function(result) {
      vaquita.rest.AUTHORIZATION = "fake neo4j auth secret"; //result;
    });

    this.activateGraph();
  }

  shouldComponentUpdate() {
    return false;
  }

  // documentPath = function() {
  //   return svgs.getDocumentPath();
  // };

  // airportPath = function() {
  //   return svgs.getAirportPath();
  // };

  activateGraph = function() {
    const template = this.state.saves.pax;

    let vaq = this.state.vaquita;
    vaq.graph.HORIZONTAL_NODES = template.horiz || 1;

    // call start only when there's no rootnode
    //TODO vaquita - expose a status field on graph?
    if (vaquita.dataModel.getRootNode() === undefined) {
      vaq.start(template);
      this.setState({ isReloaded: false });
    }
    // refresh graph arena if the page reloads with new pax data
    else if (this.state.isReloaded) {
      vaq.refresh(template);
      this.setState({ isReloaded: false });
    }
  };

  onClickSavedGraph = function(id) {
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

    let vaq = this.state.vaquita;

    vaq.graph.mainLabel = this.state.saves[id];
    vaq.graph.HORIZONTAL_NODES = this.state.saves[id].horiz || 1;
    vaq.tools.reset();
  };

  render() {
    return (
      <div className="line-container" ref={this.setRef.bind(this)}>
        <div className="flex flex-vert ie-fix-md full-width align-items-center scroll-container-outer">
          <div className="cbp-card-container full-width">
            <div className="cbp-card cbp-card-shadow">
              <h4 className="h-label no-margin-top">Search</h4>

              <div className="ppt-body">
                <section className="ppt-section-main">
                  <div className="ppt-container-graph row">
                    <nav id="popoto-saves" className="col-lg-2 ppt-taxo-nav">
                      <div id="saves">
                        <span className="ppt-header-span">This Passenger:</span>
                        <table className="ppt-saved-ul">
                          <tr id="Pax" onClick={() => this.onClickSavedGraph("pax")}>
                            <td>
                              <i className="fa fa-user-circle-o pptpassenger"></i>
                            </td>
                            <td>
                              <span className="ppt-label" title="Passenger links">
                                Passenger
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Address"
                            onClick={() => this.onClickSavedGraph("address")}
                          >
                            <td>
                              <i class="fa fa-home pptaddress"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="Addresses used by this passenger"
                              >
                                Address
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="CreditCard"
                            onClick={() => this.onClickSavedGraph("creditcard")}
                          >
                            <td>
                              <i class="fa fa-credit-card-alt pptcreditcard"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="Credit cards used by this passenger"
                              >
                                Credit Card
                              </span>
                            </td>
                          </tr>
                          <tr id="Phone" onClick={() => this.onClickSavedGraph("phone")}>
                            <td>
                              <i class="fa fa-phone pptphone"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="Phone numbers used by this passenger"
                              >
                                Phone
                              </span>
                            </td>
                          </tr>
                          <tr id="Email" onClick={() => this.onClickSavedGraph("email")}>
                            <td>
                              <i class="fa fa-envelope pptemail"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="Email addresses used by this passenger"
                              >
                                Emails
                              </span>
                            </td>
                          </tr>
                          <tr id="Hit" onClick={() => this.onClickSavedGraph("hit")}>
                            <td>
                              <i class="fa fa-exclamation-circle ppthit"></i>
                            </td>
                            <td>
                              <span class="ppt-label" title="Hits for this passenger">
                                Hits
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Document"
                            onClick={() => this.onClickSavedGraph("document")}
                          >
                            <td>
                              <img
                                alt=""
                                src="resources/img/document.svg"
                                class="pptdocument"
                              ></img>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="Documents used by this passenger"
                              >
                                Documents
                              </span>
                            </td>
                          </tr>
                        </table>
                        <hr />
                        <br />

                        <span class="ppt-header-span">This FLight:</span>
                        <table class="ppt-saved-ul">
                          <tr
                            id="Flight"
                            onClick={() => this.onClickSavedGraph("flight")}
                          >
                            <td>
                              <i class="fa fa-plane pptflight"></i>
                            </td>
                            <td>
                              <span class="ppt-label" title="Flight links">
                                Flight
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Address"
                            onClick={() => this.onClickSavedGraph("addressall")}
                          >
                            <td>
                              <i class="fa fa-home pptaddress"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger addresses for this flight"
                              >
                                All Addresses
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="CreditCard"
                            onClick={() => this.onClickSavedGraph("creditcardall")}
                          >
                            <td>
                              <i class="fa fa-credit-card-alt pptcreditcard"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger credit cards for this flight"
                              >
                                All Credit Cards
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Phone"
                            onClick={() => this.onClickSavedGraph("phoneall")}
                          >
                            <td>
                              <i class="fa fa-phone pptphone"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger phone numbers for this flight"
                              >
                                All Phones
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Email"
                            onClick={() => this.onClickSavedGraph("emailall")}
                          >
                            <td>
                              <i class="fa fa-envelope pptemail"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger email addresses for this flight"
                              >
                                All Emails
                              </span>
                            </td>
                          </tr>
                          <tr id="Hit" onClick={() => this.onClickSavedGraph("hitall")}>
                            <td>
                              <i class="fa fa-exclamation-circle ppthit"></i>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger hits for this flight"
                              >
                                All Hits
                              </span>
                            </td>
                          </tr>
                          <tr
                            id="Document"
                            onClick={() => this.onClickSavedGraph("documentall")}
                          >
                            <td>
                              <img
                                alt=""
                                src="resources/img/document.svg"
                                class="pptdocument"
                              ></img>
                            </td>
                            <td>
                              <span
                                class="ppt-label"
                                title="All passenger documents for this flight"
                              >
                                All Documents
                              </span>
                            </td>
                          </tr>
                        </table>
                      </div>
                    </nav>
                    <div id="popoto-graph" class="col-lg-10 ppt-div-graph"></div>
                  </div>

                  <div id="popoto-query" class="ppt-container-query"></div>

                  <div class="ppt-section-header">
                    RESULTS
                    <span id="result-total-count" class="ppt-count-results"></span>
                  </div>

                  <div id="popoto-results" class="ppt-container-results"></div>
                </section>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default Graph;
