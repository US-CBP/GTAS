/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.service("flightsModel", [function () {
    'use strict';
    var defaultSort = [
            {column: 'ruleHitCount', dir: 'desc'},
            {column: 'listHitCount', dir: 'desc'},
            {column: 'eta', dir: 'desc'}
        ],
        startDate = new Date(),
        endDate = new Date();
    	
    	endDate.setDate(endDate.getDate() + 3);

    this.reset = function () {
        this.pageNumber = 1;
        this.pageSize = typeof this.pageSize != "undefined" ? this.pageSize : 10;
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