
var app = angular.module('isaApp');

app.factory('ProjectionService', function ProjectionService($http) {
	
	ProjectionService.getProjection = function() {
		return $http({
			method : 'GET',
			url : 'isa/Projection/getProjections'
		});
	}

	ProjectionService.registerProjection = function(cinema_id,projection) {
		return $http({
			method : 'POST',
			url : 'isa/Projection/registerProjection',
			data : {
				"name" : projection.name,
				"actors" : projection.actors,
				"description" : projection.description,
				"genre" : projection.genre,
				"price" : projection.price,
				"duration" : projection.duration,
				"poster" : projection.poster,
				"rating" : 0,
				"time" : projection.time
			}
		});
	}
	return cinemaTheaterService;
});

