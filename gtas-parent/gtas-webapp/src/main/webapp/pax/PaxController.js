/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  ////     PAX DETAIL CONTROLLER     //////////////
  app.controller('PassengerDetailCtrl', function ($scope, $mdDialog,$mdSidenav,$timeout, passenger, $mdToast, spinnerService, user,caseHistory,ruleCats, ruleHits, watchlistLinks, paxDetailService, caseService, watchListService, codeTooltipService) {
      $scope.passenger = passenger.data;
      $scope.watchlistLinks = watchlistLinks.data;
      $scope.isLoadingFlightHistory = true;
      $scope.isClosedCase = false;
      $scope.casesListWithCats=[];
      $scope.ruleHits = ruleHits;
      $scope.caseHistory = caseHistory.data; 
      $scope.ruleCats=ruleCats.data;
      $scope.slides = [];
      $scope.jsonData = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify($scope.passenger));
      
      $scope.getAttachment = function(paxId){
        //TO-DO add specific pax-id here to grab from current passenger
        paxDetailService.getPaxAttachments(paxId).then(function(data){
          var attList = '';
          $.each(data.data, function(index,value){
            var slideString = '';
            if(index === 0){
              slideString += '<slide active="'+value.active+'">';
            } else{
              slideString += '<slide>';
            }
            slideString += '<img ng-src="data:'+value.contentType+';base64,'+value.content+'"></slide>';
            attList += slideString;
          });
          $scope.slides = attList;
          $scope.showAttachments(attList);
        });
      };
      
      //Service call for tooltip data
        $scope.getCodeTooltipData = function(field, type){
           return codeTooltipService.getCodeTooltipData(field,type);
        }
        
        $scope.resetTooltip = function(){
           $('md-tooltip').remove();
        };
      
      $scope.watchlistCategoryId;
      
      watchListService.getWatchlistCategories().then(function(res){
        $scope.watchlistCategories =  res.data;
      });

      $scope.uploadAttachment = function(){
        //TO-DO add specific pax information here as well as credentials of some kind to insure we don't get arbitrary uploads.
        paxDetailService.savePaxAttachments('name','pw',$scope.attachmentDesc,$scope.passenger.paxId,$scope.attachment);
      };
      
      $scope.assignRuleCats = function(){
          angular.forEach($scope.ruleCats, function(item, index){
              $scope.casesListWithCats[item.catId] = item.category;
          });
      };

      $scope.assignRuleCats();

      //Bandaid: Parses out seat arrangements not for the particular PNR, returns new seat array. This should be handled on the back-end.
      var parseOutExtraSeats = function(seats, flightLegs){
        var newSeats = [];
        $.each(seats, function(index,value){
          $.each(flightLegs, function(i,v){
            if(value.flightNumber === v.flightNumber){
              newSeats.push(value);
              return;
            }
          });
        });
        return newSeats;
      };

      //Bandaid: Re-orders TVL lines for flight legs, making sure it is ordered by date.
      var reorderTVLdata = function(flightLegs){
        var orderedTvlData = [];

        //Sorts flightLeg objects based on etd
        // * 5/8/2018*No longer required to sort but does add +1 to leg number visually still, so will keep that functionality.
         
        flightLegs.sort(function(a,b){
          if(a.legNumber < b.legNumber) return -1;
          if(a.legNumber > b.legNumber) return 1;
          else return 0;
        });
        
        //sets each flightLeg# to the newly sorted index value
        $.each(flightLegs, function(index,value){
          value.legNumber = index+1; //+1 because 0th flight leg doesn't read well to normal humans
        });

        orderedTvlData = flightLegs;
          
        return orderedTvlData
      };
      
      function setId (coll) {
          var result = [];
          for(var rec of coll) {
          var res = rec;
            var id = (res.bookingDetailId == null) ? "P" + res.flightId : "D" + res.bookingDetailId;
          res.id = id;
            result.push(res);
          }
        return result;
      }
      
      var getPassengerBags = function(legs) {
          var passengers = $scope.passenger.pnrVo.passengers;
          var bags = $scope.passenger.pnrVo.bagSummaryVo.bagsByFlightLeg;

          bags = bags.filter(bag => bag.data_source === 'PNR');
          var newbags = setId(bags);

          newbags.sort((a,b) => (a.id > b.id) ? 1 : ((b.id > a.id) ? -1 : 0));
          newbags.sort((a,b) => (a.passengerId > b.passengerId) ? 1 : ((b.passengerId > a.passengerId) ? -1 : 0));

          // merge bag records by pax/flight
          var prevId;
          var prevPax;
          var prevBag;
          var acc = undefined;
          var result = [];
          for(var bag of newbags) {
              if(bag.id != prevId || bag.passengerId != prevPax){
                  if (acc!= undefined) {
                      result.push(acc);
                  }
                  acc = bag;
                  acc.bagList = bag.bagId;
                  var pax = passengers.find((p) => {
                      return p.paxId == bag.passengerId;
                  });

                  if (pax) {
                      acc.passLastName = pax.lastName;
                      acc.passFirstName = pax.firstName;
                  }
              }
              else {
                  if (bag.bagId != prevBag)
                      acc.bagList = acc.bagList + ", " + bag.bagId;
              }
              prevId = bag.id;
              prevPax = bag.passengerId;
              prevBag = bag.bagId
          }
          
          result.push(acc);
          return result;
      };

      if(angular.isDefined($scope.passenger.pnrVo) && $scope.passenger.pnrVo != null){
          $scope.passenger.pnrVo.seatAssignments = parseOutExtraSeats($scope.passenger.pnrVo.seatAssignments, $scope.passenger.pnrVo.flightLegs);
        $scope.passenger.pnrVo.flightLegs = reorderTVLdata($scope.passenger.pnrVo.flightLegs);
          $scope.orderedFlightLegs = setId($scope.passenger.pnrVo.flightLegs);
          $scope.orderedBags = getPassengerBags($scope.orderedFlightLegs);

          var raw = $scope.passenger.pnrVo.raw;
          $scope.passenger.pnrVo.documents = $scope.passenger.pnrVo.documents.filter(doc => raw.includes(doc.documentNumber));
      }

      //Removes extraneous characters from rule hit descriptions
      if($scope.ruleHits != typeof 'undefined' && $scope.ruleHits != null && $scope.ruleHits.length > 0){
        $.each($scope.ruleHits, function(index,value){
          value.ruleConditions = value.ruleConditions.replace(/[.*+?^${}()|[\]\\]/g, '');
        });
      }

      $scope.getTotalOf = function(coll, id, fieldToTotal) {
          var filtered = coll.filter(item => (item || {}).id == id);

          var total = filtered.reduce(function(accum, current){
              return current[fieldToTotal] + accum;
          }, 0);

          //refac - set the bag count header to the greatest bag count per leg.
          if ( fieldToTotal === "bag_count" && ($scope.passenger.pnrVo.totalbagCount || 0) < total) {
              $scope.passenger.pnrVo.totalbagCount = total;
          }

          return total;
      }

      $scope.highlightClass = function(className) {

          //remove existing highlights
        var existing = document.querySelectorAll('#pnrtable .ng-scope td')
        existing.forEach( elem => elem.classList.remove("highlight"));

        var elems = document.getElementsByClassName(className);
        for (var i=0; i<elems.length; i++) {
          if(elems[i].classList.contains("highlight")){
            elems[i].classList.remove("highlight");
          }
          else {
            elems[i].classList.add("highlight");
            elems[i].scrollIntoView(true, {behavior:"smooth"});
          }
        }
      };

      $scope.getWatchListMatchByPaxId = function (){
        paxDetailService.getPaxWatchlistLink($scope.passenger.paxId)
        .then(function(response){
          $scope.watchlistLinks = response.data;
        });
      }
      
     $scope.refreshCasesHistory = function () {
         paxDetailService.getPaxCaseHistory($scope.passenger.paxId).then(function(cases){
           $scope.caseHistory = cases.data;
         });
      }

      $scope.saveWatchListMatchByPaxId = function (){
        paxDetailService.savePaxWatchlistLink($scope.passenger.paxId)
        .then(function(response) {
          $scope.getWatchListMatchByPaxId();
          $scope.refreshCasesHistory($scope.passenger.paxId);
        });
      }

      $scope.saveDisposition = function(){
        var disposition = {
                  'passengerId':$scope.passenger.paxId,
                  'flightId':$scope.passenger.flightId,
                  'statusId':$scope.currentDispStatus,
                  'comments':$scope.currentDispComments,
                  'user':user.data.userId,
                  'createdBy': user.data.userId,
                  'createdAt': new Date()
              };

        spinnerService.show('html5spinner');
        caseService.createDisposition(disposition)
        .then(function(response){
          spinnerService.hide('html5spinner');
          //Clear input, reload history
          $scope.currentDispStatus="-1";
          $scope.currentDispComments="";
          //This makes it palatable to the front-end
          $.each($scope.dispositionStatus, function(index,value){
            if(value.id === parseInt(disposition.statusId)){
              var status = {status:value.name};
              $.extend(disposition,status);
            }
          });
          if($scope.passenger.dispositionHistory != null && typeof $scope.passenger.dispositionHistory.length != "undefined"
            && response.data.status.toUpperCase() === "SUCCESS"){
            //Add to disposition length without service calling if success
            $scope.passenger.dispositionHistory.push(disposition);
          } else{
            $scope.passenger.dispositionHistory = [disposition];
          }
        });
      }

   var getMostRecentCase = function(dispHistory){
    var mostRecentCase = null;
     $.each(dispHistory, function(index,value){
       if(mostRecentCase === null || mostRecentCase.createdAt < value.createdAt){
         mostRecentCase = value;
       }
     });
     return mostRecentCase;
   }

     $scope.isCaseDisabled = function(dispHistory){

     //Find if most recent case is closed
        var mostRecentCase = getMostRecentCase(dispHistory);
       //If Closed, find out if current user is Admin
        if(mostRecentCase != null && mostRecentCase.statusId == 3){
            var isAdmin = false;
            $.each(user.data.roles,function(index,value){
              if(value.roleId === 1){
                isAdmin = true;
              }
            });
       //If user is admin do not disable, else disable
          if(isAdmin){
            return false;
          } else return true;
        } else return false; //if not closed do not disable
      };

       $scope.isCaseDropdownItemDisabled = function(statusId){
         var mostRecentCase = getMostRecentCase($scope.passenger.dispositionHistory);
         if(mostRecentCase != null){
           if(mostRecentCase.statusId == 1){
             if(statusId == 3 || statusId == 4 || statusId == 1){
               return true;
             }
           }else if(mostRecentCase.statusId == 2){
             if(statusId == 3 || statusId == 4 || statusId == 1){
               return true;
             }
           }else if(mostRecentCase.statusId == 3){
             if(statusId != 4){
               return true;
             }
           }else if(mostRecentCase.statusId == 4){
              if(statusId == 2 || statusId == 3 || statusId == 1){
                return true;
              }
           }else if(mostRecentCase.statusId == 5){
             if(statusId == 2 || statusId == 1){
               return true;
             }
           } else if(mostRecentCase.statusId > 5){
             if(statusId == 1 || statusId == 3){
               return true
             }
           }
           return false;
         } if(statusId == 1){
           return false;
         } else{ return true;}
       };

      caseService.getDispositionStatuses()
      .then(function(response){
        $scope.dispositionStatus = response.data;
      });

      paxDetailService.getPaxFlightHistory($scope.passenger.paxId, $scope.passenger.flightId)
      .then(function(response){
        $scope.getPaxBookingDetailHistory($scope.passenger);
        $scope.passenger.flightHistoryVo = response.data;
  });

      $scope.getPaxFullTravelHistory= function(passenger){
        paxDetailService.getPaxFullTravelHistory(passenger.paxId, passenger.flightId).then(function(response){
          $scope.passenger.fullFlightHistoryVo ={'map': response.data};
          $scope.isLoadingFlightHistory = false;
        });
      };

      $scope.getPaxBookingDetailHistory= function(passenger){
          paxDetailService.getPaxBookingDetailHistory(passenger.paxId, passenger.flightId).then(function(response){
          $scope.passenger.fullFlightHistoryVo ={'map': response.data};
          $scope.isLoadingFlightHistory = false;        
        });
      };

      //Adds user from pax detail page to watchlist.
      $scope.addEntityToWatchlist = function(){
          spinnerService.show('html5spinner');
          var terms = [];
          //Add passenger firstName, lastName, dob to wlservice call
          terms.push({entity: "PASSENGER", field: "firstName", type: "string", value: $scope.passenger.firstName});
          terms.push({entity: "PASSENGER", field: "lastName", type: "string", value: $scope.passenger.lastName});
          terms.push({entity: "PASSENGER", field: "dob", type: "date", value: $scope.passenger.dob});
          terms.push({entity: "PASSENGER", field: "categoryId", type: "integer", value: $scope.watchlistCategoryId});
          watchListService.addItem("Passenger", "PASSENGER", null, terms).then(function(){
              terms = [];
              //Add documentType and documentNumber to wlservice call
              $.each($scope.passenger.documents, function(index,value){
                  if(value.documentType === "P" || value.documentType === "V"){
                      terms.push({entity: "DOCUMENT", field: "documentType", type: "string", value: value.documentType});
                      terms.push({entity: "DOCUMENT", field: "documentNumber", type: "string", value: value.documentNumber});
                      terms.push({entity: "DOCUMENT", field: "categoryId", type: "integer", value: $scope.watchlistCategoryId});
                      watchListService.addItem("Document", "DOCUMENT", null, terms).then(function(response){

                          if(response.data.status=='FAILURE'){
                              console.log(JSON.stringify(response));
                          }else{
                              //Compiles after each document add.
                              watchListService.compile();
                              //clear out terms list
                              terms = [];
                              spinnerService.hide('html5spinner');
                              $mdSidenav('addWatchlist').close();
                          }
                      });
                  }
              });
          });
      };

      $scope.addToWatchlist = function(){
          $timeout(function () {
              $mdSidenav('addWatchlist').open();
          });
      }
  //dialog function for watchlist addition dialog
  $scope.showConfirm = function () {
      var confirm = $mdDialog.confirm()
          .title('WARNING: Please Confirm The Watchlist Addition')
          .textContent('This will add both the current passenger and their applicable documents to the watchlist.')
          .ariaLabel('Add To Watchlist Warning')
          .ok('Confirm Addition')
          .cancel('Cancel');

      $mdDialog.show(confirm).then(function () {
         $scope.addEntityToWatchlist();
      }, function () {
          return false;
      });
  };

  //dialog function for image display dialog
  $scope.showAttachments = function(attachmentList) {
      $mdDialog.show({
        template:'<md-dialog><md-dialog-content>'+
        '<div><carousel>'+
              attachmentList+
          '</carousel></div>'+
        '</md-dialog-content></md-dialog>',
        parent: angular.element(document.body),
        clickOutsideToClose:true
      })
    };

  });






  ////     PAX CONTROLLER     //////////////
  app.controller('PaxController', function ($scope, $injector, $stateParams, $state, $mdToast, paxService, sharedPaxData, uiGridConstants, gridService,
                                            jqueryQueryBuilderService, jqueryQueryBuilderWidget, executeQueryService, passengers,
                                            $timeout, paxModel, $http, codeTooltipService, codeService, spinnerService) {
      $scope.errorToast = function(error){
          $mdToast.show($mdToast.simple()
           .content(error)
           .position('top right')
           .hideDelay(4000)
           .parent($scope.toastParent));
      };

      var exporter = {
        'csv': function () {
            $scope.gridApi.exporter.csvExport('all', 'all');
        },
        'pdf': function () {
            $scope.gridApi.exporter.pdfExport('all', 'all');
        }
      };

      $scope.export = function (format) {
          exporter[format]();
      };

      function createFilterFor(query) {
          var lowercaseQuery = query.toLowerCase();
          return function filterFn(contact) {
              return (contact.lowerCasedName.indexOf(lowercaseQuery) >= 0);
          };
      }
      /* Search for airports. */
      function querySearch(query) {
          var results = query && (query.length) && (query.length >= 3) ? self.allAirports.filter(createFilterFor(query)) : [];
          return results;
      }

      $scope.searchSort = querySearch;
      $scope.model = paxModel.model;

      var self = this, airports,
          stateName = $state.$current.self.name,
          ruleGridColumns = [{
              name: 'ruleTitle',
              displayName: 'Title',
              cellTemplate: '<md-button aria-label="title" class="md-primary md-button md-default-theme" ng-click="grid.appScope.ruleIdClick(row)">{{COL_FIELD}}</md-button>'
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
                  paxService.getPax($stateParams.id, $scope.model).then(update);
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
      $scope.ruleIdClick = function (row) {
          $scope.getRuleObject(row.entity.ruleId);
      };

      $scope.getRuleObject = function (ruleID) {
          jqueryQueryBuilderService.loadRuleById('rule', ruleID).then(function (myData) {
              $scope.$builder.queryBuilder('readOnlyRules', myData.result.details);
              $scope.hitDetailDisplay = myData.result.summary.title;
              document.getElementById("QBModal").style.display = "block";

              $scope.closeDialog = function () {
                  document.getElementById("QBModal").style.display = "none";
              };
          });
      };

      $scope.isExpanded = true;
      $scope.paxHitList = [];
      $scope.list = sharedPaxData.list;
      $scope.add = sharedPaxData.add;
      $scope.getAll = sharedPaxData.getAll;

      $scope.getPaxSpecificList = function (index) {
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
              onRegisterApi: function (gridApi) {
                  $scope.gridApi = gridApi;

                  gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                      $scope.model.pageNumber = newPage;
                      $scope.model.pageSize = pageSize;
                      resolvePage();
                  });

                  gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                      if (sortColumns.length === 0) {
                          $scope.model.sort = null;
                      } else {
                          $scope.model.sort = [];
                          for (var i = 0; i < sortColumns.length; i++) {
                              $scope.model.sort.push({column: sortColumns[i].name, dir: sortColumns[i].sort.direction});
                          }
                      }
                      resolvePage();
                  });

                  gridApi.core.on.columnVisibilityChanged( $scope, function( changedColumn ){
                    $scope.columnChanged = { name: changedColumn.colDef.name, visible: changedColumn.colDef.visible };
                  });

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
          enableVerticalScrollbar: 0,
          enableColumnMenus: false,
          multiSelect: false,
          enableExpandableRowHeader: false,
          minRowsToShow: 10,
          expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

          onRegisterApi: function (gridApi) {
              $scope.gridApi = gridApi;

              gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                  $scope.model.pageSize = pageSize;
              });

              gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
                  if (row.isExpanded) {
                      paxService.getRuleHits(row.entity.id).then(function (data) {
                          row.entity.subGridOptions.data = data;
                      });
                  }
              });
          }
      };

      $scope.getCodeTooltipData = function(field, type){
         return codeTooltipService.getCodeTooltipData(field,type);
      }

      $scope.hitTooltipData = ['Loading...'];

      $scope.resetTooltip = function(){
        $scope.hitTooltipData = ['Loading...'];
        // $('md-tooltip').remove();
      };

    $scope.getHitTooltipData = function(row){
      var dataList = [];
      paxService.getRuleHits(row.entity.id).then(function (data){
        $.each(data,function(index,value){
          dataList.push(value.ruleDesc);
        });
        if(dataList.length === 0){
          dataList = "No Description Available";
        }
        $scope.hitTooltipData = dataList;
      });
    };

      if (stateName === 'queryPassengers') {
          $scope.passengerQueryGrid.columnDefs = [
              {
                  field: 'onRuleHitList',
                  name: 'onRuleHitList',
                  displayName: 'Rule Hits',
                  width: 100,
                  cellClass: "rule-hit",
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 1
                  },
                  cellTemplate: '<md-button aria-label="hits" ng-mouseover="grid.appScope.getHitTooltipData(row)" ng-mouseleave="grid.appScope.resetTooltip()" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" ng-disabled={{!row.entity.onRuleHitList}}>'
                +'<md-tooltip class="multi-tooltip" md-direction="right"><div ng-repeat="item in grid.appScope.hitTooltipData">{{item}}<br/></div></md-tooltip>'
                +'<span ng-if="row.entity.onRuleHitList" class="warning-color"><i class="fa fa-flag" aria-hidden="true"></i></span></md-button>'
              },
              {
                  name: 'onWatchList', displayName: 'Watchlist Hits', width: 130,
                  cellClass: gridService.anyWatchlistHit,
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 0
                  },
                  cellTemplate: '<div>' +
                      '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList || row.entity.onWatchListLink" ' +
                      'ng-class="(row.entity.onWatchListDoc || row.entity.onWatchList) ? \'danger-color\' : \'alert-color\'" >' +
                      '<i class="fa fa-flag" aria-hidden="true"></i></span></div>'                },
              {
                  field: 'passengerType',
                  name: 'passengerType',
                  displayName:'T',
                  width: 50},
              {
                  field: 'lastName',
                  name: 'lastName',
                  displayName:'pass.lastname', headerCellFilter: 'translate',
                  cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.id}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme">{{COL_FIELD}}</md-button>'
              },
              {
                  field: 'firstName',
                  name: 'firstName',
                  displayName:'pass.firstname', headerCellFilter: 'translate'},
              {
                  field: 'middleName',
                  name: 'middleName',
                  displayName:'pass.middlename', headerCellFilter: 'translate'
              },
              {
                field: 'documents[0].documentNumber',
                name:'documentNumber',
                displayName:'pass.docNum', headerCellFilter: 'translate', width: 120
              },
              {
                  field: 'flightNumber',
                  name: 'flightNumber',
                  displayName:'pass.flight', headerCellFilter: 'translate',
                  cellTemplate: '<span>{{row.entity.carrier}}{{COL_FIELD}}</span>'
              },
              {
                  field: 'flightOrigin',
                  name: 'flightOriginairport',
                  displayName:'pass.originairport', headerCellFilter: 'translate',
                  cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                    +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
                    +'</md-button>'
              },
              {
                  field: 'flightDestination',
                  name: 'flightDestinationairport',
                  displayName:'pass.destinationairport', headerCellFilter: 'translate',
                  cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                    +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
                    +'</md-button>'
              },
              {
                  field: 'etaLocalTZ',
                  name: 'etaLocalTZ',
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 2
                  },
                  displayName:'pass.eta', headerCellFilter: 'translate'
              },
              {
                  field: 'etdLocalTZ',
                  name: 'etdLocalTZ',
                  displayName:'pass.etd', headerCellFilter: 'translate'
              },
              {
                  field: 'gender',
                  name: 'gender',
                  displayName:'G',
                  width: 50},
              {
                  name: 'dob',
                  displayName:'pass.dob', headerCellFilter: 'translate',
                  cellFilter: 'date',
                  cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'
              },
              {
                  name: 'nationality',
                  displayName:'Nationality', headerCellFilter: 'translate',
                  width: 75,
                  cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetTooltip()">'
                    +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
                    +'</md-button>'
              }
          ];
      } else {
          $scope.passengerGrid.columnDefs = [
              {
                  name: 'onRuleHitList', displayName: 'Rule Hits', width: 100,
                  cellClass: "rule-hit",
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 1
                  },
                  cellTemplate: '<md-button aria-label="hits" ng-mouseover="grid.appScope.getHitTooltipData(row)" ng-mouseleave="grid.appScope.resetTooltip()" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" ng-disabled={{!row.entity.onRuleHitList}}>'
                +'<md-tooltip class="multi-tooltip" md-direction="right"><div ng-repeat="item in grid.appScope.hitTooltipData">{{item}}<br/></div></md-tooltip>'
                +'<span ng-if="row.entity.onRuleHitList" class="warning-color"><i class="fa fa-flag" aria-hidden="true"></i></span></md-button>'
              },
              {
                  name: 'onWatchList', displayName: 'Watchlist Hits', width: 130,
                  cellClass: gridService.anyWatchlistHit,
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 0
                  },
                  cellTemplate: '<div>' +
                      '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList || row.entity.onWatchListLink" ' +
                      'ng-class="(row.entity.onWatchListDoc || row.entity.onWatchList) ? \'danger-color\' : \'alert-color\'" >' +
                      '<i class="fa fa-flag" aria-hidden="true"></i></span></div>'
              },
              {name: 'passengerType', displayName:'T', width: 50, headerCellFilter: 'translate',
                cellTemplate: '<md-button>'
                +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"passenger")}}</div></md-tooltip>{{COL_FIELD}}'
                +'</md-button>'
              },
              {
                  name: 'lastName', displayName:'pass.lastname', headerCellFilter: 'translate',
                  cellTemplate: '<md-button aria-label="Last Name" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail" class="md-primary md-button md-default-theme">{{COL_FIELD}}</md-button>'
              },
              {name: 'firstName', displayName:'pass.firstname', headerCellFilter: 'translate'},
              {name: 'middleName', displayName:'pass.middlename', headerCellFilter: 'translate'},
              {
                field: 'documents[0].documentNumber',
                name:'documentNumber',
                displayName:'pass.docNum', headerCellFilter: 'translate', width:130
              },
              {
                  name: 'eta',
                  sort: {
                      direction: uiGridConstants.DESC,
                      priority: 2
                  },
                  displayName:'pass.eta', headerCellFilter: 'translate',
                  visible: (stateName === 'paxAll')
              },
              {name: 'etd', displayName:'pass.etd', headerCellFilter: 'translate', visible: (stateName === 'paxAll')},
              {name: 'gender', displayName:'G', width:50, headerCellFilter: 'translate',
              cellTemplate: '<md-button>'
              +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"gender")}}</div></md-tooltip>{{COL_FIELD}}'
              +'</md-button>'
              },
              {name: 'dob', displayName:'pass.dob', headerCellFilter: 'translate', cellFilter: 'date',
                cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'},
              {name: 'nationality', displayName:'Nationality', headerCellFilter: 'translate', width:120,
                cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetTooltip()">'
                +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
                +'</md-button>'}
          ];
      }

      var populateAirports = function(){

          var originAirports = new Array();
          var destinationAirports = new Array();

          angular.forEach($scope.model.origin,function(value,index){
              originAirports.push(value.id);
          })

          angular.forEach($scope.model.dest,function(value,index){
              destinationAirports.push(value.id);
          })

          $scope.model.originAirports = originAirports;
          $scope.model.destinationAirports = destinationAirports;
      };

      var mapAirports = function(){

          var originAirports = new Array();
          var destinationAirports = new Array();
          var airport = { id: "" };

          if($scope.model.origin ) {
              if ($scope.model.origin instanceof Array ){
                  angular.forEach($scope.model.origin, function (value, index) {
                      if(value instanceof Object) {
                          originAirports.push({id:value.id});
                      }else{
                          originAirports.push({id: value});
                      }
                  });
              }else{
                  originAirports.push({id: $scope.model.origin});
              }
              $scope.model.origin = originAirports;
          }

          if($scope.model.dest ) {
              if ($scope.model.dest instanceof Array ) {
                angular.forEach($scope.model.dest, function (value, index) {
                  if(value instanceof Object) {
                      destinationAirports.push({id:value.id});
                  }else{
                    destinationAirports.push({id: value});
                  }
                });
              }else{
                  destinationAirports.push({id: $scope.model.dest});
              }
              $scope.model.dest = destinationAirports;
          }

      };

      $scope.filter = function () {
          resolvePage();
          if($scope.gridApi.pagination.getPage() > 1){
              $scope.gridApi.pagination.seek(1);
          }
      };

      $scope.reset = function () {
          paxModel.reset();
          resolvePage();
      };
      $scope.getTableHeight = function () {
          if( stateName != "queryPassengers"){
              return gridService.calculateGridHeight($scope.passengerGrid.data.length);
          } // Sets minimal height for front-end pagination controlled variant of grid
          return gridService.calculateGridHeight($scope.model.pageSize);
      };
      //toggleDiv and filterCheck required for sidepanel
      $scope.toggleDiv = function(div) {
          var element = document.getElementById(div);
          if(element.classList.contains("active")){
            element.classList.remove("active");
          }
          else {
            element.className +=" active";
          }
        };
      $scope.filterCheck = function(option) {
        var filters = ['origin', 'destination', 'flight', 'direction', 'date'];
        return filters.includes(option);
      }

      getPage();
      mapAirports();
  });
  
}());
