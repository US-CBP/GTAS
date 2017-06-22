/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
// Tests here

describe('Controller: RiskCriteriaController', function() {
    var scope, restService, $location;

    beforeEach(function() {
        var mockRestService = {};
        module('app', function($provide) {
            $provide.value('restService', mockRestService);
        });

        inject(function($q) {
            mockRestService.data = [{
                id: 1,
                summary: {
                    title: 'sample',
                    description: null,
                    startDate: '2015-01-01',
                    endDate: null,
                    enabled: true
                }
            }];

            mockRestService.getAll = function() {
                var defer = $q.defer();

                defer.resolve(this.data);

                return defer.promise;
            };

            mockRestService.save = function(obj) {
                var defer = $q.defer();

                obj.id = this.data.length;

                this.data.push(obj);
                defer.resolve(obj);

                return defer.promise;
            };
        });
    });

    beforeEach(inject(function($controller, $rootScope, _$location_, _restService_) {
        scope = $rootScope.$new();
        $location = _$location_;
        restService = _restService_;

        $controller('RiskCriteriaController', {$scope: scope, $location: $location, restService: restService });

        scope.$digest();
    }));

    it('should calculate today\'s date', function() {
        var today = Date.now();
        var todayString = [today.getFullYear(), ('0' + today.getMonth()).slice(-2), ('0' + today.getDate()).slice(-2)].join('-');
        expect(scope.today).toEqual(todayString);
    });

    it('should set startDate to today\'s date', function() {
        var today = Date.now();
        var todayString = [today.getFullYear(), ('0' + today.getMonth()).slice(-2), ('0' + today.getDate()).slice(-2)].join('-');
        expect(scope.startDate).toEqual(todayString);
    });
});
