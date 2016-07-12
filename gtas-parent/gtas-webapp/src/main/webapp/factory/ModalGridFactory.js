/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.factory('Modal', ['$compile', '$rootScope', function ($compile, $rootScope) {
    'use strict';
    return function () {
        var elm,
            modal = {
                open: function () {
                    var html = '<div class="modal" ng-style="modalStyle">{{modalStyle}}<div class="modal-dialog"><div class="modal-content"><div class="modal-header"><h4 style="font-size: 18px;" align="left" class="ng-binding"><span class="glyphicon glyphicon-user glyphiconFlightPax img-circle"></span> {{modalTitle}}</h4></div><div class="modal-body"><table style="width: 100%"><thead><tr><th>P</th><th>H</th><th>L</th><th>Carrier</th><th>Flight #</th><th>Direction</th><th>Origin</th><th>Country</th><th>ETD</th><th>Destination</th><th>Country</th><th>ETA</th></tr></thead><tbody><tr><td>{{selectedRow.totalPax}}</td><td>{{selectedRow.ruleHits}}</td><td>{{selectedRow.watchlistHits}}</td><td>{{selectedRow.Carrier}}</td><td>{{selectedRow.flightNumber}}</td><td>{{selectedRow.direction}}</td><td>{{selectedRow.origin}}</td><td>{{selectedRow.originCountry}}</td> <td>{{selectedRow.etd}}</td><td>{{selectedRow.destination}}</td><td>{{selectedRow.destinationCountry}}</td><td>{{selectedRow.etd}}</td></tr></tbody></table><div id="grid1" ui-grid="gridOptions" ui-grid-expandable ui-grid-pagination ui-grid-exporter ui-grid-resize-columns ui-grid-move-columns class="grid"></div></div><div class="modal-footer"><button id="buttonClose" class="btn btn-primary" ng-click="close()">Close</button></div></div></div></div>';
                    elm = angular.element(html);
                    angular.element(document.body).prepend(elm);

                    $rootScope.close = function () {
                        modal.close();
                    };
                    $rootScope.modalStyle = {"display": "block"};
                    $compile(elm)($rootScope);
                },
                close: function () {
                    if (elm) {
                        elm.remove();
                    }
                }
            };

        return modal;
    };
}]);
