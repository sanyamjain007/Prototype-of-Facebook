var app = angular.module('myApp', []);
app.controller('myCtrl', function($scope, $http) {
    $scope.selectedSources = {};
    $scope.Sources = [];
    $scope.category='--Category--';
    var self = $scope;
    $http({
        method: 'GET',
        url: 'https://newsapi.org/v1/sources?apiKey=b3951ffd52444e74a92c867f4ca954de',
    }).success(function(result) {
        $scope.Sources = result.sources;
        $scope.selectedSources.name = 'The Times of India';
        $scope.selectedSources.id = 'the-times-of-india';
        $http.get("https://newsapi.org/v1/articles?source=the-times-of-india&apiKey=b3951ffd52444e74a92c867f4ca954de")
            .then(function(response) {
                $scope.articles = response.data.articles;
                console.log($scope.articles);
            });
    });
    $scope.updateSources = function() {
        console.log($scope.selectedSources.name + " " + $scope.selectedSources.id);
        $http.get("https://newsapi.org/v1/articles?source="+$scope.selectedSources.id+"&apiKey=b3951ffd52444e74a92c867f4ca954de")
            .then(function(response) {
                $scope.articles = response.data.articles;
                console.log($scope.articles );
            });
    }

    $scope.updateCategory = function(){
        console.log($scope.category)
        if($scope.category != 'all'){
                $http.get("https://newsapi.org/v1/sources?category="+$scope.category+"&apiKey=b3951ffd52444e74a92c867f4ca954de")
            .then(function(response) {
                $scope.Sources = response.data.sources;
                console.log(response);
            });
    }
    }
});