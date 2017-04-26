app.controller("NewOrEditSchedulerController", function ($scope, $http, $stateParams, $filter) {
//    $(document).ready(function (e) {
//        $("#customDateRange").click(function (e) {
//            e.stopPropagation();
//            $(".scheduler-list-style").show();
//        });
//        $(".dropdown").click(function (e) {
//            $(".scheduler-list-style").show();
//        });
//         $(document).on("click", function (e) {
//            e.stopPropagation();
//            $(".scheduler-list-style").hide();
//        });
//        $("body").on("click", function (e) {
//            $(".scheduler-list-style").show();
//        });
//        
//        $(".ranges ul > li").on("click",function(e){
//            $(".dropdown button").text($(this).text());
//        });
//    });

    $scope.accountId = $stateParams.accountId;
    $scope.accountName = $stateParams.accountName;
    $scope.startDate = $stateParams.startDate;
    $scope.endDate = $stateParams.endDate;
    $scope.schedulerRepeats = ["Now", "Once", "Daily", "Weekly", "Monthly"];
//    $scope.schedulerRepeats = ["Now", "Once", "Daily", "Weekly", "Monthly", "Yearly", "Year Of Week"];
    $scope.weeks = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

    $http.get("admin/ui/report").success(function (response) {
        $scope.reports = response;
    });
    $scope.accounts = [];


    var unique = function (origArr) {
        var newArr = [],
                origLen = origArr.length,
                found, x, y;

        for (x = 0; x < origLen; x++) {
            found = undefined;
            for (y = 0; y < newArr.length; y++) {
                if (origArr[x] === newArr[y]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newArr.push(origArr[x]);
            }
        }
        return newArr;
    }


    $http.get('admin/user/account').success(function (response) {
        $scope.emailAccounts = [];
        $scope.accounts = response;
        angular.forEach($scope.accounts, function (val, key) {
            $scope.emailAccounts.push(val.agencyId.email)
            $scope.accountEmails = unique($scope.emailAccounts);
        });
    });

    function dateFormat(date) {
        return $filter('date')(date, 'MM/dd/yyyy')
    }

    $scope.scheduler = {};
    $http.get("admin/scheduler/scheduler/" + $stateParams.schedulerId).success(function (response) {
        if (!response) {
            $scope.scheduler = {
                accountId: {id: $stateParams.accountId, accountName: $stateParams.accountName}
            }
            return;
        }
        var email;
        if (response.schedulerEmail == null || "") {
            email = ''
        } else {
            email = response.schedulerEmail.split(',');
        }
        $scope.schedulers = response;
        $scope.scheduler = {
            id: response.id,
            schedulerName: response.schedulerName,
            startDate: dateFormat(response.startDate),
            endDate: dateFormat(response.endDate),
            schedulerRepeatType: response.schedulerRepeatType,
            schedulerWeekly: response.schedulerWeekly,
            schedulerTime: response.schedulerTime,
            schedulerMonthly: response.schedulerMonthly,
            schedulerYearly: response.schedulerYearly,
            schedulerYearOfWeek: response.schedulerYearOfWeek,
            reportId: response.reportId,
            schedulerType: response.schedulerType,
            schedulerEmail: email,
            dateRangeName: response.dateRangeName,
            accountId: response.accountId,
            lastNdays: response.lastNdays,
            lastNweeks: response.lastNweeks,
            lastNmonths: response.lastNmonths,
            lastNyears: response.lastNyears,
            customStartDate: response.customStartDate,
            customEndDate: response.customEndDate,
            isAccountEmail: response.isAccountEmail,
            lastExecutionStatus: response.lastExecutionStatus,
            status: response.status
        };
    });

    $scope.addNewScheduler = function () {
        $scope.scheduler = "";
    };

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
    var endOfYear = getWeeks(new Date(endOfYearDate))
    $scope.totalYearOfWeeks = [];
    for (var i = 1; i <= endOfYear; i++) {
        $scope.totalYearOfWeeks.push(i);
    }

    $scope.selectDuration = function (dateRangeName, scheduler) {
        //scheduler.dateRangeName = dateRangeName;
        console.log(dateRangeName)
        if (dateRangeName == 'Last N Days') {
            if (scheduler.lastNdays) {
                $scope.scheduler.dateRangeName = "Last " + scheduler.lastNdays + " Days";
            } else {
                $scope.scheduler.dateRangeName = "Last 0 Days";
            }
            $scope.scheduler.lastNweeks = "";
            $scope.scheduler.lastNmonths = "";
            $scope.scheduler.lastNyears = "";
        } else if (dateRangeName == 'Last N Weeks') {
            if (scheduler.lastNweeks) {
                $scope.scheduler.dateRangeName = "Last " + scheduler.lastNweeks + " Weeks";
            } else {
                $scope.scheduler.dateRangeName = "Last 0 Weeks";
            }
            $scope.scheduler.lastNdays = "";
            $scope.scheduler.lastNmonths = "";
            $scope.scheduler.lastNyears = "";
        } else if (dateRangeName == 'Last N Months') {
            if (scheduler.lastNmonths) {
                $scope.scheduler.dateRangeName = "Last " + scheduler.lastNmonths + " Months";
            } else {
                $scope.scheduler.dateRangeName = "Last 0 Months";
            }
            $scope.scheduler.lastNdays = "";
            $scope.scheduler.lastNweeks = "";
            $scope.scheduler.lastNyears = "";
        } else if (dateRangeName == 'Last N Years') {
            if (scheduler.lastNyears) {
                $scope.scheduler.dateRangeName = "Last " + scheduler.lastNyears + " Years";
            } else {
                $scope.scheduler.dateRangeName = "Last 0 Years";
            }
            $scope.scheduler.lastNdays = "";
            $scope.scheduler.lastNweeks = "";
            $scope.scheduler.lastNmonths = "";
        } else {
            $scope.scheduler.dateRangeName = dateRangeName;
            $scope.scheduler.lastNdays = "";
            $scope.scheduler.lastNweeks = "";
            $scope.scheduler.lastNmonths = "";
            $scope.scheduler.lastNyears = "";
        }
    }
    $scope.saveScheduler = function (scheduler) {
        try {
            $scope.customStartDate = moment($('#customDateRange').data('daterangepicker').startDate).format('MM/DD/YYYY') ? moment($('#customDateRange').data('daterangepicker').startDate).format('MM/DD/YYYY') : $stateParams.startDate;//$scope.startDate.setDate($scope.startDate.getDate() - 1);

            $scope.customEndDate = moment($('#customDateRange').data('daterangepicker').endDate).format('MM/DD/YYYY') ? moment($('#customDateRange').data('daterangepicker').endDate).format('MM/DD/YYYY') : $stateParams.endDate;
        } catch (e) {

        }
        if (scheduler.schedulerRepeatType === "Now") {
            scheduler.schedulerNow = new Date();
            scheduler.schedulerTime = null;
            scheduler.startDate = null;
            scheduler.endDate = null;
            scheduler.schedulerWeekly = null;
            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Once") {
            scheduler.schedulerNow = null;
            //scheduler.schedulerTime = scheduler.schedulerTime;
            scheduler.schedulerWeekly = null;
            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Daily") {
            scheduler.schedulerNow = null;
            //scheduler.schedulerTime = scheduler.schedulerTime;
            scheduler.schedulerWeekly = null;
            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Weekly") {
            scheduler.schedulerNow = null;
            //scheduler.schedulerTime = scheduler.schedulerTime;
//            scheduler.schedulerWeekly= null;
            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Monthly") {
            scheduler.schedulerNow = null;
            scheduler.schedulerTime = null;
            scheduler.schedulerWeekly = null;
//            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Yearly") {
            scheduler.schedulerNow = null;
            scheduler.schedulerTime = null;
            scheduler.schedulerWeekly = null;
            scheduler.schedulerMonthly = null;
//            scheduler.schedulerYearly = null;
            scheduler.schedulerYearOfWeek = null;
        } else if (scheduler.schedulerRepeatType === "Year Of Week") {
            scheduler.schedulerNow = null;
//            scheduler.schedulerTime = null;
//            scheduler.schedulerWeekly = null;
            scheduler.schedulerMonthly = null;
            scheduler.schedulerYearly = null;
//            scheduler.schedulerYearOfWeek = null;
        } else {
            return null;
        }

        var emails = scheduler.schedulerEmail.map(function (value, key) {
            if (value) {
                return value;
            }
        }).join(',');
        if (scheduler.dateRangeName === 'Custom') {
            scheduler.customStartDate = $scope.customStartDate;
            scheduler.customEndDate = $scope.customEndDate;
        } else {
            scheduler.customStartDate = "";
            scheduler.customEndDate = "";
        }

        scheduler.schedulerEmail = emails;
        console.log(scheduler)
        $http({method: scheduler.id ? 'PUT' : 'POST', url: 'admin/scheduler/scheduler', data: scheduler}).success(function (response) {
        });
        $scope.scheduler = "";
    };

    $(function () {
        var start = moment();
        var end = moment();
        function cb(start, end) {
            $('#customDateRange span').html(start.format('MM-DD-YYYY') + ' - ' + end.format('MM-DD-YYYY'));
        }
        //Initialize Select2 Elementsmouseenter
        $(".select2").select2();
        //Datemask dd/mm/yyyy
        $("#datemask").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
        //Datemask2 mm/dd/yyyy
        $("#datemask2").inputmask("mm/dd/yyyy", {"placeholder": "mm/dd/yyyy"});
        //Money Euro
        $("[data-mask]").inputmask();
        //Date range picker
        $('#reservation').daterangepicker();
        //Date range picker with time picker
        $('#reservationtime').daterangepicker({timePicker: true, timePickerIncrement: 30, format: 'MM/DD/YYYY h:mm A'});
        //Date range as a button
        $('#customDateRange').daterangepicker(
                {startDate: start,
                    endDate: end,
                    ranges: {
                        'Today': [moment(), moment()],
                        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                        'Last 14 Days ': [moment().subtract(13, 'days'), moment()],
                        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                        'This Week (Sun - Today)': [moment().startOf('week'), moment().endOf(new Date())],
//                        'This Week (Mon - Today)': [moment().startOf('week').add(1, 'days'), moment().endOf(new Date())],
                        'Last Week (Sun - Sat)': [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')],
//                        'Last 2 Weeks (Sun - Sat)': [moment().subtract(2, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')],
//                        'Last Week (Mon - Sun)': [moment().subtract(1, 'week').startOf('week').add(1, 'days'), moment().subtract(1, 'week').add(1, 'days').endOf('week').add(1, 'days')],
//                        'Last Business Week (Mon - Fri)': [moment().subtract(1, 'week').startOf('week').add(1, 'days'), moment().subtract(1, 'week').add(1, 'days').endOf('week').subtract(1, 'days')],
                        'This Month': [moment().startOf('month'), moment().endOf(new Date())],
                        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
//                        'Last 2 Months': [moment().subtract(2, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
//                        'Last 3 Months' : [moment().subtract(3, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                        'This Year': [moment().startOf('year'), moment().endOf(new Date())],
                        'Last Year': [moment().subtract(1, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')],
//                        'Last 2 Years': [moment().subtract(2, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')]
//                        'Last 3 Years': [moment().subtract(3, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')]
                    },

                    maxDate: new Date()
                }, cb);
        cb(start, end);
        //Date picker
        $('#datepicker').datepicker({
            autoclose: true
        });
        //iCheck for checkbox and radio inputs
        $('input[type="checkbox"].minimal,  input[type="radio"].minimal').iCheck({
            checkboxClass: 'icheckbox_minimal-bluee',
            radioClass: 'iradio_minimal-blue'
        });
        //Red color scheme for iCheck
        $('input[type="checkbox"].minimal-red, input[type="radio"].minimal-red').iCheck({
            checkboxClass: 'icheckbox_minimal-red',
            radioClass: 'iradio_minimal-red'
        });
        //Flat red color scheme for iCheck
        $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
            checkboxClass: 'icheckbox_flat-green',
            radioClass: 'iradio_flat-green'
        });
        //Colorpicker
        $(".my-colorpicker1").colorpicker();
        //color picker with addon
        $(".my-colorpicker2").colorpicker();
        //Timepicker
        $(".timepicker").timepicker({
            showInputs: false
        });

        //$("#config-demo").click(function (e) {       

    });
});
app.directive('jqdatepicker', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl, ctrl) {
            $(element).datetimepicker({
                format: 'm/d/Y',
                timepicker: false,
                onSelect: function (date) {
                    ctrl.$setViewValue(date);
                    ctrl.$render();
                    scope.$apply();
                }
            });
        }
    };
});
app.directive('jqdatepickerTime', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl, ctrl) {
            $(element).datetimepicker({
                //format: 'm/d/Y',
                datepicker: false,
                format: 'H:i',
//                step: 5,
                onSelect: function (date) {
                    ctrl.$setViewValue(date);
                    ctrl.$render();
                    scope.$apply();
                }
            });
        }
    };
});
app.directive('jqdatetimepicker', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl, ctrl) {
            $(element).datetimepicker({
                format: 'm/d/y H:i',
//                step: 5,
                onSelect: function (date) {
                    ctrl.$setViewValue(date);
                    ctrl.$render();
                    scope.$apply();
                }
            });
        }
    };
});