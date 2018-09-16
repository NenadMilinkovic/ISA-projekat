var myReservation;

var app = angular.module('isaApp');

app.factory('CinemaTheaterService', function cinemaTheaterService($http) {
	
	cinemaTheaterService.getCinemas = function() {
		return $http({
			method : 'GET',
			url : 'isa/cinemaTheater/getCinemas'
		});
	}
	cinemaTheaterService.getTheaters = function() {
			return $http({
				method : 'GET',
				url : 'isa/cinemaTheater/getTheaters'
			});
		}
	cinemaTheaterService.registerTheater = function(theater) {
		return $http({
			method : 'POST',
			url : 'isa/cinemaTheater/registerTheater',
			data : {
				"name" : theater.name,
				"type" : 'THEATER',
				"description" : theater.description,
				"adress" : theater.adress,
				"rating" : 0
			}
		});
	}
	cinemaTheaterService.registerCinema = function(cinema) {
		return $http({
			method : 'POST',
			url : 'isa/cinemaTheater/registerCinema',
			data : {
				"name" : cinema.name,
				"type" : 'CINEMA',
				"description" : cinema.description,
				"adress" : cinema.adress,
				"city" : cinema.city,
				"rating" : 0
			}
		});
	}
	
	cinemaTheaterService.editCinema = function(cinema){
		return $http({
			method : 'POST',
			url : 'isa/cinemaTheater/editCinema',
			data : {
				"id": cinema.id,
				"name" : cinema.name,
				"type" : cinema.type,
				"description" : cinema.description,
				"adress" : cinema.adress,
				"city" : cinema.city,
				"rating" : cinema.rating
			}
		});
	}
	
	cinemaTheaterService.registerCinemaTheaterAdmin = function(cinema_id, user) {
		console.log('usaoooo');
		return $http({
			method : 'POST',
			url : 'user/registerCinemaTheaterAdmin?id=' + cinema_id,
			data : {
				"name" : user.name,
				"userRole" : "ADMIN_OF_CINEMA_THEATRE",
				"surname" : user.surname,
				"password" : user.password,
				"email" : user.email,
				"city" : user.city,
				"phone" : user.phone
			}
		});
	}
	

	cinemaTheaterService.getLoggedInUser = function() {
		return $http({
			method : 'GET',
			url : 'user/getLoggedInUser'
		});
	}
	cinemaTheaterService.getProjections = function(id) {
		return $http({
			method : 'GET',
			url : '/projection/getProjections',
			params: {id : id}
		});
	}
	cinemaTheaterService.getCinemaReservations = function(id) {
		return $http({
			method : 'GET',
			url : '/reservation/getCinemaReservations',
			params: {id : id}
		});
	}
	
	cinemaTheaterService.getHalls = function(id){
		return $http({
			method: "GET",
			url: '/projection/getHall/'+id
		});
	}
	
	cinemaTheaterService.changeSeats = function(vip,regular,balcony,hall_id){
		return $http({
			method: 'PUT',
			url : '/reservation/changeSeats',
			params: { vip : vip, regular : regular, balcony : balcony, hall_id : hall_id}
		});
	}
	
	
	

	
	return cinemaTheaterService;
});

