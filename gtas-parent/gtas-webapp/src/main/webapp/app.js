/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
var app;
(function () {
    'use strict';
    var pageDefaults = {
            pageNumber: 1,
            pageSize: 10
        },
        appDependencies = [
            'ui.router',
            'ui.grid',
            'ui.grid.resizeColumns',
            'ui.grid.moveColumns',
            'ui.grid.pagination',
            'ui.grid.autoResize',
            'ui.grid.edit',
            'ui.grid.rowEdit',
            'ui.grid.cellNav',
            'ui.grid.selection',
            'ui.grid.importer',
            'ui.grid.exporter',
            'ui.grid.expandable',
            'ngMaterial',
            'ngMessages',
            'ngAria',
            'ngAnimate',
            'angularSpinners',
            'ngFileUpload',
            'spring-security-csrf-token-interceptor',
            'swxSessionStorage',
            'ngCookies',
            'pascalprecht.translate',
            'ngIdle',
            'chart.js',
            'summernote',
            'angularTrix'
        ],
        language = function ($translateProvider) {

    		$translateProvider.useUrlLoader('/gtas/messageBundle/');
    		$translateProvider.useCookieStorage();
    		$translateProvider.preferredLanguage('en');
    		$translateProvider.fallbackLanguage('en');
    		$translateProvider.useSanitizeValueStrategy('escape');

		},
		idleWatchConfig = function(IdleProvider, KeepaliveProvider, TitleProvider){
			TitleProvider.enabled(false);
			IdleProvider.interrupt('notDefault');
			IdleProvider.idle(540);
			IdleProvider.timeout(60);
			IdleProvider.keepalive(false);
		},
        localDateMomentFormat = function ($mdDateLocaleProvider) {
            // Example of a French localization.
            //$mdDateLocaleProvider.months = ['janvier', 'février', 'mars', ...];
            //$mdDateLocaleProvider.shortMonths = ['janv', 'févr', 'mars', ...];
            //$mdDateLocaleProvider.days = ['dimanche', 'lundi', 'mardi', ...];
            //$mdDateLocaleProvider.shortDays = ['Di', 'Lu', 'Ma', ...];
            // Can change week display to start on Monday.
            //$mdDateLocaleProvider.firstDayOfWeek = 1;
            // Optional.
            //$mdDateLocaleProvider.dates = [1, 2, 3, 4, 5, 6, ...];
            // Example uses moment.js to parse and format dates.
            $mdDateLocaleProvider.parseDate = function (dateString) {
                var manipulated, year, m;

                manipulated = dateString.split('-');
                year = manipulated.shift();
                manipulated.push(year);
                m = moment(manipulated.join('/'), 'L', true);

                return m.isValid() ? m.toDate() : new Date(NaN);
            };
            $mdDateLocaleProvider.formatDate = function (date) {
                return moment(date).format('YYYY-MM-DD');
            };
        },
        initialize = function ($rootScope, $location, AuthService, userService, USER_ROLES, $state, APP_CONSTANTS, $sessionStorage, checkUserRoleFactory, Idle, $mdDialog, configService, codeService) {
            $rootScope.ROLES = USER_ROLES;
            $rootScope.$on('$stateChangeStart',

                function (event, toState) {
            		Idle.watch();
                    var currentUser = $sessionStorage.get(APP_CONSTANTS.CURRENT_USER);
                    if (currentUser === undefined) {
                        $rootScope.$broadcast('unauthorizedEvent');
                    }
                    var roleCheck = checkUserRoleFactory.checkRoles(currentUser);
                    if (toState.authenticate && !roleCheck.hasRoles(toState.roles)) {
                        // User isn?t authenticated or authorized
                        window.location = APP_CONSTANTS.LOGIN_PAGE;
                        event.preventDefault();
                    }
                });

           $rootScope.currentlyLoggedInUser = $sessionStorage.get(APP_CONSTANTS.CURRENT_USER);

           $rootScope.searchBarContent = {
        		   content : ""
           };


           //  //For tooltips
           $rootScope.refreshCountryTooltips = function() {
              codeService.getCountryTooltips().then(function(result) {
                $rootScope.countriesList = result;
              });
            }
            $rootScope.refreshAirportTooltips = function() {
              codeService.getAirportTooltips().then(function(result) {
              $rootScope.airportsList = result;
             });
            }
          $rootScope.refreshCarrierTooltips = function() {
            codeService.getCarrierTooltips().then(function(result) {
              $rootScope.carriersList = result;
            });
          }

          $rootScope.refreshAirportTooltips();
          $rootScope.refreshCarrierTooltips();
          $rootScope.refreshCountryTooltips();

           //For tooltips
           $.getJSON('./data/passenger_types.json', function(data){
         	  $rootScope.passengerTypes = data;
            });
           
           //For tooltips
           $.getJSON('./data/doc_types.json', function(data){
         	  $rootScope.documentTypes = data;
            });
           
           //For tooltips
           $.getJSON('./data/genders.json', function(data){
         	  $rootScope.genders = data;
            });

           $rootScope.$on('$locationChangeSuccess', function(event){
        	   $rootScope.currentLocation.val = $location.path();
           });

           $rootScope.currentLocation ={
        		   val:$location.path()
           };

           $rootScope.searchBarQuery = function(){
        	   if($rootScope.currentLocation.val != '/adhocquery' && typeof $rootScope.searchBarContent != 'undefined' && $rootScope.searchBarContent.content != '' && $rootScope.searchBarContent.content.length > 1){
        		   window.location.href = APP_CONSTANTS.HOME_PAGE + "#/adhocquery";
        	   }
           };

           $rootScope.triggerIdle = function(){
        	   	//Prevent triggers pre-login
        	   	if(Idle.running()){
        	   		Idle.watch();
           		}
           };

           $rootScope.setSelectedTab = function(route){
             return route === $location.path();
           };

           $rootScope.$on('IdleStart', function(){
        	   $rootScope.showConfirm();
           });

           $rootScope.$on('IdleEnd', function(){
        	   //Keep session alive via small request
        	  userService.getUserData().then(console.log('No longer Idle'));
           });

           $rootScope.$on('IdleTimeout', function(){
        	  $mdDialog.hide();
        	  $rootScope.userTimedout = true;
        	  $rootScope.$broadcast('unauthorizedEvent');
        	  window.location.href = APP_CONSTANTS.LOGIN_PAGE +"?userTimeout";
           });

           $rootScope.showConfirm = function() {
        	   var confirm = $mdDialog.confirm({
	        	   parent: angular.element(document.body),
	               template:'<md-dialog ng-cloak>'+
	            	   '<form>'+
	            	   		'<md-dialog-content>'+
	            	   			'<div class="md-dialog-content" style="padding-top:0px;padding-bottom:0px;">'+
	            	   				'<h5 class="md-title"><strong>Idle Session Timeout Warning</strong></h5>'+
	            	   					'<div class="_md-dialog-content-body ng-scope"><p class="ng-binding">'+
	            	   						'Your session has been idle for too long. If you wish to maintain your session please click the button below.</p></div>'+
	            	   					'<span idle-countdown="countdown"> You will be logged out automatically in <strong style="font-size:xx-large;">{{countdown}}</strong> seconds. </span>'+
	            	   			'</div>'+
	            	   		'</md-dialog-content>'+
	            	   	'<md-dialog-actions layout="row">'+
        	      '<md-dialog-actions layout="row" class="layout-row">'+
        	      '<md-button ng-click="dialog.hide()">Continue Session</md-button>'+
        	    '</md-dialog-actions>'+
        	  '</form>'+
        	  '</md-dialog>'})

	           $mdDialog.show(confirm).then(function() {
	            	      Idle.watch();
	            	    }, function() {
	            	      return false;

	           });
       	  };

       	  $rootScope.hide = function(){
       		  $mdDialog.hide();
       	  };

        },
        router = function ($stateProvider, $urlRouterProvider, $httpProvider, USER_ROLES) {

            $stateProvider
                .state('login', {
                    url: '/login',
                    controller: 'LoginController',
                    templateUrl: 'login.html',
                    authenticate: false

                })
                .state('dash', { // temporary mapping to show sample dashboard data
                    url: '/dash',
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST],
                    authenticate: true,
                    views: {
                        '@': {
                            controller: 'DashboardController',
                            templateUrl: 'dashboard/dash.html'
                        }
                    }
                        ,
                        resolve: {
                            sampleData: function(){
                                return true;
                            },
                            ytdRuleHits: function(dashboardService){
                                return dashboardService.getYtdRulesCount();
                            },
                            ytdAirportStats: function (dashboardService) {
                                return dashboardService.getYtdAirportStats();
                            }
                        }

                })
                .state('dashboard', {
                    url: '/dashboard',
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST],
                    authenticate: true,
                    views: {
                        '@': {
                            controller: 'DashboardController',
                            templateUrl: 'dashboard/dashboard.html'
                        }
                    },
                    resolve: {
                        sampleData: function(){
                            return false;
                        },
                        ytdRuleHits: function(dashboardService){
                            return dashboardService.getYtdRulesCount();
                        },
                        ytdAirportStats: function (dashboardService) {
                            return dashboardService.getYtdAirportStats();
                        }
                    }
                })
                .state('admin', {
                    url: '/admin',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN],
                    views: {
                        "@": {
                            controller: 'AdminCtrl',
                            templateUrl: 'admin/admin.html'
                        }
                    },
                    resolve: {
                      settingsInfo: function(defaultSettingsService){
                        return defaultSettingsService.getAllSettings();
                      }
                    }
                })
                .state('modifyUser', {
                    url: '/user/:userId',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN],
                    views: {
                        '@': {
                            controller: 'UserCtrl',
                            templateUrl: 'admin/user.html'
                        }
                    }
                })
                .state('upload', {
                    url: '/upload',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN],
                    views: {
                        '@': {
                            controller: 'UploadCtrl',
                            templateUrl: 'admin/upload.html'
                        }
                    }
                })
                .state('flights', {
                    url: '/flights',
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.ONE_DAY_LOOKOUT],
                    authenticate: true,
                    views: {
                        '@': {
                            controller: 'FlightsController as flights',
                            templateUrl: 'flights/flights.html'
                        }
                    },
                    resolve: {
                        //TODO research why this resolve doesn't stick...
                        flights: function (passengersBasedOnUserFilter, flightsModel) {
                            return passengersBasedOnUserFilter.load();
                        },
                        flightSearchOptions: function(flightService){
                            return flightService.getFlightDirectionList();
                        }
                    }
                })
                .state('cases', {
                    url: '/cases',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'CasesCtrl',
                            templateUrl: 'cases/cases.html'
                        }
                    },
                    resolve: {
                        	newCases: function(caseService){
                        		return caseService.getAllCases();
                        	}
                    	}
                })
                .state('casemanual', {
                    url: '/casemanual/:flightId/:paxId',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES],
                    views: {
                        '@': {
                            controller: 'CaseDispositionManualCtrl',
                            templateUrl: 'cases/case.manual.html'
                        }
                    }
                    ,
                    resolve: {
                        passenger: function (paxDetailService, $stateParams) {
                            return paxDetailService.getPaxDetail($stateParams.paxId, $stateParams.flightId);
                        },
                        ruleCats: function(caseDispositionService){
                            return caseDispositionService.getRuleCats();
                        }
                    }
                })
                .state('casedetail', {
                    url: '/casedetail/:caseId',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.ONE_DAY_LOOKOUT],
                    views: {
                        '@': {
                            controller: 'CaseDispositionDetailCtrl',
                            templateUrl: 'cases/case.detail.html'
                        }
                    }
                    ,
                    resolve: {
                        newCases: function(caseDispositionService, $stateParams){
                            return caseDispositionService.getOneHitsDisposition($stateParams.caseId);
                        }
                    }
                })
                .state('caseDisposition', {
                    url: '/casedisposition',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES],
                    views: {
                        '@': {
                            controller: 'CaseDispositionCtrl',
                            templateUrl: 'cases/caseDisposition.html'
                        }

                    },
                    resolve: {
                        newCases: function(caseDispositionService, spinnerService){
                            spinnerService.show('html5spinner');
                            return caseDispositionService.getAllCases();
                        },
                        ruleCats: function(caseDispositionService){
                            return caseDispositionService.getRuleCats();
                        }
                    }
                })
                .state('caseDisposition.detail', {
                    url: '/:id',
                    views: {
                        'detail@caseDisposition': {
                            controller: 'CaseDispositionCtrl',
                            templateUrl: 'cases/case.detail.html'
                        }
                    }

                })
                .state('onedaylookout', {
                    url: '/onedaylookout',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.ONE_DAY_LOOKOUT],
                    views: {
                        '@': {
                            controller: 'OneDayLookoutCtrl',
                            templateUrl: 'onedaylookout/oneDayLookout.html'
                        }

                    },
                   
                   resolve: {
                        lookoutData: function(oneDayLookoutService, $stateParams){
                            return oneDayLookoutService.getOneDayLookout($stateParams.flightDate);
                        }
                    }
                })
                .state('adhocquery', {
                    url: '/adhocquery',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'AdhocQueryCtrl',
                            templateUrl: 'adhocquery/adhocquery.html'
                        }
                    },
                    resolve:{
                    	searchBarResults: function(adhocQueryService, $rootScope){
                    		var defaultSort = {
                    				column:'_score',
                    				dir:'desc'
                    		};

                    		if(typeof $rootScope.searchBarContent != 'undefined' && $rootScope.searchBarContent.content.length > 0){
                    			return adhocQueryService.getPassengers($rootScope.searchBarContent.content, 1, 10, defaultSort);
                    		} else{
                    			return adhocQueryService.getPassengers('', 1, 10, defaultSort);
                    		}
                    	}
                    }
                })
                .state('queryFlights', {
                    url: '/query/flights',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'FlightsController',
                            templateUrl: 'flights/query-flights.html'
                        }
                    },
                    resolve: {
                        flights: function (executeQueryService) {
                           //removed return due to it being an empty call to the service, returning an erroneous 400 Bad Request.
                           //Kept resolve rather than restructuring flights.html to not use flights entity as it was.
                        },
                        flightSearchOptions: function(flightService){
                            return flightService.getFlightDirectionList();
                        }
                    }
                })
                .state('paxAll', {
                    url: '/passengers',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        passengers: function (paxService, paxModel) {
                            return paxService.getPassengersBasedOnUser(paxModel);
                        }
                    }
                })
                .state('flightpax', {
                    url: '/flightpax/:id/:flightNumber/:origin/:destination/:direction/:eta/:etd',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        paxModel: function ($stateParams, paxModel) {
                        	$stateParams.dest = $stateParams.destination;
                            $stateParams.etaStart = $stateParams.eta;
                            $stateParams.etaEnd = $stateParams.etd;
                        	return {
                                model: paxModel.initial($stateParams),
                                reset: function () {
                                    this.model.lastName = '';
                                },
                                alldatamodel: function() {
                                  var mod = Object.assign({}, paxModel.initial($stateParams));
                                  mod.pageSize = 1000;
                                  return mod;
                                }
                            };
                        },
                        passengers: function (paxService, $stateParams, paxModel) {
                            //because of field/model not standard
                            $stateParams.dest = $stateParams.destination;
                            $stateParams.etaStart = $stateParams.eta;
                            $stateParams.etaEnd = $stateParams.etd;
                            return paxService.getPax($stateParams.id, paxModel.alldatamodel());
                        }
                    }
                })
                .state('queryPassengers', {
                    url: '/query/passengers',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        passengers: function (executeQueryService, $stateParams) {
                            var postData, query = JSON.parse(localStorage['query']);
                            postData = {
                                pageNumber: $stateParams.pageNumber || pageDefaults.pageNumber,
                                pageSize: $stateParams.pageSize || pageDefaults.pageSize,
                                query: query
                            };
                            return executeQueryService.queryPassengers(postData);
                        }
                    }
                })
                .state('detail', {
                    url: '/paxdetail/{paxId}/{flightId}',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.ONE_DAY_LOOKOUT],
                    views: {
                        '@': {
                            controller: 'PassengerDetailCtrl',
                            templateUrl: 'pax/pax.detail.html'
                        }
                    },
                    resolve: {
                        passenger: function (paxDetailService, $stateParams) {
                            return paxDetailService.getPaxDetail($stateParams.paxId, $stateParams.flightId);
                        },
                        user: function (userService) {
                            return userService.getUserData();
                        },
                        caseHistory : function (paxDetailService, $stateParams) {
                        	return paxDetailService.getPaxCaseHistory($stateParams.paxId);
                        },
                        ruleCats: function(caseDispositionService){
                            return caseDispositionService.getRuleCats();
                        },
                        ruleHits: function(paxService, $stateParams){
                        	return paxService.getRuleHitsByFlightAndPax($stateParams.paxId, $stateParams.flightId);
                        },
                        watchlistLinks: function(paxDetailService, $stateParams){
                          return paxDetailService.getPaxWatchlistLink($stateParams.paxId)
                        }
                    }
                })
                .state('build', {
                    url: '/build/:mode',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES],
                    views: {
                        '@': {
                            controller: 'BuildController',
                            controllerAs: 'build',
                            templateUrl: 'build/build.html'
                        }
                    }
                })
                .state('watchlists', {
                    url: '/watchlists',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_WATCHLIST],
                    views: {
                        '@': {
                            controller: 'WatchListController',
                            templateUrl: 'watchlists/watchlists.html'
                        }
                    }
                })
                .state('userlocation', {
                    url: '/userlocation',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST],
                    views: {
                        '@': {
                            controller: 'UserLocationController',
                            templateUrl: 'userlocation/userlocation.html'
                        }
                    },
                    resolve: {
                        userLocationData: function (userLocationService) {
                            return userLocationService.getAllUserLocations();
                        }
                    }
                })
                .state('userSettings', {
                    url: '/userSettings',
                    authenticate: true,
                    roles: [USER_ROLES.ONE_DAY_LOOKOUT, USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST],
                    views: {
                        '@': {
                            controller: 'UserSettingsController',
                            templateUrl: 'userSettings/userSettings.html'
                        }
                    },
                    resolve: {
                        user: function (userService) {
                            return userService.getUserData();
                        }
                    }
                })
                .state('seatsMap', {
                	url: '/seatsMap/{paxId}/{flightId}/{seat}',
                	authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST],
                    views: {
                    	'@' : {
                    		controller: 'SeatsMapController',
                    		templateUrl: 'seatsMap/seats.html'
                    	}
                    },
                    resolve: {
                    	seatData : function($stateParams, seatService){
                    		var data = seatService.getSeatsByFlightId($stateParams.flightId);
                    		return data;
                    	}
                    }
                });               

            $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
            $httpProvider.defaults.withCredentials = false;
        },

        NavCtrl = function ($scope, $http, APP_CONSTANTS,USER_ROLES, $sessionStorage, $rootScope, $cookies, notificationService, configService) {
            $http.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
            $http.defaults.xsrfCookieName = 'CSRF-TOKEN';
            $scope.errorList = [];
            $scope.hitCount = 0;
            $scope.neo4jUrl = "http://localhost:7474/browser/";
            var originatorEv;

            this.openMenu = function($mdOpenMenu, ev) {
                originatorEv = ev;
                $mdOpenMenu(ev);
                originatorEv = null;
            };
            var lookup = {
                admin: {name: ['admin', 'addUser', 'modifyUser']},
                dashboard: {name: ['dashboard']},
                flights: {name: ['flights']},
                passengers: {name: ['paxAll', 'flightpax']},
                queries: {mode: ['query']},
                adhocquery: {name: ['adhocquery']},
                risks: {mode: ['rule']},
                watchlists: {name: ['watchlists']},
                userSettings: {name: ['userSettings', 'setFilter']},
                userlocation: {name: ['userlocation']},
                upload: {name: ['upload']},
                cases: {name: ['cases']},
                onedaylookout: {name: ['onedaylookout']}
            };
            $scope.onRoute = function (key) {
                return (lookup[key].name && lookup[key].name.indexOf($scope.stateName) >= 0) || (lookup[key].mode && lookup[key].mode.indexOf($scope.mode) >= 0);
            };
            $scope.showNav = function () {
                return ['queryFlights', 'queryPassengers', 'detail'].indexOf($scope.stateName) === -1;
            };
            notificationService.getErrorData().then(function(value){
              $scope.errorList = value;
            }, function(reason){
              alert("Error Loading Notifications: " + reason);
            });

            notificationService.getWatchlistCount().then(function(value) {
               $scope.hitCount = value;
            });

            $scope.getHitCount = function() {
                    return $scope.hitCount;
            };

            configService.neo4j().then(function(value) {
               $scope.neo4jUrl = value.data;
            });

            $scope.getNeo4JUrl = function() {
                return $scope.neo4jUrl;
            };

            let oneDayLookoutUser = false;
            let user = $sessionStorage.get(APP_CONSTANTS.CURRENT_USER);
            user.roles.forEach(function (role) {
                if (role.roleDescription === USER_ROLES.ONE_DAY_LOOKOUT) {
                    oneDayLookoutUser = true;
                }
            });

            if (oneDayLookoutUser) {
                $scope.homePage = "onedaylookout";
            } else {
                configService.defaultHomePage().then(function success(response) {
                $scope.homePage = response.data;
                });
            }
            
            $scope.$on('stateChanged', function (e, state, toParams) {
                $scope.stateName = state.name;
                $scope.mode = toParams.mode;
                notificationService.getWatchlistCount().then(function(value) {
                    $scope.hitCount = value;
                });
            });

            $rootScope.$on('unauthorizedEvent', function () {
                $sessionStorage.remove(APP_CONSTANTS.CURRENT_USER);
                window.location = APP_CONSTANTS.LOGIN_PAGE;
            });

            $rootScope.$on('operationNotAllowedEvent', function () {
                $scope.logout();
                window.location = APP_CONSTANTS.LOGIN_PAGE;
            });

            $scope.logout = function () {

                $http({
                    method: 'POST',
                    url: '/gtas/logout'
                }).then(function (response) {
                    if (response.status === 200 || response.status === 403 || response.status === 405) {
                        var cookies = $cookies.getAll();
                        angular.forEach(cookies, function (v, k) {
                            $cookies.remove(k);
                        });
                        $sessionStorage.remove(APP_CONSTANTS.CURRENT_USER);
                        $rootScope.currentlyLoggedInUser = '';
                        $rootScope.authenticated = false;
                        window.location = APP_CONSTANTS.LOGIN_PAGE;
                    }
                });
            };
        };
    app = angular
        .module('myApp', appDependencies)
        .config(router)
        .config(localDateMomentFormat)
        .config(language)
        .config(idleWatchConfig)
        .constant('USER_ROLES', {
            ADMIN: 'Admin',
            VIEW_FLIGHT_PASSENGERS: 'View Passenger',
            MANAGE_QUERIES: 'Manage Queries',
            MANAGE_RULES: 'Manage Rules',
            MANAGE_WATCHLIST: 'Manage Watch List',
            ONE_DAY_LOOKOUT: 'One Day Lookout'
        })
        .constant('APP_CONSTANTS', {
            LOGIN_PAGE: 'login.html',
            HOME_PAGE: 'main.html',
            MAIN_PAGE: 'main.html#/'+ 'flights',
            ONE_DAY_LOOKOUT: 'main.html#/onedaylookout',
            CURRENT_USER: 'CurrentUser',
            LOCALE_COOKIE_KEY: 'myLocaleCookie',
            LOGIN_ERROR_MSG: ' Invalid User Name or Password. Please Try Again '
        })
        .run(initialize)
        .factory('sessionFactory', function () {
            var currentUser;

            return {
                setCurrentUser: function (user) {
                    currentUser = user;
                    currentUser.hasRole = function (requiredRole) {
                        var hasRole = false;

                        for (var i = 0; i < currentUser.roles.length; i++) {
                            if (currentUser.roles[i].toLowerCase() === requiredRole) {
                                hasRole = true;
                                break;
                            }
                        }
                        return hasRole;
                    };
                },
                getCurrentUser: function () {
                    return currentUser;
                }
            };
        })
        .factory('checkUserRoleFactory', function () {
            var currentUser;
            return {
                checkRole: function (user) {
                    currentUser = user;
                    currentUser.hasRole = function (requiredRole) {
                        var hasRole = false;

                        for (var i = 0; i < currentUser.roles.length; i++) {
                            if (String(currentUser.roles[i].roleDescription).toLowerCase() === requiredRole.toLowerCase()) {
                                hasRole = true;
                                break;
                            }
                        }
                        return hasRole;
                    };
                    return currentUser;
                },
                checkRoles: function (user) {
                    currentUser = user;
                    currentUser.hasRoles = function (requiredRoles) {
                        var hasRole = false;

                        for (var j = 0; j < requiredRoles.length; j++) {
                            for (var i = 0; i < currentUser.roles.length; i++) {
                                if (String(currentUser.roles[i].roleDescription).toLowerCase() === requiredRoles[j].toLowerCase()) {
                                    hasRole = true;
                                    break;
                                }
                            } // end of inner for loop
                            if (hasRole) {
                                break;
                            }
                        } // end of outer for loop
                        return hasRole;
                    };
                    return currentUser;
                }
            }
        })
        .directive('hasRole', function (sessionFactory, $sessionStorage, checkUserRoleFactory, APP_CONSTANTS) {
            return {
                restrict: 'A',
                link: function (scope, element, attributes) {
                    var currentUser = $sessionStorage.get(APP_CONSTANTS.CURRENT_USER);
                    if (currentUser != undefined || currentUser != null) {
                        var roleCheck = checkUserRoleFactory.checkRole(currentUser);
                        var hasRole = false;
                        var attr = String(attributes.hasRole);
                        attr = attr.split(',');
                        for (var i = 0; i < attr.length; i++) {
                            hasRole = false;
                            //console.log(attr[i].replace(/[^\w\s]/gi, '').trim());
                            if (roleCheck.hasRole(attr[i].replace(/[^\w\s]/gi, '').trim())) {
                                //console.log(attr[i].replace(/[^\w\s]/gi, '').trim() + ' role exists');
                                hasRole = true;
                                break;
                            } else {
                                hasRole = false;
                            }

                        } // end of for loop

                        if (!hasRole) {
                            element.remove();
                        }

                    }

                } // End of Function
            };
        })
        .directive('linearChart', function ($window) {
                return {
                    restrict: 'EA',
                    template: "<div class=\"container\"><div class=\"row\"><abc><lin></lin></abc></div></div>",
                    link: function (scope, elem, attrs) {

                        (function () {


                            d3.bullet = function () {
                                var orient = "left", // TODO top & bottom
                                    reverse = false,
                                    duration = 0,
                                    ranges = bulletRanges,
                                    markers = bulletMarkers,
                                    measures = bulletMeasures,
                                    width = 380,
                                    height = 30,
                                    tickFormat = null;

                                // For each small multiple…
                                function bullet(g) {
                                    g.each(function (d, i) {
                                        var rangez = ranges.call(this, d, i).slice().sort(d3.descending),
                                            markerz = markers.call(this, d, i).slice().sort(d3.descending),
                                            measurez = measures.call(this, d, i).slice().sort(d3.descending),
                                            g = d3.select(this);

                                        // Compute the new x-scale.
                                        var x1 = d3.scale.linear()
                                            .domain([0, Math.max(rangez[0], markerz[0], measurez[0])])
                                            .range(reverse ? [width, 0] : [0, width]);

                                        // Retrieve the old x-scale, if this is an update.
                                        var x0 = this.__chart__ || d3.scale.linear()
                                                .domain([0, Infinity])
                                                .range(x1.range());

                                        // Stash the new scale.
                                        this.__chart__ = x1;

                                        // Derive width-scales from the x-scales.
                                        var w0 = bulletWidth(x0),
                                            w1 = bulletWidth(x1);

                                        // Update the range rects.
                                        var range = g.selectAll("rect.range")
                                            .data(rangez);

                                        range.enter().append("rect")
                                            .attr("class", function (d, i) {
                                                return "range s" + i;
                                            })
                                            .attr("width", w0)
                                            .attr("height", height)
                                            .transition()
                                            .duration(2000)
                                            .ease("linear")
                                            .attr("x", reverse ? x0 : 0)
                                            .transition()
                                            .duration(duration)
                                            .transition()
                                            .duration(2000)
                                            .ease("linear")

                                            .attr("width", w1)
                                            .transition()
                                            .duration(2000)
                                            .ease("linear")

                                            .attr("x", reverse ? x1 : 0);

                                        range.transition()
                                            .duration(duration)
                                            .attr("x", reverse ? x1 : 0)
                                            .attr("width", w1)

                                            .attr("height", height);

                                        // Update the measure rects.
                                        var measure = g.selectAll("rect.measure")
                                            .data(measurez);

                                        measure.enter().append("rect")
                                            .attr("class", function (d, i) {
                                                return "measure s" + i;
                                            })
                                            .attr("width", w0)
                                            .attr("height", height / 3)
                                            .attr("x", reverse ? x0 : 0)
                                            .attr("y", height / 3)
                                            .transition()
                                            .duration(duration)
                                            .attr("width", w1)
                                            .attr("x", reverse ? x1 : 0);

                                        measure.transition()
                                            .duration(duration)
                                            .attr("width", w1)
                                            .attr("height", height / 3)
                                            .attr("x", reverse ? x1 : 0)
                                            .attr("y", height / 3);

                                        // Update the marker lines.
                                        var marker = g.selectAll("line.marker")
                                            .data(markerz);

                                        marker.enter().append("line")
                                            .attr("class", "marker")
                                            .attr("x1", x0)
                                            .attr("x2", x0)
                                            .attr("y1", height / 6)
                                            .attr("y2", height * 5 / 6)
                                            .transition()
                                            .duration(duration)

                                            .transition()
                                            .duration(2000)
                                            .ease("linear")

                                            .attr("x1", x1)
                                            .attr("x2", x1);

                                        marker.transition()
                                            .duration(duration)
                                            .attr("x1", x1)
                                            .attr("x2", x1)
                                            .attr("y1", height / 6)
                                            .attr("y2", height * 5 / 6);

                                        // Compute the tick format.
                                        var format = tickFormat || x1.tickFormat(8);

                                        // Update the tick groups.
                                        var tick = g.selectAll("g.tick")
                                            .data(x1.ticks(8), function (d) {
                                                return this.textContent || format(d);
                                            });

                                        // Initialize the ticks with the old scale, x0.
                                        var tickEnter = tick.enter().append("g")
                                            .attr("class", "tick")
                                            .attr("transform", bulletTranslate(x0))
                                            .style("opacity", 1e-6);

                                        tickEnter.append("line")
                                            .attr("y1", height)
                                            .attr("y2", height * 7 / 6);

                                        tickEnter.append("text")
                                            .attr("text-anchor", "middle")
                                            .attr("dy", "1em")
                                            .attr("y", height * 7 / 6)
                                            .text(format);

                                        // Transition the entering ticks to the new scale, x1.
                                        tickEnter.transition()
                                            .duration(duration)
                                            .attr("transform", bulletTranslate(x1))
                                            .style("opacity", 1);

                                        // Transition the updating ticks to the new scale, x1.
                                        var tickUpdate = tick.transition()
                                            .duration(duration)
                                            .attr("transform", bulletTranslate(x1))
                                            .style("opacity", 1);

                                        tickUpdate.select("line")
                                            .attr("y1", height)
                                            .attr("y2", height * 7 / 6);

                                        tickUpdate.select("text")
                                            .attr("y", height * 7 / 6);

                                        // Transition the exiting ticks to the new scale, x1.
                                        tick.exit().transition()
                                            .duration(duration)
                                            .attr("transform", bulletTranslate(x1))
                                            .style("opacity", 1e-6)
                                            .remove();
                                    });
                                    d3.timer.flush();
                                }

                                // left, right, top, bottom
                                bullet.orient = function (x) {
                                    if (!arguments.length) return orient;
                                    orient = x;
                                    reverse = orient == "right" || orient == "bottom";
                                    return bullet;
                                };

                                // ranges (bad, satisfactory, good)
                                bullet.ranges = function (x) {
                                    if (!arguments.length) return ranges;
                                    ranges = x;
                                    return bullet;
                                };

                                // markers (previous, goal)
                                bullet.markers = function (x) {
                                    if (!arguments.length) return markers;
                                    markers = x;
                                    return bullet;
                                };

                                // measures (actual, forecast)
                                bullet.measures = function (x) {
                                    if (!arguments.length) return measures;
                                    measures = x;
                                    return bullet;
                                };

                                bullet.width = function (x) {
                                    if (!arguments.length) return width;
                                    width = x;
                                    return bullet;
                                };

                                bullet.height = function (x) {
                                    if (!arguments.length) return height;
                                    height = x;
                                    return bullet;
                                };

                                bullet.tickFormat = function (x) {
                                    if (!arguments.length) return tickFormat;
                                    tickFormat = x;
                                    return bullet;
                                };

                                bullet.duration = function (x) {
                                    if (!arguments.length) return duration;
                                    duration = x;
                                    return bullet;
                                };

                                return bullet;
                            };

                            function bulletRanges(d) {
                                return d.ranges;
                            }

                            function bulletMarkers(d) {
                                return d.markers;
                            }

                            function bulletMeasures(d) {
                                return d.measures;
                            }

                            function bulletTranslate(x) {
                                return function (d) {
                                    return "translate(" + x(d) + ",0)";
                                };
                            }

                            function bulletWidth(x) {
                                var x0 = x(0);
                                return function (d) {
                                    return Math.abs(x(d) - x0);
                                };
                            }

                        })();


                        var margin = {top: 5, right: 40, bottom: 20, left: 120},
                            width = 960 - margin.left - margin.right,
                            height = 55 - margin.top - margin.bottom;

                        var chart = d3.bullet()
                            .width(width)
                            .height(height);

                        d3.json("data/bullets.json", function (error, data) {
                            if (error) throw error;

                            var svg = d3.select("abc").selectAll("lin")
                                .data(data)
                                .enter().append("svg")
                                .attr("class", "bullet")
                                .attr("width", width + margin.left + margin.right)
                                .attr("height", height + margin.top + margin.bottom)
                                .append("g")
                                .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
                                .call(chart);

                            var title = svg.append("g")
                                .style("text-anchor", "end")
                                .attr("transform", "translate(-6," + height / 2 + ")");

                            title.append("text")
                                .attr("class", "title")
                                .text(function (d) {
                                    return d.title;
                                });

                            title.append("text")
                                .attr("class", "subtitle")
                                .attr("dy", "1em")
                                .text(function (d) {
                                    return d.subtitle;
                                });


                        });

                    }
                };
            }
        ) // End of Linear Chart
        .directive('barChart', function ($window, dashboardService, $http) {
            return {
                restrict: 'EA',
                template: "<svg1></svg1>",
                link: function (scope, elem, attrs) {

                    var margin = {top: 10, right: 20, bottom: 20, left: 20},
                        width = 1600 - margin.left - margin.right,
                        height = 450 - margin.top - margin.bottom;

                    var x0 = d3.scale.ordinal()
                        .rangeRoundBands([0, width], .6);

                    var x1 = d3.scale.ordinal();

                    var yaxistext = '# of Messages Loaded';

                    var y = d3.scale
                        .linear()
                        .range([height, 0]);

                    var color = d3.scale.ordinal()
                        .range(["#3A9DC6", "#376180"]);

                    var xAxis = d3.svg.axis()
                        .scale(x0)
                        .orient("bottom");

                    var yAxis = d3.svg.axis()
                        .scale(y)
                        .orient("left")
                        .tickFormat(d3.format("3d"));


                    var svg = d3.select("svg1").append("svg").attr("class", 'col-sm-offset-0')
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .append("g")
                        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    //var fvg = d3.select("svg1").append("svg");

                    var today = moment();
                    var credentials = {
                        beforeDate: '',
                        startDate: '',
                        endDate: ''
                    };
                    credentials.startDate = today.format('YYYY-MM-DD');
                    credentials.endDate = today.format('YYYY-MM-DD');
                    credentials.beforeDate = today.format('YYYY-MM-DD');

                    var csvData = '';

                    var request = $http({
                        method: 'get',
                        url: '/gtas/getMessagesCount',
                        params: {
                            startDate: credentials.startDate,
                            endDate: credentials.endDate
                        }
                    });

                    request.then(JSON2CSV, handleError);

                    function handleError(response) {

                    }

                    function JSON2CSV(objArray) {
                        var array = typeof objArray != 'object' ? JSON.parse(JSON.stringify(objArray.data)) : objArray.data;
                        var str = '';
                        var line = '';
                        var dataString = "";
                        var csvContent = "";

                        displayData (objArray.data);
                        return str;
                    }

                    d3.csv("data/data.csv", function (error, data) {
                        // displayData(data);
                    });

                    function displayData (data) {
                        //if (error) throw error;

                        var ageNames = d3.keys(data[0]).filter(function (key) {
                            return key.toUpperCase() !== "STATE";
                        });

                        data.forEach(function (d) {
                            d.ages = ageNames.map(function (name) {
                                return {name: name, value: +d[name]};
                            });
                        });

                        x0.domain(data.map(function (d) {
                            return d.state;
                        }));
                        x1.domain(ageNames).rangeRoundBands([0, x0.rangeBand()]);
                        y.domain([0, d3.max(data, function (d) {
                            return d3.max(d.ages, function (d) {
                                return d.value;
                            });
                        })]);

                        svg.append("g")
                            .attr("class", "x axis")
                            .attr("transform", "translate(0," + height + ")")
                            .call(xAxis);

                        svg.append("g")

                            .attr("class", "y axis")
                            .call(yAxis)
                            .append("text")

                            .attr("transform", "rotate(-90)")

                            .attr("y", 6)

                            .attr("dy", ".71em")
                            .style("text-anchor", "end")
                            .text(yaxistext);


                        var state = svg.selectAll(".state")
                            .data(data)
                            .enter().append("g")

                            .attr("class", "state")

                            .attr("transform", function (d) {
                                return "translate(" + x0(d.state) + ",0)";
                            })

                            ;

                        state.transition()
                            .duration(2000)
                            .ease("circle")
                        ;

                        state.selectAll("rect")

                            .data(function (d) {
                                return d.ages;
                            })
                            .enter()

                            .append("rect")
                            .attr("width", x1.rangeBand())
                            .attr("x", function (d) {
                                return x1(d.name);
                            })
                            .attr("y", height)
                            .attr("height", 0)
                            .transition()
                            .duration(2000)
                            .ease("linear")
                            .attr("y", function (d) {
                                return y(d.value);
                            })
                            .attr("height", function (d) {
                                return height - y(d.value);
                            })
                            .style("fill", function (d) {
                                return color(d.name);
                            })
                        ;

                        var legend = svg.selectAll(".legend")
                            .data(ageNames.slice().reverse())
                            .enter().append("g")
                            .attr("class", "legend")
                            .attr("transform", function (d, i) {
                                return "translate(0," + i * 20 + ")";
                            });

                        legend.append("rect")
                            .attr("x", width - 18)
                            .attr("width", 18)
                            .attr("height", 18)
                            .style("fill", color);

                        legend.append("text")
                            .attr("x", width - 29)
                            .attr("y", 9)
                            .attr("dy", ".35em")
                            .style("text-anchor", "end")
                            .text(function (d) {
                                return d;
                            });

                    } // End of Display Data

                }
            }
        })// END of Bar Chart Directive

        // Begin Sample Data Bar Chart
        .directive('sampleBarChart', function ($window) {
            return {
                restrict: 'EA',
                template: "<svg1></svg1>",
                link: function (scope, elem, attrs) {

                    var margin = {top: 10, right: 20, bottom: 20, left: 20},
                        width = 1600 - margin.left - margin.right,
                        height = 450 - margin.top - margin.bottom;

                    var x0 = d3.scale.ordinal()
                        .rangeRoundBands([0, width], .6);

                    var x1 = d3.scale.ordinal();

                    var yaxistext = '# of Messages Loaded';

                    var y = d3.scale
                        .linear()
                        .range([height, 0]);

                    var color = d3.scale.ordinal()
                        .range(["#3A9DC6", "#376180"]);

                    var xAxis = d3.svg.axis()
                        .scale(x0)
                        .orient("bottom");

                    var yAxis = d3.svg.axis()
                        .scale(y)
                        .orient("left")
                        .tickFormat(d3.format("3d"));


                    var svg = d3.select("svg1").append("svg").attr("class", 'col-sm-offset-0')
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .append("g")
                        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    d3.csv("data/data.csv", function (error, data) {
                        if (error) throw error;

                        var ageNames = d3.keys(data[0]).filter(function (key) {
                            return key !== "State";
                        });

                        data.forEach(function (d) {
                            d.ages = ageNames.map(function (name) {
                                return {name: name, value: +d[name]};
                            });
                        });

                        x0.domain(data.map(function (d) {
                            return d.State;
                        }));
                        x1.domain(ageNames).rangeRoundBands([0, x0.rangeBand()]);
                        y.domain([0, d3.max(data, function (d) {
                            return d3.max(d.ages, function (d) {
                                return d.value;
                            });
                        })]);

                        svg.append("g")
                            .attr("class", "x axis")
                            .attr("transform", "translate(0," + height + ")")
                            .call(xAxis);

                        svg.append("g")

                            .attr("class", "y axis")
                            .call(yAxis)
                            .append("text")

                            .attr("transform", "rotate(-90)")

                            .attr("y", 6)

                            .attr("dy", ".71em")
                            .style("text-anchor", "end")
                            .text(yaxistext);


                        var state = svg.selectAll(".state")
                            .data(data)
                            .enter().append("g")

                            .attr("class", "state")

                            .attr("transform", function (d) {
                                return "translate(" + x0(d.State) + ",0)";
                            })

                            ;

                        state.transition()
                            .duration(2000)
                            .ease("circle")
                        ;

                        state.selectAll("rect")

                            .data(function (d) {
                                return d.ages;
                            })
                            .enter()

                            .append("rect")
                            .attr("width", x1.rangeBand())
                            .attr("x", function (d) {
                                return x1(d.name);
                            })
                            .attr("y", height)
                            .attr("height", 0)
                            .transition()
                            .duration(2000)
                            .ease("linear")
                            .attr("y", function (d) {
                                return y(d.value);
                            })
                            .attr("height", function (d) {
                                return height - y(d.value);
                            })
                            .style("fill", function (d) {
                                return color(d.name);
                            })
                        ;

                        var legend = svg.selectAll(".legend")
                            .data(ageNames.slice().reverse())
                            .enter().append("g")
                            .attr("class", "legend")
                            .attr("transform", function (d, i) {
                                return "translate(0," + i * 20 + ")";
                            });

                        legend.append("rect")
                            .attr("x", width - 18)
                            .attr("width", 18)
                            .attr("height", 18)
                            .style("fill", color);

                        legend.append("text")
                            .attr("x", width - 29)
                            .attr("y", 9)
                            .attr("dy", ".35em")
                            .style("text-anchor", "end")
                            .text(function (d) {
                                return d;
                            });

                    });

                }
            }
        })// END of Sample Data Bar Chart Directive


        // amcharts directive


    .directive('myElem',
        function () {
            return {
                restrict: 'E',
                replace:true,

                template: '<div id="chartdiv" style="min-width: 310px; height: 400px; margin: 0 auto"></div>',
                 link: function (scope, element, attrs) {

                }//end watch
            }
        })

        // END amchanrts directive

        //Countdown Timer Directive
        .directive('countdown', [
            'timeUtil',
            '$interval',
            function (timeUtil, $interval) {
                return {
                    restrict: 'EA',
                    replace: false,
                    transclude: true,
                    template: "<div ng-transclude>{{message}}</div>",
                    scope: { date: '@',
                             message: '@',
                             currenttime: '@',
                             showcountdownlabelflag: '@'
                            },
                    link: function (scope, element, attrs) {
                        var future, message, current, duration;
                        var messageStart = '<div>';
                        var messageWarn = '&nbsp;<span class="label label-warning">Warning</span>';
                        var messageWheelsUp = '&nbsp;<span class="label label-danger">WheelsUp</span>';
                        var messageEndDiv = '</div>';
                        var interval = 1000;


                        future = new Date(parseInt(scope.date));
                        if((typeof current === "undefined") ) {
                            current = new Date(parseInt(attrs.currenttime));
                        }else{
                            current = current - interval;
                        }

                            var timelyCheck = function () {
                                var diff;
                                current = moment(current).add(1,'minutes');
                                future = moment(future);
                                diff = Math.floor((future - current) / 1000);

                                if(Math.floor(( future - current ) / 86400000) < 2 ){

                                    if(scope.showcountdownlabelflag === "true"){
                                        if(Math.floor(( future - current ) / 86400000) < 0 ){
                                            current = moment(current).subtract(2,'minutes');
                                            diff = Math.floor((future - current) / 1000);
                                            message = messageStart + timeUtil.dhms(diff) + messageWheelsUp + messageEndDiv;
                                        }else {
                                            message = messageStart + timeUtil.dhms(diff) + messageWarn + messageEndDiv;
                                        }
                                    }else {
                                        message = messageStart+timeUtil.dhms(diff)+messageEndDiv;
                                    }
                                }else{
                                    message = messageStart+timeUtil.dhms(diff)+messageEndDiv;
                                }
                                return element.html(message);
                            };

                        timelyCheck();

                        scope.$watch('date', function(date){

                            future = new Date(parseInt(date));
                            if((typeof current === "undefined") ) {
                                current = new Date(parseInt(attrs.currenttime));
                            }else{
                                current = current - interval;
                            }

                            $interval(function () {
                                var diff;
                                current = moment(current).add(1,'minutes');
                                future = moment(future);
                                diff = Math.floor((future - current) / 1000);

                                if(Math.floor(( future - current ) / 86400000) < 2 ){

                                    if(scope.showcountdownlabelflag === "true"){
                                        if(Math.floor(( future - current ) / 86400000) < 0 ){
                                            current = moment(current).subtract(2,'minutes');
                                            diff = Math.floor((future - current) / 1000);
                                            message = messageStart + timeUtil.dhms(diff) + messageWheelsUp + messageEndDiv;
                                        }else {
                                            message = messageStart + timeUtil.dhms(diff) + messageWarn + messageEndDiv;
                                        }
                                    }else {
                                        message = messageStart+timeUtil.dhms(diff)+messageEndDiv;
                                    }
                                }else{
                                    message = messageStart+timeUtil.dhms(diff)+messageEndDiv;
                                }
                                return element.html(message);
                            },60000);

                        });

                    }
                };
            }
        ]).factory('timeUtil', [function () {
            return {
                dhms: function (t) {
                    var days, hours, minutes, seconds;
                    if(t<0){days = Math.ceil(t / 86400)}
                    else{days = Math.floor(t / 86400)}
                    t -= days * 86400;
                    hours = Math.floor(t / 3600) % 24;
                    t -= hours * 3600;
                    minutes = Math.floor(t / 60) % 60;
                    t -= minutes * 60;
                    seconds = t % 60;

                    return [
                        days + 'd',
                        hours + 'h',
                        minutes + 'm'
                       ].join(' ');
                }
            };
        }]).factory('timePastUtil', [function () {
        return {
            dhms: function (t) {
                var days, hours, minutes, seconds;
                days = Math.floor(t / 86400);
                t -= days * 86400;
                hours = Math.floor(t / 3600) % 24;
                t -= hours * 3600;
                minutes = Math.floor(t / 60) % 60;
                t -= minutes * 60;
                seconds = t % 60;

                return [
                    days + 'd',
                    hours + 'h',
                    minutes + 'm',
                    seconds + 's'
                ].join(' ');
            }
        };
    }])

        .controller('NavCtrl', NavCtrl)

        .config(function ($provide, $httpProvider) {
            $httpProvider.interceptors.push('httpSecurityInterceptor');
        })
        .factory('httpSecurityInterceptor', function ($q, $rootScope ) {
            return {
                request: function(config){
                	$rootScope.triggerIdle();
                	return config;
                },
                responseError: function (response) {
                    if ([401, 403].indexOf(response.status) >= 0) {
                        $rootScope.$broadcast('operationNotAllowedEvent');
                    }

                    return $q.reject(response);
                }
            };
        })
        .filter('nospace', function () {
            return function (value) {
                return (!value) ? '' : value.replace(/ /g, '');
            };
        })
    ;
}());
