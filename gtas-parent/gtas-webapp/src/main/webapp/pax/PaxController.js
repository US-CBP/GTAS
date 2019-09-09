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
    configService
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

    $scope.paxIdTag = $scope.passenger.paxIdTag;
    $scope.paxLastName = $scope.passenger.lastName;
    $scope.paxFlightIdTag = $scope.passenger.flightIdTag;
    $scope.paxFullFlightNumber = ($scope.passenger.carrier + $scope.passenger.flightNumber).toUpperCase();
    $scope.origin = $scope.passenger.embarkation;
    $scope.destination = $scope.passenger.debarkation;
    $scope.SvgType = 2;
    $scope.isReloaded = true;

    configService.cypherUrl().then(function(result){
      vaquita.rest.CYPHER_URL = result;  
    });

    configService.cypherAuth().then(function(result){
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

    vaquita.provider.node.Provider = {
      Address: {
        returnAttributes: ["address_line_1", "country", "city"],
        displayAttribute: "address_line_1",
        getDisplayType: function(node) {
          return $scope.SvgType;
        },
        getColor: function(node) {
          return $scope.palette.address;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getAddressPath(),
              fill: vaquita.provider.node.getColor(node)
            }
          ];
        }
      },
      Airport: {
        returnAttributes: ["country_code", "airport_code"],
        constraintAttribute: "airport_code",
        displayAttribute: "airport_code",
        getDisplayType: function(node) {
          return $scope.SvgType;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getAirportPath(),
              fill: vaquita.provider.node.getColor(node)
            }
          ];
        },
        getColor: function(node) {
          return $scope.palette.airport;
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
          return $scope.SvgType;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getPassengerPath(),
              fill: vaquita.provider.node.getColor(node)
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
          return $scope.SvgType;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getPhonePath(),
              fill: vaquita.provider.node.getColor(node)
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
          return $scope.SvgType;
        },
        getColor: function(node) {
          return $scope.palette.creditcard;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getCreditCardPath(),
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
          return $scope.SvgType;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getDocumentPath(),
              fill: vaquita.provider.node.getColor(node)
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
          return $scope.SvgType;
        },
        getColor: function(node) {
          return $scope.palette.email;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getEmailPath(),
              fill: vaquita.provider.node.getColor(node)
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
          return $scope.SvgType;
        },
        getColor: function(node) {
          return $scope.palette.flight; //
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getFlightPath(),
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
          return $scope.SvgType;
        },
        getSVGPaths: function(node) {
          return [
            {
              d: paxDetailService.getHitPath(),
              fill: vaquita.provider.node.getColor(node)
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

    vaquita.provider.link.Provider = {
      getColor: function(link) {
        return $scope.palette[link.source.label.toLowerCase()];
      }
    };

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
        target: { 
          label: "Flight",
          value: [
          {
            flight_id_tag: $scope.paxFlightIdTag,
            full_flight_number: $scope.paxFullFlightNumber
          }]
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
    $scope.thisPax = {
        label: "Passenger",
        value: [
          {
            id_tag: $scope.paxIdTag,
            last_name: $scope.paxLastName
          }
        ]
    };

    $scope.saves = {
      pax: {  // this pax
        label: "Passenger",
        horiz: 1,
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
        horiz: 1,
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
      email: {    // all emails this pax
        label: "Email",
        horiz: 2,
        rel: [{
          label: "used_email",
          target: $scope.thisPax}
        ] // rel
      },
      address: {    //all addys this pax
        label: "Address",
        horiz: 2,
        rel: [
          {
            label: "lived_at",
            target: $scope.thisPax
          }
        ] // rel
      },
      document: {    //all docs this pax
        label: "Document",
        horiz: 2,
        rel: [
          {
            label: "used_document",
            target: $scope.thisPax
          }
        ] // rel
      },
      creditcard: {    //all ccards this pax
        label: "CreditCard",
        horiz: 2,
        rel: [
          {
            label: "used_creditcard",
            target: $scope.thisPax
          }
        ] // rel
      },
      phone: {    //all phones this pax
        label: "Phone",
        horiz: 2,
        rel: [
          {
            label: "used_phone",
            target: $scope.thisPax
          }
        ] // rel
      },
      hit: {    //  hits this pax
        label: "Passenger",
        horiz: 2,
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
        horiz: 3,
        rel: [
          {
            label: "used_email",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      addressall: {    //all addys this flight
        label: "Address",
        horiz: 3,
        rel: [
          {
            label: "lived_at",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      documentall: {    //all docs this flight
        label: "Document",
        horiz: 3,
        rel: [
          {
            label: "used_document",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      creditcardall: {    //all ccards this flight
        label: "CreditCard",
        horiz: 3,
        rel: [
          {
            label: "used_creditcard",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      phoneall: {    //all phones this flight
        label: "Phone",
        horiz: 3,
        rel: [
          {
            label: "used_phone",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
      hitall: {    // all pax with hits this flight
        label: "Hit",
        horiz: 3,
        rel: [
          {
            label: "flagged",
            target: $scope.thisPaxFlight
          }
        ] // rel
      },
    }; //saves

    vaquita.result.onTotalResultCount(function(count) {
      document.getElementById("result-total-count").innerHTML =
        "(" + count + ")";
    });

    $scope.documentPath = function() {
      return paxDetailService.getDocumentPath();
    }
    $scope.airportPath = function() {
      return paxDetailService.getAirportPath();
    }

    $scope.activateGraph = function() {
      const template = $scope.saves.pax;
      vaquita.graph.HORIZONTAL_NODES = (template.horiz) || 1;

      // call start only when there's no rootnode
      //TODO vaquita - expose a status field on graph?
      if (vaquita.dataModel.getRootNode() === undefined) {
        vaquita.start(template);
        $scope.isReloaded = false;
      }
      // refresh graph arena if the page reloads with new pax data
      else if ($scope.isReloaded) {
        vaquita.refresh(template);
        $scope.isReloaded = false;
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

      vaquita.graph.mainLabel = $scope.saves[id];
      vaquita.graph.HORIZONTAL_NODES = ($scope.saves[id].horiz) || 1;
      vaquita.tools.reset();
    };

    vaquita.graph.onSave(function(graph) {
    });

    $scope.getAttachment = function(paxId) {
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

      flightLegs.sort(function(a, b) {
        if (a.legNumber < b.legNumber) return -1;
        if (a.legNumber > b.legNumber) return 1;
        else return 0;
      });

      $scope.liteLegs = [];    //holds discreet start and endpoints for non-contiguous segments

      $.each(flightLegs, function(index, curr) {
        curr.legNumber = index + 1;

        if ($scope.liteLegs.length === 0) {
          $scope.liteLegs.push({flightId: curr.flightId, originAirport: curr.originAirport});
        }
        else if ($scope.liteLegs[$scope.liteLegs.length-1].originAirport !== curr.originAirport) {
          $scope.liteLegs[$scope.liteLegs.length-1].segmentEnd = true;
          $scope.liteLegs.push({flightId: curr.flightId, originAirport: curr.originAirport});
        }
        else {
          $scope.liteLegs[$scope.liteLegs.length-1].flightId = curr.flightId;
        }
       
        $scope.liteLegs.push({flightId: null, originAirport: curr.destinationAirport});
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
          var results = query && (query.length) && (query.length >= 3) ? self.allAirports.filter(createFilterFor(query)) : [];
          return results;
      }

      $scope.searchSort = querySearch;
      $scope.model = paxModel.model;
      $scope.alldatamodel = Object.assign({}, paxModel.model);
      $scope.alldatamodel.pageSize = 1000;

      var self = this, airports,
          stateName = $state.$current.self.name,
          ruleGridColumns = [{
              name: 'ruleTitle',
              displayName: 'Title',
              cellTemplate: '<md-button aria-label="title" class="md-primary md-button md-default-theme">{{COL_FIELD}}</md-button>'
          }, {
              name: 'ruleConditions',
              displayName: 'Conditions',
              field: 'hitsDetailsList[0]',
              cellFilter: 'hitsConditionDisplayFilter'
          }],
          setSubGridOptions = function (data, appScopeProvider) {
              data.passengers.forEach(function (entity_row) {
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
          paxPassParser = function(passengers){
            var pax = {};
            //Obtain aggregate values
            if (passengers.length>0){
              pax.passCount=0;
              pax.crewCount=0;
              pax.hitCount=0;
              pax.openCaseCount=0;
              pax.closedCaseCount=0;
              for(var i=0; i<passengers.length; i++){
                if(passengers[i].passengerType==="P"){
                  pax.passCount+=1;
                }
                if(passengers[i].passengerType==="C"){
                  pax.crewCount+=1;
                }
                if(passengers[i].onWatchList || passengers[i].onRuleHitList ||passengers[i].onWatchListDoc){
                  pax.hitCount+=1;
                }
              }
              pax.eta = Date.parse(passengers[0].eta);
              pax.etd = Date.parse(passengers[0].etd);
            }
            $scope.pax = pax;
          },
          setPassengersGrid = function (grid, response) {
              var data = stateName === 'queryPassengers' ? response.data.result : response.data;
              setSubGridOptions(data, $scope);
              grid.totalItems = data.totalPassengers === -1 ? 0 : data.totalPassengers;
              grid.data = data.passengers;
              //Add specific passenger info to scope for paxDetail
              stateName === 'queryPassengers' ? null : paxPassParser(grid.data);
              if(!grid.data || grid.data.length == 0){
                  $scope.errorToast('No results found for selected filter criteria');
              }
              spinnerService.hide('html5spinner');
          },
          getPage = function () {
              if(stateName === "queryPassengers"){
                  setPassengersGrid($scope.passengerQueryGrid, passengers);
                  $scope.queryLimitReached = passengers.data.result.queryLimitReached;
              }else{
                  setPassengersGrid($scope.passengerGrid, passengers);
              }
          },
          update = function (data) {
              passengers = data;
              getPage();
              spinnerService.hide('html5spinner');
          },
          fetchMethods = {
              'queryPassengers': function () {
                  var postData, query = JSON.parse(localStorage['query']);
                  postData = {
                      pageNumber: $scope.model.pageNumber,
                      pageSize: $scope.model.pageSize,
                      query: query
                  };
                  spinnerService.show('html5spinner');
                  executeQueryService.queryPassengers(postData).then(update);
              },
              'flightpax': function () {
                  spinnerService.show('html5spinner');
                  paxService.getPax($stateParams.id, $scope.alldatamodel).then(update);
              },
              'paxAll': function () {
                  spinnerService.show('html5spinner');
                  paxService.getAllPax($scope.model).then(update);
              }
          },
          resolvePage = function () {
              populateAirports();
              fetchMethods[stateName]();
          },
          flightDirections = [
              {label: 'Inbound', value: 'I'},
              {label: 'Outbound', value: 'O'},
              {label: 'Any', value: 'A'}
          ];

          self.querySearch = querySearch;
          codeService.getAirportTooltips()
            .then(function (allAirports) {
                self.allAirports = allAirports.map(function (contact) {
                    contact.lowerCasedName = contact.id.toLowerCase();
                    return contact;
                });
                self.filterSelected = true;
                $scope.filterSelected = true;
            });
          $scope.flightDirections = flightDirections;
    
          $injector.invoke(jqueryQueryBuilderWidget, this, {$scope: $scope});
          $scope.stateName = $state.$current.self.name;    

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
      paginationPageSize: 25,
      paginationCurrentPage: $scope.model.pageNumber,
      useExternalPagination: false,
      useExternalSorting: false,
      useExternalFiltering: false,
      enableHorizontalScrollbar: 0,
      enableVerticalScrollbar: 1,
      enableFiltering: true,
      enableColumnMenus: false,
      multiSelect: false,
      enableGridMenu: true,
      exporterCsvFilename: 'PassengerGrid.csv',
      exporterExcelFilename: 'PassengerGrid.xlsx',
      exporterExcelSheetName: 'Data',
      enableExpandableRowHeader: false,

      expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',
      onRegisterApi: function (gridApi) {
          $scope.gridApi = gridApi;

          gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
              if (row.isExpanded) {
                  paxService.getRuleHits(row.entity.id).then(function (data) {
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
      enableVerticalScrollbar: 1,
      enableColumnMenus: false,
      enableGridMenu: true,
      exporterCsvFilename: 'passengerQueryGrid.csv',
      exporterExcelFilename: 'passengerQueryGrid.xlsx',
      exporterExcelSheetName: 'Data',
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
