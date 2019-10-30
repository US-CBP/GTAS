/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

(function () {
    'use strict';
    app.controller('EmailNotificationModalCtrl',
        function ($scope, userService, $uibModalInstance, notificationService) {

            $scope.noneGtasUser = {};
            $scope.notesForEmailNotification = '';
            var setUserData = function (data) {
                $scope.allUsers = data;
                $scope.allUsers.forEach(function (user) {
                    user.selected = false;
                })
            };
            userService.getAllUsers().then(setUserData);

            $scope.submit = function () {
                $scope.selectedEmails = [];
                $scope.allUsers.forEach(function (user) {
                    if (user.selected && user.email != null) {
                        $scope.selectedEmails.push(user.email);
                    }
                });
                if ($scope.noneGtasUser.email != null) {
                    $scope.selectedEmails.push($scope.noneGtasUser.email);
                }

                if ($scope.selectedEmails.length > 0) {//if selectedEmail is not empty send notification
                   notificationService.notifyByEmail($scope.selectedEmails,
                        $scope.paxId,
                        $scope.notesForEmailNotification);
                }

                $uibModalInstance.dismiss('cancel');

            }

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };

            $scope.toggleUser = function () {
                angular.forEach($scope.allUsers, function (user) {
                    user.selected = event.target.checked;
                });
            }

        });
}());