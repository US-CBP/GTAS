/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function() {
  "use strict";
  ////     PAX DETAIL CONTROLLER     //////////////
  app.controller("PassengerDetailCtrl", function(
    $scope,
    $mdDialog,
    $mdSidenav,
    $timeout,
    passenger,
    $mdToast,
    spinnerService,
    user,
    caseHistory,
    ruleCats,
    ruleHits,
    watchlistLinks,
    paxDetailService,
    caseService,
    watchListService,
    codeTooltipService,
    configService,
    $sce
  ) {
    $scope.passenger = passenger.data;
    $scope.watchlistLinks = watchlistLinks.data;
    $scope.isLoadingFlightHistory = true;
    $scope.isClosedCase = false;
    $scope.casesListWithCats = [];
    $scope.ruleHits = ruleHits;
    $scope.caseHistory = caseHistory.data;
    $scope.ruleCats = ruleCats.data;
    $scope.slides = [];
    $scope.jsonData =
      "data:text/json;charset=utf-8," +
      encodeURIComponent(JSON.stringify($scope.passenger));
    $scope.paxDetailKibanaUrl = $sce.trustAsResourceUrl(
      "/app/kibana#/visualize/edit/29ac1380-66a9-11e9-9ffd-9d63a89be4bb?embed=true&_g=()&_a=(query:(query_string:(analyze_wildcard:!t,query:'flightId:" +
        $scope.passenger.flightId +
        " AND passengerId:" +
        $scope.passenger.paxId +
        "')))"
    );

    $scope.paxIdTag = $scope.passenger.paxIdTag;
    $scope.paxLastName = $scope.passenger.lastName;
    $scope.paxFlightIdTag = $scope.passenger.flightIdTag;
    $scope.paxFullFlightNumber = ($scope.passenger.carrier + $scope.passenger.flightNumber).toUpperCase();
    // $scope.gtas_message_id;
    $scope.origin = $scope.passenger.embarkation;
    $scope.destination = $scope.passenger.debarkation;

    configService.cypherUrl().then(function(result){
      popoto_gtas.rest.CYPHER_URL = result;  
    });

    configService.cypherAuth().then(function(result){
      popoto_gtas.rest.AUTHORIZATION = result;  
    });
    popoto_gtas.tools.TOGGLE_TAXONOMY = false;
    popoto_gtas.query.USE_RELATION_DIRECTION = false;
    popoto_gtas.tools.SAVE_GRAPH = false;
    popoto_gtas.query.RESULTS_PAGE_SIZE = 100;
    popoto_gtas.query.MAX_RESULTS_COUNT = 30;
    // popoto_gtas.logger.LEVEL = popoto_gtas.logger.LogLevels.INFO;
    popoto_gtas.graph.link.SHOW_MARKER = false;
    popoto_gtas.graph.node.DONUT_WIDTH = 15;

    popoto_gtas.graph.setZoom(0.5, 2);

    $scope.palette = {
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

    popoto_gtas.provider.node.Provider = {
      Address: {
        returnAttributes: ["address_line_1", "country", "city"],
        displayAttribute: "address_line_1",
        getDisplayType: function(node) {
          return 2;
        },
        getColor: function(node) {
          return $scope.palette.address;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "m249.023476,157.071489l-156.524599,145.070219l0,156.572659a13.58352,15.285634 0 0 0 13.58352,15.285634l95.135578,-0.277052a13.58352,15.285634 0 0 0 13.515602,-15.285634l0,-91.436751a13.58352,15.285634 0 0 1 13.58352,-15.285634l54.33408,0a13.58352,15.285634 0 0 1 13.58352,15.285634l0,91.369877a13.58352,15.285634 0 0 0 13.58352,15.333402l95.101619,0.296159a13.58352,15.285634 0 0 0 13.58352,-15.285634l0,-156.677747l-156.49064,-144.96513a10.348944,11.645742 0 0 0 -12.989241,0zm247.245533,98.601892l-70.973892,-65.833314l0,-132.325822a10.18764,11.464225 0 0 0 -10.18764,-11.464225l-47.54232,0a10.18764,11.464225 0 0 0 -10.18764,11.464225l0,69.368117l-76.008284,-70.371237a40.75056,45.856902 0 0 0 -51.78717,0l-214.899776,199.162256a10.18764,11.464225 0 0 0 -1.358352,16.145451l21.648735,29.615916a10.18764,11.464225 0 0 0 14.356083,1.557224l199.694723,-185.089919a10.348944,11.645742 0 0 1 12.989241,0l199.703213,185.089919a10.18764,11.464225 0 0 0 14.347593,-1.528563l21.648735,-29.615916a10.18764,11.464225 0 0 0 -1.443249,-16.174111z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        }
      },
      Airport: {
        returnAttributes: ["country_code", "airport_code"],
        constraintAttribute: "airport_code",
        displayAttribute: "airport_code",
        getDisplayType: function(node) {
          return 2;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M520.522,186.686c-2.63-3.087-6.482-4.868-10.541-4.868h-88.029l20.363-97.538c0.853-4.081-0.18-8.326-2.81-11.56 c-2.628-3.237-6.576-5.114-10.743-5.114H319.85V27.689h24.458c7.645,0,13.845-6.197,13.845-13.845S351.953,0,344.308,0H267.7 c-7.645,0-13.845,6.197-13.845,13.845s6.2,13.845,13.845,13.845h24.458v39.919h-108.91c-4.167,0-8.116,1.877-10.743,5.114 c-2.63,3.234-3.661,7.479-2.81,11.56l20.363,97.538h-88.029c-4.056,0-7.908,1.78-10.541,4.868 c-2.63,3.087-3.774,7.177-3.126,11.181l12.837,79.651c0.003,0.014,0.003,0.025,0.003,0.039l14.847,92.111 c0.676,4.192,3.193,7.692,6.643,9.727c2.071,1.221,4.477,1.913,7.025,1.913h85.374v216.851c0,7.648,6.2,13.845,13.845,13.845 H383.07c7.645,0,13.845-6.197,13.845-13.845V381.31h85.374c2.547,0,4.956-0.692,7.025-1.913c3.45-2.035,5.967-5.532,6.643-9.727 l14.847-92.111c0.003-0.014,0.003-0.025,0.003-0.039l12.837-79.651C524.296,193.863,523.152,189.773,520.522,186.686z M405.008,127.478h-26.642v-32.18h33.36L405.008,127.478z M350.679,95.298v32.18H319.85v-32.18H350.679z M292.161,127.478h-30.832 v-32.18h30.829v32.18H292.161z M233.64,95.298v32.18h-26.643l-6.717-32.18H233.64z M187.864,261.505h-61.201l-8.382-51.997h69.58 v51.997H187.864z M292.161,261.505h-76.608v-51.997h76.608V261.505z M396.455,261.505h-76.608v-51.997h76.608V261.505z M485.346,261.505h-61.201v-51.997h69.58L485.346,261.505z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.airport;
        },
        getImagePath: function(node) {
          return "~/../../gtas/resources/img/airport-.svg";
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
          return 2;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M224 256c70.7 0 128-57.3 128-128S294.7 0 224 0 96 57.3 96 128s57.3 128 128 128zm89.6 32h-16.7c-22.2 10.2-46.9 16-72.9 16s-50.6-5.8-72.9-16h-16.7C60.2 288 0 348.2 0 422.4V464c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48v-41.6c0-74.2-60.2-134.4-134.4-134.4z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.passenger;
        }
      },
      Phone: {
        returnAttributes: ["number"],
        displayAttribute: "number",
        constraintAttribute: "number",
        getDisplayType: function(node) {
          return 2;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M493.4 24.6l-104-24c-11.3-2.6-22.9 3.3-27.5 13.9l-48 112c-4.2 9.8-1.4 21.3 6.9 28l60.6 49.6c-36 76.7-98.9 140.5-177.2 177.2l-49.6-60.6c-6.8-8.3-18.2-11.1-28-6.9l-112 48C3.9 366.5-2 378.1.6 389.4l24 104C27.1 504.2 36.7 512 48 512c256.1 0 464-207.5 464-464 0-11.2-7.7-20.9-18.6-23.4z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.phone;
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
          return 2;
        },
        getColor: function(node) {
          return $scope.palette.creditcard;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M0 432c0 26.5 21.5 48 48 48h480c26.5 0 48-21.5 48-48V256H0v176zm192-68c0-6.6 5.4-12 12-12h136c6.6 0 12 5.4 12 12v40c0 6.6-5.4 12-12 12H204c-6.6 0-12-5.4-12-12v-40zm-128 0c0-6.6 5.4-12 12-12h72c6.6 0 12 5.4 12 12v40c0 6.6-5.4 12-12 12H76c-6.6 0-12-5.4-12-12v-40zM576 80v48H0V80c0-26.5 21.5-48 48-48h480c26.5 0 48 21.5 48 48z",
              fill: popoto_gtas.provider.node.getColor(node)
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
          return 2;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M129.62 176h39.09c1.49-27.03 6.54-51.35 14.21-70.41-27.71 13.24-48.02 39.19-53.3 70.41zm0 32c5.29 31.22 25.59 57.17 53.3 70.41-7.68-19.06-12.72-43.38-14.21-70.41h-39.09zM224 286.69c7.69-7.45 20.77-34.42 23.43-78.69h-46.87c2.67 44.26 15.75 71.24 23.44 78.69zM200.57 176h46.87c-2.66-44.26-15.74-71.24-23.43-78.69-7.7 7.45-20.78 34.43-23.44 78.69zm64.51 102.41c27.71-13.24 48.02-39.19 53.3-70.41h-39.09c-1.49 27.03-6.53 51.35-14.21 70.41zM416 0H64C28.65 0 0 28.65 0 64v384c0 35.35 28.65 64 64 64h352c17.67 0 32-14.33 32-32V32c0-17.67-14.33-32-32-32zm-80 416H112c-8.8 0-16-7.2-16-16s7.2-16 16-16h224c8.8 0 16 7.2 16 16s-7.2 16-16 16zm-112-96c-70.69 0-128-57.31-128-128S153.31 64 224 64s128 57.31 128 128-57.31 128-128 128zm41.08-214.41c7.68 19.06 12.72 43.38 14.21 70.41h39.09c-5.28-31.22-25.59-57.17-53.3-70.41z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.document;
        },
        getIsTextDisplayed: function(node) {
          return true;
        }
      },
      Email: {
        returnAttributes: ["address"],
        displayAttribute: "address",
        getDisplayType: function(node) {
          return 2;
        },
        getColor: function(node) {
          return $scope.palette.email;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M502.3 190.8c3.9-3.1 9.7-.2 9.7 4.7V400c0 26.5-21.5 48-48 48H48c-26.5 0-48-21.5-48-48V195.6c0-5 5.7-7.8 9.7-4.7 22.4 17.4 52.1 39.5 154.1 113.6 21.1 15.4 56.7 47.8 92.2 47.6 35.7.3 72-32.8 92.3-47.6 102-74.1 131.6-96.3 154-113.7zM256 320c23.2.4 56.6-29.2 73.4-41.4 132.7-96.3 142.8-104.7 173.4-128.7 5.8-4.5 9.2-11.5 9.2-18.9v-19c0-26.5-21.5-48-48-48H48C21.5 64 0 85.5 0 112v19c0 7.4 3.4 14.3 9.2 18.9 30.6 23.9 40.7 32.4 173.4 128.7 16.8 12.2 50.2 41.8 73.4 41.4z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
      },
      Flight: {
        returnAttributes: ["full_flight_number", "flight_id_tag", "eta_date", "full_eta_dtm", "destination_country", "gtas_message_id", "flight_number", "origin",
          "destination", "passenger_count", "full_etd_dtm", "origin_country", "gtas_message_create_dtm", "carrier", "last_updated_on", "etd_date", "direction"
        ],
        constraintAttribute: "flight_id_tag",
        displayAttribute: "full_flight_number",
        getPredefinedConstraints: function() {
          return ["$identifier.flight_id_tag is not null"];
        },
        getDisplayType: function(node) {
          return 2; // TEXT
        },
        getColor: function(node) {
          return $scope.palette.flight; //
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M 480 192 H 365.71 L 260.61 8.06 A 16.014 16.014 0 0 0 246.71 0 h -65.5 c -10.63 0 -18.3 10.17 -15.38 20.39 L 214.86 192 H 112 l -43.2 -57.6 c -3.02 -4.03 -7.77 -6.4 -12.8 -6.4 H 16.01 C 5.6 128 -2.04 137.78 0.49 147.88 L 32 256 L 0.49 364.12 C -2.04 374.22 5.6 384 16.01 384 H 56 c 5.04 0 9.78 -2.37 12.8 -6.4 L 112 320 h 102.86 l -49.03 171.6 c -2.92 10.22 4.75 20.4 15.38 20.4 h 65.5 c 5.74 0 11.04 -3.08 13.89 -8.06 L 365.71 320 H 480 c 35.35 0 96 -28.65 96 -64 s -60.65 -64 -96 -64 Z",
              fill: popoto_gtas.provider.node.getColor(node)
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
          return 2;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: "M176 432c0 44.112-35.888 80-80 80s-80-35.888-80-80 35.888-80 80-80 80 35.888 80 80zM25.26 25.199l13.6 272C39.499 309.972 50.041 320 62.83 320h66.34c12.789 0 23.331-10.028 23.97-22.801l13.6-272C167.425 11.49 156.496 0 142.77 0H49.23C35.504 0 24.575 11.49 25.26 25.199z",
              fill: popoto_gtas.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.hit;
        },
        getIsTextDisplayed: function(node) {
          return true;
        }
      }
    };

    popoto_gtas.provider.link.Provider = {
      getColor: function(link) {
        return $scope.palette[link.source.label.toLowerCase()];
      },
      getCSSClass: function(link) {
        return $scope.palette[link.target.label];
      }
    };

    $scope.pax = "pax";
    $scope.flight = "flight";
    $scope.thisPaxFlight = {
      label: "Passenger",
      rel: [
        {
          label: "flew_on",
          target: {
            label: "Flight",
            value: [
              {
                flight_id_tag: $scope.paxFlightIdTag,
                full_flight_number: $scope.paxFullFlightNumber
              }
            ]
          }
        }
      ] // rel
    };
    $scope.paxRelations = [
      {
        label: "flew_on",
        target: { label: "Flight" }
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
    $scope.thisPax = {
        label: "Passenger",
        value: [
          {
            id_tag: $scope.paxIdTag,
            last_name: $scope.paxLastName
          }
        ]
    };

    $scope.savecount = 2;
    $scope.saves = {
      pax: {  // this pax
        label: "Passenger",
        value: [
          {
            id_tag: $scope.paxIdTag,
            last_name: $scope.paxLastName
          }
        ],
        rel: $scope.paxRelations
      },
      flight: { // this flight, all pax, ports
        label: "Flight",
        value: [
          {
            flight_id_tag: $scope.paxFlightIdTag,
            full_flight_number: $scope.paxFullFlightNumber
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
              value: [{airport_code: $scope.origin}
              ] }
          },
          {
            label: "has_destination",
            target: { 
              label: "Airport",
              value: [{airport_code: $scope.destination}
              ] }
          }
        ]
      },
      email: {    //all emails this flight
        label: "Email",
        rel: [{
          label: "used_email",
          target: $scope.thisPax}
        ] // rel
      },
      address: {    //all addys this flight
        label: "Address",
        rel: [
          {
            label: "lived_at",
            target: $scope.thisPax
          }
        ] // rel
      },
      document: {    //all docs this flight
        label: "Document",
        rel: [
          {
            label: "used_document",
            target: $scope.thisPax
          }
        ] // rel
      },
      creditcard: {    //all ccards this flight
        label: "CreditCard",
        rel: [
          {
            label: "used_creditcard",
            target: $scope.thisPax
          }
        ] // rel
      },
      phone: {    //all phones this flight
        label: "Phone",
        rel: [
          {
            label: "used_phone",
            target: $scope.thisPax
          }
        ] // rel
      },
      hit: {    // all pax with hits this flight
        label: "Passenger",
        value: [
          {
            id_tag: $scope.paxIdTag,
            last_name: $scope.paxLastName
          }],
        rel: [
          {
            label: "flagged",
            target: {label: "Hit"}
          }
        ]}, // rel

      emailall: {    //all emails this flight
        label: "Email",
        rel: [
          {
            label: "used_email",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      addressall: {    //all addys this flight
        label: "Address",
        rel: [
          {
            label: "lived_at",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      documentall: {    //all docs this flight
        label: "Document",
        rel: [
          {
            label: "used_document",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      creditcardall: {    //all ccards this flight
        label: "CreditCard",
        rel: [
          {
            label: "used_creditcard",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      phoneall: {    //all phones this flight
        label: "Phone",
        rel: [
          {
            label: "used_phone",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      hitall: {    // all pax with hits this flight
        label: "Hit",
        rel: [
          {
            label: "flagged",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
    }; //saves

    popoto_gtas.result.onTotalResultCount(function(count) {
      document.getElementById("result-total-count").innerHTML =
        "(" + count + ")";
    });

    $scope.activateGraph = function() {
      if (popoto_gtas.dataModel.getRootNode() === undefined) {
        console.log(popoto_gtas.rest.AUTHORIZATION);
        console.log(popoto_gtas.rest.CYPHER_URL);
        popoto_gtas.start($scope.saves[$scope.pax]);
      } else {
        popoto_gtas.refresh($scope.saves[$scope.pax]);
      }
    };

    $scope.onClickSavedGraph = function(id) {
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

      popoto_gtas.graph.mainLabel = $scope.saves[id];
      popoto_gtas.tools.reset();
    };

    popoto_gtas.graph.onSave(function(graph) {
      console.log(graph);
      // generate a unique id
      // var id = "save-" + $scope.savecount++;
      // // save it in JavaScript "saves" var
      // $scope.saves[id] = graph;

      // // Update page with the new saved graph item in list with a on click event to illustrate how it can be used.
      // var li = d3
      //   .select("#saves")
      //   .selectAll("ul")
      //   .append("li")
      //   .attr("id", id)
      //   .on("click", $scope.onClickSavedGraph);

      // li.append("span")
      //   .attr("class", "ppt-icon ppt-save-tag")
      //   .html("&nbsp;");

      // li.append("span")
      //   .attr("class", "ppt-label")
      //   .attr("title", "Load Graph")
      //   .text(id);
    });

    $scope.getAttachment = function(paxId) {
      //TO-DO add specific pax-id here to grab from current passenger
      paxDetailService.getPaxAttachments(paxId).then(function(data) {
        var attList = "";
        $.each(data.data, function(index, value) {
          var slideString = "";
          if (index === 0) {
            slideString += '<slide active="' + value.active + '">';
          } else {
            slideString += "<slide>";
          }
          slideString +=
            '<img ng-src="data:' +
            value.contentType +
            ";base64," +
            value.content +
            '"></slide>';
          attList += slideString;
        });
        $scope.slides = attList;
        $scope.showAttachments(attList);
      });
    };

    //Service call for tooltip data
    $scope.getCodeTooltipData = function(field, type) {
      return codeTooltipService.getCodeTooltipData(field, type);
    };

    $scope.resetTooltip = function() {
      $("md-tooltip").remove();
    };

    $scope.watchlistCategoryId;

    watchListService.getWatchlistCategories().then(function(res) {
      $scope.watchlistCategories = res.data;
    });

    $scope.uploadAttachment = function() {
      //TO-DO add specific pax information here as well as credentials of some kind to insure we don't get arbitrary uploads.
      paxDetailService.savePaxAttachments(
        "name",
        "pw",
        $scope.attachmentDesc,
        $scope.passenger.paxId,
        $scope.attachment
      );
    };

    $scope.assignRuleCats = function() {
      angular.forEach($scope.ruleCats, function(item, index) {
        $scope.casesListWithCats[item.catId] = item.category;
      });
    };

    $scope.assignRuleCats();

    //Bandaid: Parses out seat arrangements not for the particular PNR, returns new seat array. This should be handled on the back-end.
    var parseOutExtraSeats = function(seats, flightLegs) {
      var newSeats = [];
      $.each(seats, function(index, value) {
        $.each(flightLegs, function(i, v) {
          if (value.flightNumber === v.flightNumber) {
            newSeats.push(value);
            return;
          }
        });
      });
      return newSeats;
    };

    //Bandaid: Re-orders TVL lines for flight legs, making sure it is ordered by date.
    var reorderTVLdata = function(flightLegs) {
      var orderedTvlData = [];

      //Sorts flightLeg objects based on etd
      // * 5/8/2018*No longer required to sort but does add +1 to leg number visually still, so will keep that functionality.

      flightLegs.sort(function(a, b) {
        if (a.legNumber < b.legNumber) return -1;
        if (a.legNumber > b.legNumber) return 1;
        else return 0;
      });

      //sets each flightLeg# to the newly sorted index value
      $.each(flightLegs, function(index, value) {
        value.legNumber = index + 1; //+1 because 0th flight leg doesn't read well to normal humans
      });

      orderedTvlData = flightLegs;

      return orderedTvlData;
    };

    function setId(coll) {
      var result = [];
      for (var rec of coll) {
        var res = rec;
        var id =
          res.bookingDetailId == null
            ? "P" + res.flightId
            : "D" + res.bookingDetailId;
        res.id = id;
        result.push(res);
      }
      return result;
    }

    var getPassengerBags = function(legs) {
      var passengers = $scope.passenger.pnrVo.passengers;
      var bags = $scope.passenger.pnrVo.bagSummaryVo.bagsByFlightLeg;

      bags = bags.filter(bag => bag.data_source === "PNR");
      var newbags = setId(bags);

      newbags.sort((a, b) => (a.id > b.id ? 1 : b.id > a.id ? -1 : 0));
      newbags.sort((a, b) =>
        a.passengerId > b.passengerId
          ? 1
          : b.passengerId > a.passengerId
          ? -1
          : 0
      );

      // merge bag records by pax/flight
      var prevId;
      var prevPax;
      var prevBag;
      var acc = undefined;
      var result = [];
      for (var bag of newbags) {
        if (bag.id != prevId || bag.passengerId != prevPax) {
          if (acc != undefined) {
            result.push(acc);
          }
          acc = bag;
          acc.bagList = bag.bagId;
          var pax = passengers.find(p => {
            return p.paxId == bag.passengerId;
          });

          if (pax) {
            acc.passLastName = pax.lastName;
            acc.passFirstName = pax.firstName;
          }
        } else {
          if (bag.bagId != prevBag)
            acc.bagList = acc.bagList + ", " + bag.bagId;
        }
        prevId = bag.id;
        prevPax = bag.passengerId;
        prevBag = bag.bagId;
      }

      result.push(acc);
      return result;
    };

    if (
      angular.isDefined($scope.passenger.pnrVo) &&
      $scope.passenger.pnrVo != null
    ) {
      $scope.passenger.pnrVo.seatAssignments = parseOutExtraSeats(
        $scope.passenger.pnrVo.seatAssignments,
        $scope.passenger.pnrVo.flightLegs
      );
      $scope.passenger.pnrVo.flightLegs = reorderTVLdata(
        $scope.passenger.pnrVo.flightLegs
      );
      $scope.orderedFlightLegs = setId($scope.passenger.pnrVo.flightLegs);
      $scope.orderedBags = getPassengerBags($scope.orderedFlightLegs);

      var raw = $scope.passenger.pnrVo.raw;
      $scope.passenger.pnrVo.documents = $scope.passenger.pnrVo.documents.filter(
        doc => raw.includes(doc.documentNumber)
      );
    }

    //Removes extraneous characters from rule hit descriptions
    if (
      $scope.ruleHits != typeof "undefined" &&
      $scope.ruleHits != null &&
      $scope.ruleHits.length > 0
    ) {
      $.each($scope.ruleHits, function(index, value) {
        value.ruleConditions = value.ruleConditions.replace(
          /[.*+?^${}()|[\]\\]/g,
          ""
        );
      });
    }

    $scope.getTotalOf = function(coll, id, fieldToTotal) {
      var filtered = coll.filter(item => (item || {}).id == id);

      var total = filtered.reduce(function(accum, current) {
        return current[fieldToTotal] + accum;
      }, 0);

      //refac - set the bag count header to the greatest bag count per leg.
      if (
        fieldToTotal === "bag_count" &&
        ($scope.passenger.pnrVo.totalbagCount || 0) < total
      ) {
        $scope.passenger.pnrVo.totalbagCount = total;
      }

      return total;
    };

    $scope.highlightClass = function(className) {
      //remove existing highlights
      var existing = document.querySelectorAll("#pnrtable .ng-scope td");
      existing.forEach(elem => elem.classList.remove("highlight"));

      var elems = document.getElementsByClassName(className);
      for (var i = 0; i < elems.length; i++) {
        if (elems[i].classList.contains("highlight")) {
          elems[i].classList.remove("highlight");
        } else {
          elems[i].classList.add("highlight");
          elems[i].scrollIntoView(true, { behavior: "smooth" });
        }
      }
    };

    $scope.getWatchListMatchByPaxId = function() {
      paxDetailService
        .getPaxWatchlistLink($scope.passenger.paxId)
        .then(function(response) {
          $scope.watchlistLinks = response.data;
        });
    };

    $scope.refreshCasesHistory = function() {
      paxDetailService
        .getPaxCaseHistory($scope.passenger.paxId)
        .then(function(cases) {
          $scope.caseHistory = cases.data;
        });
    };

    $scope.saveWatchListMatchByPaxId = function() {
      paxDetailService
        .savePaxWatchlistLink($scope.passenger.paxId)
        .then(function(response) {
          $scope.getWatchListMatchByPaxId();
          $scope.refreshCasesHistory($scope.passenger.paxId);
        });
    };

    $scope.saveDisposition = function() {
      var disposition = {
        passengerId: $scope.passenger.paxId,
        flightId: $scope.passenger.flightId,
        statusId: $scope.currentDispStatus,
        comments: $scope.currentDispComments,
        user: user.data.userId,
        createdBy: user.data.userId,
        createdAt: new Date()
      };

      spinnerService.show("html5spinner");
      caseService.createDisposition(disposition).then(function(response) {
        spinnerService.hide("html5spinner");
        //Clear input, reload history
        $scope.currentDispStatus = "-1";
        $scope.currentDispComments = "";
        //This makes it palatable to the front-end
        $.each($scope.dispositionStatus, function(index, value) {
          if (value.id === parseInt(disposition.statusId)) {
            var status = { status: value.name };
            $.extend(disposition, status);
          }
        });
        if (
          $scope.passenger.dispositionHistory != null &&
          typeof $scope.passenger.dispositionHistory.length != "undefined" &&
          response.data.status.toUpperCase() === "SUCCESS"
        ) {
          //Add to disposition length without service calling if success
          $scope.passenger.dispositionHistory.push(disposition);
        } else {
          $scope.passenger.dispositionHistory = [disposition];
        }
      });
    };

    var getMostRecentCase = function(dispHistory) {
      var mostRecentCase = null;
      $.each(dispHistory, function(index, value) {
        if (
          mostRecentCase === null ||
          mostRecentCase.createdAt < value.createdAt
        ) {
          mostRecentCase = value;
        }
      });
      return mostRecentCase;
    };

    $scope.isCaseDisabled = function(dispHistory) {
      //Find if most recent case is closed
      var mostRecentCase = getMostRecentCase(dispHistory);
      //If Closed, find out if current user is Admin
      if (mostRecentCase != null && mostRecentCase.statusId == 3) {
        var isAdmin = false;
        $.each(user.data.roles, function(index, value) {
          if (value.roleId === 1) {
            isAdmin = true;
          }
        });
        //If user is admin do not disable, else disable
        if (isAdmin) {
          return false;
        } else return true;
      } else return false; //if not closed do not disable
    };

    $scope.isCaseDropdownItemDisabled = function(statusId) {
      var mostRecentCase = getMostRecentCase(
        $scope.passenger.dispositionHistory
      );
      if (mostRecentCase != null) {
        if (mostRecentCase.statusId == 1) {
          if (statusId == 3 || statusId == 4 || statusId == 1) {
            return true;
          }
        } else if (mostRecentCase.statusId == 2) {
          if (statusId == 3 || statusId == 4 || statusId == 1) {
            return true;
          }
        } else if (mostRecentCase.statusId == 3) {
          if (statusId != 4) {
            return true;
          }
        } else if (mostRecentCase.statusId == 4) {
          if (statusId == 2 || statusId == 3 || statusId == 1) {
            return true;
          }
        } else if (mostRecentCase.statusId == 5) {
          if (statusId == 2 || statusId == 1) {
            return true;
          }
        } else if (mostRecentCase.statusId > 5) {
          if (statusId == 1 || statusId == 3) {
            return true;
          }
        }
        return false;
      }
      if (statusId == 1) {
        return false;
      } else {
        return true;
      }
    };

    caseService.getDispositionStatuses().then(function(response) {
      $scope.dispositionStatus = response.data;
    });

    paxDetailService
      .getPaxFlightHistory($scope.passenger.paxId, $scope.passenger.flightId)
      .then(function(response) {
        $scope.getPaxBookingDetailHistory($scope.passenger);
        $scope.passenger.flightHistoryVo = response.data;
      });

    $scope.getPaxFullTravelHistory = function(passenger) {
      paxDetailService
        .getPaxFullTravelHistory(passenger.paxId, passenger.flightId)
        .then(function(response) {
          $scope.passenger.fullFlightHistoryVo = { map: response.data };
          $scope.isLoadingFlightHistory = false;
        });
    };

    $scope.getPaxBookingDetailHistory = function(passenger) {
      paxDetailService
        .getPaxBookingDetailHistory(passenger.paxId, passenger.flightId)
        .then(function(response) {
          $scope.passenger.fullFlightHistoryVo = { map: response.data };
          $scope.isLoadingFlightHistory = false;
        });
    };

    //Adds user from pax detail page to watchlist.
    $scope.addEntityToWatchlist = function() {
      spinnerService.show("html5spinner");
      var terms = [];
      //Add passenger firstName, lastName, dob to wlservice call
      terms.push({
        entity: "PASSENGER",
        field: "firstName",
        type: "string",
        value: $scope.passenger.firstName
      });
      terms.push({
        entity: "PASSENGER",
        field: "lastName",
        type: "string",
        value: $scope.passenger.lastName
      });
      terms.push({
        entity: "PASSENGER",
        field: "dob",
        type: "date",
        value: $scope.passenger.dob
      });
      terms.push({
        entity: "PASSENGER",
        field: "categoryId",
        type: "integer",
        value: $scope.watchlistCategoryId
      });
      watchListService
        .addItem("Passenger", "PASSENGER", null, terms)
        .then(function() {
          terms = [];
          //Add documentType and documentNumber to wlservice call
          $.each($scope.passenger.documents, function(index, value) {
            if (value.documentType === "P" || value.documentType === "V") {
              terms.push({
                entity: "DOCUMENT",
                field: "documentType",
                type: "string",
                value: value.documentType
              });
              terms.push({
                entity: "DOCUMENT",
                field: "documentNumber",
                type: "string",
                value: value.documentNumber
              });
              terms.push({
                entity: "DOCUMENT",
                field: "categoryId",
                type: "integer",
                value: $scope.watchlistCategoryId
              });
              watchListService
                .addItem("Document", "DOCUMENT", null, terms)
                .then(function(response) {
                  if (response.data.status == "FAILURE") {
                    console.log(JSON.stringify(response));
                  } else {
                    //Compiles after each document add.
                    watchListService.compile();
                    //clear out terms list
                    terms = [];
                    spinnerService.hide("html5spinner");
                    $mdSidenav("addWatchlist").close();
                  }
                });
            }
          });
        });
    };

    $scope.addToWatchlist = function() {
      $timeout(function() {
        $mdSidenav("addWatchlist").open();
      });
    };
    //dialog function for watchlist addition dialog
    $scope.showConfirm = function() {
      var confirm = $mdDialog
        .confirm()
        .title("WARNING: Please Confirm The Watchlist Addition")
        .textContent(
          "This will add both the current passenger and their applicable documents to the watchlist."
        )
        .ariaLabel("Add To Watchlist Warning")
        .ok("Confirm Addition")
        .cancel("Cancel");

      $mdDialog.show(confirm).then(
        function() {
          $scope.addEntityToWatchlist();
        },
        function() {
          return false;
        }
      );
    };

    //dialog function for image display dialog
    $scope.showAttachments = function(attachmentList) {
      $mdDialog.show({
        template:
          "<md-dialog><md-dialog-content>" +
          "<div><carousel>" +
          attachmentList +
          "</carousel></div>" +
          "</md-dialog-content></md-dialog>",
        parent: angular.element(document.body),
        clickOutsideToClose: true
      });
    };
  });

  ////     PAX CONTROLLER     //////////////
  app.controller("PaxController", function(
    $scope,
    $injector,
    $stateParams,
    $state,
    $mdToast,
    paxService,
    sharedPaxData,
    uiGridConstants,
    gridService,
    jqueryQueryBuilderService,
    jqueryQueryBuilderWidget,
    executeQueryService,
    passengers,
    $timeout,
    paxModel,
    $http,
    codeTooltipService,
    codeService,
    spinnerService
  ) {
    $scope.errorToast = function(error) {
      $mdToast.show(
        $mdToast
          .simple()
          .content(error)
          .position("top right")
          .hideDelay(4000)
          .parent($scope.toastParent)
      );
    };

    var exporter = {
      csv: function() {
        $scope.gridApi.exporter.csvExport("all", "all");
      },
      pdf: function() {
        $scope.gridApi.exporter.pdfExport("all", "all");
      }
    };

    $scope.export = function(format) {
      exporter[format]();
    };

    function createFilterFor(query) {
      var lowercaseQuery = query.toLowerCase();
      return function filterFn(contact) {
        return contact.lowerCasedName.indexOf(lowercaseQuery) >= 0;
      };
    }
    /* Search for airports. */
    function querySearch(query) {
      var results =
        query && query.length && query.length >= 3
          ? self.allAirports.filter(createFilterFor(query))
          : [];
      return results;
    }

    $scope.searchSort = querySearch;
    $scope.model = paxModel.model;

    var self = this,
      airports,
      stateName = $state.$current.self.name,
      ruleGridColumns = [
        {
          name: "ruleTitle",
          displayName: "Title",
          cellTemplate:
            '<md-button aria-label="title" class="md-primary md-button md-default-theme" ng-click="grid.appScope.ruleIdClick(row)">{{COL_FIELD}}</md-button>'
        },
        {
          name: "ruleConditions",
          displayName: "Conditions",
          field: "hitsDetailsList[0]",
          cellFilter: "hitsConditionDisplayFilter"
        }
      ],
      setSubGridOptions = function(data, appScopeProvider) {
        data.passengers.forEach(function(entity_row) {
          if (!entity_row.flightId) {
            entity_row.flightId = $stateParams.id;
          }
          entity_row.subGridOptions = {
            appScopeProvider: appScopeProvider,
            columnDefs: ruleGridColumns,
            data: []
          };
        });
      },
      //TODO There is probably a better location to put this
      //Parses Passengers object for front-end in flightpax
      paxPassParser = function(passengers) {
        var pax = {};
        //Obtain aggregate values
        if (passengers.length > 0) {
          pax.passCount = 0;
          pax.crewCount = 0;
          pax.hitCount = 0;
          pax.openCaseCount = 0;
          pax.closedCaseCount = 0;
          for (var i = 0; i < passengers.length; i++) {
            if (passengers[i].passengerType === "P") {
              pax.passCount += 1;
            }
            if (passengers[i].passengerType === "C") {
              pax.crewCount += 1;
            }
            if (
              passengers[i].onWatchList ||
              passengers[i].onRuleHitList ||
              passengers[i].onWatchListDoc
            ) {
              pax.hitCount += 1;
            }
          }
          pax.eta = Date.parse(passengers[0].eta);
          pax.etd = Date.parse(passengers[0].etd);
        }
        $scope.pax = pax;
      },
      setPassengersGrid = function(grid, response) {
        var data =
          stateName === "queryPassengers"
            ? response.data.result
            : response.data;
        setSubGridOptions(data, $scope);
        grid.totalItems =
          data.totalPassengers === -1 ? 0 : data.totalPassengers;
        grid.data = data.passengers;
        //Add specific passenger info to scope for paxDetail
        stateName === "queryPassengers" ? null : paxPassParser(grid.data);
        if (!grid.data || grid.data.length == 0) {
          $scope.errorToast("No results found for selected filter criteria");
        }
        spinnerService.hide("html5spinner");
      },
      getPage = function() {
        if (stateName === "queryPassengers") {
          setPassengersGrid($scope.passengerQueryGrid, passengers);
          $scope.queryLimitReached = passengers.data.result.queryLimitReached;
        } else {
          setPassengersGrid($scope.passengerGrid, passengers);
        }
      },
      update = function(data) {
        passengers = data;
        getPage();
        spinnerService.hide("html5spinner");
      },
      fetchMethods = {
        queryPassengers: function() {
          var postData,
            query = JSON.parse(localStorage["query"]);
          postData = {
            pageNumber: $scope.model.pageNumber,
            pageSize: $scope.model.pageSize,
            query: query
          };
          spinnerService.show("html5spinner");
          executeQueryService.queryPassengers(postData).then(update);
        },
        flightpax: function() {
          spinnerService.show("html5spinner");
          paxService.getPax($stateParams.id, $scope.model).then(update);
        },
        paxAll: function() {
          spinnerService.show("html5spinner");
          paxService.getAllPax($scope.model).then(update);
        }
      },
      resolvePage = function() {
        populateAirports();
        fetchMethods[stateName]();
      },
      flightDirections = [
        { label: "Inbound", value: "I" },
        { label: "Outbound", value: "O" },
        { label: "Any", value: "A" }
      ];

    self.querySearch = querySearch;
    codeService.getAirportTooltips().then(function(allAirports) {
      self.allAirports = allAirports.map(function(contact) {
        contact.lowerCasedName = contact.id.toLowerCase();
        return contact;
      });
      self.filterSelected = true;
      $scope.filterSelected = true;
    });
    $scope.flightDirections = flightDirections;

    $injector.invoke(jqueryQueryBuilderWidget, this, { $scope: $scope });
    $scope.stateName = $state.$current.self.name;
    $scope.ruleIdClick = function(row) {
      $scope.getRuleObject(row.entity.ruleId);
    };

    $scope.getRuleObject = function(ruleID) {
      jqueryQueryBuilderService
        .loadRuleById("rule", ruleID)
        .then(function(myData) {
          $scope.$builder.queryBuilder("readOnlyRules", myData.result.details);
          $scope.hitDetailDisplay = myData.result.summary.title;
          document.getElementById("QBModal").style.display = "block";

          $scope.closeDialog = function() {
            document.getElementById("QBModal").style.display = "none";
          };
        });
    };

    $scope.isExpanded = true;
    $scope.paxHitList = [];
    $scope.list = sharedPaxData.list;
    $scope.add = sharedPaxData.add;
    $scope.getAll = sharedPaxData.getAll;

    $scope.getPaxSpecificList = function(index) {
      return $scope.list(index);
    };

    $scope.buildAfterEntitiesLoaded();

    $scope.passengerGrid = {
      paginationPageSizes: [10, 25, 50],
      paginationPageSize: $scope.model.pageSize,
      paginationCurrentPage: $scope.model.pageNumber,
      useExternalPagination: true,
      useExternalSorting: true,
      useExternalFiltering: true,
      enableHorizontalScrollbar: 0,
      enableVerticalScrollbar: 0,
      enableColumnMenus: false,
      multiSelect: false,
      enableGridMenu: true,
      enableExpandableRowHeader: false,
      expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        gridApi.pagination.on.paginationChanged($scope, function(
          newPage,
          pageSize
        ) {
          $scope.model.pageNumber = newPage;
          $scope.model.pageSize = pageSize;
          resolvePage();
        });

        gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
          if (sortColumns.length === 0) {
            $scope.model.sort = null;
          } else {
            $scope.model.sort = [];
            for (var i = 0; i < sortColumns.length; i++) {
              $scope.model.sort.push({
                column: sortColumns[i].name,
                dir: sortColumns[i].sort.direction
              });
            }
          }
          resolvePage();
        });

        gridApi.core.on.columnVisibilityChanged($scope, function(
          changedColumn
        ) {
          $scope.columnChanged = {
            name: changedColumn.colDef.name,
            visible: changedColumn.colDef.visible
          };
        });

        gridApi.expandable.on.rowExpandedStateChanged($scope, function(row) {
          if (row.isExpanded) {
            paxService.getRuleHits(row.entity.id).then(function(data) {
              row.entity.subGridOptions.data = data;
            });
          }
        });
      }
    };
    //Front-end pagination configuration object for gridUi
    //Should only be active on stateName === 'queryPassengers'
    $scope.passengerQueryGrid = {
      paginationPageSizes: [10, 25, 50],
      paginationPageSize: $scope.model.pageSize,
      paginationCurrentPage: 1,
      useExternalPagination: false,
      useExternalSorting: false,
      useExternalFiltering: false,
      enableHorizontalScrollbar: 0,
      enableVerticalScrollbar: 0,
      enableColumnMenus: false,
      multiSelect: false,
      enableExpandableRowHeader: false,
      minRowsToShow: 10,
      expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        gridApi.pagination.on.paginationChanged($scope, function(
          newPage,
          pageSize
        ) {
          $scope.model.pageSize = pageSize;
        });

        gridApi.expandable.on.rowExpandedStateChanged($scope, function(row) {
          if (row.isExpanded) {
            paxService.getRuleHits(row.entity.id).then(function(data) {
              row.entity.subGridOptions.data = data;
            });
          }
        });
      }
    };

    $scope.getCodeTooltipData = function(field, type) {
      return codeTooltipService.getCodeTooltipData(field, type);
    };

    $scope.hitTooltipData = ["Loading..."];

    $scope.resetTooltip = function() {
      $scope.hitTooltipData = ["Loading..."];
      // $('md-tooltip').remove();
    };

    $scope.getHitTooltipData = function(row) {
      var dataList = [];
      paxService.getRuleHits(row.entity.id).then(function(data) {
        $.each(data, function(index, value) {
          dataList.push(value.ruleDesc);
        });
        if (dataList.length === 0) {
          dataList = "No Description Available";
        }
        $scope.hitTooltipData = dataList;
      });
    };

    if (stateName === "queryPassengers") {
      $scope.passengerQueryGrid.columnDefs = [
        {
          field: "onRuleHitList",
          name: "onRuleHitList",
          displayName: "Rule Hits",
          width: 100,
          cellClass: "rule-hit",
          sort: {
            direction: uiGridConstants.DESC,
            priority: 1
          },
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseover="grid.appScope.getHitTooltipData(row)" ng-mouseleave="grid.appScope.resetTooltip()" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" ng-disabled={{!row.entity.onRuleHitList}}>' +
            '<md-tooltip class="multi-tooltip" md-direction="right"><div ng-repeat="item in grid.appScope.hitTooltipData">{{item}}<br/></div></md-tooltip>' +
            '<span ng-if="row.entity.onRuleHitList" class="warning-color"><i class="fa fa-flag" aria-hidden="true"></i></span></md-button>'
        },
        {
          name: "onWatchList",
          displayName: "Watchlist Hits",
          width: 130,
          cellClass: gridService.anyWatchlistHit,
          sort: {
            direction: uiGridConstants.DESC,
            priority: 0
          },
          cellTemplate:
            "<div>" +
            '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList || row.entity.onWatchListLink" ' +
            "ng-class=\"(row.entity.onWatchListDoc || row.entity.onWatchList) ? 'danger-color' : 'alert-color'\" >" +
            '<i class="fa fa-flag" aria-hidden="true"></i></span></div>'
        },
        {
          field: "passengerType",
          name: "passengerType",
          displayName: "T",
          width: 50
        },
        {
          field: "lastName",
          name: "lastName",
          displayName: "pass.lastname",
          headerCellFilter: "translate",
          cellTemplate:
            '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.id}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme">{{COL_FIELD}}</md-button>'
        },
        {
          field: "firstName",
          name: "firstName",
          displayName: "pass.firstname",
          headerCellFilter: "translate"
        },
        {
          field: "middleName",
          name: "middleName",
          displayName: "pass.middlename",
          headerCellFilter: "translate"
        },
        {
          field: "documents[0].documentNumber",
          name: "documentNumber",
          displayName: "pass.docNum",
          headerCellFilter: "translate",
          width: 120
        },
        {
          field: "flightNumber",
          name: "flightNumber",
          displayName: "pass.flight",
          headerCellFilter: "translate",
          cellTemplate: "<span>{{row.entity.carrier}}{{COL_FIELD}}</span>"
        },
        {
          field: "flightOrigin",
          name: "flightOriginairport",
          displayName: "pass.originairport",
          headerCellFilter: "translate",
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">' +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        },
        {
          field: "flightDestination",
          name: "flightDestinationairport",
          displayName: "pass.destinationairport",
          headerCellFilter: "translate",
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">' +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        },
        {
          field: "etaLocalTZ",
          name: "etaLocalTZ",
          sort: {
            direction: uiGridConstants.DESC,
            priority: 2
          },
          displayName: "pass.eta",
          headerCellFilter: "translate"
        },
        {
          field: "etdLocalTZ",
          name: "etdLocalTZ",
          displayName: "pass.etd",
          headerCellFilter: "translate"
        },
        {
          field: "gender",
          name: "gender",
          displayName: "G",
          width: 50
        },
        {
          name: "dob",
          displayName: "pass.dob",
          headerCellFilter: "translate",
          cellFilter: "date",
          cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'
        },
        {
          name: "nationality",
          displayName: "Nationality",
          headerCellFilter: "translate",
          width: 75,
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetTooltip()">' +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        }
      ];
    } else {
      $scope.passengerGrid.columnDefs = [
        {
          name: "onRuleHitList",
          displayName: "Rule Hits",
          width: 100,
          cellClass: "rule-hit",
          sort: {
            direction: uiGridConstants.DESC,
            priority: 1
          },
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseover="grid.appScope.getHitTooltipData(row)" ng-mouseleave="grid.appScope.resetTooltip()" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" ng-disabled={{!row.entity.onRuleHitList}}>' +
            '<md-tooltip class="multi-tooltip" md-direction="right"><div ng-repeat="item in grid.appScope.hitTooltipData">{{item}}<br/></div></md-tooltip>' +
            '<span ng-if="row.entity.onRuleHitList" class="warning-color"><i class="fa fa-flag" aria-hidden="true"></i></span></md-button>'
        },
        {
          name: "onWatchList",
          displayName: "Watchlist Hits",
          width: 130,
          cellClass: gridService.anyWatchlistHit,
          sort: {
            direction: uiGridConstants.DESC,
            priority: 0
          },
          cellTemplate:
            "<div>" +
            '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList || row.entity.onWatchListLink" ' +
            "ng-class=\"(row.entity.onWatchListDoc || row.entity.onWatchList) ? 'danger-color' : 'alert-color'\" >" +
            '<i class="fa fa-flag" aria-hidden="true"></i></span></div>'
        },
        {
          name: "passengerType",
          displayName: "T",
          width: 50,
          headerCellFilter: "translate",
          cellTemplate:
            "<md-button>" +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"passenger")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        },
        {
          name: "lastName",
          displayName: "pass.lastname",
          headerCellFilter: "translate",
          cellTemplate:
            '<md-button aria-label="Last Name" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail" class="md-primary md-button md-default-theme">{{COL_FIELD}}</md-button>'
        },
        {
          name: "firstName",
          displayName: "pass.firstname",
          headerCellFilter: "translate"
        },
        {
          name: "middleName",
          displayName: "pass.middlename",
          headerCellFilter: "translate"
        },
        {
          field: "documents[0].documentNumber",
          name: "documentNumber",
          displayName: "pass.docNum",
          headerCellFilter: "translate",
          width: 130
        },
        {
          name: "eta",
          sort: {
            direction: uiGridConstants.DESC,
            priority: 2
          },
          displayName: "pass.eta",
          headerCellFilter: "translate",
          visible: stateName === "paxAll"
        },
        {
          name: "etd",
          displayName: "pass.etd",
          headerCellFilter: "translate",
          visible: stateName === "paxAll"
        },
        {
          name: "gender",
          displayName: "G",
          width: 50,
          headerCellFilter: "translate",
          cellTemplate:
            "<md-button>" +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"gender")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        },
        {
          name: "dob",
          displayName: "pass.dob",
          headerCellFilter: "translate",
          cellFilter: "date",
          cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'
        },
        {
          name: "nationality",
          displayName: "Nationality",
          headerCellFilter: "translate",
          width: 120,
          cellTemplate:
            '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetTooltip()">' +
            '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}' +
            "</md-button>"
        }
      ];
    }

    var populateAirports = function() {
      var originAirports = new Array();
      var destinationAirports = new Array();

      angular.forEach($scope.model.origin, function(value, index) {
        originAirports.push(value.id);
      });

      angular.forEach($scope.model.dest, function(value, index) {
        destinationAirports.push(value.id);
      });

      $scope.model.originAirports = originAirports;
      $scope.model.destinationAirports = destinationAirports;
    };

    var mapAirports = function() {
      var originAirports = new Array();
      var destinationAirports = new Array();
      var airport = { id: "" };

      if ($scope.model.origin) {
        if ($scope.model.origin instanceof Array) {
          angular.forEach($scope.model.origin, function(value, index) {
            if (value instanceof Object) {
              originAirports.push({ id: value.id });
            } else {
              originAirports.push({ id: value });
            }
          });
        } else {
          originAirports.push({ id: $scope.model.origin });
        }
        $scope.model.origin = originAirports;
      }

      if ($scope.model.dest) {
        if ($scope.model.dest instanceof Array) {
          angular.forEach($scope.model.dest, function(value, index) {
            if (value instanceof Object) {
              destinationAirports.push({ id: value.id });
            } else {
              destinationAirports.push({ id: value });
            }
          });
        } else {
          destinationAirports.push({ id: $scope.model.dest });
        }
        $scope.model.dest = destinationAirports;
      }
    };

    $scope.filter = function() {
      resolvePage();
      if ($scope.gridApi.pagination.getPage() > 1) {
        $scope.gridApi.pagination.seek(1);
      }
    };

    $scope.reset = function() {
      paxModel.reset();
      resolvePage();
    };
    $scope.getTableHeight = function() {
      if (stateName != "queryPassengers") {
        return gridService.calculateGridHeight(
          $scope.passengerGrid.data.length
        );
      } // Sets minimal height for front-end pagination controlled variant of grid
      return gridService.calculateGridHeight($scope.model.pageSize);
    };
    //toggleDiv and filterCheck required for sidepanel
    $scope.toggleDiv = function(div) {
      var element = document.getElementById(div);
      if (element.classList.contains("active")) {
        element.classList.remove("active");
      } else {
        element.className += " active";
      }
    };
    $scope.filterCheck = function(option) {
      var filters = ["origin", "destination", "flight", "direction", "date"];
      return filters.includes(option);
    };

    getPage();
    mapAirports();
  });
})();
