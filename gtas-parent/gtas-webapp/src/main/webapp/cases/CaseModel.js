/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
app.service("caseModel",  function (caseDispositionService) {
    'use strict';
    let defaultSort = [
            {column: 'countDown', dir: 'asc'},

        ],
    displayStatusCheckBoxes =     {
        NEW: true,
        RE_OPENED: true,
        REVIEWED: false
    },
    ruleTypes =     {
        WATCHLIST: true,
        USER_RULE: true,
        GRAPH_RULE: true,
        MANUAL: true,
        EXTERNAL_RULE: true,
        PARTIAL_WATCHLIST: false
    },
    ruleCatFilter,
    startDate = new Date(),
    endDate = new Date();
    endDate.setDate(endDate.getDate() + 1);
    startDate.setMinutes(startDate.getMinutes()-15);
    ruleCatFilter = caseDispositionService.getDefaultCats();
    this.reset = function () {
        this.pageNumber = 1;
        this.pageSize = typeof this.pageSize != "undefined" ? this.pageSize : 10;
        this.origin = [];
        this.dest = [];
        this.etaStart = startDate;
        this.ruleCatFilter = ruleCatFilter;
        this.myRulesOnly = false;
        this.etaEnd = endDate;
        this.ruleTypes=ruleTypes;
        this.sort = defaultSort;
        this.displayStatusCheckBoxes = displayStatusCheckBoxes;
        this.withTimeLeft = true;
    };
    this.reset();
});
