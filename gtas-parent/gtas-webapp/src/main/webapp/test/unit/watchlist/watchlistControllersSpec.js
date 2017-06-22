/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
'use strict';
/* *************************************************** */
/* Jasmine test specifications for the WatchlistController */
/* *************************************************** */

describe('Watchlist controller:', function() {

  /* Test1: Invoke the controller on the default Document Tab */
  describe('Activation of Default(Document) Tab:', function(){
    var scope, ctrl, $httpBackend, gridOptionsLookupService, spinnerService;

    beforeEach(module('myApp'));
    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, _gridOptionsLookupService_, _spinnerService_) {
      spinnerService = _spinnerService_;
      $httpBackend = _$httpBackend_;
      scope = $rootScope.$new();
      gridOptionsLookupService = _gridOptionsLookupService_;
      ctrl = $controller('WatchListController', {$scope: scope});
    }));

    it('should set the default scope elements', function() {
      expect(scope.tabs.length).toBe(2);
      expect(scope.documentTypes).toEqual([
                                          {id: "P", label: "PASSPORT"},
                                          {id: "V", label: "VISA"}
                                      ]);
      expect(scope.watchlistGrid.enableRowHeaderSelection).toBe(true);
      expect(scope.watchlistGrid.enableSelectAll).toBe(true);
      expect(scope.watchlistGrid.multiSelect).toBe(true);
      expect(scope.watchlistGrid.columnDefs).toEqual(gridOptionsLookupService.getLookupColumnDefs('watchlist').DOCUMENT);
      expect(scope.activeTab).toBe('Document');
      //expect(scope.icon).toBe('file');
      expect(scope.rowSelected).toBe(null);
    });

    /* Test2: Invoke the controller for the Document tab */    
    it('should fetch two documents from xhr', function() {
      $httpBackend.expectGET('/gtas/wl/DOCUMENT/Document').
          respond({
              status:'SUCCESS',
              message:'Operation was successful',
              result:{
                  name:'Document',
                  entity:'DOCUMENT',
                  watchlistItems:
                   [
                     {
                         id:12345,
                         action:'',
                         terms:[
                                {field:'documentType', type:'string', value:'P'},
                                {field:'documentNumber', type:'string', value:'12345'},                             
                                ]
                     },
                     {
                         id:9921,
                         action:'',
                         terms:[
                                {field:'documentType', type:'string', value:'V'},
                                {field:'documentNumber', type:'string', value:'V7657'},                             
                                ]
                     }
                   ]
          }});
      spyOn(spinnerService, 'show');
      spyOn(spinnerService, 'hide');
      
      //set up a dummy gridApi
      scope.gridApi = {selection:{clearSelectedRows:function(){}}}
      spyOn(scope.gridApi.selection, 'clearSelectedRows');
      
      scope.getListItemsFor('Document');
      
      $httpBackend.flush();
      
      expect(scope.watchlistGrid.data).toEqual([{id:12345, documentType: 'P', documentNumber: '12345'}, {id:9921,documentType: 'V', documentNumber: 'V7657'}]);
      expect(spinnerService.show).toHaveBeenCalledWith('html5spinner');
      expect(spinnerService.hide).toHaveBeenCalledWith('html5spinner');
      expect(scope.gridApi.selection.clearSelectedRows).toHaveBeenCalled();
    });

    /* Test3: Handle Error from the back-end */    
    it('should invoke the global error handler', function() {
      $httpBackend.expectGET('/gtas/wl/DOCUMENT/Document').
          respond(500, {status:'FAILURE', message:'There was an error', result:{}});
      spyOn(spinnerService, 'show');
      spyOn(spinnerService, 'hide');
            
      scope.getListItemsFor('Document');
      
      $httpBackend.flush();
      
      expect(spinnerService.show).toHaveBeenCalledWith('html5spinner');
      //expect(spinnerService.hide).toHaveBeenCalledWith('html5spinner');
    });

  
  });

});
