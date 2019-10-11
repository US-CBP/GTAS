/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
app.service("caseModel", [function () {
    'use strict';
    var defaultSort = [
        ],
        startDate = new Date(),
        endDate = new Date();
    endDate.setDate(endDate.getDate() + 1);
    startDate.setHours(startDate.getHours()-1);

    this.reset = function () {
        this.pageNumber = 1;
        this.pageSize = typeof this.pageSize != "undefined" ? this.pageSize : 10;
        this.origin = [];
        this.dest = [];
        this.etaStart = startDate;
        this.etaEnd = endDate;
        this.sort = defaultSort;
    };
    this.reset();
}]);
