/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('CaseDispositionManualCtrl',
        function ($scope, $http, $mdToast,
                  gridService, $timeout,
                  spinnerService, caseDispositionService, caseService, $state, $mdSidenav, AuthService,
                  passenger, ruleCats) {

            $scope.passenger = passenger.data;
            $scope.caseItem;
            $scope.caseItemHits;
            $scope.caseItemHitComments;
            $scope.commentText;
            $scope.hitDispStatus;
            $scope.caseDispStatus;
            $scope.caseItemHitsVo;
            $scope.ruleCatSet;
            $scope.ruleCat;
            $scope.ruleCats=ruleCats.data;
            $scope.caseCommentAttachment = null;
            $scope.caseDispositionStatuses = [];
            $scope.hitDispositionStatuses = [];
            $scope.dispStatus={
                hitStatusShow:true,
                caseStatusShow:true,
                allHitsClosed:true,
                caseStatusAdminView:false
            };
            $scope.dispStatus.constants={
                CLOSED: 'CLOSED',
                NEW: 'NEW',
                PENDINGCLOSURE: 'PENDING CLOSURE'
            };
            $scope.hitValidityStatuses=[
                {id: 1, name: 'Yes'},
                {id: 1, name: 'No'},
                {id: 1, name: 'N/A'}
            ];
            $scope.options = {
                height: 150,
                toolbar: [
                    ['style', ['bold', 'italic', 'underline', 'clear']],
                    ['color', ['color']],
                    ['para', ['ul', 'ol', 'paragraph']],
                    ['height', ['height']]
                ]
            };
            $scope.hitDetailTrueHitFlag = false;
            $scope.caseItemHitId=null;
            $scope.currentUser=null;

            AuthService.getCurrentUser().then(function (user) {
                $scope.currentUser = user;
                $scope.dispStatus.caseStatusAdminView = ($scope.currentUser.roles[0].roleDescription.toUpperCase()===$scope.ROLES.ADMIN.toUpperCase())? true: false;
            });

            $scope.changeState = function(){
                $scope.hitDetailTrueHitFlag = hitDetailTrueHitFlag;
            };

            $scope.errorToast = function (error) {
                $mdToast.show($mdToast.simple()
                    .content(error)
                    .position('bottom left')
                    .hideDelay(4000)
                    .parent($scope.toastParent));
            };

            $scope.successToast = function (msg) {
                $mdToast.show($mdToast.simple()
                    .content(msg)
                    .position('top right')
                    .hideDelay(4000)
                    .parent($scope.toastParent));
            };

            $scope.printCard = function(){
                var element = document.getElementById('dom-to-print');
                html2pdf(element);
            };

            $scope.populateDispStatuses = function(){
                caseDispositionService.getDispositionStatuses().then(function (response) {
                    $scope.caseDispositionStatuses = [];
                    angular.forEach(response.data, function(item){
                        $scope.caseDispositionStatuses.push(item);
                        if($scope.currentUser!=null
                            && !($scope.currentUser.roles[0].roleDescription.toUpperCase()===$scope.ROLES.ADMIN.toUpperCase())
                            && item.name.toUpperCase()===$scope.dispStatus.constants.CLOSED
                        ) {
                            $scope.caseDispositionStatuses.pop(item);
                        }
                    });
                });
            };

            $scope.populateDispStatuses();


            $scope.populateHitDispStatuses = function(){
                caseDispositionService.getHitDispositionStatuses().then(function (response) {
                    $scope.hitDispositionStatuses = [];
                    angular.forEach(response.data, function(item){
                        $scope.hitDispositionStatuses.push(item);
                        if($scope.currentUser!=null
                            && !($scope.currentUser.roles[0].roleDescription.toUpperCase()===$scope.ROLES.ADMIN.toUpperCase())
                            && item.name.toUpperCase()===$scope.dispStatus.constants.CLOSED
                        ) {
                            $scope.hitDispositionStatuses.pop(item);
                        }
                    });
                });
            };

            $scope.populateHitDispStatuses();

            $scope.caseConfirm = function() {
                //check whether all the hits are CLOSED or not
                angular.forEach($scope.caseItemHits, function (item) {
                    if ( ($scope.caseDispStatus != $scope.dispStatus.constants.CLOSED) ||
                        (item.status === $scope.dispStatus.constants.NEW) ||
                        (item.valid == null)) $scope.dispStatus.allHitsClosed = false;
                });
                if($scope.dispStatus.allHitsClosed){
                    spinnerService.show('html5spinner');
                    $scope.caseDispStatus = "Case" + $scope.caseDispStatus;
                    caseDispositionService.updateHitsDisposition($scope.caseItem.id, $scope.caseItem.flightId, $scope.caseItem.paxId,
                        $scope.caseItemHitId, $scope.commentText,
                        $scope.caseDispStatus,
                        $scope.hitDetailTrueHitFlag,null, null)
                        .then(function (aCase) {
                            $scope.caseItem = aCase.data;
                            $scope.caseItemHits = $scope.caseItem.hitsDispositions;
                            $scope.caseDispStatus = $scope.caseItem.status;
                            $scope.dispStatus.caseStatusShow = false;
                            spinnerService.hide('html5spinner');
                            $mdSidenav('comments').close();
                        });// END of caseDispositionService call
                }else{
                    var toastPosition = angular.element(document.getElementById('caseForm'));
                    $mdToast.show($mdToast.simple()
                        .content("Be Sure To Validate All Hits And Mark 'Closed' To Close This Case")
                        .position('top right')
                        .hideDelay(4000)
                        .parent(toastPosition));

                }
            };

            $scope.commentConfirm = function(){
                spinnerService.show('html5spinner');
                caseDispositionService.postManualCase($scope.passenger.flightId, $scope.passenger.paxId,
                    $scope.rule.ruleCat, $scope.commentText, null)
                    .then(function (aCase) {
                        spinnerService.hide('html5spinner');
                        var toastPosition = angular.element(document.getElementById('hitForm'));
                        $scope.successToast("Case Created");
                        //$timeout($state.transitionTo('caseDisposition'),5000);
                        $timeout($state.transitionTo('casedetail', { caseId: aCase.data.id }),5000);
                    });
            };

            $scope.closeSideNav = function(){
                $mdSidenav('comments').close();
            };

            $scope.sideNav = function(id, position) {
                $scope.caseItemHitComments = $scope.caseItemHits[position];
                $scope.caseItemHitId = $scope.caseItemHits[position].hitId;
                $scope.hitDetailTrueHitFlag = $scope.caseItemHits[position].valid;
                $scope.hitDispStatus = $scope.caseItemHits[position].status;
                $scope.dispStatus.hitStatusShow = ($scope.caseItemHits[position].status === $scope.dispStatus.constants.CLOSED)? false: true;
                $scope.populateDispStatuses();
                if(typeof $scope.hitDetailTrueHitFlag !== undefined && $scope.hitDetailTrueHitFlag !== null) {
                    if($scope.hitDetailTrueHitFlag == 'true') $scope.hitDetailTrueHitFlag = true;
                    else if($scope.hitDetailTrueHitFlag == 'false') $scope.hitDetailTrueHitFlag = false;
                }
                $mdSidenav(id).toggle();
            };


            //Angular Trix related event handlers
            $scope.trixInitialize = function(e, editor) {
                angular.element(editor.element).prop('contenteditable', false);
                angular.element(editor.element.toolbarElement).remove();
            };

            // Trix attachment logic

            var createStorageKey, host, uploadAttachment;

            $scope.trixAttachmentAdd = function(e) {
                var attachment;
                attachment = e.attachment;
                $scope.caseCommentAttachment = e.attachment.file;


                // if (attachment.file) {
                //     return uploadAttachment(attachment);
                // }
            };

            host = "/gtas/uploadattachments";

            uploadAttachment = function(attachment) {
                var file, form, key, xhr;
                file = attachment.file;
                key = createStorageKey(file);
                form = new FormData;
                form.append("key", key);
                form.append("Content-Type", file.type);
                form.append("file", file);
                xhr = new XMLHttpRequest;
                xhr.open("POST", host, true);
                xhr.upload.onprogress = function(event) {
                    var progress;
                    progress = event.loaded / event.total * 100;
                    return attachment.setUploadProgress(progress);
                };
                xhr.onload = function() {
                    var href, url;
                    if (xhr.status === 204) {
                        url = href = host + key;
                        return attachment.setAttributes({
                            url: url,
                            href: href
                        });
                    }
                };
                return xhr.send(form);
            };

            createStorageKey = function(file) {
                var date, day, time;
                date = new Date();
                day = date.toISOString().slice(0, 10);
                time = date.getTime();
                return "tmp/" + day + "/" + time + "-" + file.name;
            };


            // End Trix attachment logic



        })
}());
