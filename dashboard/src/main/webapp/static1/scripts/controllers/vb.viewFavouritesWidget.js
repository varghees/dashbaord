app.controller('ViewFavouritesWidgetController', function ($http, $scope, $stateParams, $timeout, $state, $cookies, $translate, $rootScope, localStorageService) {
    $scope.accountId = $stateParams.accountId;
    $scope.accountName = $stateParams.accountName;
    $scope.productId = $stateParams.productId;
    $scope.startDate = $stateParams.startDate;
    $scope.endDate = $stateParams.endDate;
    $scope.favouriteId = $stateParams.favouriteId;
    $scope.favouritesWidgets = [];
    $scope.favouriteName = $stateParams.favouriteName;
    $scope.userId = $cookies.getObject("userId");

    console.log("User Id ====> " + $scope.userId);
    $scope.agencyLanguage = $stateParams.lan;//$cookies.getObject("agencyLanguage");

    var lan = $scope.agencyLanguage;
    changeLanguage(lan);

    function changeLanguage(key) {
        $translate.use(key);
    }

    var tableTypeByDateRange = localStorageService.get("selectedTableType") ? localStorageService.get("selectedTableType") : "compareOff";
    console.log(tableTypeByDateRange);
    if (tableTypeByDateRange == 'compareOn') {
        $scope.selectedTablesType = 'compareOn';
        $scope.compareDateRangeType = true;
    } else {
        $scope.selectedTablesType = 'compareOff';
        $scope.compareDateRangeType = false;
    }
    $scope.loadStatus = true;
    $rootScope.$on("loadStatusChanged", function (event, loadStatus) {
        $scope.loadStatus = "";
        $timeout(function () {
            var tableTypeByDateRange = localStorageService.get("selectedTableType") ? localStorageService.get("selectedTableType") : "compareOff";
            $scope.getTableType = tableTypeByDateRange ? tableTypeByDateRange : "compareOff";
            var compareStartDate = localStorageService.get("comparisonStartDate");
            var compareEndDate = localStorageService.get("comparisonEndDate");
            $scope.compareDateRange = {
                startDate: compareStartDate,
                endDate: compareEndDate
            };
            $scope.loadStatus = loadStatus;
        }, 20);
    });
    $scope.getTableType = tableTypeByDateRange ? tableTypeByDateRange : "compareOff";
    var compareStartDate = localStorageService.get("comparisonStartDate");
    var compareEndDate = localStorageService.get("comparisonEndDate");
    $scope.compareDateRange = {
        startDate: compareStartDate,
        endDate: compareEndDate
    };
    $http.get("admin/tag/widgetTag/" + $stateParams.favouriteName).success(function (response) {
        var widgetItems = [];
        widgetItems = response;
        $http.get("admin/tag/getAllFav/").success(function (favResponse) {
            widgetItems.forEach(function (value, key) {
                var favWidget = $.grep(favResponse, function (b) {
                    return b.id === value.widgetId.id;
                });
                if (favWidget.length > 0) {
                    value.widgetId.isFav = true;
                } else {
                    value.widgetId.isFav = false;
                }
            });
            $http.get("admin/ui/getChartColorByUserId").success(function (response) {
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
                $scope.favouritesWidgets = widgetItems;
            }).error(function () {
                $scope.favouritesWidgets = widgetItems;

            });
            console.log($scope.favouritesWidgets);
        });

    });

    $scope.toggleFavourite = function (favouritesWidget, index) {
        var isFav = favouritesWidget.widgetId.isFav;
        if (isFav) {
            $http({method: 'POST', url: "admin/tag/removeFav/" + favouritesWidget.widgetId.id}).success(function (response) {
                $scope.favouritesWidgets.splice(index, 1);
            });
            favouritesWidget.widgetId.isFav = false;
        } else {
            $http({method: 'POST', url: "admin/tag/setFav/" + favouritesWidget.widgetId.id});
            favouritesWidget.widgetId.isFav = true;
        }
    };

    $scope.expandWidget = function (widget) {
        var expandchart = widget.chartType;
        widget.chartType = null;
        if (widget.width == 3) {
            widget.width = widget.width + 1;
        } else if (widget.width == 4) {
            widget.width = widget.width + 2;
        } else if (widget.width == 6) {
            widget.width = widget.width + 2;
        } else {
            widget.width = 12;
        }
        saveWidgetSize(widget, expandchart);
    };

    $scope.reduceWidget = function (widget) {
        var expandchart = widget.chartType;
        widget.chartType = null;

        if (widget.width == 12) {
            widget.width = widget.width - 4;
        } else if (widget.width == 8) {
            widget.width = widget.width - 2;
        } else if (widget.width == 6) {
            widget.width = widget.width - 2;
        } else {
            widget.width = 3;
        }
        saveWidgetSize(widget, expandchart);
    };

    function saveWidgetSize(widget, expandchart) {
        $timeout(function () {
            widget.chartType = expandchart;
            $http({method: widget.id ? 'PUT' : 'POST', url: 'admin/ui/editWidgetSize/' + widget.id + "?width=" + widget.width}).success(function (response) {
            });
//            var data = {
//                id: widget.id,
//                chartType: widget.chartType,
//                widgetTitle: widget.widgetTitle,
//                widgetColumns: widget.columns,
//                dataSourceId: widget.dataSourceId.id,
//                dataSetId: widget.dataSetId.id,
//                tableFooter: widget.tableFooter,
//                zeroSuppression: widget.zeroSuppression,
//                maxRecord: widget.maxRecord,
//                dateDuration: widget.dateDuration,
//                content: widget.content,
//                width: widget.width,
//                jsonData: widget.jsonData,
//                queryFilter: widget.queryFilter
//            };
//            $http({method: widget.id ? 'PUT' : 'POST', url: 'admin/ui/dbWidget/' + widget.tabId.id, data: data}).success(function (response) {
//            });
        }, 50);
    }

    $scope.deleteReportWidget = function (favouritesWidget, index) {                            //Delete Widget
        $http({method: 'DELETE', url: 'admin/ui/dbWidget/' + favouritesWidget.widgetId.id}).success(function (response) {
            $scope.favouritesWidgets.splice(index, 1);
        });
    };

    $http.get("admin/report/reportWidget").success(function (response) {
        $scope.reportWidgets = response;
    });

    $http.get('admin/report/getReport').success(function (response) {
        $scope.reportList = response;
    });
    $scope.addWidgetToReport = function (favouritesWidget) {
        var data = {};
        data.widgetId = favouritesWidget.widgetId.id;
        data.reportId = favouritesWidget.widgetId.reportWidget.id;
        $http({method: favouritesWidget.widgetId.id ? 'PUT' : 'POST', url: 'admin/report/reportWidget', data: data}).success(function (response) {
        });
        $scope.reportLogo = "";
        $scope.reportDescription = "";
        $scope.reportWidgetTitle = "";
        $scope.showReportWidgetName = false;
        $scope.showReportEmptyMessage = false;
    };

    $scope.selectReport = function (reportWidget) {
        $scope.showReportWidgetName = false;
        $scope.reportWidgetTitle = []
        $scope.reportLogo = reportWidget.logo;
        $scope.reportDescription = reportWidget.description;
        $http.get("admin/report/reportWidget/" + reportWidget.id + "?locationId=" + $stateParams.accountId).success(function (response) {
            if (response.length > 0) {
                $scope.showReportWidgetName = true;
                $scope.reportWidgetTitle = response;
                $scope.showReportEmptyMessage = false;
            } else {
                $scope.showReportEmptyMessage = true;
                $scope.reportEmptyMessage = "No Data Found"
            }
        });
    };

    $scope.goReport = function () {
        $state.go('index.report.reports', {accountId: $stateParams.accountId, accountName: $stateParams.accountName, startDate: $stateParams.startDate, endDate: $stateParams.endDate});
    };

    $scope.setLineChartFn = function (lineFn) {
        $scope.directiveLineFn = lineFn;
    };
    $scope.setAreaChartFn = function (areaFn) {
        $scope.directiveAreaFn = areaFn;
    };
    $scope.setBarChartFn = function (barFn) {
        $scope.directiveBarFn = barFn;
    };
    $scope.setPieChartFn = function (pieFn) {
        $scope.directivePieFn = pieFn;
    };
    $scope.setStackedBarChartFn = function (stackedBarChartFn) {
        $scope.directiveStackedBarChartFn = stackedBarChartFn;
    };
    $scope.setTableChartFn = function (tableFn) {
        $scope.directiveTableFn = tableFn;
    };
    $scope.setTickerFn = function (tickerFn) {
        $scope.directiveTickerFn = tickerFn;
    };
    $scope.setFunnelFn = function (funnelFn) {
        $scope.directiveFunnelFn = funnelFn;
    };

    $scope.moveWidget = function (list, from, to) {
        list.splice(to, 0, list.splice(from, 1)[0]);
        return list;
    };

    $scope.onDropComplete = function (index, favWidget, evt) {
        if (favWidget !== "" && favWidget !== null) {
            var otherObj = $scope.favouritesWidgets[index];
            var otherIndex = $scope.favouritesWidgets.indexOf(favWidget);
            $scope.favouritesWidgets = $scope.moveWidget($scope.favouritesWidgets, otherIndex, index);
//            $scope.favouritesWidgets[index] = favWidget;
//            $scope.favouritesWidgets[otherIndex] = otherObj;
            var favWidgetOrder = $scope.favouritesWidgets.map(function (value, key) {
                if (!value) {
                    return;
                }
                return value.id;
            }).join(',');
            if (favWidgetOrder) {
                console.log(favWidgetOrder)
                $http({method: 'GET', url: 'admin/tag/favWidgetUpdateOrder/' + favWidget.id + "?widgetOrder=" + favWidgetOrder});
            }
        }
    };
});