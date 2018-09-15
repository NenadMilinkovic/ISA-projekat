var photo;

var app = angular.module('isaApp');

app.factory('ProjectionService', function ProjectionService($http) {
	
	ProjectionService.getProjections = function(id) {
		return $http({
			method : 'GET',
			url : '/projection/getProjections',
			params: {id : id}
		});
	}
	
	ProjectionService.getHall = function(id){
		return $http({
			method: "GET",
			url: '/projection/getHall/'+id
		});
	}
	
	ProjectionService.getTerm = function(id){
		return $http({
			method:"GET",
			url: '/projection/getTerm/'+id
		});
	}
	
	ProjectionService.deleteProjection = function(id){
		return $http({
			method: "DELETE",
			url: '/projection/deleteProjection/'+id
		});
	}

	ProjectionService.registerProjection = function(cinema_id,projection) {
		return $http({
			method : 'POST',
			url : '/projection/registerProjection/'+cinema_id,
			data : {
				"name" : projection.name,
				"actors" : projection.actors,
				"description" : projection.description,
				"genre" : projection.genre,
				"director" : projection.director,
				"duration" : projection.duration,
				"poster" : projection.poster
			}
		});
	}
	
	ProjectionService.changeProjection = function(cinema_id,projection) {
		return $http({
			method : 'PUT',
			url : '/projection/changeProjection/'+cinema_id,
			data : {
				"id": projection.id,
				"name" : projection.name,
				"actors" : projection.actors,
				"description" : projection.description,
				"genre" : projection.genre,
				"director" : projection.director,
				"duration" : projection.duration,
				"poster" : projection.poster
			}
		});
	}
	
	ProjectionService.deleteTerm = function(id){
		return $http({
			method : 'DELETE',
			params : {id : id},
			url : '/term/deleteTerm'
		});
	}
	
	ProjectionService.registerTerm = function(term,projection_id){
		return $http({
			method: 'POST',
			url: '/term/registerTerm/'+projection_id+"/"+term.hall,
			data: {
				"termDate" : term.termDate,
				"termTime" : term.termTime,
				"priceRegular": term.priceRegular,
				"priceBalcony": term.priceBalcony,
				"priceVip" : term.priceVip
			}
		});
	}
	
	
	
	
	return ProjectionService;
});

app.controller(
		'ProjectionController',
		[
				'$rootScope',
				'$scope',
				'$location',
				'$route',
				'$interval',
				'ProjectionService',
				function($rootScope, $scope, $location, $routeParam, $interval,
						 ProjectionService) {
					
					$scope.show=true;
					
					$scope.showView = function(number){
						$scope.show = number;
					}
					
					$scope.display = function(tab){
						$scope.projection = $scope.selected;
						$scope.show = tab;	
					}
					

					$scope.setSelected = function(selected) {
						$scope.selected = selected;
						$rootScope.projection = $scope.selected;
						ProjectionService.getTerm($rootScope.projection.id).then(function(response)
								{
									$scope.terms = response.data.sort();
								}, function myError(response) {
									
							    });
						$scope.show = null;
						$scope.projection = null;
						
					}
					$scope.registerProjection = function(){
						var projection = $scope.newprojection;
						var id = $routeParam.current.params.id;
						projection.poster = photo;
						photo="";
						ProjectionService.registerProjection(id,
							projection).then(
									function(response) {
											alert('Successfuly create projection')
											location.reload();
										
									}, function myError(response) {
										
								    });
						}
					
					$scope.getProjections = function(){
						ProjectionService.getProjections($routeParam.current.params.id).then(function(response)
								{
							
								console.log(response.data);
								$scope.projections = response.data.sort();
								}, function myError(response) {
							
					    });
					
					
					}
					
					$scope.getHalls = function(){
					ProjectionService.getHall($routeParam.current.params.id).then(function(response)
							{
								
								console.log(response.data);
								$scope.halls = response.data.sort();
							}, function myError(response) {
								
						    });
					}
					
					$scope.deleteProjection = function(){
						var id = $scope.projection.id;
						ProjectionService.deleteProjection(id).then(function(response)
						{
							alert("Delete projection");
							location.reload();
						}, function myError(response) {
							
					    });
						
					}
					
					$scope.changeProjection = function(projection){
						var id = $routeParam.current.params.id;
						ProjectionService.changeProjection(id,projection).then(function(response){
							alert("Change projection");
							location.reload();
						});
					}
					
					$scope.deleteTerm = function(id){
						ProjectionService.deleteTerm(id).then(function(response){
							alert("delete term");
							location.reload();
						}, function myError(response) {
							alert("Cant delete term");
						});
					}
					
					$scope.registerTerm = function(){
						console.log("napravi log");
						var term = $scope.term;
						console.log(term);
						var projection_id = $scope.projection.id;
						console.log(projection_id);
						ProjectionService.registerTerm(term,projection_id).then(function(response){
							alert("add term");
							location.reload();
						}, function myError(response) {
						
						});
					}
				}				
		]);

function uploadImage2() {
	console.log( $('#poster'));
    let image = $('#poster').prop('files')[0];
	
    var formData = new FormData();
    formData.append("image", image);
    $.ajax({
        method: 'POST',
        headers: {
            'Authorization': 'Client-ID 177690a47fd843f',
            'Accept': 'application/json'
        },
        url: 'https://api.imgur.com/3/image',
        data: formData,
        processData: false,
        contentType: false,
        mimeType: 'multipart/form-data',
        success: function(data) {
            image_url = JSON.parse(data).data.link;
            photo = image_url;
            alert("Uspesno aploadovana.");
        },
        error: function(data) {

            alert("Neuspesno.");
        }
    });
}

function uploadImage3() {
	console.log( $('#poster'));
    let image = $('#poster').prop('files')[1];
	
    var formData = new FormData();
    formData.append("image", image);
    $.ajax({
        method: 'POST',
        headers: {
            'Authorization': 'Client-ID 177690a47fd843f',
            'Accept': 'application/json'
        },
        url: 'https://api.imgur.com/3/image',
        data: formData,
        processData: false,
        contentType: false,
        mimeType: 'multipart/form-data',
        success: function(data) {
            image_url = JSON.parse(data).data.link;
            photo = image_url;
            alert("Uspesno aploadovana.");
        },
        error: function(data) {

            alert("Neuspesno.");
        }
    });
}
