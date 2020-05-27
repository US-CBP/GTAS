/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  app
      .service('notificationService', function($http,$q,$mdToast) {
        var GET_MESSAGE_ERRORS_URL ="/gtas/errorMessage";
        function handleError(response) {
            if (response.data.message === undefined) {
                return $q.reject("An unknown error occurred.");
            }
            return $q.reject(response.data.message);
        }

        function handleSuccess(response) {
            if(response.status !== 200) {
                var toastPosition = angular.element(document.getElementById('notificationForm'));
                $mdToast.show(
                    $mdToast.simple()
                        .content('Unable to send email notification')
                        .position('top right')
                        .hideDelay(4000)
                        .parent(toastPosition));
                return $q.reject(response.data.message);
            }

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
            notifyByEmail: function(to, paxId, note) {
              var request = $http({
                  method: 'post',
                  url: "/gtas/notify",
                  params: {
                    to: to,
                    paxId: paxId,
                    note: note
                  }
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

        var request = $http({
          method: "put",
          url: `${CODE_URL}${type}/restore`,
          data: code
        });
        return (request.then(handleSuccess, handleError));
      },

      restoreAllCodes: function(type) {

        var request = $http({
          method: "put",
          url: `${CODE_URL}${type}/restoreAll`
        });
        return (request.then(handleSuccess, handleError));
      },


        getCountryTooltips: function () {
            if (localStorage.getItem("countriesList") !== null) {
                const response = JSON.parse(localStorage.getItem("countriesList"));
                return Promise.resolve(response.map(x => ({id: x.iso3, name: getTidyName(x.name)})));
            } else {
                return this.getAllCodes('country').then(function (response) {
          if (Array.isArray(response)) {
                        localStorage.setItem("countriesList", JSON.stringify(response));
            return response.map(x => ({id: x.iso3, name: getTidyName(x.name)}));
          }
        }, handleError);
            }
      },

        getCarrierTooltips: function () {
            if (localStorage.getItem("carriersList") !== null) {
                return Promise.resolve(JSON.parse(localStorage.getItem("carriersList")));
            } else {
                return this.getAllCodes('carrier').then(function (response) {
            if (Array.isArray(response)) {
                        const list = response.map(x => ({id: x.iata, name: getTidyName(x.name)}));
                        localStorage.setItem("carrier", JSON.stringify(response));
                        return list;
                    } else return;

        }, handleError);
            }
      },

        getAirportTooltips: function () {
            if (localStorage.getItem("airportCache") !== null) {
                console.log(JSON.parse(localStorage.getItem("airportCache")));
                let list = JSON.parse(localStorage.getItem("airportCache"));
                return Promise.resolve(list.map(x => ({id: x.iata, name: x.name + ', ' + x.city + ', ' + x.country})));
            } else {
                return this.getAllCodes('airport').then(function (response) {
                    console.log(response);
                    localStorage.setItem("airportCache", JSON.stringify(response));
          if (Array.isArray(response)) {
            return response.map(x => ({id: x.iata, name: x.name + ', ' + x.city + ', ' + x.country}));
          }
        }, handleError);
            }
      },

        getAirportsWithCode: function () {
            if (localStorage.getItem("airportCache") !== null) {
                const list = JSON.parse(localStorage.getItem("airportCache"));
                return Promise.resolve(list.map(x => ({id: x.iata, name: x.name + '  (' + x.iata + ')'})));
            } else {
                return this.getAllCodes('airport').then(function (response) {
                    localStorage.setItem("airportCache", JSON.stringify(response));
          if (Array.isArray(response)) {
            return response.map(x => ({id: x.iata, name: x.name + '  (' + x.iata + ')'}));
                    } else return;

        }, handleError);
      }
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
    .service("gridOptionsLookupService", function ($translate, uiGridConstants) {
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
              enableSelectAll: false,
              exporterMenuPdf: false
            },
            exporterOptions = {
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
                enableSelectAll: true,
                exporterMenuPdf: false
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
                enableSelectAll: true,
                exporterMenuPdf: false
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
                enableSelectAll: false,
                exporterMenuPdf: false

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
                useExternalFiltering: true,
                exporterMenuPdf: false

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
                exporterMenuPdf: false,
              },
              query: $.extend({}, defaultOptions, {
                enableVerticalScrollbar: 2
              }),
              rule: $.extend({}, defaultOptions, {
                enableVerticalScrollbar: 2
              }),
              watchlist: defaultOptions,
              noteType: defaultOptions,
              zipLogs: {
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
                enableGridMenu: false,
                enableSelectAll: false
              }
            },
            columns = {
              audit: [
                {
                  name: "action",
                  displayName: $translate.instant('log.action'),
                  field: "actionType",
                  width: "10%",
                  sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                  }
                },
                {
                  name: "user",
                  displayName: $translate.instant('log.user'),
                  field: "user",
                  width: "15%"
                },
                {
                  name: "status",
                  displayName: $translate.instant('log.status'),
                  field: "status",
                  width: "10%"
                },
                {
                  name: "message",
                  displayName: $translate.instant('log.message'),
                  field: "message",
                  width: "20%"
                },
                {
                  name: "timestamp",
                  displayName: $translate.instant('log.timestamp'),
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
                  name: "id", field: "id", visible: false,
                  width: "0%", type: "string"
                },
                {
                  name: "iata",
                  displayName: $translate.instant('airport.iata'),
                  field: "iata",
                  width: "10%", type: "string"
                },
                {
                  name: "icao",
                  displayName: $translate.instant('airport.icao'),
                  field: "icao",
                  width: "10%", type: "string",
                 },
                {
                  name: "name",
                  displayName: $translate.instant('airport.name'),
                  field: "name",
                  width: "*",
                  type: "string"
                },
                {
                  name: "city",
                  displayName: $translate.instant('airport.city'),
                  field: "city",
                  width: "20%",
                  type: "string"
                },
                {
                  name: "country",
                  displayName: $translate.instant('airport.country'),
                  field: "country",
                  width: "10%",
                  type: "string"
                },
                {
                  name: "latitude",
                  displayName: $translate.instant('airport.latitude'),
                  field: "latitude",
                  width: "10%",
                  type: "string"
                },
                {
                  name: "longitude",
                  displayName: $translate.instant('airport.longitude'),
                  field: "longitude",
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
                  displayName: $translate.instant('carrier.iata'),
                  field: "iata",
                  width: "25%"
                },
                {
                  name: "name",
                  displayName: $translate.instant('carrier.name'),
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
                  displayName: $translate.instant('country.iso2'),
                  field: "iso2",
                  width: "14%"
                },
                {
                  name: "iso3",
                  displayName: $translate.instant('country.iso3'),
                  field: "iso3",
                  width: "15%"
                },
                {
                  name: "isoNumeric",
                  displayName: $translate.instant('country.isonumeric'),
                  field: "isoNumeric",
                  width: "15%"
                },
                {
                  name: "name",
                  displayName: $translate.instant('country.name'),
                  field: "name",
                  width: "55%"
                }
              ],
              ziplogs: [
                {
                  name: " ",
                  enableFiltering: false,
                  enableSorting: false,
                  width: "2%", cellTemplate: '<a class="full-width editLink" ng-click="grid.appScope.downloadZip(row.entity)"><i class="fa fa-download"></a>'
                },
                {
                  name: "fileName",
                  displayName: $translate.instant('files.filename'),
                  field: "fileName",
                  cellTemplate: "<div>{{COL_FIELD | uppercase}}</div>",
                  width: "43%"
                },
                {
                  name: "size",
                  displayName: $translate.instant('files.size'),
                  field: "size",
                  cellTemplate: "<div>{{grid.appScope.formatBytes(COL_FIELD)}}</div>",
                  width: "15%"
                },
                {
                  name: "creationDate",
                  displayName: $translate.instant('files.datecreated'),
                  cellTemplate: "<div>{{COL_FIELD | date:\'yyyy-MM-dd HH:mm:ss\'}}</div>",
                  field: "creationDate",
                  width: "20%"
                },
                {
                  name: "lastModified",
                  displayName: $translate.instant('files.lastmodified'),
                  field: "lastModified",
                  cellTemplate: "<div>{{COL_FIELD | date:\'yyyy-MM-dd HH:mm:ss\'}}</div>",
                  width: "20%"
                }
              ],
              error: [
                {
                  name: "Error ID",
                  field: "errorId",
                  displayName: $translate.instant('log.errorid'),
                  width: "15%",
                  sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                  }
                },
                {
                  name: "Error Code",
                  displayName: $translate.instant('log.errorcode'),
                  field: "errorCode",
                  width: "15%"
                },
                {
                  name: "DateTime",
                  displayName: $translate.instant('log.timestamp'),
                  field: "errorTimestamp",
                  width: "15%"
                },
                {
                  name: "Error Description",
                  displayName: $translate.instant('log.errordescription'),
                  field: "errorDescription",
                  width: "55%"
                }
              ],
              admin: [
                {
                  name: "active",
                  displayName: $translate.instant('user.active'),
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
                  displayName: $translate.instant('user.username'),
                  field: "userId",
                  width: "15%",
                  cellTemplate:
                    '<div><md-button class="md-primary md-button md-default-theme" ng-click="grid.appScope.lastSelectedUser(row.entity)" href="#/user/{{COL_FIELD}}">{{COL_FIELD}}</md-button></div>'
                },
                {
                  name: "firstName",
                  displayName: $translate.instant('user.firstname'),
                  field: "firstName",
                  width: "15%"
                },
                {
                  name: "lastName",
                  displayName: $translate.instant('user.lastname'),
                  field: "lastName",
                  width: "20%"
                },
                {
                  name: "roles",
                  displayName: $translate.instant('user.roles'),
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
                  displayName: $translate.instant('qry.name'),
                  field: "title",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "description",
                  displayName: $translate.instant('qry.desc'),
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
                  displayName: $translate.instant('qry.hits'),
                  field: "hitCount",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "title",
                  displayName: $translate.instant('qry.name'),
                  field: "title",
                  cellTemplate:
                    '<md-button aria-label="title" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "description",
                  displayName: $translate.instant('qry.desc'),
                  field: "description",
                  cellTemplate:
                    '<md-button aria-label="description" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "startDate",
                  displayName: $translate.instant('flight.startdate'),
                  field: "startDate",
                  cellTemplate:
                    '<md-button aria-label="start date" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "endDate",
                  displayName: $translate.instant('flight.enddate'),
                  field: "endDate",
                  cellTemplate:
                    '<md-button aria-label="end date" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "enabled",
                  displayName: $translate.instant('qry.enabled'),
                  field: "enabled",
                  cellTemplate:
                    '<md-button aria-label="enabled" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "overMaxHits",
                  displayName: $translate.instant('qry.overmaxhits'),
                  field: "overMaxHits",
                  cellTemplate:
                    '<md-button aria-label="overMaxHits" ng-click="grid.api.selection.selectRow(row.entity)">{{COL_FIELD}}</md-button>',
                  enableCellEdit: false,
                  enableColumnMenu: false
                },
                {
                  name: "modifiedOn",
                  displayName: $translate.instant('qry.modified'),
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
                    displayName: $translate.instant('doc.type'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "documentNumber",
                    name: "documentNumber",
                    displayName: $translate.instant('doc.number'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "categoryId",
                    name: "categoryId",
                    displayName: $translate.instant('doc.category'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{grid.appScope.categories[COL_FIELD]}}</md-button>',
                    type: "integer"
                  }
                ],
                PASSENGER: [
                  {
                    field: "dob",
                    name: "dob",
                    displayName: $translate.instant('doc.dob'),
                    width: 100,
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD | date:\'yyyy-MM-dd\'}}</md-button>',
                    type: "date"
                  },
                  {
                    field: "firstName",
                    name: "firstName",
                    displayName: $translate.instant('pass.firstname'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "lastName",
                    name: "lastName",
                    displayName: $translate.instant('pass.lastname'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{COL_FIELD}}</md-button>',
                    type: "string"
                  },
                  {
                    field: "categoryId",
                    name: "categoryId",
                    displayName: $translate.instant('doc.category'),
                    cellTemplate:
                      '<md-button class="md-primary"  ng-click="grid.appScope.editRecord(row.entity)" style="min-width: 0; margin: 0 auto; width: 100%;" >{{grid.appScope.categories[COL_FIELD]}}</md-button>',
                    type: "integer"
                  }
                ],
                CATEGORY: [
                  {
                    field: "id",
                    name: "id",
                    displayName: "ID",
                    type: "string"
                  },
                  {
                    field: "label",
                    name: "label",
                    displayName: "Name",
                    type: "string"
                  },
                  {
                    field: "description",
                    name: "description",
                    displayName: "Description",
                    type: "string"
                  },
                    {
                        field: "severity",
                        name: "severity",
                        displayName: "Severity",
                        type: "string"
                    }
                ],
                  NOTE_TYPE: [
                      {
                          field: "id",
                          name: "id",
                          displayName: "ID",
                          type: "string"
                      },
                      {
                          field: "noteType",
                          name: "noteType",
                          displayName: "Note Type",
                          type: "string"
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
              deleteItems: function (entity, watchlistItems) {
                  var request,
                      url = baseUrl + entity;

                  if (!watchlistItems || !watchlistItems.length) {
                      return false;
                  }

                  request = $http({
                      method: 'delete',
                      url: url + "/" + watchlistItems,
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
              },
              saveCategory: function (WatchlistCategory) {
                  let url = '/gtas/wlput/wlcat/';
                  const request = $http({
                      method: 'post',
                      url: url,
                      data: WatchlistCategory
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
              else if (type === 'dictionary') {
            	 return getFullNameByCodeAndCodeList(field,$rootScope.dictionary)
              }
            }
          };

          //Used for countries/airports/carriers, pass in code + code list, return full name
          //APB - REFAC. Angular ng-repeat is causing this method to called repeatedly for all
          //grid cells on each digest cycle, which can be dozens of repeat calls on every
          //mouseover/out even for small datasets. Probably not a huge perf hit, but it's messy.
          function getFullNameByCodeAndCodeList(code, codeList){
            if (!codeList) return '';

            if(codeList == $rootScope.dictionary) {
            	let result = codeList.find(x => x.id == code);
            	return result == undefined ? code:result.definition;
            }
            return (codeList.find(x => x.id == code) || {}).name;   // allowing type coersion for now.
          };
        return({
          getCodeTooltipData:getCodeTooltipData
        });
      })
      .service('fileDownloadService', function ($http, $q) {
        var LOGS_URL = "/gtas/api/logs/";

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
          getLogTypes: function() {
            var request = $http({
              method: "get",
              url: LOGS_URL,
              headers: 'Accept:application/json'});
            return request.then(handleSuccess, handleError);
          },

          getLogZipList: function(type) {
            var request = $http({
              method: "get",
              url: LOGS_URL + type,
              headers: 'Accept:application/json'});
            return request.then(handleSuccess, handleError);
          },

          getLogZip: function(type, file) {
            window.open(LOGS_URL + type + '/' + file, '_self');
          }

          };    // return codeService
        })
      .service('statisticService', function ($http, $q) {
          const STAT_URL = "/gtas/api/statistics";

          function getApplicationStatistics() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: STAT_URL
              }));
              return dfd.promise;
          }

          return ({
              getApplicationStatistics: getApplicationStatistics
          });
      })
      .service('configService', function ($http, $q) {

          const CONFIG_URL = "/gtas/api/config";

          function defaultHomePage() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: CONFIG_URL + "/dashboard/"
              }));
              return dfd.promise;
          }

          function neo4jProtocol() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: CONFIG_URL + "/neo4jProtocol/"
              }));
              return dfd.promise;
          }

          function kibanaProtocol() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: CONFIG_URL + "/kibanaProtocol/"
              }));
              return dfd.promise;
          }

          function neo4j() {
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'get',
                  url: CONFIG_URL + "/neo4j/"
              }));
              return dfd.promise;
          }

          function kibanaUrl() {
              var dfd = $q.defer();
              dfd.resolve($http({
                method: 'get',
                url: CONFIG_URL + "/kibanaUrl/"
            }));
            return dfd.promise;
          }

          function cypherUrl() {
            var response = $http({
              method: 'get',
              url: CONFIG_URL + "/cypherUrl"
          });
          return response.then(handleSuccess, handleError);
        }

        function cypherAuth() {
          var response = $http({
            method: 'get',
            url: CONFIG_URL + "/cypherAuth"
        });
        return response.then(handleSuccess, handleError);
      }

       function agencyName() {
        	var dfd = $q.defer();
            dfd.resolve($http({
              method: 'get',
              url: CONFIG_URL + "/agencyName"
          }));
          return dfd.promise;
        }

      function handleError(response) {
        return $q.reject(response.data.message);
      }

      function handleSuccess(response) {
        return response.data;
      }

      function enableEmailNotificationService() {
    	  var dfd = $q.defer();
          dfd.resolve($http({
            method: 'get',
            url: CONFIG_URL + "/enableEmailNotification"
        }));
        return dfd.promise;
      }

          return ({
              defaultHomePage: defaultHomePage,
              neo4j: neo4j,
              kibanaProtocol: kibanaProtocol,
              neo4jProtocol: neo4jProtocol,
              kibanaUrl: kibanaUrl,
              cypherUrl: cypherUrl,
              cypherAuth: cypherAuth,
              agencyName: agencyName,
              enableEmailNotificationService: enableEmailNotificationService
          });
        })
   .service("paxReportService", function($http, $q){

        	const PAX_DETAIL_REPORT_URL = "/gtas/paxdetailreport";

        	function getPaxDetailReport(paxId, flightId){
        		var dfd = $q.defer();
        		dfd.resolve($http({
        			method: 'get',
        			params: {
        				paxId: paxId,
        				flightId: flightId
        			},
        			url: PAX_DETAIL_REPORT_URL,
        			responseType: 'arraybuffer'
        
                   
        		}));
        		return dfd.promise;
        	}


        return({
        	
        	getPaxDetailReport:getPaxDetailReport
        });
      })
        .service("paxNotesService", function($http, $q){

        	const PAX_URL = "/gtas/passengers/passenger";

        	function saveNote(note){
        		var dfd = $q.defer();
        		dfd.resolve($http({
        			method: 'post',
        			data: note,
        			url: PAX_URL + "/note"
        		}));
        		return dfd.promise;
        	}

            function saveNoteType(noteType){
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    data: noteType,
                    url: "/gtas/api/noteType"
                }));
                return dfd.promise;
            }

        	function getEventNotes(paxId){
        		var dfd = $q.defer();
        		dfd.resolve($http({
        			method: 'get',
        			params: {
        				paxId: paxId,
        				historicalNotes: false
        			},
        			url: PAX_URL + "/notes"
        		}));
        		return dfd.promise;
        	}

        	function getHistoricalNotes(paxId){
        		var dfd = $q.defer();
        		dfd.resolve($http({
        			method: 'get',
        			params: {
        				paxId: paxId,
        				historicalNotes: true
        			},
        			url: PAX_URL + "/notes"
        		}));
        		return dfd.promise;
        	}

        	function getNoteTypes(){
        		var dfd = $q.defer();
        		dfd.resolve($http({
        			method: 'get',
        			url: PAX_URL + "/notetypes"			
        		}));
        		return dfd.promise;
        	}


        return({
          saveNote:saveNote,
          saveNoteType: saveNoteType,
          getEventNotes:getEventNotes,
          getHistoricalNotes:getHistoricalNotes,
          getNoteTypes:getNoteTypes
        });
      })
      .service("pendingHitDetailsService", function($http, $q){

          function createManualPvl(paxId, flightId, hitCategoryId, desc){
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'post',
                  params: {
                      paxId : paxId,
                      flightId : flightId,
                      hitCategoryId : hitCategoryId,
                      desc : desc
                  },
                  url: "/gtas/createmanualpvl"
              }));
              return dfd.promise
          }
          return({
              createManualPvl:createManualPvl
          });
      });
  }());
