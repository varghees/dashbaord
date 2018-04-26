app.controller('PdfController', function ($stateParams, $http, $scope, $filter, $cookies, $translate, localStorageService) {
    $scope.reportStartDate = $filter('date')(new Date($stateParams.startDate), 'MMM dd yyyy');//$filter(new Date($stateParams.startDate, 'MM/dd/yyyy'));
    $scope.reportEndDate = $filter('date')(new Date($stateParams.endDate), 'MMM dd yyyy'); //$filter(new Date($stateParams.endDate, 'MM/dd/yyyy'));
    $scope.pdfWidget = [];
    console.log("userId>>>>>>>>" + $stateParams.userId);
    $scope.agencyLanguage = $stateParams.lan;//$cookies.getObject("agencyLanguage");

    var lan = $scope.agencyLanguage;
    changeLanguage(lan);

    function changeLanguage(key) {
        $translate.use(key);
    }
    $scope.dashboardProductName=$stateParams.productName;
    $scope.getTableType = $stateParams.getTableType;
    var compareStartDate = localStorageService.get("comparisonStartDate");
    var compareEndDate = localStorageService.get("comparisonEndDate");
    console.log("tableType--------------->"+localStorageService.get("selectedTableType"));
    console.log("compareStartDate--------------->"+localStorageService.get("comparisonStartDate"));
    console.log("compareStartDate--------------->"+localStorageService.get("comparisonEndDate"));
    $scope.compareDateRange = {
        startDate: compareStartDate,
        endDate: compareEndDate
    };
    $http.get('admin/ui/getAccount/' + $stateParams.accountId).success(function (response) {
        response.forEach(function (val, key) {
            $scope.userAccountName = val.accountName;
            $scope.userAccountLogo = val.logo;
        });
    });

    $http.get('admin/ui/dashboardTemplates/' + $stateParams.productId + "/" + $stateParams.userId + "/" + $stateParams.agencyId).success(function (response) {
        $scope.templates = response;
        var template = $filter('filter')(response, {id: $stateParams.templateId})[0];
        $scope.templateName = template ? template.templateName : null;
    });

    $http.get("admin/ui/dbWidget/" + $stateParams.tabId + "/" + $stateParams.accountId).success(function (response) {
        console.log("****************&&&&&&&&&&&&************ ",response);
        var pdfProductName = response[0].tabId.agencyProductId ? response[0].tabId.agencyProductId.productName : null;
        $scope.userProductName = pdfProductName;
        var pdfWidgetItems = [];
        pdfWidgetItems = response;
        $http.get("admin/ui/getChartColorByUserId/" + $stateParams.userId).success(function (response) {
            var widgetColors;
            if (response.optionValue) {
                widgetColors = response.optionValue.split(',');
            }
            pdfWidgetItems.forEach(function (value, key) {
                value.chartColors = widgetColors;
                if (value.chartType == 'horizontalBar') {
                    value.isHorizontalBar = true;
                } else {
                    value.isHorizontalBar = false;
                }
                angular.forEach(value.columns, function (value, k) {
                    value.expand = true;
                });
            });
            $scope.pdfWidgets = pdfWidgetItems;
        }).error(function (response) {
            $scope.pdfWidgets = pdfWidgetItems;
        });
        setInterval(function () {
            window.status = "done";
        }, 35000);
    });

    $scope.downloadUiPdf = function () {
        window.open("admin/pdf/download?windowStatus=done&url=" + encodeURIComponent(window.location.href));
    };
});