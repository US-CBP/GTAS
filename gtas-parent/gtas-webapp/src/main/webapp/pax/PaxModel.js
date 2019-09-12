/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.service("paxModel", [function () {
  'use strict';
  var defaultSort = [
          {column: 'onWatchList', dir: 'desc'},
          {column: 'onRuleHitList', dir: 'desc'},
          {column: 'eta', dir: 'desc'}
      ],
      startDate = new Date(),
      endDate = new Date();
  
    endDate.setDate(endDate.getDate() + 1);

  this.initial = function (params) {
      return {
          pageNumber: 1,
          pageSize: typeof this.model.pageSize != "undefined" ? this.model.pageSize : 25,
          lastName: '',
          flightNumber: params && params.flightNumber ? params.flightNumber : '',
          origin: params && params.origin ? params.origin : [],
          dest: params && params.dest ? params.dest : [],
          direction: params && params.direction ? params.direction : 'I',
          etaStart: params && params.eta ? params.eta : startDate,
          etaEnd: params && params.etd ? params.etd : endDate,
          sort: defaultSort
      };
  };

  this.model = {};

  this.reset = function (params) {
      angular.copy(this.initial(params), this.model);
  }

  this.reset();
}]);
