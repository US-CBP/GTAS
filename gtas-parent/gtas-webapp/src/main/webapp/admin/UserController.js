/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('UserCtrl', function ($scope, $stateParams, userService, $mdToast, $location, $timeout) {
    'use strict';
    var backToAdmin = function () { $location.path('/admin'); },
        newUser = { password: '', userId: '', firstName: '', lastName: '', active: 1},
        adminIndex,
        ADMIN = 'Admin',
        setUser = function () {
            var setRole, userSelectedRoles, selectedUser;
            if(angular.isDefined(localStorage['lastSelectedUser'])){selectedUser = JSON.parse(localStorage['lastSelectedUser'])};
            if (angular.isDefined(selectedUser) && selectedUser.userId === $stateParams.userId) {
                userSelectedRoles = selectedUser.roles.map(function (role) { return role.roleDescription; });
                setRole = function (role) { role.selected = userSelectedRoles.indexOf(role.roleDescription) >= 0; };
                $scope.user = selectedUser;
                $scope.roles.forEach(setRole);
                $scope.setNonAdminRoles($scope.roles[adminIndex]);
                $scope.action = 'manageUser';
            } else {
                $scope.user = newUser;
                $scope.action = 'createUser';
                if (/chrom(e|ium)/.test(navigator.userAgent.toLowerCase())) { $timeout(function () {
                    $('#userId, #password').each(function () { this.value = ''; }); }, 100);
                }
            }
        },
        getSelectedRoles = function () {
            var selectedRoles = [];
            $scope.roles.forEach(function (role) {
                if (role.selected && !(role.disabled)) {
                    selectedRoles.push({
                        roleId: role.roleId,
                        roleDescription: role.roleDescription
                    });
                }
            });

            return selectedRoles;
        },
        alertUser = function (content) {
            $mdToast.show(
                $mdToast.simple().content(content).position("top right").hideDelay(3000)
            );
        },
        scopeRoles = function (roles) {
            $scope.roles = [];
            roles.forEach(function (role, index) {
                if (role.roleDescription === ADMIN) { adminIndex = index; }
                $scope.roles.push({
                    roleDescription: role.roleDescription,
                    roleId: role.roleId,
                    selected: false,
                    disabled: false
                });
            });
            setUser();
        };
    
   $scope.displayPasswordRules = function(){
    	$mdToast.show({
    		hideDelay   : 0,
            position    : 'top right',
            ok:"OK",
            template    : '<md-toast style="height:100%;margin-top:160px;position:fixed;z-index: 10000;"><div class="md-toast-content" style="height:100%">Password Criteria:'+
    			'<ul><li>10 to 20 characters</li>'+
    			'<li>At least one special character (!@#$%^&*)</li>'+
    			'<li>At least one number</li>'+
    			'<li>At least one letter</li>'+
    			'<li>At least one upper case character</li>'+
    			'<li>At least one lower case character</li>'+
    			'</ul></div></md-toast>'
       });
    };
    
    $scope.updatedPassword = "";
    $scope.confirmUpdatedPassword = "";
    $scope.validationErrorMessage = "";
    $scope.error=false;
    

    
    $scope.hideToast = function(){
    	$mdToast.hide();
    };
    
    $scope.setNonAdminRoles = function (roleToggled) {
        if (roleToggled.roleDescription === ADMIN) {
            $scope.roles.forEach(function (role) {
                if (role.roleDescription !== ADMIN) {
                    role.disabled = roleToggled.selected;
                    role.selected = false;
                }
            });
        }
    };

    $scope.saveUser = function () {
    	
    	$scope.error=false;
    	$scope.validationErrorMessage = "";
        $scope.user.userId = $scope.user.userId.trim();
        
         if(!$scope.updatedPassword!==null)
        	$scope.updatedPassword = $scope.updatedPassword.trim();
         if($scope.confirmUpdatedPassword!==null)
        	 $scope.confirmUpdatedPassword = $scope.confirmUpdatedPassword.trim();
         
        if($scope.updatedPassword)
        	{
        		var enteredPassword = $scope.updatedPassword;
        		var passwordRegEx = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[$@$!%*^#?&])[A-Za-z\d$@$!%*^#?&]{10,20}$/;
        		var regExResult = passwordRegEx.test(enteredPassword);
		  
        		if(!regExResult)
        		{
        			$scope.validationErrorMessage = "The password you entered does not satisfy the password criteria.";
        			$scope.error=true;
        			return;
        		}
        
        	}
        if ( ( ($scope.updatedPassword!==null && $scope.updatedPassword.trim()!=='')  && (($scope.confirmUpdatedPassword===null) || ($scope.confirmUpdatedPassword.trim() ==='')) ) || 
        	 ( ($scope.updatedPassword===null || $scope.updatedPassword.trim()==='') &&  (($scope.confirmUpdatedPassword!==null) && ($scope.confirmUpdatedPassword.trim()!=='')) ) )
        	{
        		$scope.validationErrorMessage = "Both the Password and Confirm Password fields should have a value.";
        		$scope.error=true;
        		return;
        	}
    	
       
       if( ($scope.updatedPassword) && ($scope.confirmUpdatedPassword)  )
        {
    	 
    		   $scope.updatedPassword = $scope.updatedPassword.trim();
    		   $scope.confirmUpdatedPassword = $scope.confirmUpdatedPassword.trim();
    		   $scope.user.password = $scope.updatedPassword.trim();
    		 
    		  if( $scope.updatedPassword !== $scope.confirmUpdatedPassword )
    			{
    			   $scope.validationErrorMessage = "The passwords you entered in the Password and Confirm Password fields are not the same.";
                   $scope.error=true;
                   return;
    			}
   	 
    	 }
   
       else if( ($scope.updatedPassword===null ||$scope.updatedPassword.trim()==='' ) && ($scope.confirmUpdatedPassword === null || $scope.confirmUpdatedPassword.trim()===''  ) ) 
    	   {
    	   		$scope.user.password= null;
    	   }
       
       else  
 	  	{
 	  		$scope.validationErrorMessage = "Either the Password or Confirm Password fields have invalid value.";
 	  		$scope.error=true;
 	  		return;
 	  	}
    	  
       if ($scope.user.userId.length === 0 ) {
           $scope.validationErrorMessage = "The User Id can not be blank.";
           $scope.error=true;
           return;
       }  
    	  
      
        $scope.user.roles = getSelectedRoles();
        

        userService[$scope.action]($scope.user).then(backToAdmin);
    };

    userService.getRoles().then(scopeRoles); // <- Init()
});
