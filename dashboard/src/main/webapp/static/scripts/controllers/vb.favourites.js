app.controller('FavouritesController', function($http,$scope, $stateParams){
    $scope.accountId = $stateParams.accountId;
    $scope.accountName = $stateParams.accountName;
    $scope.startDate = $stateParams.startDate;
    $scope.endDate = $stateParams.endDate;
    
    $scope.favourites=[];
     $http.get("admin/tag").success(function (response) {
     $scope.favourites=response;
     })    
})