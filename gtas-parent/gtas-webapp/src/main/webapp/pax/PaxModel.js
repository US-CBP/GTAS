/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.service("paxModel", [function () {
    'use strict';
    var defaultSort = [
            {column: 'onRuleHitList', dir: 'desc'},
            {column: 'onWatchList', dir: 'desc'},
            {column: 'eta', dir: 'desc'}
        ],
        startDate = new Date(),
        endDate = new Date();
    endDate.setDate(endDate.getDate() + 3);

    this.initial = function (params) {
        return {
            pageNumber: 1,
            pageSize: typeof this.model.pageSize != "undefined" ? this.model.pageSize : 10,
            lastName: '',
            flightNumber: params && params.flightNumber ? params.flightNumber : '',
            origin: params && params.origin ? params.origin : [],
            dest: params && params.destination ? params.destination : [],
            direction: params && params.direction ? params.direction : 'I',
            etaStart: params && params.etaStart ? new Date(params.etaStart.split('-').join(',')) : startDate,
            etaEnd: params && params.etaEnd ? new Date(params.etaEnd.split('-').join(',')) : endDate,
            sort: defaultSort
        };
    };

    this.model = {};

    this.reset = function (params) {
        angular.copy(this.initial(params), this.model);
    }

    this.reset();
}]);