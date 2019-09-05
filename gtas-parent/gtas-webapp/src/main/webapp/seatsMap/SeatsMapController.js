/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

(function () {
    'use strict';
    app.controller('SeatsMapController',
        function ($state, $scope, $rootScope, $q, $stateParams, $mdToast, $mdDialog, seatData) {
        
    		$scope.paxId=$stateParams.paxId;
    		$scope.flightId = $stateParams.flightId;
    		$scope.selectedSeatNumber = $stateParams.seat;
    		
    		$scope.selectedSeat = seatData.data.find(function(element){
    			return element.number == $scope.selectedSeatNumber;
    		})
    		
    		$scope.coTravelersSeats = $scope.selectedSeat.coTravellers;

  var loadChart =   function(){	   
    	    var columns = [];
    	    var rows = [];

    	    data.map.forEach(p => {
    	       var element = p.match(/[^\d]+|\d+/g);
    	       
    	       if(rows.indexOf(+element[0]) <= -1)
    	    	   rows.push(+element[0])
    	       if(columns.indexOf(element[1]) <= -1)
    	    	   columns.push(element[1])
    	   });

    	   columns = columns.sort(); //letters
    	   rows = rows.sort((a,b) => a-b); //sort numbers, by default sort() method sorts elements alphabetically 
    	   
    	   columns = fill(columns[0],columns[columns.length - 1]);
    	   rows = range(rows[0], rows[rows.length -1]);
    	   
    	   var map =[]
    	   
    	   for(var i=0; i< rows.length; i++){
    		   var row ='';
    		   for(var j=0;j < columns.length; j++){
    			   if (seatData.data.filter(p => p.hasHits).map(v => v.number).includes(rows[i]+columns[j]))
    				   row+='h';
    			   else if ($scope.selectedSeat.coTravellers.includes(rows[i]+columns[j]))
    				   row+='c';
    			   else if(data.map.includes(rows[i]+columns[j]))
    				   row+='a';
    			   else
    				   row+='u';
    		   }
    		   map.push(row)
    	   }
      
    	   var sc = $('#seat-map').seatCharts({
    	       
   	        map: map,
   	        seats: {
   				a: {
   					click : function(){
   						
   						var seat_id = this.settings.id;
   						
   						$scope.selectedSeat = seatData.data.find(
   								function(elem){
   									return elem.number == seat_id
   									});
   						$scope.selectedSeatNumber = $scope.selectedSeat.number;

   						showConfirm();
   					},
   	                classes : 'available', 
   	                description : 'available seat'
   				},
   				c: {
   					click : function(){
   						var seat_id = this.settings.id;
   						
   						$scope.selectedSeat = seatData.data.find(
   								function(elem){
   									return elem.number == seat_id
   									});
   						
   						showConfirm();
   					},
   					classes : ['cotravelers']
   				},
   				u : {
   					
   					classes: 'unavailable'
   				},
   				h: {
   					click : function(){
   						
   						var seat_id = this.settings.id;
   						
   						$scope.selectedSeat = seatData.data.find(
   								function(elem){
   									return elem.number == seat_id
   									});
   						$scope.selectedSeatNumber = $scope.selectedSeat.number;

   						showConfirm();
   					},
   					classes: 'hits'
   				}
   			
   			},
   	        naming : {
   	            columns : columns,
   	            rows: rows,
   	            getLabel : function(character, row, column){
   	            	return row+column;
   	            },
   	            left: false,
   	            top: false,
   	            getId: function(character, row, column){
   	            	return row+column;
   	            }
	        }
   			,
	        legend : {
	        	node: $("#legend"),
	        	items: [
	        		['a', 'available', 'Seated'],
	        		['u', 'unavailable', 'Empty Seat'],
	        		['c', 'cotravelers','Co-Travlers'],
	        		['h', 'hashits', 'Hits']
	        	]
	        }

   		});
   	   
       //
   	   sc.get([$scope.selectedSeatNumber]).node().css({
	    	   "background-color": '#f4f'
   	   });
   	   
   	   //
   	   sc.get($scope.coTravelersSeats).node().css({
   		   "background-color": '#FF8C00'
   	   })
    };
  
	var  data = {
			 map : seatData.data.map(p => p.number)
	 };
	
	var range = function(start, end){
		
		var array = [];
		
	    for (var i = start; i < end; i++) {
	        array.push(i);
	    }
	    return array;
	}
	
	function fill(a='a', z='z'){
		var alphabets = "ABCDEFGHJKLMNOPQRSTUVWXYZ".split('');
		
		//return the specific letters which are assigned to seats
		return (alphabets.slice(alphabets.indexOf(a),alphabets.indexOf(z)+1))
	}
	
	var showConfirm = function() {
 	
		var confirm = $mdDialog.confirm({
     	   parent: angular.element(document.body),
            template:'<md-dialog ng-cloak>'+
         	   '<form>'+
         	   		'<md-dialog-content>'+
         	   			'<div class="md-dialog-content" style="padding-top:0px;padding-bottom:0px;">'+
         	   				'<h5 class="md-title"><strong></strong></h5>'+
         	   					'<div class="_md-dialog-content-body ng-scope">'+
         	   					
         	   					'<p class="ng-binding"> Seating: <strong>{{seat.number}}</strong> </p>'+
         	   					
         	   					'<div>First Name: &nbsp; <strong>{{seat.firstName}}</strong></div>'+
         	   					'<div>Last Name: &nbsp; <strong>{{seat.lastName}}</strong></div>'+
         	   					'<div>Middle Name: &nbsp; <strong>{{seat.middleInitial}}</strong></div>'+
         	   					'<p />' +
         	   					'<div>Co-Travelers: <span class="label label-default" ng-repeat="co in seat.coTravellers">{{co}}</span></div>' + 
         	   					'<p />' +
         	   					'<p >'+
         	   						'<a ng-href="#/paxdetail/{{seat.paxId}}/{{seat.flightId}}" ng-click="hide()"> <i class="fa fa-link" aria-hidden="true"></i> Pax Detail </a>' +
         	   					'</p></div>'+
         	   			'</div>'+
         	   		'</md-dialog-content>'+
         	   	'<md-dialog-actions layout="row">'+
 	      '<md-dialog-actions layout="row" class="layout-row">'+
 	      '<md-button ng-click="hide()"> <i class="fa fa-times" aria-hidden="true"></i> Close</md-button>'+
 	    '</md-dialog-actions>'+
 	  '</form>'+
 	  '</md-dialog>',
 	  locals: {
 		  seat: $scope.selectedSeat
 	  },
 	  controller : ['$scope', 'seat','$mdDialog', function($scope, seat, $mdDialog){
 	
 		  $scope.seat = seat;
 		  
 		  // Close dialog
 		  $scope.hide = function(){			
 			 $mdDialog.hide();
 		};
 	  }]
		});

        $mdDialog.show(confirm).then(function() {
         	   
        }, function() {
         	      return false;
        });
  
	  };
   			
	loadChart();
  
        }); 

}());

