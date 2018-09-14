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
											$route.reload();
										
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
					/*ProjectionService.getHall($routeParam.current.params.id).then(function(response)
							{
								
								console.log(response.data);
								$scope.projections = response.data.sort();
							}, function myError(response) {
								
						    });
					*/
					$scope.deleteProjection = function(){
						var id = $scope.projection.id;
						ProjectionService.deleteProjection(id).then(function(response)
						{
							alert("Delete projection");
							$route.reload();
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
