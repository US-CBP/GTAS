/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
// Tests here

describe('RiskCriteriaController:', function() {
    var scope, restService, $location, $stateParams, $httpBackend, jqueryQueryBuilderWidget;

//    beforeEach(function() {
//        var mockRestService = {};
//        module('myApp', function($provide) {
//            $provide.value('restService', mockRestService);
//        });
//
//        inject(function($q) {
//            mockRestService.data = [{
//                id: 1,
//                summary: {
//                    title: 'sample',
//                    description: null,
//                    startDate: '2015-01-01',
//                    endDate: null,
//                    enabled: true
//                }
//            }];
//
//            mockRestService.getAll = function() {
//                var defer = $q.defer();
//
//                defer.resolve(this.data);
//
//                return defer.promise;
//            };
//
//            mockRestService.save = function(obj) {
//                var defer = $q.defer();
//
//                obj.id = this.data.length;
//
//                this.data.push(obj);
//                defer.resolve(obj);
//
//                return defer.promise;
//            };
//        });
//    });
    
    beforeEach(module('myApp'));

    beforeEach(inject(function(_$httpBackend_, $controller, $rootScope, _$location_, _$stateParams_) {
        $httpBackend = _$httpBackend_;
        scope = $rootScope.$new();
        $location = _$location_;
        //restService = _restService_;
        $stateParams = _$stateParams_;
        $stateParams.mode = 'rule';
        $controller('BuildController', {$scope: scope});//, $location: $location, restService: restService});

        //scope.$digest();
    }));

    it('should be initialized with Risk Criteria defaults', function() {
      expect(scope.query.title).toBe('');
      expect(scope.query.description).toBe(null);
      expect(scope.rule).toBeDefined();
      
      expect(scope.calendarOptions).toEqual({format: 'yyyy-mm-dd', autoClose: true});
      expect(scope.formats).toEqual(["YYYY-MM-DD"]);
      expect(scope.ruleId).toBe(null);
      expect(scope.saving).toBe(false);

      expect(scope.qbGrid).toBeDefined();
      expect(scope.qbGrid.columnDefs).toBeDefined();
      expect(scope.qbGrid.onRegisterApi).toBeDefined();
      
      expect(scope.qbGrid.enableRowHeaderSelection).toBe(true);
      expect(scope.qbGrid.enableSelectAll).toBe(false);
      expect(scope.qbGrid.multiSelect).toBe(false);
      expect(scope.qbGrid.exporterCsvFilename).toBe('rule.csv');
      expect(scope.qbGrid.exporterPdfHeader).toEqual({text: 'rule', style: 'headerStyle'});
    });

});
