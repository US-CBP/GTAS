/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('CaseDispositionDetailCtrl',
        function ($scope, $http, $mdToast,
                  gridService,
                  spinnerService, caseDispositionService, newCases, caseService, $state, $mdSidenav) {

            $scope.caseItem;
            $scope.caseItemHits;
            $scope.caseItemHitComments;
            $scope.commentText='Initial Comment';
            $scope.options = {
                height: 150,
                toolbar: [
                    ['style', ['bold', 'italic', 'underline', 'clear']],
                    ['color', ['color']],
                    ['para', ['ul', 'ol', 'paragraph']],
                    ['height', ['height']]
                ]
            };
            $scope.hitDetailTrueHitFlag=false;

            if(typeof newCases.data !== undefined && newCases.data !== null) {
                $scope.caseItem = newCases.data.cases[0];
                $scope.caseItemHits = $scope.caseItem.hitsDispositions;

            }

            $scope.errorToast = function (error) {
                $mdToast.show($mdToast.simple()
                    .content(error)
                    .position('top right')
                    .hideDelay(4000)
                    .parent($scope.toastParent));
            };

            // var exporter = {
            //     'csv': function () {
            //         $scope.gridApi.exporter.csvExport('all', 'all');
            //     },
            //     'pdf': function () {
            //         $scope.gridApi.exporter.pdfExport('all', 'all');
            //     }
            // };

            caseService.getDispositionStatuses().then(function (response) {
                $scope.dispositionStatuses = response.data;
            });

            $scope.pageSize = 10;

            $scope.commentConfirm = function(){
                caseDispositionService.updateHitsDisposition();
            };

            caseService.getDispositionStatuses().then(function (response) {
                $scope.dispositionStatuses = response.data;
            });

            $scope.sideNav = function(id, position) {
                $scope.caseItemHitComments = $scope.caseItemHits[position];
                $mdSidenav(id).toggle();
            }
        })
}());
