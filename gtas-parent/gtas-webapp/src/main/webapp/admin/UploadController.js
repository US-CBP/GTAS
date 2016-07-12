/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('UploadCtrl', ['$scope', '$timeout', '$http', 'Upload', function ($scope, $timeout, $http, Upload) {
    $scope.$watch('files', function () {
        $scope.upload($scope.files);
    });
    $scope.$watch('file', function () {
        if ($scope.file != null) {
            $scope.files = [$scope.file]; 
        }
    });

    $scope.log = '';
      
    $scope.upload = function (files) {
        if (files && files.length) {
            for (var i = 0; i < files.length; i++) {
              var file = files[i];
              if (!file.$error) {
                Upload.upload({
                    url: '/gtas/upload',
                    data: {
                      username: 'gtas',
                      file: file  
                    }
                }).progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope.log = 'progress: ' + progressPercentage + '% ' +
                                evt.config.data.file.name + '\n' + $scope.log;
                }).success(function (data, status, headers, config) {
                    $timeout(function() {
                        $scope.log = 'file: ' + config.data.file.name + ', Response: ' + JSON.stringify(data) + '\n' + $scope.log;
                    });
                });
              }
            }
        }
    };
    
    $scope.deleteall = function() {
        var r = confirm("This will delete all messages and associated data (flights, pax, etc).\n\nYou sure?");
        if (r == true) {
            return $http.get('/gtas/deleteall').then(function (res) {
                alert('Successfully deleted all messages.');
            });
        }
    };
}]);