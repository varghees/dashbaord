app.controller('FavouritesPdfController', function ($stateParams, $http, $scope, $filter, $cookies, $translate) {

    $scope.favPdfStartDate = $filter('date')(new Date($stateParams.startDate), 'MMM dd yyyy');//$filter(new Date($stateParams.startDate, 'MM/dd/yyyy'));
    $scope.favPdfEndDate = $filter('date')(new Date($stateParams.endDate), 'MMM dd yyyy'); //$filter(new Date($stateParams.endDate, 'MM/dd/yyyy'));
    $scope.agencyLanguage = $stateParams.lan;//$cookies.getObject("agencyLanguage");
    console.log("userId>>>>>>>>" + $stateParams.userId);

    var lan = $scope.agencyLanguage;
    changeLanguage(lan);

    function changeLanguage(key) {
        $translate.use(key);
    }

    $http.get('admin/ui/getAccount/' + $stateParams.accountId).success(function (response) {
        response.forEach(function (val, key) {
            $scope.favAccountName = val.accountName;
            $scope.favAccountLogo = val.logo;
        });
    });

    $http.get("admin/tag/widgetTag/" + $stateParams.favouriteName).success(function (response) {
//        $scope.favPdfWidgets = response;
        var widgetItems = response;
        $http.get("admin/ui/getChartColorByUserId/" + $stateParams.userId).success(function (response) {
            $scope.userChartColors = response;
            var widgetColors;
            if (response.optionValue) {
                widgetColors = response.optionValue.split(',');
            }
            widgetItems.forEach(function (value, key) {
                value.widgetId.chartColors = widgetColors;
                if (value.widgetId.chartType == 'horizontalBar') {
                    value.widgetId.isHorizontalBar = true;
                } else {
                    value.widgetId.isHorizontalBar = false;
                }
            });
            $scope.favPdfWidgets = widgetItems;
        }).error(function () {
            $scope.favPdfWidgets = widgetItems;
        });
        setInterval(function () {
            window.status = "done";
        }, 15000);
    });


    $scope.downloadUiPdf = function () {
        window.open("admin/pdf/download?windowStatus=done&url=" + encodeURIComponent(window.location.href));
    };
});