app.controller('PanelController', function ($scope, $http, $stateParams) {
    $http.get("static/datas/" + $stateParams.panelId + ".json").success(function (response) {
        $scope.panels = response;
    });    
    
    $http.get('admin/datasources').success(function(response){
        $scope.datasources = response;
    })


    $scope.panels = [];
    var uid = 7;
    $scope.add = function () {
        $scope.id = uid++;
        $scope.panels.push({chartId: $scope.id});
    };
    
    $scope.onDropComplete = function (index, panel, evt) {
        var otherObj = $scope.panels[index];
        var otherIndex = $scope.panels.indexOf(panel);
        $scope.panels[index] = panel;
        $scope.panels[otherIndex] = otherObj;
    };
    
    $scope.removePanel = function (index) {
        $scope.panels.splice(index, 1);
    };
});