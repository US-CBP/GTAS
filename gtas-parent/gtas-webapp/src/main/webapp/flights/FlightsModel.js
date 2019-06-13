/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.service("flightsModel", [function () {
    'use strict';
    var defaultSort = [
            {column: 'eta', dir: 'asc'},
            {column: 'listHitCount', dir: 'desc'},
            {column: 'ruleHitCount', dir: 'desc'},
            {column: 'graphHitCount', dir: 'desc'},
            {column: 'fuzzyHitCount', dir: 'desc'}
        ],
        startDate = new Date(),
        endDate = new Date();
    	
    	endDate.setDate(endDate.getDate() + 3);

    this.reset = function () {
        this.pageNumber = 1;
        this.pageSize = typeof this.pageSize != "undefined" ? this.pageSize : 25;
        this.flightNumber = '';
        this.origin = [];
        this.dest = [];
        this.direction = 'I';
        this.etaStart = startDate;
        this.etaEnd = endDate;
        this.sort = defaultSort;
    };

    this.reset();
}]);
