app.controller('PdfController', function ($stateParams, $http, $scope) {
    $scope.userAccountName = $stateParams.accountName;
    $scope.userProductName = $stateParams.productName;
    $scope.reportStartDate = new Date($stateParams.startDate);
    $scope.reportEndDate = new Date($stateParams.endDate);
    $scope.pdfWidget = [];
    $http.get("admin/ui/dbWidget/" + $stateParams.tabId).success(function (response) {
        $scope.pdfWidgets = response;
    });
});