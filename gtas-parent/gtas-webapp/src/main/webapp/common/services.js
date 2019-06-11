/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  app
      .service('notificationService', function($http,$q) {
        var GET_MESSAGE_ERRORS_URL ="/gtas/errorMessage";
        var GET_NOTIFICATION_HITS ="/gtas/hitCount";
        function handleError(response) {
            if (response.data.message === undefined) {
                return $q.reject("An unknown error occurred.");
            }
            return $q.reject(response.data.message);
        }

        function handleSuccess(response) {
            return response.data;
        }
        return {
            getErrorData: function () {
                var request = $http({
                    method: "get",
                    url: GET_MESSAGE_ERRORS_URL
                });
                return (request.then(handleSuccess, handleError));
            },
            getWatchlistCount: function () {
                var request = $http({
                    method: "get",
                    url: GET_NOTIFICATION_HITS
                });
                return (request.then(handleSuccess, handleError));
            }
        }
      })
      .service('errorService', function ($http, $q) {
          var GET_ERROR_RECORDS_URL = "/gtas/errorlog";

          function handleError(response) {
              if (response.data.message === undefined) {
                  return $q.reject("An unknown error occurred.");
              }
              return $q.reject(response.data.message);
          }

          function handleSuccess(response) {
              return response.data;
          }

          return {
              getErrorData: function (code, commence, fin) {
                  var st = commence != null ? moment(commence).format('YYYY-MM-DD') : null;
                  var nd = fin != null ? moment(fin).format('YYYY-MM-DD') : null;
                  var urlString = GET_ERROR_RECORDS_URL;
                  if (code != null || st != null || nd != null) {
                      urlString += '?';
                  }
                  var sep = '';
                  if (code != null) {
                      urlString += sep.concat('code=', code);
                      sep = '&';
                  }
                  if (st != null) {
                      urlString += sep.concat('startDate=', st);
                      sep = '&';
                  }
                  if (nd != null) {
                      urlString += sep.concat('endDate=', nd);
                  }
                  var request = $http({
                      method: "get",
                      url: urlString
                  });

                  return (request.then(handleSuccess, handleError));
              }
          };
      })
      /* audit log viewer service */
      .service('auditService', function ($http, $q) {
          var GET_AUDIT_RECORDS_URL = "/gtas/auditlog";

//            function handleError(response) {
//                if (response.data.message === undefined) {
//                    return $q.reject("An unknown error occurred.");
//                }
//                return $q.reject(response.data.message);
//            }

          function handleSuccess(response) {
              if(response.status > 299){
                  if (response.data.message === undefined) {
                      return $q.reject("An unknown error occurred.");
                  }
                  return $q.reject(response.data.message);
              }
              return response.data;
          }

          return {
              getAuditData: function (action, user, commence, fin) {
                  var st = commence != null ? moment(commence).format('YYYY-MM-DD') : null;
                  var nd = fin != null ? moment(fin).format('YYYY-MM-DD') : null;
                  var urlString = GET_AUDIT_RECORDS_URL;
                  if (action != null || user != null || st != null || nd != null) {
                      urlString += '?';
                  }
                  var sep = '';
                  if (user != null) {
                      urlString += sep.concat('user=', user);
                      sep = '&';
                  }
                  if (action != null) {
                      urlString += sep.concat('action=', action);
                      sep = '&';
                  }
                  if (st != null) {
                      urlString += sep.concat('startDate=', st);
                      sep = '&';
                  }
                  if (nd != null) {
                      urlString += sep.concat('endDate=', nd);
                  }
                  var request = $http({
                      method: "get",
                      url: urlString
                  });

                  return (request.then(handleSuccess));
              },
              auditActions: [
                  'ALL_ACTIONS',
                  'CREATE_UDR',
                  'UPDATE_UDR',
                  'UPDATE_UDR_META',
                  'DELETE_UDR',
                  'CREATE_WL',
                  'UPDATE_WL',
                  'DELETE_WL',
                  'LOAD_APIS',
                  'LOAD_PNR',
                  'CREATE_USER',
                  'UPDATE_USER',
                  'SUSPEND_USER',
                  'DELETE_USER',
                  'TARGETING_RUN',
                  'LOADER_RUN',
                  'UPDATE_DASHBOARD_RUN',
                  'MESSAGE_INGEST_PARSING',
                  'RULE_HIT_CASE_OPEN',
                  'DISPOSITION_STATUS_CHANGE'
              ]
          };
      })
      .service('defaultSettingsService', function($http, $q) {
        var SETTINGS = "/gtas/settingsinfo"
        var dfd = $q.defer();
        function getAllSettings(){
          dfd.resolve($http.get(SETTINGS));
          return dfd.promise;
        }
        function saveSettings(settings){
          return $http({
            method: 'PUT',
            url: SETTINGS,
            params: settings,
            headers: 'Accept:application/json'
          });
        }
        return {
          getAllSettings: getAllSettings,
          saveSettings: saveSettings
        }
      })
  .service('codeService', function ($http, $q) {
    var CODE_URL = "/gtas/api/";

    function handleError(response) {
      if (response.data.message === undefined) {
        return $q.reject("An unknown error occurred.");
      }
      return $q.reject(response.data.message);
    }

    function handleErrorGeneric(msg) {
      return $q.reject(msg);
    }

    function handleSuccess(response) {
      return response.data;
    }

    //untidy name format "fullName(code)"
    //removes "(code)"
    function getTidyName(name) {
      return name.split("(")[0];
    }
    
    return {
      getAllCodes: function(type) {
        var request = $http({
          method: "get",
          url: CODE_URL+type,
          headers: 'Accept:application/json'});
        return request.then(handleSuccess, handleError);
      },

      updateCode : function(type, code) {
        var request = $http({
          method: "put",
          url: CODE_URL + type,
          data: code
        });
        return (request.then(handleSuccess, handleError));
      },
      
      createCode : function(type, code) {
        var request = $http({
          method: "post",
          url: CODE_URL + type,
          data: code
        });
        return (request.then(handleSuccess, handleError));
      },

      deleteCode : function(type, id) {
        var request = $http({
          method: "delete",
          url: CODE_URL + type + '/' + id
        });
        return (request.then(handleSuccess, handleError));
      },

      restoreCode: function(type, code) {
        console.log(type, code);

        var request = $http({
          method: "put",
          url: `${CODE_URL}${type}/restore`,
          data: code
        });
        return (request.then(handleSuccess, handleError));
      },

      restoreAllCodes: function(type) {
        console.log(type);

        var request = $http({
          method: "put",
          url: `${CODE_URL}${type}/restoreAll`
        });
        console.log(request);
        return (request.then(handleSuccess, handleError));
      },


      getCountryTooltips: function() {
        return this.getAllCodes('country').then(function(response){

          return response.map(x => ({id: x.iso3, name: getTidyName(x.name)}));

        }, handleError);
      },

      getCarrierTooltips: function() {
        return this.getAllCodes('carrier').then(function(response){
          return response.map(x => ({id: x.iata, name: getTidyName(x.name)}));

        }, handleError);
      },

      getAirportTooltips: function() {
        return this.getAllCodes('airport').then(function(response) {

          return response.map(x => ({id: x.iata, name: x.name + ', ' + x.city + ', ' + x.country}));
        }, handleError);
      },

      getAirportsWithCode: function() {
        return this.getAllCodes('airport').then(function(response) {

          return response.map(x => ({id: x.iata, name: x.name + '  (' + x.iata + ')'}));
        }, handleError);
      }


      };    // return codeService
    })
    .service('userService', function ($http, $q) {
          var USER_ROLES_URL = "/gtas/roles/",
              USERS_URL = "/gtas/users/",
              MANAGE_USER_URL = "/gtas/manageuser/",
              USER_URL = "/gtas/user/";

          function handleError(response) {
              if (response.data.message !== undefined) {
                  return $q.reject("An unknown error occurred.");
              }
              return $q.reject(response.data.message);
          }

          function handleSuccess(response) {
              return response.data;
          }

          function getRoles() {
              var request = $http({
                  method: "get",
                  url: USER_ROLES_URL,
                  params: {
                      action: "get"
                  }
              });
              return (request.then(handleSuccess, handleError));
          }

          //function saveUser(user, method) {
          //    return request = $http({
          //        method: method,
          //        url: baseUsersURL + user.userId,
          //        data: user
          //    });
          //}
          function updateUser(user) {
              var request = $http({
                  method: "put",
                  url: USERS_URL + user.userId,
                  data: user
              });
              return (request.then(handleSuccess, handleError));
          }
          
          function manageUser(user) {
              var request = $http({
                  method: "put",
                  url: MANAGE_USER_URL + user.userId,
                  data: user
              });
              return (request.then(handleSuccess, handleError));
          }

          function createUser(user) {
              var request = $http({
                  method: "post",
                  url: USERS_URL + user.userId,
                  data: user

              });
              return (request.then(handleSuccess, handleError));
          }

          function getUserData() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: USER_URL
              }));
              return dfd.promise;
          }

          return {
              getRoles: getRoles,
              createUser: createUser,
              updateUser: updateUser,
              manageUser:manageUser,
              getUserData: getUserData,
              getAllUsers: function () {
                  var request = $http({
                      method: "get",
                      url: USERS_URL
                  });

                  return (request.then(handleSuccess, handleError));
              }
          };
      })
    .service("gridOptionsLookupService", function (uiGridConstants) {
          var today = moment().format("YYYY-MM-DD"),
            pageOfPages = function(currentPage, pageCount) {
              return (
                today +
                (pageCount === 1
                  ? ""
                  : "\t" +
                    currentPage.toString() +
                    " of " +
                    pageCount.toString())
              );
            },
            standardOptions = {
              paginationPageSize: 10,
              paginationPageSizes: [],
              enableHorizontalScrollbar: 0,
              enableVerticalScrollbar: 0,
              enableFiltering: true,
              enableCellEditOnFocus: false,
              showGridFooter: true,
              multiSelect: false,
              enableGridMenu: true,
              enableSelectAll: false
            },
            exporterOptions = {
              exporterPdfDefaultStyle: { fontSize: 9 },
              exporterPdfTableStyle: { margin: [10, 10, 10, 10] },
              exporterPdfTableHeaderStyle: {
                fontSize: 10,
                bold: true,
                italics: true
              },
              exporterPdfFooter: function(
                currentPage,
                pageCount
              ) {
                return {
                  text: pageOfPages(currentPage, pageCount),
                  style: "footerStyle"
                };
              },
              exporterPdfCustomFormatter: function(
                docDefinition
              ) {
                docDefinition.pageMargins = [0, 40, 0, 40];
                docDefinition.styles.headerStyle = {
                  fontSize: 22,
                  bold: true,
                  alignment: "center",
                  lineHeight: 1.5
                };
                docDefinition.styles.footerStyle = {
                  fontSize: 10,
                  italic: true,
                  alignment: "center"
                };
                return docDefinition;
              },
              exporterPdfOrientation: "landscape",
              exporterPdfPageSize: "LETTER",
              exporterPdfMaxGridWidth: 600,
              exporterCsvLinkElement: angular.element(
                document.querySelectorAll(
                  ".custom-csv-link-location"
                )
              )
            },
            defaultOptions = $.extend(
              {},
              standardOptions,
              exporterOptions
            ),
            gridOptions = {
              admin: $.extend({}, defaultOptions, {
                enableVerticalScrollbar: 2
              }),
              audit: {
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                enableFullRowSelection: true,
                paginationPageSize: 10,
                paginationPageSizes: [],
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableFiltering: true,
                enableCellEditOnFocus: false,
                showGridFooter: true,
                multiSelect: false,
                enableGridMenu: true,
                enableSelectAll: true
              },
              error: {
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                enableFullRowSelection: true,
                paginationPageSize: 10,
                paginationPageSizes: [],
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableFiltering: true,
                enableCellEditOnFocus: false,
                showGridFooter: true,
                multiSelect: false,
                enableGridMenu: true,
                enableSelectAll: true
              },
              code: {
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                enableFullRowSelection: true,
                paginationPageSize: 10,
                paginationPageSizes: [10, 25, 50, 100],
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableFiltering: true,
                enableCellEditOnFocus: false,
                showGridFooter: true,
                multiSelect: false,
                enableGridMenu: true,
                enableSelectAll: false
              },
              flights: {
                enableSorting: false,
                multiSelect: false,
                enableFiltering: false,
                enableRowSelection: true,
                enableSelectAll: false,
                enableRowHeaderSelection: false,
                enableGridMenu: false,
                paginationPageSizes: [10, 25, 50],
                paginationPageSize: 10,
                useExternalPagination: true,
                useExternalSorting: true,
                useExternalFiltering: true
              },
              passengers: {
                enableSorting: false,
                multiSelect: false,
                enableFiltering: false,
                enableRowSelection: false,
                enableSelectAll: false,
                enableGridMenu: false,
                paginationPageSizes: [15, 25, 50],
                paginationPageSize: 15,
                useExternalPagination: true,
                useExternalSorting: true,
                useExternalFiltering: true,
                expandableRowTemplate:
                  '<div ui-grid="row.entity.subGridOptions"></div>'
              },
              query: $.extend({}, defaultOptions, {
                enableVerticalScrollbar: 2
              }),
              rule: $.extend({}, defaultOptions, {
                enableVerticalScrollbar: 2
              }),
              watchlist: defaultOptions
            },
            columns = {
              audit: [
                {
                  name: "action",
                  displayName: "admin.action",
                  headerCellFilter: "translate",
                  field: "actionType",
                  width: "10%",
                  sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                  }
                },
                {
                  name: "user",
                  displayName: "admin.user",
                  headerCellFilter: "translate",
                  field: "user",
                  width: "15%"
                },
                {
                  name: "status",
                  displayName: "admin.status",
                  headerCellFilter: "translate",
                  field: "status",
                  width: "10%"
                },
                {
                  name: "message",
                  displayName: "admin.message",
                  headerCellFilter: "translate",
                  field: "message",
                  width: "20%"
                },
                {
                  name: "timestamp",
                  displayName: "admin.timestamp",
                  headerCellFilter: "translate",
                  field: "timestamp",
                  width: "45%"
                }
              ],
              airport: [
                {
                  name: " ",
                  enableFiltering: false,
                  enableSorting: false,
                  width: "1%", cellTemplate: '<a class="full-width editLink" ng-click="grid.appScope.openSidebarEdit(row.entity)"><i class="fa fa-edit"></a>'
                },
                {
                  name: "id", headerCellFilter: "translate", field: "id", visible: false,
                  width: "0%", type: "string"
                },
                {
                  name: "iata", displayName: "IATA", headerCellFilter: "translate", field: "iata",
                  width: "10%", type: "string"
                },
                {
                  name: "icao", displayName: "ICAO", field: "icao",
                  headerCellFilter: "translate", width: "10%", type: "string",
                 },
                {
                  name: "name",
                  displayName: "NAME",
                  headerCellFilter: "translate",
                  field: "name",
                  width: "*",
                  type: "string"
                },
                {
                  name: "city",
                  displayName: "CITY",
                  headerCellFilter: "translate",
                  field: "city",
                  width: "20%",
                  type: "string"
                },
                {
                  name: "country",
                  displayName: "COUNTRY",
                  headerCellFilter: "translate",
                  field: "country",
                  width: "10%",
                  type: "string"
                }
              ],
              carrier: [
                {
                  name: " ",
                  enableFiltering: false,
                  enableSorting: false,
                  width: "1%", cellTemplate: '<a class="full-width editLink" ng-click="grid.appScope.openSidebarEdit(row.entity)"><i class="fa fa-edit"></a>'
                },
                {
                  name: "id", headerCellFilter: "translate", field: "id", visible: false,
                  width: "0%", type: "string"
                },
                {
                  name: "iata",
                  displayName: "IATA",
                  headerCellFilter: "translate",
                  field: "iata",
                  width: "25%"
                },
                {
                  name: "name",
                  displayName: "NAME",
                  headerCellFilter: "translate",
                  field: "name",
                  width: "74%"
                }
              ],
              country: [
                {
                  name: " ",
                  enableFiltering: false,
                  enableSorting: false,
                  width: "1%", cellTemplate: '<a class="full-width editLink" ng-click="grid.appScope.openSidebarEdit(row.entity)"><i class="fa fa-edit"></a>'
                },
                {
                  name: "id", headerCellFilter: "translate", field: "id", visible: false,
                  width: "0%", type: "string"
                },
                {
                  name: "iso2",
                  displayName: "ISO2",
                  headerCellFilter: "translate",
                  field: "iso2",
                  width: "14%"
                },
                {
                  name: "iso3",
                  displayName: "ISO3",
                  headerCellFilter: "translate",
                  field: "iso3",
                  width: "15%"
                },
                {
                  name: "isoNumeric",
                  displayName: "ISO Numeric",
                  headerCellFilter: "translate",
                  field: "isoNumeric",
                  width: "15%"
                },
                {
                  name: "name",
                  displayName: "NAME",
                  headerCellFilter: "translate",
                  field: "name",
                  width: "55%"
                }
              ],
              error: [
                {
                  name: "Error ID",
                  field: "errorId",
                  displayName: "admin.errorid",
                  headerCellFilter: "translate",
                  width: "15%",
                  sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                  }
                },
                {
                  name: "Error Code",
                  displayName: "admin.errorcode",
                  headerCellFilter: "translate",
                  field: "errorCode",
                  width: "15%"
                },
                {
                  name: "DateTime",
                  displayName: "admin.DateTime",
                  headerCellFilter: "translate",
                  field: "errorTimestamp",
                  width: "15%"
                },
                {
                  name: "Error Description",
                  displayName: "admin.errordescription",
                  headerCellFilter: "translate",
                  field: "errorDescription",
                  width: "55%"
                }
              ],
              admin: [
                {
                  name: "active",
                  displayName: "admin.active",
                  headerCellFilter: "translate",
                  field: "active",
                  cellFilter: "userStatusFilter",
                  width: "10%",
                  sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                  }
                },
                {
                  name: "userId",
                  displayName: "admin.userid",
                  headerCellFilter: "translate",
                  field: "userId",
                  width: "15%",
                  cellTemplate:
                    '<div><md-button class="md-primary md-button md-default-theme" ng-click="grid.appScope.lastSelectedUser(row.entity)" href="#/user/{{COL_FIELD}}">{{COL_FIELD}}</md-button></div>'
                },
                {
                  name: "firstName",
                  displayName: "pass.firstname",
                  headerCellFilter: "translate",
                  field: "firstName",
                  width: "15%"
                },
                {
                  name: "lastName",
                  displayName: "pass.lastname",
                  headerCellFilter: "translate",
                  field: "lastName",
                  width: "20%"
                },
                {
                  name: "roles",
                  displayName: "user.roles",
                  headerCellFilter: "translate",
                  field: "roles",
                  cellFilter: "roleDescriptionFilter",
                  width: "40%",
                  cellTooltip: function(row) {
                    return row.entity.roles;
                  },
                  cellTemplate:
                    '<div class="ui-grid-cell-contents wrap" style="white-space: normal" title="TOOLTIP">{{COL_FIELD CUSTOM_FILTERS}}</div>'
                }
              ],
              flights: [
                {
                  name: "P",
                  field: "passengerCount",
                  width: 50,
                  enableFiltering: false,
                  cellTemplate:
                    '<md-button class="md-primary" style="min-width: 0; margin: 0 auto; width: 100%;" ng-click="grid.appScope.passengerNav(row)">{{COL_FIELD}}</md-button>'
                },
                {
                  name: "H",
                  field: "ruleHitCount",
                  width: 50,
                  enableFiltering: false
                },
                {
                  name: "L",
                  field: "listHitCount",
                  width: 50,
                  enableFiltering: false
                },
                {
                  name: "Carrier",
                  field: "carrier",
                  width: 75
                },
                {
                  name: "Flight",
                  field: "flightNumber",
                  width: 75
                },
                {
                  name: "Dir",
                  field: "direction",
                  width: 50
                },
                {
                  name: "ETA",
                  displayName: "ETA",
                  field: "eta"
                },
                {
                  name: "ETD",
                  displayName: "ETD",
                  field: "etd"
                },
                {
                  name: "Origin",
                  field: "origin"
                },
                {
                  name: "OriginCountry",
                  displayName: "Country",
                  field: "originCountry"
                },
                {
                  name: "Dest",
                  field: "destination"
                },
                {
                  name: "DestCountry",
                  displayName: "Country",
                  field: "destinationCountry"
                }
              ],
              passengers: [
                //needs gridService reference
              ],
              query: [
                {
                  name: "title",
                  displayName: "qry.name",
                  headerCellFilter: "translate",
                  field: "title",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "description",
                  displayName: "qry.desc",
                  headerCellFilter: "translate",
                  field: "description",
                  cellTemplate:
                    '<md-button aria-label="description" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                }
              ],
              rule: [
                {
                  name: "hitCount",
                  displayName: "Hits",
                  headerCellFilter: "translate",
                  field: "hitCount",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "title",
                  displayName: "Name",
                  headerCellFilter: "translate",
                  field: "title",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "description",
                  displayName: "Description",
                  headerCellFilter: "translate",
                  field: "description",
                  cellTemplate:
                    '<md-button aria-label="description" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "startDate",
                  displayName: "flight.startdate",
                  headerCellFilter: "translate",
                  field: "startDate",
                  cellTemplate:
                    '<md-button aria-label="start date" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "endDate",
                  displayName: "flight.enddate",
                  headerCellFilter: "translate",
                  field: "endDate",
                  cellTemplate:
                    '<md-button aria-label="end date" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "enabled",
                  displayName: "user.status.enabled",
                  headerCellFilter: "translate",
                  field: "enabled",
                  cellTemplate:
                    '<md-button aria-label="enabled" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "overMaxHits",
                  displayName: "Over Max Hits",
                  headerCellFilter: "translate",
                  field: "overMaxHits",
                  cellTemplate:
                    '<md-button aria-label="overMaxHits" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "modifiedOn",
                  displayName: "qry.modified",
                  headerCellFilter: "translate",
                  field: "modifiedOn",
                  cellTemplate:
                    '<md-button aria-label="modified" ng-click="grid.api.selection.selectRow(row.entity)">{{row.entity.modifiedOn}} | {{row.entity.modifiedBy}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                }
              ],
              watchlist: {
                DOCUMENT: [
                  {
                    field: "documentType",
                    name: "documentType",
                    //displayName: "Type",
                    displayName: "doc.type",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "documentNumber",
                    name: "documentNumber",
                    //displayName: "Number",
                    displayName: "doc.Number",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "categoryId",
                    name: "categoryId",
                    //displayName: "Number",
                    displayName: "Category",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{grid.appScope.categories[COL_FIELD]}}</md-button>',
                    type: "integer"
                  }
                ],
                PASSENGER: [
                  {
                    field: "dob",
                    name: "dob",
                    //displayName: "DOB",
                    displayName: "doc.dob",
                    headerCellFilter: "translate",
                    width: 100,
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD | date:\'yyyy-MM-dd\'}}</md-button>',
                    type: "date"
                  },
                  {
                    field: "firstName",
                    name: "firstName",
                    //displayName: "First Name",
                    displayName: "pass.firstname",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "lastName",
                    name: "lastName",
                    //displayName: "Last Name",
                    displayName: "pass.lastname",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "categoryId",
                    name: "categoryId",
                    //displayName: "Number",
                    displayName: "Category",
                    headerCellFilter: "translate",
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{grid.appScope.categories[COL_FIELD]}}</md-button>',
                    type: "integer"
                  }
                ]
              }
            };
          return {
              paginationOptions: {
                  pageNumber: 1,
                  pageSize: 10,
                  sort: null
              },
              getGridOptions: function (entity) {
                return gridOptions[entity];
              },
              getLookupColumnDefs: function (entity) {
                return columns[entity] || [];
              }
          };
      })
      .service("gridService", function () {
          /**
           * Take the number of rows in the grid and calculate the
           * correct 'height' style to show all of the data at once.
           * We use this as part of auto-resizing grids.
           */
          var calculateGridHeight = function (numRows) {
                  var MIN_NUM_ROWS = 10;

                  var rowHeight = 30;
                  var headerHeight = 30;
                  var n = numRows >= MIN_NUM_ROWS ? numRows : MIN_NUM_ROWS;
                  return {
                      height: (n * rowHeight + 3 * headerHeight) + "px"
                  };
              },
              /**
               * @return the cell color for the rule hit and watch list hit
               * columns.  cellValue can either be a count or a boolean value.
               */
              colorHits = function (grid, row, col) {
                  if (grid.getCellValue(row, col) > 0) {
                    if(col.field === 'ruleHitCount'){
                      return 'red';
                    }else if(col.field === 'listHitCount'){
                      return 'yellow';
                    }
                  }
              },
              ruleHit = function (grid, row, col) {
                  return grid.getCellValue(row, col) ? 'rule-hit' : 'invisible';
              },
              anyWatchlistHit = function (grid, row) {
                  if (row.entity.onWatchList || row.entity.onWatchListDoc || row.entity.onWatchListLink) {
                      return 'watchlist-hit';
                  }
              };

          return ({
              anyWatchlistHit: anyWatchlistHit,
              calculateGridHeight: calculateGridHeight,
              colorHits: colorHits,
              ruleHit: ruleHit
          });
      })
      .service("watchListService", function ($http, $q) {
          var baseUrl = '/gtas/wl/',
              handleError = function (response) {
                  if (!angular.isObject(response.data) || !response.data.message) {
                      return ($q.reject("An unknown error occurred."));
                  }
                  return ($q.reject(response.data.message));
              },
              handleSuccess = function (response) {
                  return (response);
              };

          return {
              compile: function () {
                  var request = $http({
                      method: "get",
                      url: baseUrl + 'compile'
                  });

                  return (request.then(handleSuccess, handleError));
              },
              getTabs: function () {
                  var request = $http({
                      method: "get",
                      url: baseUrl + 'list'
                  });

                  return (request.then(handleSuccess, handleError));
              },
              getListItems: function (entity, listTypeName) {
                  if (!entity || !listTypeName) {
                      return false;
                  }
                  var request = $http({
                      method: "get",
                      url: baseUrl + entity + "/" + listTypeName
                  });

                  return (request.then(handleSuccess, handleError));
              },
              deleteListItems: function (entity, listTypeName) {
                  if (!entity || !listTypeName) {
                      return false;
                  }
                  var request = $http({
                      method: "delete",
                      url: baseUrl + entity + "/" + listTypeName
                  });

                  return (request.then(handleSuccess, handleError));
              },
              deleteItems: function (listTypeName, entity, watchlistItems) {
                  var request,
                      url = baseUrl + entity;

                  if (!listTypeName || !watchlistItems || !watchlistItems.length) {
                      return false;
                  }

                  request = $http({
                      method: 'put',
                      url: url,
                      data: {
                          "@class": "gov.gtas.model.watchlist.json.WatchlistSpec",
                          "name": listTypeName,
                          "entity": entity,
                          "watchlistItems": watchlistItems
                      }
                  });

                  return (request.then(handleSuccess, handleError));
              },
              addItems: function (listTypeName, entity, data) {
                  var request,
                      url = baseUrl + entity

                  if (!listTypeName || !entity || !data) {
                      return false;
                  }

                  request = $http({
                      method: 'post',
                      url: url,
                      data: {
                          "@class": "gov.gtas.model.watchlist.json.WatchlistSpec",
                          "name": listTypeName,
                          "entity": entity,
                          "watchlistItems": data
                      }
                  });

                  return (request.then(handleSuccess, handleError));
              },

              addItem: function (listTypeName, entity, id, terms) {
                  var request,
                      url = baseUrl + entity,
                      action = 'Create';

                  if (!listTypeName || !entity || !terms) {
                      return false;
                  }

                  request = $http({
                      method: 'post',
                      url: url,
                      data: {
                          "@class": "gov.gtas.model.watchlist.json.WatchlistSpec",
                          "name": listTypeName,
                          "entity": entity,
                          "watchlistItems": [{
                              "id": id,
                              "action": action,
                              "terms": terms
                          }]
                      }
                  });

                  return (request.then(handleSuccess, handleError));
              },
              updateItem: function (listTypeName, entity, id, terms) {
                  var request,
                      url = baseUrl + entity,
                      action = 'Update';

                  if (!listTypeName || !entity || !id || !terms) {
                      return false;
                  }

                  request = $http({
                      method: 'put',
                      url: url,
                      data: {
                          "@class": "gov.gtas.model.watchlist.json.WatchlistSpec",
                          "name": listTypeName,
                          "entity": entity,
                          "watchlistItems": [{
                              "id": id,
                              "action": action,
                              "terms": terms
                          }]
                      }
                  });

                  return (request.then(handleSuccess, handleError));
              },
              createListType: function (listName, columns) {
                  //watchlist.types[listName] = {columns: columns, data: []};
                  //return watchlist.types;
              },
              getWatchlistCategories: function(){
                var request = $http({
                      method: "get",
                      url: baseUrl + 'watchlistCategories'
                  });

                  return (request.then(handleSuccess, handleError));
              }
          };
      })
      .service("jqueryQueryBuilderService", function ($http, $q) {
          var URLS = {
                  query: '/gtas/query/',
                  rule: '/gtas/udr/',
                  all: '/gtas/all_udr/',
                  copy: '/gtas/copy_udr/',
                  rule_cat: '/gtas/all_cat/'
              },
              handleError = function (response) {
                  if (!angular.isObject(response.data) || !response.data.message) {
                      return ($q.reject("An unknown error occurred."));
                  }
                  return ($q.reject(response.data.message));
              },
              handleSuccess = function (response) {
                  return (response.data);
              },
              services = {
                  loadRuleById: function (mode, ruleId) {
                      var request, baseUrl = URLS[mode];

                      if (!ruleId) {
                          return false;
                      }

                      request = $http({
                          method: "get",
                          url: [baseUrl, ruleId].join('')
                      });

                      return (request.then(handleSuccess, handleError));
                  },
                  copyRule: function (ruleId) {
                      var request, baseUrl = URLS.copy;

                      if (!ruleId) {
                          return false;
                      }

                      request = $http({
                          method: 'post',
                          url: [baseUrl, ruleId].join('')
                      });

                      return (request.then(handleSuccess, handleError));
                  },
                  delete: function (mode, ruleId) {
                      var request, baseUrl = URLS[mode];

                      if (!ruleId) {
                          return false;
                      }

                      request = $http({
                          method: 'delete',
                          url: [baseUrl, ruleId].join('')
                      });

                      return (request.then(handleSuccess, handleError));
                  },
                  save: function (mode, data) {
                      var method, request, url, baseUrl = URLS[mode];

                      if (data.id === null) {
                          method = 'post';
                          url = baseUrl;
                      } else {
                          method = 'put';
                          url = baseUrl + data.id;
                      }

                      request = $http({
                          method: method,
                          url: url,
                          data: data
                      });

                      return (request.then(handleSuccess, handleError));
                  },
                  getList: function (mode) {
                      var request, baseUrl = URLS[mode];

                      request = $http({
                          method: "get",
                          url: baseUrl
                      });

                      return (request.then(handleSuccess, handleError));
                  },
                  getRuleCat: function() {
                    var request, baseUrl = URLS.rule_cat;

                    request = $http({
                        method: "get",
                        url: baseUrl
                    });

                    return (request.then(handleSuccess, handleError));
                  }
              };

          // Return public API.
          return ({
              copyRule: services.copyRule,
              getList: services.getList,
              loadRuleById: services.loadRuleById,
              delete: services.delete,
              save: services.save,
              getRuleCat: services.getRuleCat
          });
      })
      .service("executeQueryService", function ($http, $q, spinnerService) {
          var serviceURLs = {
                  flights: '/gtas/query/queryFlights/',
                  passengers: '/gtas/query/queryPassengers/'
              },
              queryFlights = function (qbData) {
              spinnerService.show('html5spinner');
                  var dfd = $q.defer();

                  dfd.resolve($http({
                      method: 'post',
                      url: serviceURLs.flights,
                      data: qbData
                  }));

                  return dfd.promise;
              },
              queryPassengers = function (qbData) {
                  spinnerService.show('html5spinner');
                  var dfd = $q.defer();
                  dfd.resolve($http({
                      method: 'post',
                      url: serviceURLs.passengers,
                      data: qbData
                  }));
                  return dfd.promise;
              };
          // Return public API.
          return ({
              queryFlights: queryFlights,
              queryPassengers: queryPassengers
          });
      })
      .service("filterService", function ($http, $q) {
          var filterURLS = {
                  filter: '/gtas/filter/'
              },
              getFilter = function (userId) {
                  var dfd = $q.defer();
                  dfd.resolve($http({
                      method: 'get',
                      url: serviceURLs.filter + userId
                  }));
                  return dfd.promise;
              },
              setFilter = function (filter, userId) {

                  var dfd = $q.defer();
                  dfd.resolve($http({
                      method: 'post',
                      url: filterURLS.filter + userId,
                      data: filter
                  }));
                  return dfd.promise;
              },

              updateFilter = function (filter, userId) {
                  var dfd = $q.defer();
                  dfd.resolve($http({
                      method: 'put',
                      url: filterURLS.filter + userId,
                      data: filter
                  }));
                  return dfd.promise;
              };
          // Return public API.
          return ({
              getFilter: getFilter,
              setFilter: setFilter,
              updateFilter: updateFilter
          });
      })
      .service("passengersBasedOnUserFilter", function(userService, flightService, flightsModel, $q) {

          var today = new Date(),
              loadUser = function()
              {
                  return userService
                      .getUserData(  )                     // Request #1
                      .then( function( user ) {
                          if(user.data.filter!=null) {
                              if (user.data.filter.flightDirection)
                                  flightsModel.direction = user.data.filter.flightDirection;
                              if (typeof user.data.filter.etaStart  != undefined && user.data.filter.etaStart != null) {
                                  flightsModel.starteeDate = new Date();
                                  flightsModel.etaStart.setDate(today.getDate() + user.data.filter.etaStart);
                              }
                              if (typeof user.data.filter.etaEnd  != undefined && user.data.filter.etaEnd != null) {
                                  flightsModel.endDate = new Date();
                                  flightsModel.etaEnd.setDate(today.getDate() + user.data.filter.etaEnd);
                              }// Response Handler #1
                              if (user.data.filter.originAirports != null)
                                  flightsModel.origins = user.data.filter.originAirports;
                              if (user.data.filter.destinationAirports != null)
                                  flightsModel.destinations = user.data.filter.destinationAirports;
                          }
                          return flightsModel;
                      });
              },
              loadPassenger = function( flightsModel)
              {
                  var dfd = $q.defer();
                  dfd.resolve(flightService.getFlights(flightsModel));
                  return dfd.promise;
              },
              load = function ()
              {
                  return loadUser().then(loadPassenger );
              };
          // Return public API.
          return ({
              load: load
          });
      })
      //Tooltip service attempting to be generic
      .service("codeTooltipService", function($rootScope){

          function getCodeTooltipData(field, type){
            if(field != null && typeof field != 'undefined' && field != ''){
              if(type === 'country'){
                return getFullNameByCodeAndCodeList(field, $rootScope.countriesList);
              }
              else if(type === 'airport'){
                return getFullNameByCodeAndCodeList(field, $rootScope.airportsList);
              }
              else if(type === 'carrier'){
                return getFullNameByCodeAndCodeList(field, $rootScope.carriersList);
              }
              else if (type === 'passenger'){
                return getFullNameByCodeAndCodeList(field, $rootScope.passengerTypes);
              }
              else if (type === 'document'){
                return getFullNameByCodeAndCodeList(field,$rootScope.documentTypes)
              }
              else if (type === 'gender') {
                return getFullNameByCodeAndCodeList(field,$rootScope.genders)
              }
            }
          };

          //Used for countries/airports/carriers, pass in code + code list, return full name
          //APB - REFAC. Angular ng-repeat is causing this method to called repeatedly for all 
          //grid cells on each digest cycle, which can be dozens of repeat calls on every 
          //mouseover/out even for small datasets. Probably not a huge perf hit, but it's messy.
          function getFullNameByCodeAndCodeList(code, codeList){
            if (!codeList) return '';

            return codeList.find(x => x.id == code).name;   // allowing type coersion for now.
          };
        return({
          getCodeTooltipData:getCodeTooltipData
        });
      })
      .service('configService', function($rootScope, $http,$q){
        /*
         * Read Kibana settings from ./config/kibana_settings.json
         * 
         * By default kibana-dashboard is disabled. The landing page after successful login is flights page for now
         */
        function defaultHomePage(){
          
          var dfd = $q.defer();
          
              dfd.resolve($http({
                   method: 'get',
                   url: './config/kibana_settings.json'
               }));
               
             
              return dfd.promise;
              
        };
        
        return ({
          defaultHomePage : defaultHomePage
        });
      });
}());