app.controller(
		'CinemaTheaterController',
		[
				'$rootScope',
				'$scope',
				'$location',
				'$route',
				'CinemaTheaterService',
				function($rootScope, $scope, $location, $route,
						 cinemaTheaterService) {

					$scope.showView = function(number){
						$scope.show = number;
					}
					
					$scope.display = function(tab){
						$scope.cinemaTheater = $scope.selected;
						$scope.show = tab;
						
						
						
					}
					
					$scope.setSelected = function(selected) {
						$scope.selected = selected;
						$rootScope.cinema = $scope.selected;
						cinemaTheaterService.getCinemaReservations($scope.selected.id).then(function(response)
								{
							
								console.log(response.data);
								myReservation = response.data;
								}, function myError(response) {
							
					    });
						
						cinemaTheaterService.getHalls($scope.selected.id).then(function(response){
							$scope.halls = response.data;
						});
						$scope.selectedCinemaAdmin = null;
						$scope.show = null;
						$scope.newCinemaAdmin = null;
						address = $scope.selected.adress;
						console.log($scope.selected.adress);
					}
					$scope.setSelected2 = function(selected) {
						$scope.selected = selected;
						$rootScope.theater = $scope.selected;
						$scope.selectedTheaterAdmin = null;
						$scope.show = null;
						$scope.theater = null;
						$scope.newTheaterAdmin = null;
						

					}
					
					cinemaTheaterService.getTheaters().then(function(response) {
						console.log('fja kontr');
						console.log(response.data);
						$scope.theaters = response.data;
						
					}, function myError(response) {
						
				    });
				
				$scope.registerTheater = function() {
					var theater = $scope.theater;
					cinemaTheaterService.registerTheater(
							theater).then(
							function(response) {
								console.log(response.data);
								if (response.data) {
									alert('Successfuly registrated theater')
									$scope.show = null;
									$route.reload();
								}
							}, function myError(response) {
								
						    });
				}
					
					cinemaTheaterService.getCinemas().then(function(response) {
							console.log('fja kontr');
							console.log(response.data);
							$scope.cinemas = response.data;
							
						}, function myError(response) {
							
					    });
					
					$scope.registerCinema = function() {
						var cinema = $scope.cinema;
						cinemaTheaterService.registerCinema(
								cinema).then(
								function(response) {
									console.log(response.data);
									if (response.data) {
										alert('Successfuly registrated cinema')
										$scope.show = null;
										$route.reload();
									}
								}, function myError(response) {
									
							    });
					}
					
					$scope.changeCinema = function(cinema){
						cinemaTheaterService.editCinema(cinema).then(function(response){
							if (response.data) {
								alert('Successfuly edit cinema')
								$scope.show = null;
								$route.reload();
							}
						}, function myError(response) {
							
					    });
					}
					
					$scope.registerCinemaTheaterAdmin = function() {
						var newCinemaAdmin = $scope.newCinemaAdmin;
						console.log($scope.newCinemaAdmin);
						cinemaTheaterService.registerCinemaTheaterAdmin(
								$scope.selected.id,
								newCinemaAdmin).then(
								function(response) {
									if (response.data != null) {

										alert('Successfuly registrated cinema admin');

									} 
									$scope.show = null;
									$scope.newCinemaAdmin = null;
									$route.reload();
								}, function myError(response) {
						
								});
						
					}				
					
					$scope.verifyPassword = function(password, passwordCheck)
					{
						if(password != passwordCheck)
							$scope.noMatch = true;
						else if(password == passwordCheck)
							$scope.noMatch = false;
					}
					
					
					$scope.seeStatic = function(){
						var cinemarating=0;
						var cinemaearned=0;
						var visitors = 0;
						var h="";
						var start = document.getElementById("start").value;
						var end = document.getElementById("end").value;
						for(var i =0; i < myReservation.length; i++){
							console.log(myReservation[i].term.termDate);
							if(start <= myReservation[i].term.termDate && end >= myReservation[i].term.termDate){
							
								cinemarating = cinemarating+myReservation[i].visitRating;
								cinemaearned= cinemaearned+myReservation[i].price;
								visitors = visitors+1;
								
								var name = myReservation[i].term.projection.name;
								var projectionrating = myReservation[i].projectionRating;
								console.log(name);
								h+="<h4>"+name+" &nbsp "+projectionrating+"</h4>";
						
							}
							
						}
						cinemarating = cinemarating/visitors;
						document.getElementById("rating").innerHTML=cinemarating;
						document.getElementById("profit").innerHTML=cinemaearned;
						document.getElementById("visitors").innerHTML=visitors;
						document.getElementById("pr").innerHTML=h;
					}
					
					$scope.changeSeats = function(){
						var vip = document.getElementById("vip").checked;
						var regular = document.getElementById("regular").checked;
						var balcony = document.getElementById("balcony").checked;
						console.log($scope.cinema.hall);
						var hall_id = $scope.cinema.hall;
						
						cinemaTheaterService.changeSeats(vip,regular,balcony,hall_id).then(
								function(response) {
									alert("Close segment");
								}, function myError(response) {
						
								});
					}
				}
		]);

