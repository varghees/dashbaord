app.controller("ReportController", function ($scope, $http, $stateParams, $state, httpService, $window, $translate) {
    var authObj = {};
    var schedulerObj = {};
    authObj = angular.fromJson(sessionStorage.getItem('auth'));
    authObj.lan = sessionStorage.getItem('agencyLanguage');

    schedulerObj.permission = authObj.permission;
    schedulerObj.startDate = $stateParams.startDate;
    schedulerObj.endDate = $stateParams.endDate;
    schedulerObj.reportId = $stateParams.reportId;
    schedulerObj.accountId = $stateParams.accountId;
    schedulerObj.userId = authObj.id;
    $scope.saveBtnIsDisable = true;
//    console.log(schedulerObj.userId);
    schedulerObj.accountName = $stateParams.accountName;
    $scope.reportWidgets = [];
    if (schedulerObj.permission.scheduleReport === true) {
        $scope.showSchedulerReport = true;
    } else {
        $scope.showSchedulerReport = false;
    }

    schedulerObj.lan = $stateParams.lan;//$cookies.getObject("agencyLanguage");

    var lan = schedulerObj.lan;
    changeLanguage(lan);

    function changeLanguage(key) {
        $translate.use(key);
    }

    schedulerObj.getTableType = $stateParams.getTableType;
//    var compareStartDate = localStorageService.get("comparisonStartDate");
    var compareStartDate = $stateParams.compareStartDate;
//    var compareEndDate = localStorageService.get("comparisonEndDate");
    var compareEndDate = $stateParams.compareEndDate;

    $scope.compareDateRange = {
        startDate: compareStartDate,
        endDate: compareEndDate
    };

    $scope.schedulerRepeats = ["Now", "Once", "Daily", "Weekly", "Monthly"];
//    $scope.schedulerRepeats = ["Now", "Once", "Daily", "Weekly", "Monthly", "Yearly", "Year Of Week"];
    $scope.weeks = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

    function getWeeks(d) {
        var first = new Date(d.getFullYear(), 0, 1);
        var dayms = 1000 * 60 * 60 * 24;
        var numday = ((d - first) / dayms);
        var weeks = Math.ceil((numday + first.getDay() + 1) / 7);
        return weeks;
    }
    function getLastDayOfYear(datex)
    {
        var year = datex.getFullYear();
        var month = 12;
        var day = 31;
        return month + "/" + day + "/" + year;
    }
    var endOfYearDate = getLastDayOfYear(new Date());
    var endOfYear = getWeeks(new Date(endOfYearDate));
    $scope.totalYearOfWeeks = [];
    for (var i = 1; i <= endOfYear; i++) {
        $scope.totalYearOfWeeks.push(i);
    }

    var reportUrl = 'admin/report/getReport';
    var report = httpService.httpProcess('GET', reportUrl);
    report.then(function (response) {
        console.log(response)

//    $http.get("admin/report/getReport").success(function (response) {
        schedulerObj.reports = response;
    });

    $scope.addReportToScheduler = function (scheduler, report) {
        if (scheduler.schedulerRepeatType === "Now") {
            scheduler.schedulerNow = new Date();
        }
        scheduler.reportId = report.id;

        var addReportToSchedulerUrl = 'admin/scheduler/scheduler';
        var reqMethod = scheduler.id ? 'PUT' : 'POST';
        var addReportToScheduler = httpService.httpProcess(reqMethod, addReportToSchedulerUrl, scheduler);
        addReportToScheduler.then(function (response) {

//        $http({method: scheduler.id ? 'PUT' : 'POST', url: 'admin/scheduler/scheduler', data: scheduler}).success(function (response) {
        });

        // $scope.scheduler = "";
    };

    $scope.deleteReport = function (report, index) {
        $http({method: 'DELETE', url: 'admin/report/' + report.id}).success(function (response) {
            schedulerObj.reports.splice(index, 1);
        });
    };

    $scope.downloadReportPdf = function (report) {
        var url = "admin/proxy/downloadReport/" + report.id + "?dealerId=" + $stateParams.accountId + "&location=" + $stateParams.accountId + "&accountId=" + $stateParams.accountId + "&startDate=" + $stateParams.startDate + "&endDate=" + $stateParams.endDate + "&exportType=pdf";
        $window.open(url);
    };

    $scope.goScheduler = function () {
        $state.go("index.schedulerIndex.scheduler", {
            accountId: $stateParams.accountId,
            accountName: $stateParams.accountName,
            startDate: $stateParams.startDate,
            endDate: $stateParams.endDate
        });
    };
    $scope.schedulerObj = schedulerObj;
});
