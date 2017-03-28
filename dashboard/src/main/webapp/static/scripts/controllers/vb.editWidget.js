app.controller('EditWidgetController', function ($scope, $http, $stateParams, localStorageService, $timeout, $filter, $state) {
    $scope.editWidgetData = []
    $scope.permission = localStorageService.get("permission");
    $scope.accountID = $stateParams.accountId;
    $scope.accountName = $stateParams.accountName;
    $scope.productID = $stateParams.productId;
    $scope.widgetTabId = $stateParams.tabId;
    $scope.widgetStartDate = $stateParams.startDate;
    $scope.widgetEndDate = $stateParams.endDate;

    $http.get("admin/ui/dbWidget/" + $stateParams.tabId).success(function (response) {
        $scope.widgets = response;
        if ($stateParams.widgetId) {
            $scope.editWidgetData.push($filter('filter')($scope.widgets, {id: $stateParams.widgetId})[0]);
            angular.forEach($scope.editWidgetData, function (value, key) {
                $scope.editWidget(value)
            })
        }
    });

    $scope.selectAggregations = [
        {name: 'None', value: ""},
        {name: 'Sum', value: "sum"},
        {name: 'CTR', value: "ctr"},
        {name: 'CPC', value: "cpc"},
        {name: 'CPCS', value: "cpcs"},
        {name: 'CPS', value: "cps"},
        {name: 'CPA', value: "cpa"},
        {name: 'CPAS', value: "cpas"},
        {name: 'Avg', value: "avg"},
        {name: 'Count', value: "count"},
        {name: 'Min', value: "min"},
        {name: 'Max', value: "max"},
        {name: 'CPL', value: "cpl"},
        {name: 'CPLC', value: "cplc"},
        {name: 'CPComment', value: "cpcomment"},
        {name: 'CPostE', value: "cposte"},
        {name: 'CPageE', value: "cpagee"},
        {name: 'CPP', value: "cpp"},
        {name: 'CPR', value: "cpr"}

    ];   //Aggregation Type-Popup
    $scope.selectGroupPriorities = [
        {num: 'None', value: ""},
        {num: 1, value: 1},
        {num: 2, value: 2}
    ];
    $scope.selectDateDurations = [
        {duration: "None", value: 'none'},
        {duration: "Today", value: 'today'},
        {duration: "Last N days", value: ''},
        {duration: "Last N Weeks", value: ''},
        {duration: "Last N Months", value: ''},
        {duration: "This Month", value: 'thisMonth'},
        {duration: "This Year", value: 'thisYear'},
        {duration: "Last Year", value: 'lastYear'},
        {duration: "Yesterday", value: 'yesterday'},
        {duration: "Custom", value: 'custom'}
    ]; // Month Durations-Popup
    $scope.selectXAxis = [
        {label: 'None', value: ""},
        {label: "X-1", value: 1}
    ];
    $scope.selectYAxis = [
        {label: 'None', value: ""},
        {label: "Y-1", value: 1},
        {label: "Y-2", value: 2}
    ];
    $scope.alignments = [
        {name: '', displayName: 'None'},
        {name: "left", displayName: "Left"},
        {name: "right", displayName: "Right"},
        {name: "center", displayName: "Center"}
    ];
    $scope.sorting = [
        {name: 'None', value: ''},
        {name: 'asc', value: 'asc'},
        {name: 'desc', value: 'desc'}
    ];
    $scope.tableWrapText = [
        {name: 'None', value: ''},
        {name: 'Yes', value: "yes"}
    ];
    $scope.hideOptions = [
        {name: 'Yes', value: 1},
        {name: 'No', value: ''}
    ];
    $scope.isEditPreviewColumn = false;

    $scope.formats = [
        {name: "Currency", value: '$,.2f'},
        {name: "Integer", value: ',.0f'},
        {name: "Percentage", value: ',.2%'},
        {name: "Decimal1", value: ',.1f'},
        {name: "Decimal2", value: ',.2f'},
        {name: "None", value: ''}
    ];

    $('.dropdown-menu input').click(function (e) {
        e.stopPropagation();
    });

    $scope.dateDuration = function (widget, selectDateDuration) {
        widget.duration = selectDateDuration.duration;
    };

    $http.get('admin/ui/dataSource').success(function (response) {
        $scope.dataSources = response;
    });

    $scope.selectDataSource = function (dataSourceName, widget) {
        if (!dataSourceName) {
            return;
        }
        $http.get('admin/ui/dataSet').success(function (response) {
            $scope.dataSets = []
            angular.forEach(response, function (value, key) {
                if (value.dataSourceId.name == dataSourceName.name) {
                    $scope.dataSets.push(value);
                }
            });
        });
    };

    $scope.widgets = [];
    function getWidgetItem() {      //Default Loading Items
        if (!$stateParams.tabId) {
            $stateParams.tabId = 1;
        }
        $http.get("admin/ui/dbWidget/" + $stateParams.tabId).success(function (response) {
            $scope.widgets = response;
        });
    }
    getWidgetItem();
    $scope.collectionField = {};
    $scope.dispName = function (currentColumn) {
        $scope.filterName = $filter('filter')($scope.collectionFields, {fieldName: currentColumn.fieldName})[0];
        currentColumn.displayName = $scope.filterName.displayName;
    };


    $scope.editWidgetItems = [{previewTitle: "test"}]
    $scope.editWidget = function (widget) {    //Edit widget
        $scope.editPreviewTitle = false;
        $scope.y1Column = [];
        $scope.y2Column = [];
        $scope.tickerItem = []
        $scope.editWidgetItems.push(widget);
        $scope.tableDef(widget);
        $scope.selectedRow = widget.chartType;
        widget.previewUrl = widget.dataSetId;//widget.directUrl;
        $scope.selectDataSource(widget.dataSourceId, widget)
        widget.previewType = widget.chartType;
        $scope.editChartType = widget.chartType;
        $scope.previewChart(widget, widget);
        angular.forEach(widget.columns, function (value, key) {
            angular.forEach($scope.formats, function (val, header) {
                if (value.displayFormat === val.value) {
                    $scope.tickerItem.push({displayName: value.displayName, displayFormat: {name: val.name, value: val.value}})
                }
            })
        })
        angular.forEach(widget.columns, function (val, key) {
            if (val.xAxis == 1) {
                $scope.xColumn = val;
                $scope.selectX1Axis(widget, val)
//                $scope.chartFormats.push(val)
            }
            if (val.yAxis == 1) {
                $scope.y1Column.push(val);
                $scope.selectY1Axis(widget, val, null)
//                $scope.chartFormats.push(val)
            }
            if (val.yAxis == 2) {
                $scope.y2Column.push(val);
                $scope.selectY2Axis(widget, val, null)
//                $scope.chartFormats.push(val)
            }
            if (val.xAxis == 1 && val.yAxis == 1) {
                $scope.selectPieChartAxis = val;
            }
        })
    };

    $scope.tableDef = function (widget) {      //Dynamic Url from columns Type data - Popup
        if (widget.columns) {
            widget.columns = widget.columns;
            if (widget.dataSetId) {
                var url = "admin/proxy/getJson?url=../dbApi/admin/dataSet/getData&";
                if (widget.dataSetId.dataSourceId.dataSourceType == "csv") {
                    url = "admin/csv/getData?";
                }
                if (widget.dataSetId.dataSourceId.dataSourceType == "facebook") {
                    url = "admin/proxy/getFbData?";
                }
                $http.get(url + 'connectionUrl=' + widget.dataSetId.dataSourceId.connectionString + "&dataSetId=" + widget.dataSetId.id + "&accountId=" + $stateParams.accountId + "&driver=" + widget.dataSetId.dataSourceId.sqlDriver + "&location=" + $stateParams.locationId + "&startDate=" + $stateParams.startDate + "&endDate=" + $stateParams.endDate + '&username=' + widget.dataSetId.dataSourceId.userName + '&password=' + widget.dataSetId.dataSourceId.password + '&port=3306&schema=vb&query=' + encodeURI(widget.dataSetId.query) + "&fieldsOnly=true").success(function (response) {
                    $scope.collectionFields = [];
                    $scope.collectionFields = response.columnDefs;
                    console.log("Table Def 1: ")
                    console.log($scope.collectionFields)
                });
            }
        } else {
            if (widget.dataSetId) {
                var url = "admin/proxy/getJson?url=../dbApi/admin/dataSet/getData&";
                if (widget.dataSetId.dataSourceId.dataSourceType == "csv") {
                    url = "admin/csv/getData?";
                }
                if (widget.dataSetId.dataSourceId.dataSourceType == "facebook") {
                    url = "admin/proxy/getFbData?";
                }
                $http.get(url + 'connectionUrl=' + widget.dataSetId.dataSourceId.connectionString + "&dataSetId=" + widget.dataSetId.id +"&accountId=" + $stateParams.accountId + "&driver=" + widget.dataSetId.dataSourceId.sqlDriver + "&location=" + $stateParams.locationId + "&startDate=" + $stateParams.startDate + "&endDate=" + $stateParams.endDate + '&username=' + widget.dataSetId.dataSourceId.userName + '&password=' + widget.dataSetId.dataSourceId.password + '&port=3306&schema=vb&query=' + encodeURI(widget.dataSetId.query) + "&fieldsOnly=true").success(function (response) {
                    $scope.collectionFields = [];
                    widget.columns = response.columnDefs;
                    $scope.collectionFields = response.columnDefs;
                    console.log("table Def 2 :")
                    console.log($scope.collectionFields)
                });
            }
        }
    };

    $scope.changeUrl = function (dataSet, widget) {
        if (!dataSet) {
            return;
        }
        $scope.editChartType = null;
        widget.previewUrl = dataSet;
        widget.columns = [];
        var chartType = widget
        var url = "admin/proxy/getJson?url=../dbApi/admin/dataSet/getData&";
        if (dataSet.dataSourceId.dataSourceType == "csv") {
            url = "admin/csv/getData?";
        }
        if (widget.dataSetId.dataSourceId.dataSourceType == "facebook") {
            url = "admin/proxy/getFbData?";
        }
        $http.get(url + 'connectionUrl=' + dataSet.dataSourceId.connectionString + "&dataSetId=" + dataSet.id + "&accountId=" + $stateParams.accountId + "&driver=" + dataSet.dataSourceId.sqlDriver + "&location=" + $stateParams.locationId + "&startDate=" + $stateParams.startDate + "&endDate=" + $stateParams.endDate + '&username=' + dataSet.dataSourceId.userName + '&password=' + dataSet.dataSourceId.password + '&port=3306&schema=vb&query=' + encodeURI(dataSet.query) + "&fieldsOnly=true").success(function (response) {
            $scope.collectionFields = [];
            angular.forEach(response.columnDefs, function (value, key) {
                widget.columns.push({fieldName: value.fieldName, displayName: value.displayName,
                    agregationFunction: value.agregationFunction, displayFormat: value.displayFormat, fieldType: value.type,
                    groupPriority: value.groupPriority, sortOrder: value.sortOrder, sortPriority: value.sortPriority});

            });
            angular.forEach(response.columnDefs, function (value, key) {
//                angular.forEach(value, function (value, key) {
                $scope.collectionFields.push(value);
//                });
            });
            $timeout(function () {
                $scope.previewChart(chartType, widget)
            }, 300);
        });
    };

    $scope.pageRefresh = function () {          //Page Refresh
        getWidgetItem();
    };
    $http.get("static/datas/panelSize.json").success(function (response) {      //Default Panel in Ui
        $scope.newWidgets = response;
    });

    $scope.openPopup = function (response) {
        $timeout(function () {
            $scope.editWidget(response);
            $('#preview' + response.id).modal('show');
        }, 100);
    };
    $scope.addWidget = function (newWidget) {       //Add Widget
        var data = {
            width: newWidget, 'minHeight': 25, columns: [], chartType: ""
        };
        $http({method: 'POST', url: 'admin/ui/dbWidget/' + $stateParams.tabId, data: data}).success(function (response) {
            $scope.widgets.unshift({id: response.id, width: newWidget, 'minHeight': 25, columns: [], tableFooter: 1, zeroSuppression: 1});
            $scope.newWidgetId = response.id;
            $scope.openPopup(response);
        });
    };

//    $scope.removeBackDrop = function () {
//        $('body').removeClass().removeAttr('style');
//        $('.modal-backdrop').remove();
//    }

    $scope.deleteWidget = function (widget, index) {                            //Delete Widget
        $http({method: 'DELETE', url: 'admin/ui/dbWidget/' + widget.id}).success(function (response) {
            $scope.widgets.splice(index, 1);
        });
    };

//    $scope.addColumns = function (widget) {                                     //Add Columns - Popup
//        widget.columns.unshift({isEdit: true});
//    };

//    $scope.saveColumn = function (widget, column) {                              //Delete Columns-Popup
//        var data = {
//            id: column.id,
//            agregationFunction: column.agregationFunction,
//            displayName: column.displayName,
//            fieldName: column.fieldName,
//            groupPriority: column.groupPriority,
//            xAxis: column.xAxis,
//            yAxis: column.yAxis,
//            sortOrder: column.sortOrder,
//            displayFormat: column.displayFormat
//        };
//    };

//    $scope.deleteColumn = function (widgetColumns, index) {        //Delete Columns - Popup
//        widgetColumns.splice(index, 1);
//    };

    $http.get('static/datas/imageUrl.json').success(function (response) {       //Popup- Select Chart-Type Json
        $scope.chartTypes = response;
    });

    //DataSource
    $http.get('admin/datasources').success(function (response) {
        $scope.datasources = response;
    });

    //Data Source
    $http.get('admin/datasources').success(function (response) {
        $scope.datasources = response;
    });
    $scope.selectedDataSource = function (selectedItem) {
        $scope.selectItem = selectedItem;
        selectedItems(selectedItem);
    };

    $scope.objectHeader = [];
    $scope.previewChart = function (chartType, widget) {
        $scope.showPreviewItems = chartType.type ? chartType.type : chartType.chartType;
        widget.chartType = chartType.type ? chartType.type : chartType.chartType;    //Selected Chart type - Bind chart-type to showPreview()
        $scope.selectedRow = chartType.type ? chartType.type : chartType.chartType;
        $scope.editChartType = chartType.type ? chartType.type : chartType.chartType;
        $scope.previewChartUrl = widget.previewUrl;
        $scope.previewColumn = widget;
    };
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
    try {
        $scope.customStartDate = moment($('#customDateRange').data('daterangepicker').startDate).format('MM/DD/YYYY') ? moment($('#customDateRange').data('daterangepicker').startDate).format('MM/DD/YYYY') : $stateParams.startDate;//$scope.startDate.setDate($scope.startDate.getDate() - 1);

        $scope.customEndDate = moment($('#customDateRange').data('daterangepicker').endDate).format('MM/DD/YYYY') ? moment($('#customDateRange').data('daterangepicker').endDate).format('MM/DD/YYYY') : $stateParams.endDate;
    } catch (e) {

    }
    $(function () {
        //Initialize Select2 Elements
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
                {
                    ranges: {
//                        'Today': [moment(), moment()],
//                        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
//                        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
//                        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                        'This Month': [moment().startOf('month'), moment().endOf(new Date())],
                        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
                    },
                    startDate: $stateParams.startDate ? $stateParams.startDate : moment().subtract(29, 'days'),
                    endDate: $stateParams.endDate ? $stateParams.endDate : moment(),
                    maxDate: new Date()
                },
                function (start, end) {
                    $('#customDateRange span').html(start.format('MM-DD-YYYY') + ' - ' + end.format('MM-DD-YYYY'));
                }
        );
        //Date picker
        $('#datepicker').datepicker({
            autoclose: true
        });
        //iCheck for checkbox and radio inputs
        $('input[type="checkbox"].minimal,  input[type="radio"].minimal').iCheck({
            checkboxClass: 'icheckbox_minimal-blue',
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

    $scope.xaxisFormat = function (widget, column) {
        console.log("Test")
        console.log(column)
        $scope.selectX1Axis(widget, column)
    }

    $scope.selectPieChartAxis = function (widget, column) {
        $scope.editChartType = null;
//        console.log(column);
//        column.xAxis = 1;   
//        column.yAxis = 1;
        if (column.displayFomat) {
            column.displayFormat = column.displayFormat.value;
        }
        var exists = false;
        angular.forEach(widget.columns, function (value, key) {
            if (column.fieldName == value.fieldName) {
                exists = true;
                value.xAxis = 1;
            }
        });
        if (exists == false) {
            widget.columns.push(column);
        }


        console.log(widget)
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };
    
    $scope.selectX1Axis = function (widget, column) {
        $scope.editChartType = null;
        console.log(column);
        column.xAxis = 1;        
       
        var exists = false;
        angular.forEach(widget.columns, function (value, key) {
            if (column.fieldName == value.fieldName) {
                exists = true;
                value.xAxis = 1;
            }
        });
        if (exists == false) {
            widget.columns.push(column);
        }


        console.log(widget)
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };

    $scope.selectY1Axis = function (widget, column, y1data) {
        console.log(column)
        var y1Column = column;
        $scope.editChartType = null;
       
            angular.forEach(y1data, function (value, key) {
                angular.forEach(widget.columns, function (val, key) {
                    if (value.fieldName === val.fieldName) {
                        val.yAxis = 1;
                    }
                });
                value.yAxis = 1;
                widget.columns.push(value);
            });
            
        console.log(widget);
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };

    $scope.selectY2Axis = function (widget, column, y2data) {
        var y1Column = column;
        $scope.editChartType = null;

        angular.forEach(y2data, function (value, key) {
            angular.forEach(widget.columns, function (val, key) {
                if (value.fieldName === val.fieldName) {
                    val.yAxis = 2;
                }
            });
            value.yAxis = 2;
            widget.columns.push(value);
        });

        console.log(widget);
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    }
    $scope.removed = function (widget, column) {
        console.log(column)
        column.yAxis = null;
        $scope.editChartType = null;
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };

    $scope.tickerTop = function (widget, column) {
        widget.columns = []
        $scope.editChartType = null;
        angular.forEach(column, function (value, key) {
            widget.columns.push(value)
        });
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };
    $scope.removedColumns = function (widget, column, tickerItem) {
        widget.columns = [];
        $scope.editChartType = null;
        angular.forEach(tickerItem, function (value, key) {
            widget.columns.push(value)
        })
        var chartType = widget;
        $timeout(function () {
            $scope.previewChart(chartType, widget)
        }, 300);
    };

//    $scope.showPreviewType = function (widget) {                                //Show Preview Chart - Popup
//        $scope.previewChartType = $scope.editChartType ? $scope.editChartType : widget.chartType;
//        $scope.previewColumn = $scope.setPreviewColumn ? $scope.setPreviewColumn : widget;
//        $scope.previewChartUrl = widget.previewUrl;
//    };

    $scope.save = function (widget) {
        widget.directUrl = widget.previewUrl ? widget.previewUrl : widget.directUrl;
        var widgetColumnsData = [];
        angular.forEach(widget.columns, function (value, key) {
            var hideColumn = value.columnHide;
            if (value.groupPriority > 0) {
                hideColumn = 1;
            }

            var columnData = {
                id: value.id,
                fieldName: value.fieldName,
                displayName: value.displayName,
                agregationFunction: value.agregationFunction,
                groupPriority: isNaN(value.groupPriority) ? null : value.groupPriority,
                xAxis: isNaN(value.xAxis) ? null : value.xAxis,
                yAxis: isNaN(value.yAxis) ? null : value.yAxis,
                sortOrder: value.sortOrder,
                displayFormat: value.displayFormat,
                alignment: value.alignment,
                baseFieldName: value.baseFieldName,
                fieldGenerationFields: value.fieldGenerationFields,
                fieldGenerationFunction: value.fieldGenerationFunction,
                fieldType: value.fieldType,
                functionParameters: value.functionParameters,
                remarks: value.remarks,
                sortPriority: isNaN(value.sortPriority) ? null : value.sortPriority,
                width: isNaN(value.width) ? null : value.width,
                wrapText: value.wrapText,
                xAxisLabel: value.xAxisLabel,
                yAxisLabel: value.yAxisLabel,
                columnHide: hideColumn,
                search: value.search
            };
            widgetColumnsData.push(columnData);
        });
        var data = {
            id: widget.id,
            chartType: $scope.editChartType ? $scope.editChartType : widget.chartType,
            widgetTitle: widget.widgetTitle,
            widgetColumns: widgetColumnsData,
            dataSourceId: widget.dataSourceId.id,
            dataSetId: widget.dataSetId.id,
            tableFooter: widget.tableFooter,
            zeroSuppression: widget.zeroSuppression,
            maxRecord: widget.maxRecord,
            dateDuration: widget.dateDuration,
            content: widget.content
        };
        //console.log(data);
        $http({method: widget.id ? 'PUT' : 'POST', url: 'admin/ui/dbWidget/' + $stateParams.tabId, data: data}).success(function (response) {
            $state.go("index.dashboard.widget", {productId: $stateParams.productId, accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate})
        });
        //widget.chartType = "";
        //$scope.previewChartType = null;
    };

    $scope.closeWidget = function (widget) {
        $scope.widget = "";
        // $scope.previewChartType = null;
        $state.go("index.dashboard.widget", {productId: $stateParams.productId, accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate})
    }
    ;
}
);

app.filter('xAxis', [function () {
        return function (chartXAxis) {
            var xAxis = ['', 'x-1']
            return xAxis[chartXAxis];
        };
    }]);
app.filter('yAxis', [function () {
        return function (chartYAxis) {
            var yAxis = ['', 'y-1', 'y-2']
            return yAxis[chartYAxis];
        };
    }]);
app.filter('hideColumn', [function () {
        return function (chartYAxis) {
            var hideColumn = ['No', 'Yes']
            return hideColumn[chartYAxis];
        };
    }]);

app.directive('widgetPreviewTable', function ($http, $stateParams, $state) {
    return{
        restrict: 'AE',
        scope: {
            previewUrls: '@',
            previewColumns: '@',
            previewWidget: '@',
            previewWidgetTable: '@',
            previewTableList: '@'
        },
        template: "<div class='panel-head'>" +
                //Panel Tools
                "<div class='row'>" +
                "<div class='col-md-12'>" +
                //Panel Title
                "<div ng-hide='editPreviewTitle'>" +
                "<a ng-click='editPreviewTitle = true'>{{previewWidgetTitle}}</a>" +
                "</div>" +
                "<div ng-show='editPreviewTitle'>" +
                "<div class='col-sm-8'><input class='form-control' type='text' ng-model='previewWidgetTitle'></div>" +
                "<div class='col-sm-4'><a ng-click='editPreviewTitle = false'><i class='fa fa-save'></i></a>" +
                "<a><i class='fa fa-close'></i></a>" +
                "</div>" +
                "</div>" +
                "<div class='pull-right list-button'>" +
                "<button class='btn btn-info' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'><i class='fa fa-list'></i> List" +
                "</button>" +
                //list columns

                "<ul class='dropdown-menu list-unstyled'>" +
                "<li ng-repeat='column in listColumns'>" +
                "<button class='btn btn-link dropdown-item' ng-click='addList(column)'>" +
                "{{column.displayName}}" +
                "</button>" +
                "</li>" +
                "</ul>" +
                "</div>" +
                "</div>" +
                "</div>" + //End Panel Title
                "</div>" +
                "</div>" +
                //Table
                "<div class='panel-body'>" +
                "<div class='table-responsive tbl-preview' style='height:200px; overflow: auto'>" +
                "<table class='table table-bordered table-hover' >" +
                "<thead >" +
                "<tr data-as-sortable='' data-ng-model='previewTableHeaderName' unselectable='on' class='unselectable'>" +
                "<th style='cursor: move;' data-as-sortable-item ng-repeat='collectionField in previewTableHeaderName track by $index'>" +
                "<div data-as-sortable-item-handle ng-hide='collectionField.isEdit'>" +
//                "<div class='preview-table-settings' ng-click='collectionField.isEdit=true'>" +
                "<div class='preview-table-settings' ng-hide='collectionField.isEdit'>" +
                "<a ng-click='collectionField.isEdit = true'>{{collectionField.displayName}}</a>" +
                "</div></div>" +
                //column Edit
                "<div ng-show='collectionField.isEdit'><input class='form-control-btn' ng-model='collectionField.displayName'>" +
                "<a class='btn btn-default btn-xs' ng-click='collectionField.isEdit=false'><i class='fa fa-save'></i></a>" +
                "<a class='btn btn-default btn-xs' ng-click='collectionField.isEdit=false'><i class='fa fa-close'></i></a></div>" +
                "</th>" +
                "</tr>" +
                "<tr><th ng-repeat='collectionField in previewTableHeaderName track by $index'>" +
                "<button class='btn btn-default btn-xs'" +
                "ns-popover=''" +
                "ns-popover-template='close'" +
                "ns-popover-trigger='click'" +
                "ns-popover-theme='ns-popover-tooltip-theme'" +
                "ns-popover-timeout='1000'" +
                "ns-popover-hide-on-inside-click='false'" +
                "ns-popover-hide-on-outside-click='true'" +
                "ns-popover-hide-on-button-click='true'><i class='fa fa-cog'></i></button>" +
                //DropDown List
                " <script type='text/ng-template' id='close'>" +
                "<div class='triangle'></div>" +
                "<div class='ns-popover-tooltip'>" +
                "<form class='form-inline'>" +
                "<ul class='scheduler-list-style'>" +
                "<li class='input-group col-sm-10'> <label>Aggregation function</label>" +
                "<select class='form-control' ng-model='collectionField.agregationFunction'>" +
                "<option ng-repeat='selectAggregation in selectAggregations'" +
                "value='{{selectAggregation.value}}'>" +
                "{{selectAggregation.name}}</option></select></li>" +
                "<li class='input-group col-sm-10'><label>Group priority</label><select ng-model='collectionField.groupPriority' class='form-control'>" +
                "<option ng-repeat='selectGroupPriority in selectGroupPriorities' value='{{selectGroupPriority.value}}'>" +
                "{{selectGroupPriority.num}}</option></select></li>" +
                "<li class='input-group col-sm-10'><label>Sort</label><select ng-model='collectionField.sortPriority' class='form-control'>" +
                "<option ng-repeat='sort in sorting' value='{{sort.value}}'>" +
                "{{sort.name}}</option></select></li>" +
                "<li class='input-group col-sm-10'><label>Format</label><select ng-model='collectionField.displayFormat' class='form-control'>" +
                "<option ng-repeat='format in formats' value='{{format.value}}'>" +
                "{{format.name}}</option></select></li>" +
                "<li class='input-group col-sm-10'><label>Alignment</label><select ng-model='collectionField.alignment' class='form-control'>" +
                "<option ng-repeat='alignment in alignments' value='{{alignment.name}}'>" +
                "{{alignment.name}}</option></select></li>" +
                "<li class='input-group col-sm-10'><label>ColumnHide</label><select ng-model='collectionField.columnHide' class='form-control'>" +
                "<option ng-repeat='hideOption in hideOptions' value='{{hideOption.value}}'>" +
                "{{hideOption.name}}</option></select></li>" +
                "<li><input type='checkbox' ng-model='collectionField.search'><label>Search</label></li>" +
//                        "<li><a class='btn' ng-click='deleteColumn($index)'>Delete</a></li>" +
                "</ul>" +
                "<button class='btn btn-primary btn-sm' ng-click='hidePopover()'>Close</button>&nbsp;" +
                "<button class='btn btn-warning btn-sm' ng-click='deleteColumn($index)'>Delete</button>" +
                "</form>" +
                "</div>" +
                "</script>" +
                "</th></tr>" +
                " </thead>" +
                //colum Fields
                "<tbody>" +
                "<tr ng-repeat='tablelist in tableData'>" +
                "<td ng-repeat='collectionField in previewTableHeaderName track by $index'>" +
                "<div>{{tablelist[collectionField.fieldName]}}</div>" +
                "</td>" +
                "</tr>" +
                "</tbody>" +
                "</table> " +
                "</div>" +
                "</div>" +
                "<div class='panel-footer'>" +
//                "<div class='form-group btn-preview pull-right'>" +
                "<button data-dismiss='modal' class='btn btn-default' ng-click='closeWidget()'><span class='fa fa-times'></span> Cancel</button>" +
                "<button data-dismiss='modal' class='btn btn-info' ng-click='save()'><span class='fa fa-save'></span> Save</button>" +
//                "</div>" +
                "</div>" +
                "</div>",
        link: function (scope, attrs) {
            console.log(scope.previewTableList);
            console.log(scope.previewColumns);
            scope.previewTableHeaderName = JSON.parse(scope.previewColumns);
            scope.listColumns = [];
            scope.listColumns = JSON.parse(scope.previewColumns);
            scope.previewWidgetTitle = JSON.parse(scope.previewWidget).widgetTitle ? JSON.parse(scope.previewWidget).widgetTitle : "Widget Title";
            var widget = JSON.parse(scope.previewWidget);
            scope.selectAggregations = [
                {name: 'None', value: ""},
                {name: 'Sum', value: "sum"},
                {name: 'CTR', value: "ctr"},
                {name: 'CPC', value: "cpc"},
                {name: 'CPS', value: "cps"},
                {name: 'CPA', value: "cpa"},
                {name: 'Avg', value: "avg"},
                {name: 'Count', value: "count"},
                {name: 'Min', value: "min"},
                {name: 'Max', value: "max"},
                {name: 'CPL', value: "cpl"},
                {name: 'CPLC', value: "cplc"},
                {name: 'CPComment', value: "cpcomment"},
                {name: 'CPostE', value: "cposte"},
                {name: 'CPageE', value: "cpagee"},
                {name: 'CPP', value: "cpp"},
                {name: 'CPR', value: "cpr"}
            ];
            scope.selectGroupPriorities = [
                {num: 'None', value: ""},
                {num: 1, value: 1},
                {num: 2, value: 2}
            ];
            scope.alignments = [
                {name: '', displayName: 'None'},
                {name: "left", displayName: "Left"},
                {name: "right", displayName: "Right"},
                {name: "center", displayName: "Center"}
            ];
            scope.sorting = [
                {name: 'None', value: ''},
                {name: 'asc', value: 'asc'},
                {name: 'desc', value: 'desc'}
            ];
            scope.formats = [
                {name: "Currency", value: '$,.2f'},
                {name: "Integer", value: ',.0f'},
                {name: "Percentage", value: ',.2%'},
                {name: "Decimal1", value: ',.1f'},
                {name: "Decimal2", value: ',.2f'},
                {name: "None", value: ''}
            ];
            scope.hideOptions = [
                {name: 'Yes', value: 1},
                {name: 'No', value: ''}
            ];
            scope.addList = function (list) {
                list.isEdit = true;
                scope.previewTableHeaderName.push(list);
            };
            var tableDataSource = JSON.parse(scope.previewUrls)
            var data = {
                url: '../dbApi/admin/dataSet/getData',
                connectionUrl: tableDataSource.dataSourceId.connectionString,
                startDate: $stateParams.startDate,
                endDate: $stateParams.endDate,
                username: tableDataSource.dataSourceId.userName,
                password: tableDataSource.dataSourceId.password,
                query: "select * from data ",
                port: 3306,
                schema: 'vb'
            }

            var url = "admin/proxy/getJson?url=../dbApi/admin/dataSet/getData&";
            if (tableDataSource.dataSourceId.dataSourceType == "csv") {
                url = "admin/csv/getData?";
            }

            $http.get(url + 'connectionUrl=' + tableDataSource.dataSourceId.connectionString + "&driver=" + tableDataSource.dataSourceId.sqlDriver + "&location=" + $stateParams.locationId + "&startDate=" + $stateParams.startDate + "&endDate=" + $stateParams.endDate + '&username=' + tableDataSource.dataSourceId.userName + '&password=' + tableDataSource.dataSourceId.password + '&port=3306&schema=vb&query=' + encodeURI(tableDataSource.query)).success(function (response) {
                scope.tableData = response.data;
            })
            scope.deleteColumn = function ($index) {
                scope.previewTableHeaderName.splice($index, 1);
            }
            scope.save = function (column) {
                var widgetColumnsData = [];
                angular.forEach(scope.previewTableHeaderName, function (value, key) {
                    var hideColumn = value.columnHide;
                    if (value.groupPriority > 0) {
                        hideColumn = 1;
                    }

                    var columnData = {
                        id: value.id,
                        fieldName: value.fieldName,
                        displayName: value.displayName,
                        agregationFunction: value.agregationFunction,
                        groupPriority: isNaN(value.groupPriority) ? null : value.groupPriority,
                        xAxis: isNaN(value.xAxis) ? null : value.xAxis,
                        yAxis: isNaN(value.yAxis) ? null : value.yAxis,
                        sortOrder: value.sortOrder,
                        displayFormat: value.displayFormat,
                        alignment: value.alignment,
                        baseFieldName: value.baseFieldName,
                        fieldGenerationFields: value.fieldGenerationFields,
                        fieldGenerationFunction: value.fieldGenerationFunction,
                        fieldType: value.fieldType,
                        functionParameters: value.functionParameters,
                        remarks: value.remarks,
                        sortPriority: isNaN(value.sortPriority) ? null : value.sortPriority,
                        width: isNaN(value.width) ? null : value.width,
                        wrapText: value.wrapText,
                        xAxisLabel: value.xAxisLabel,
                        yAxisLabel: value.yAxisLabel,
                        columnHide: hideColumn,
                        search: value.search
                    };
                    widgetColumnsData.push(columnData);
                });
                if (scope.previewWidgetTitle == "Widget Title") {
                    scope.previewWidgetTitle = "";
                }

                var data = {
                    id: widget.id,
                    chartType: widget.chartType,
                    widgetTitle: scope.previewWidgetTitle,
                    widgetColumns: widgetColumnsData,
                    dataSourceId: widget.dataSourceId.id,
                    dataSetId: widget.dataSetId.id,
                    tableFooter: JSON.parse(scope.previewWidgetTable).tableFooter,
                    zeroSuppression: JSON.parse(scope.previewWidgetTable).zeroSuppression,
                    maxRecord: JSON.parse(scope.previewWidgetTable).maxRecord,
                    dateDuration: widget.dateDuration,
                    content: widget.content
                };

                $http({method: widget.id ? 'PUT' : 'POST', url: 'admin/ui/dbWidget/' + $stateParams.tabId, data: data}).success(function (response) {
                    $state.go("index.dashboard.widget", {productId: $stateParams.productId, accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate})
                });
            };
            scope.closeWidget = function () {
                widget = "";
                $state.go("index.dashboard.widget", {productId: $stateParams.productId, accountId: $stateParams.accountId, accountName: $stateParams.accountName, tabId: $stateParams.tabId, startDate: $stateParams.startDate, endDate: $stateParams.endDate})
            };
        }
    };
});
app.directive('ckEditor', function () {
    return {
        require: '?ngModel',
        link: function (scope, elm, attr, ngModel) {
            var ck = CKEDITOR.replace(elm[0]);

            if (!ngModel)
                return;

            ck.on('pasteState', function () {
                scope.$apply(function () {
                    ngModel.$setViewValue(ck.getData());
                });
            });

            ngModel.$render = function (value) {
                ck.setData(ngModel.$viewValue);
            };
        }
    };
});