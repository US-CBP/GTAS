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
            'angularTrix',
            'ui.bootstrap.datetimepicker',
            'datetime',
            'ui.bootstrap'
        ],
        language = function ($translateProvider) {  

        var pref = window.navigator.language.split('-')[0];

        $translateProvider.useUrlLoader('/gtas/messageBundle/');
    		$translateProvider.useCookieStorage();
    		$translateProvider.preferredLanguage(pref);
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
            		//Idle.watch();
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
           
    	   if ($rootScope.currentlyLoggedInUser === undefined || $rootScope.currentlyLoggedInUser == null)
       		{
    		   		console.log('## User is not logged in ##'); 
       	  			if(Idle.running())
       	  			{
       	  					Idle.unwatch();
       	  				    console.log('## Idle Watch stopped. ##'); 
       	  			}
       		}
          else
       	   {
       	   		if(!Idle.running())
       	   		{
       		      Idle.watch();
       		      console.log('## Started Idle Watch ##'); 
       	   		}
       	   	}

           $rootScope.searchBarContent = {
        		   content : ""
           };


           //  //For tooltips
           $rootScope.refreshCountryTooltips = function() {
               localStorage.removeItem("countriesList");
              codeService.getCountryTooltips().then(function(result) {
                $rootScope.countriesList = result;
              });
            };
            $rootScope.refreshAirportTooltips = function() {
                localStorage.removeItem("airportCache");
                codeService.getAirportTooltips().then(function(result) {
              $rootScope.airportsList = result;
             });
            };
          $rootScope.refreshCarrierTooltips = function() {
              localStorage.removeItem("carriersList");
              codeService.getCarrierTooltips().then(function(result) {
              $rootScope.carriersList = result;
            });
          };

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
			
			//For tooltips
			$.getJSON('./data/dictionary.json', function(data){
				$rootScope.dictionary = data;
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
        	   /*	if(Idle.running()){
        	   		Idle.watch();
           		}*/
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
                .state('reset', {
                    url: '/reset',
                    controller: 'ResetController',
                    templateUrl: 'reset.html',
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_CASES, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.ONE_DAY_LOOKOUT],
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
                        },
                        user: function (userService) {
                            return userService.getUserData();
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_CASES],
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_CASES, USER_ROLES.ONE_DAY_LOOKOUT],
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGE_CASES],
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
                .state('aboutgtas', {
                    url: '/aboutgtas',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.ONE_DAY_LOOKOUT, USER_ROLES.MANAGE_HITS],
                    views: {
                        '@': {
                            controller: 'AboutGtasCtr',
                            templateUrl: 'help/aboutgtas.html'
                         }
                        },
                
                   resolve: {
                     appVersionNumber: function(aboutGtasService){
                         return aboutGtasService.getApplicationVersionNumber();
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
                        },
                        user: function (userService) {
                            return userService.getUserData();
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
                    url: '/flightpax/:id/',
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
                          return paxService.getPax($stateParams.id, paxModel.alldatamodel());
                        },
                        user: function (userService) {
                            return userService.getUserData();
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
                        },
                        user: function (userService) {
                            return userService.getUserData();
                        }
                    }
                })
                .state('detail', {
                    url: '/paxdetail/{paxId}/{flightId}',
                    authenticate: true,
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
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
                        ruleCats: function(caseDispositionService){
                            return caseDispositionService.getRuleCats();
                        },
                        ruleHits: function(paxService, $stateParams){
                        	return paxService.getRuleHitsByFlightAndPax($stateParams.paxId, $stateParams.flightId);
                        },
                        watchlistLinks: function(paxDetailService, $stateParams){
                          return paxDetailService.getPaxWatchlistLink($stateParams.paxId)
                        },
                        disableLinks: function() {
                            return false;
                        },
                        eventNotes: function(paxNotesService, $stateParams){
                        	return paxNotesService.getEventNotes($stateParams.paxId);
                        },
                        noteTypesList: function(paxNotesService){
                        	return paxNotesService.getNoteTypes();
                        },
                        $uibModalInstance: function() {
                            
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.ONE_DAY_LOOKOUT, USER_ROLES.MANAGE_CASES],
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
                    roles: [USER_ROLES.ONE_DAY_LOOKOUT, USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS, USER_ROLES.MANAGE_QUERIES, USER_ROLES.MANAGE_RULES, USER_ROLES.MANAGE_WATCHLIST, USER_ROLES.MANAGE_CASES],
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
                    roles: [USER_ROLES.ADMIN, USER_ROLES.VIEW_FLIGHT_PASSENGERS],
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
            $scope.agencyName = '';
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
                onedaylookout: {name: ['onedaylookout']},
                aboutgtas: {name: ['aboutgtas']}
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

            $scope.getHitCount = function() {
                    return $scope.hitCount;
            };

            configService.agencyName().then(function(value) {
                $scope.agencyName = value.data;
             });
            
            $scope.getAgencyName = function() {
            	return $scope.agencyName;
            };

            configService.neo4jProtocol().then(function(value) {
                $scope.neo4jProtocol = value.data;
            });

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
            $scope.userHasRole = function(roleId){
            	var hasRole = false;
            	$.each(user.roles, function(index, value) {
            		if (value.roleId === roleId) {
            			hasRole = true;
                        }
            		});
            	return hasRole;
            }
        };
    app = angular
        .module('myApp', appDependencies)
        .config(router)
        .config(localDateMomentFormat)

        .config(language)    // ngx
        .config(idleWatchConfig)
        .constant('USER_ROLES', {
            ADMIN: 'Admin',
            VIEW_FLIGHT_PASSENGERS: 'View Passenger',
            MANAGE_QUERIES: 'Manage Queries',
            MANAGE_RULES: 'Manage Rules',
            MANAGE_WATCHLIST: 'Manage Watch List',
            ONE_DAY_LOOKOUT: 'One Day Lookout',
            MANAGE_HITS: 'Manage Hits',
            MANAGE_CASES: 'Manage Cases'
        })
        .constant('APP_CONSTANTS', {
            LOGIN_PAGE: 'login.html',
            RESET_PAGE: 'reset.html',
            HOME_PAGE: 'main.html',
            MAIN_PAGE: 'main.html#/'+ 'flights',
            ONE_DAY_LOOKOUT: 'main.html#/onedaylookout',
            CURRENT_USER: 'CurrentUser',
            LOCALE_COOKIE_KEY: 'myLocaleCookie',  // ngx
            LOGIN_ERROR_MSG: ' Invalid User Name or Password. Please Try Again ',
            LOGIN_ERROR_MAX_ATTEMPTS: 'Too many failed attempts to log in, please check your email'

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
                	//$rootScope.triggerIdle();
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
