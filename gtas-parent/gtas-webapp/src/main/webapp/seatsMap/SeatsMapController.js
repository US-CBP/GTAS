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
    		$scope.selectedSeat = $stateParams.seat;
    		    		  			 

  var loadChart =   function(){	   
    	    var columns = [];
    	    var rows = [];

    	   data.map.forEach(p => {
    	       var element = p.match(/[^\d]+|\d+/g);
    	       
    	       if(rows.indexOf(element[0]) <= -1)
    	    	   rows.push(element[0])
    	       if(columns.indexOf(element[1]) <= -1)
    	    	   columns.push(element[1])
    	   });

    	   columns.sort();
    	   rows.sort();
    	   
    	   var map =[]
    	   
    	   for(var i=0; i<rows.length; i++){
    		   var row ='';
    		   for(var j=0;j<columns.length;j++){
    			   row+='a';
    		   }
    		   map.push(row)
    	   }
       
    	   var sc = $('#seat-map').seatCharts({
    	       
   	        map: map,
   	        seats: {
   				a: {
   	               
   					price   : 99.99,
   	                classes : 'front-seat', 
   	                description : 'This are the front rows'
   				}
   			
   			}
//    	   ,
//   			click: function () {
//   				if (this.status() == 'available') {
//   					
//   					return 'selected';
//   				} else if (this.status() == 'selected') {
//   					// seat has been vacated
//   					return 'available';
//   				} else if (this.status() == 'unavailable') {
//   					// seat has been already booked
//   					return 'unavailable';
//   				} else {
//   					return this.style();
//   				}
//   	        }
   	        ,
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

   		});
   		
   		/*
		 * Get all unavailable seats, put them in a jQuery set and change the
		 * background color
		 */
   		 sc.get(data.map).node().css({
   		 	"background-color": '#ffcf4f'
   	     });
   	    
   	    /*
		 * set line-height to 24 for all seats
		 */
   	   sc.find(/^[0-99]+[A-Z]/).node().css({
   	    "line-height": '24px',
   	     width: "24px",
   	     "height": "24px"
   	     
   	   });
   	   
   	   sc.get([$scope.selectedSeat]).node().css({
   		   
   		   "line-height": '24px',
	    	     width: "24px",
	    	     "height": "24px",
	    	   "background-color": '#f4f'
   	   });
    };
  
	var  data = {
			 map : seatData.data
	 };
   			
	loadChart();
  
        }); 

}());

