/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('CaseDispositionDetailCtrl',
        function ($scope, $http, $mdToast,
                  gridService, $mdDialog,
                  spinnerService, caseDispositionService, newCases, caseService, $state, $mdSidenav, AuthService) {
    	
            $scope.caseItem;
            $scope.caseItemHits;
            $scope.caseItemHitComments;
            $scope.commentText;
            $scope.hitDispStatus;
            $scope.caseDispStatus;
            $scope.caseItemHitsVo;
            $scope.ruleCatSet;
            $scope.ruleCat;
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
                PENDINGCLOSURE: 'PENDING CLOSURE',
                REOPEN: 'RE-OPEN'
            };
            $scope.hitValidityStatuses=[
            {id: 1, name: 'Yes'},
            {id: 2, name: 'No'},
            {id: 3, name: 'N/A'}
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

            if(typeof newCases.data !== undefined && newCases.data !== null) {
                $scope.caseItem = newCases.data.cases[0];
                $scope.caseItemHits = $scope.caseItem.hitsDispositions;
                $scope.caseItemHitsVo = $scope.caseItem.hitsDispositionVos;
                $scope.caseDispStatus = $scope.caseItem.status;
                //$scope.dispStatus.caseStatusShow = ($scope.caseItem.status === $scope.dispStatus.constants.CLOSED)? false: true;
                $scope.dispStatus.caseStatusShow = true; // put in this flow thru' to allow switching between CLOSED and other states
                if($scope.caseItem.oneDayLookoutFlag == true)
                {
                	$scope.isAddOLKButtonDisabled = true;
           	    	$scope.isRemoveOLKButtonDisabled = false;
                }
                else 
                {
                	$scope.isAddOLKButtonDisabled = false;
           	    	$scope.isRemoveOLKButtonDisabled = true;
                }
                	
            }

            $scope.errorToast = function (error) {
                $mdToast.show($mdToast.simple()
                    .content(error)
                    .position('bottom left')
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
                        if( !($scope.caseItem.status.toUpperCase()===$scope.dispStatus.constants.CLOSED)
                            && !($scope.caseItem.status.toUpperCase()===$scope.dispStatus.constants.REOPEN)
                            && (item.name.toUpperCase()===$scope.dispStatus.constants.REOPEN) ){
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
                        if( !($scope.caseItem.status.toUpperCase()===$scope.dispStatus.constants.CLOSED)
                            && (item.name.toUpperCase()===$scope.dispStatus.constants.REOPEN) ){
                            $scope.hitDispositionStatuses.pop(item);
                        }
                    });
                });
            };

            $scope.populateHitDispStatuses();

            $scope.caseConfirm = function() {
                $scope.dispStatus.allHitsClosed = true;
                //check whether all the hits are CLOSED or not
                angular.forEach($scope.caseItemHits, function (item) {
                    if ( ($scope.caseDispStatus === $scope.dispStatus.constants.CLOSED) &&
                        ( (item.valid === null)  ||
                        (typeof(item.valid) === "undefined"))
                        // ||
                        // (item.valid != $scope.hitValidityStatuses[1].name)) ||
                        // (item.valid != $scope.hitValidityStatuses[2].name)
                    )
                        $scope.dispStatus.allHitsClosed = false;
                });
                if($scope.dispStatus.allHitsClosed){
                spinnerService.show('html5spinner');
                $scope.caseDispStatus = "Case" + $scope.caseDispStatus;
                caseDispositionService.updateHitsDisposition($scope.caseItem.flightId, $scope.caseItem.paxId,
                    $scope.caseItemHitId, $scope.commentText,
                    $scope.caseDispStatus,
                    $scope.hitDetailTrueHitFlag,null)
                    .then(function (aCase) {
                        $scope.caseItem = aCase.data;
                        $scope.caseItemHits = $scope.caseItem.hitsDispositions;
                        $scope.caseDispStatus = $scope.caseItem.status;
                        //$scope.dispStatus.caseStatusShow = false;
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
                caseDispositionService.updateHitsDisposition($scope.caseItem.flightId, $scope.caseItem.paxId,
                                                             $scope.caseItemHitId, $scope.commentText,
                                                             $scope.hitDispStatus,
                                                             $scope.hitDetailTrueHitFlag,
                                                             $scope.caseCommentAttachment)
                    .then(function (aCase) {
                    $scope.caseItem = aCase.data;
                    $scope.caseItemHits = $scope.caseItem.hitsDispositions;
                    $scope.commentText=null;
                    $scope.hitDispStatus=null;
                    spinnerService.hide('html5spinner');
                    $mdSidenav('comments').close();
                    $state.reload();
                });
            };
            
            $scope.addToOneDayLookoutList = function(caseId){
                
            	caseDispositionService.addToOneDayLookout(caseId).then(
                        function(data){
                            var confirmation = data.data;
                            if(confirmation == true)
                            {
                            	 $scope.isAddOLKButtonDisabled = true;
                            	 $scope.isRemoveOLKButtonDisabled = false;
                            }
                           
                        });
            };
            
            
            $scope.removeFromOneDayLookoutList = function(caseId){
                
            	caseDispositionService.removeFromOneDayLookoutList(caseId).then(
                        function(data){
                            var confirmation = data.data;
                            if(confirmation == true)
                            {
                            	$scope.isRemoveOLKButtonDisabled = true;
                            	$scope.isAddOLKButtonDisabled = false;
                            	 
                            }
                           
                        });;
            };
            
            
            

            $scope.closeSideNav = function(){
                $mdSidenav('comments').close();
            };

            $scope.sideNav = function(id, position) {
                //$scope.caseItemHitComments = $scope.caseItemHits[position];
                $scope.caseItemHitComments = $scope.caseItemHitsVo[position];
                $scope.caseItemHitId = $scope.caseItemHitsVo[position].hit_disp_id;
                $scope.hitDetailTrueHitFlag = $scope.caseItemHitsVo[position].valid;
                $scope.hitDispStatus = $scope.caseItemHitsVo[position].status;
                //$scope.dispStatus.hitStatusShow = ($scope.caseItemHits[position].status === $scope.dispStatus.constants.CLOSED)? false: true;
                $scope.dispStatus.hitStatusShow = true; // put in this flow thru' to allow switching between CLOSED and other states
                $scope.dispStatus.hitStatusShow = ($scope.caseItem.status === $scope.dispStatus.constants.CLOSED)? false: true;//when top-level case is closed, no more comments until re-opened
                $scope.populateDispStatuses();
                if(typeof $scope.hitDetailTrueHitFlag !== undefined && $scope.hitDetailTrueHitFlag !== null) {
                    if($scope.hitDetailTrueHitFlag == 'true') $scope.hitDetailTrueHitFlag = true;
                        else if($scope.hitDetailTrueHitFlag == 'false') $scope.hitDetailTrueHitFlag = false;
                }
                $mdSidenav(id).toggle();
            };

            //dialog function for image display dialog
            $scope.showAttachments = function(attachmentList) {
                $mdDialog.show({
                    template:'<md-dialog><md-dialog-content>'+
                    '<div><carousel>'+
                    attachmentList+
                    '</carousel></div>'+
                    '</md-dialog-content></md-dialog>',
                    parent: angular.element(document.body),
                    clickOutsideToClose:true
                })
            };

            $scope.packageAttachments = function(value){
                var attList = '';
                var slideString = '';
                slideString += '<img ng-src="data:'+value.contentType+';base64,'+value.content+'"></slide>';
                attList += slideString;
                $scope.showAttachments(attList);
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


        })
}());
