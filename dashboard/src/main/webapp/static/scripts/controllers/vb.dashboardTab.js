app.controller('UiController', function ($scope, $http, $stateParams, $state, $filter, $cookies, $timeout, localStorageService) {
    $scope.userName = $cookies.getObject("username");
    $scope.permission = localStorageService.get("permission");
    $scope.accountId = $stateParams.accountId;
    $scope.accountName = $stateParams.accountName;
    //$scope.tabId = $stateParams.tabId;
    //get Templates
    $http.get('admin/ui/dashboardTemplates/'+$stateParams.productId).success(function (response) {
        $scope.templates = response;
    });
    //product tabs
    $scope.tabs = [];
    console.log($stateParams.productId);
    if ($stateParams.productId) {
        $http.get("admin/ui/dbTabs/" + $stateParams.productId + "/" + $stateParams.accountId).success(function (response) {
            console.log(response);
            var setTabId;
            if (!response) {
                setTabId = "";
            }
            if (!response[0]) {
                setTabId = "";
            } else {
                if ($stateParams.tabId == 0) {
                    setTabId = response[0].id;
                } else {
                    setTabId = $stateParams.tabId ? $stateParams.tabId : (response[0].id ? response[0].id : 0);
                }
            };
            console.log(setTabId);
            $scope.loadTab = false;
            $scope.tabs = response;
            angular.forEach($scope.tabs, function (value, key) {
                $scope.dashboardName = value.agencyProductId.productName;
            });
            $state.go("index.dashboard.widget", {accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: setTabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate});
        });
    }
    $scope.toDate = function (strDate) {
        if (!strDate) {
            return new Date();
        }
        var from = strDate.split("/");
        var f = new Date(from[2], from[0] - 1, from[1]);
        return f;
    };

    $scope.getDay = function () {
        var today = new Date();
        var yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 30);
        return yesterday;
    };

    $scope.getBeforeDay = function () {
        var today = new Date();
        var yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        return yesterday;
    };

    $scope.firstDate = $stateParams.startDate ? $scope.toDate(decodeURIComponent($stateParams.startDate)) : $scope.getDay().toLocaleDateString("en-US");
    $scope.lastDate = $stateParams.endDate ? $scope.toDate(decodeURIComponent($stateParams.endDate)) : $scope.getBeforeDay().toLocaleDateString("en-US");

    if (!$stateParams.startDate) {
        $stateParams.startDate = $scope.firstDate;
    }
    if (!$stateParams.endDate) {
        $stateParams.endDate = $scope.lastDate;
    }

    try {
        $scope.startDate = moment($('#daterange-btn').data('daterangepicker').startDate).format('MM/DD/YYYY') ? moment($('#daterange-btn').data('daterangepicker').startDate).format('MM/DD/YYYY') : $scope.firstDate;//$scope.startDate.setDate($scope.startDate.getDate() - 1);

        $scope.endDate = moment($('#daterange-btn').data('daterangepicker').endDate).format('MM/DD/YYYY') ? moment($('#daterange-btn').data('daterangepicker').endDate).format('MM/DD/YYYY') : $scope.lastDate;
    } catch (e) {
    }

//Edit Dashboard Tab
    $scope.editDashboardTab = function (tab) {
        var data = {
            tabNames: tab.tabName
        };
        $scope.tab = data;
        $timeout(function () {
            $('#editTab' + tab.id).modal('show');
        }, 100);
    };

    $scope.selectProductName = "Select Product";
    $scope.changeProduct = function (product) {
        $scope.selectProductName = product.productName;
        $scope.productId = product.id;
    };

    $http.get('admin/user/sampleDealers').success(function (response) {
        $scope.dealers = response;
    });

    $scope.loadTab = true;

    var dates = $(".pull-right i").text();

    $scope.tabs = [];
    //Add Tab
    $scope.addTab = function (tab) {
        var data = {
            tabName: tab.tabName
        };
        $http({method: 'POST', url: 'admin/ui/dbTabs/' + $stateParams.productId + "/" + $stateParams.accountId, data: data}).success(function (response) {
            $stateParams.tabId = "";
            $scope.tabs.push({id: response.id, tabName: tab.tabName, tabClose: true});
            $stateParams.tabId = $scope.tabs[$scope.tabs.length - 1].id;
            $state.go("index.dashboard.widget", {accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate});
        });
        $scope.tab = "";
    };
//Delete Tab
    $scope.deleteTab = function (index, tab) {
        $http({method: 'DELETE', url: 'admin/ui/dbTab/' + tab.id}).success(function (response) {
            $http.get("admin/ui/dbTabs/" + $stateParams.productId + "/" + $stateParams.accountId).success(function (response) {
                $scope.loadTab = false;
                $scope.tabs = response;
                $stateParams.tabId = $scope.tabs[$scope.tabs.length - 1].id;
                $state.go("index.dashboard.widget", {accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate});
            });
        });
    };

    $scope.reports = [];
    $scope.addParent = function () {
        $scope.reports.push({isEdit: true, childItems: []});
    };
    $scope.addChild = function (report) {
        report.childItems.push({isEdit: true});
    };
    //Get Reports
    $http.get('static/datas/report.json').success(function (response) {
        $scope.reports = response;
    });
//Drag and Drop in Tab
    $scope.moveItem = function (list, from, to) {
        list.splice(to, 0, list.splice(from, 1)[0]);
        return list;
    };

    $scope.onDropTabComplete = function (index, tab, evt) {
        if (tab !== "" && tab !== null) {
            var otherObj = $scope.tabs[index];
            var otherIndex = $scope.tabs.indexOf(tab);

            $scope.tabs = $scope.moveItem($scope.tabs, otherIndex, index);
            var tabOrder = $scope.tabs.map(function (value, key) {
                if (value) {
                    return value.id;
                }
            }).join(',');
            if (tabOrder) {
                $http({method: 'GET', url: 'admin/ui/dbTabUpdateOrder/' + $stateParams.productId + "?tabOrder=" + tabOrder});
            }
        }
    };

    $scope.editedItem = null;
    $scope.startEditing = function (tab) {
        tab.editing = true;
        $scope.editedItem = tab;
    };

    $scope.doneEditing = function (tab) {
        var data = {
            id: tab.id,
            createdTime: tab.createdTime,
            dashboardId: tab.dashboardId,
            agencyProductId: tab.agencyProductId,
            modifiedTime: tab.modifiedTime,
            remarks: tab.remarks,
            status: tab.status,
            tabName: tab.tabName,
            tabOrder: tab.tabOrder,
            accountId: tab.accountId,
            userId: tab.userId
        };
        $http({method: 'PUT', url: 'admin/ui/dbTabs/' + $stateParams.productId, data: data});
        tab.editing = false;
        $scope.editedItem = null;
    };
    $scope.templateId = null;
    $scope.getTemplateId = function () {
        $http.get('admin/ui/getTemplateId/' + $stateParams.accountId + '/' + $stateParams.productId).success(function (response) {
            if (!response) {
                return;
            } else {
                $scope.templateId = response.id;
            }
        })
    }
    //save Template
    $scope.saveTemplate = function (tab) {
        console.log(tab.templateName);

        var data = {
            id: $scope.templateId,
            templateName: tab.templateName,
        }
        $http({method: 'POST', url: 'admin/ui/saveTemplate/' + $stateParams.accountId + '/' + $stateParams.productId, data: data}).success(function (response) {

        })

    }
})
        .directive('ngBlur', function () {
            return function (scope, elem, attrs) {
                elem.bind('blur', function () {
                    scope.$apply(attrs.ngBlur);
                });
            };
        })

        .directive('ngFocus', function ($timeout) {
            return function (scope, elem, attrs) {
                scope.$watch(attrs.ngFocus, function (newval) {
                    if (newval) {
                        $timeout(function () {
                            elem[0].focus();
                        }, 0, false);
                    }
                });
            };
        });
