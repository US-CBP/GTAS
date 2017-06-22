/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
'use strict';
/* *************************************************** */
/* Jasmine test specifications for the AdminController */
/* *************************************************** */

describe('Admin controller:', function() {

  /* Test1: Invoke the controller on the default Tab (i.e., tab 0) */
  describe('Activation of Default(User) Tab:', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('myApp'));
    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
      $httpBackend = _$httpBackend_;
      scope = $rootScope.$new();
      ctrl = $controller('AdminCtrl', {$scope: scope});
    }));

    it('should set the default scope elements - e.g., tab index to be 0', function() {
      expect(scope.showAuditDetails).toBe(false);
      expect(scope.selectedItem).toBeUndefined();
      expect(scope.selectedTabIndex).toBe(0);
      expect(scope.auditGrid.onRegisterApi).toBeDefined();
      expect(scope.errorGrid.onRegisterApi).toBeDefined();
    });

    it('should set audit grid options to allow selection', function() {
      expect(scope.auditGrid.data).toBeUndefined();
      expect(scope.auditGrid.enableRowSelection).toBe(true);
      expect(scope.auditGrid.enableRowHeaderSelection).toBe(false);
      expect(scope.auditGrid.enableFullRowSelection).toBe(true);
      expect(scope.auditGrid.multiSelect).toBe(false);
    });

    it('should set error grid options to allow selection', function() {
      expect(scope.errorGrid.data).toBeUndefined();
      expect(scope.errorGrid.enableRowSelection).toBe(true);
      expect(scope.errorGrid.enableRowHeaderSelection).toBe(false);
      expect(scope.errorGrid.enableFullRowSelection).toBe(true);
      expect(scope.errorGrid.multiSelect).toBe(false);
    });

    it('should create two users fetched from xhr', function() {
      $httpBackend.expectGET('/gtas/users/').
          respond([{userId: 'abcd', firstName: 'Nexus S'}, {userId: 'qrst', firstName: 'Motorola DROID'}]);

      $httpBackend.flush();
      expect(scope.userGrid.data).toEqual([{userId: 'abcd', firstName: 'Nexus S'},
                                   {userId: 'qrst', firstName: 'Motorola DROID'}]);
    });

  });

  /* Test2: Invoke the controller on the audit Tab (i.e., tab 2) */

  describe('Activation of Audit Tab:', function(){
    var scope, ctrl, $httpBackend, $document;

    beforeEach(module('myApp'));
    beforeEach(inject(function(_$httpBackend_, _$document_, $rootScope, $controller) {
      $httpBackend = _$httpBackend_;
      $document = _$document_;
      scope = $rootScope.$new();
      ctrl = $controller('AdminCtrl', {$scope: scope});
      spyOn(ctrl, 'successToast');
      spyOn(scope, 'errorToast');
    }));

    it('should have no audit log entries fetched from xhr', function() {
        var today = Date.now();
        var todayStr = moment(today).format('YYYY-MM-DD');
        $httpBackend.expectGET("/gtas/auditlog?startDate="+todayStr+"&endDate="+todayStr).
            respond([]);

        scope.selectedTabIndex = 2;
        $httpBackend.flush();
        expect(ctrl.successToast).toHaveBeenCalledWith('Filter conditions did not return any Audit Log Data.');
        expect(scope.auditGrid.data).toEqual([]);
      });

    it('should display an unknown error when backend does not supply a error response', function() {
        var today = Date.now();
        var todayStr = moment(today).format('YYYY-MM-DD');
        $httpBackend.expectGET("/gtas/auditlog?startDate="+todayStr+"&endDate="+todayStr).
            respond(400, []);

        scope.selectedTabIndex = 2;
        $httpBackend.flush();
        expect(scope.errorToast).toHaveBeenCalledWith('An unknown error occurred.');
        expect(scope.auditGrid.data).toBeUndefined();
      });

    it('should display the error from the backend', function() {
        var today = Date.now();
        var todayStr = moment(today).format('YYYY-MM-DD');
        $httpBackend.expectGET("/gtas/auditlog?startDate="+todayStr+"&endDate="+todayStr).
            respond(400, {message:'Mistakes were made.'});

        scope.selectedTabIndex = 2;
        $httpBackend.flush();
        expect(scope.errorToast).toHaveBeenCalledWith('Mistakes were made.');
        expect(scope.auditGrid.data).toBeUndefined();
      });

    it('should have two audit log entries fetched from xhr', function() {
      var today = Date.now();
      var todayStr = moment(today).format('YYYY-MM-DD');
      $httpBackend.expectGET("/gtas/auditlog?startDate="+todayStr+"&endDate="+todayStr).
          respond([{userId: 'bluej', firstName: 'John Blue'}, {userId: 'qrst', firstName: 'R Olivar'}]);

      scope.selectedTabIndex = 2;
      $httpBackend.flush();
      expect(ctrl.successToast).toHaveBeenCalledWith('Audit Log Data Loaded.');
      expect(scope.auditGrid.data).toEqual([{userId: 'bluej', firstName: 'John Blue'},
                                   {userId: 'qrst', firstName: 'R Olivar'}]);
    });

  });

});
