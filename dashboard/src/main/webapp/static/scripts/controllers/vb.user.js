app.controller('UserController', function ($scope, $http, localStorageService, $cookies, $translate, $stateParams) {
    $scope.permission = localStorageService.get("permission");
    $scope.checkAdmin = $cookies.getObject("isAdmin");
    $scope.users = [];

    //Tabs
    $scope.tab = 1;

    $scope.setTabUser = function (newTab) {
        $scope.tab = newTab;
    };

    $scope.isSetUser = function (tabNum) {
        return $scope.tab === tabNum;
    };

    $scope.agencyLanguage = $stateParams.lan;//$cookies.getObject("agencyLanguage");

    var lan = $scope.agencyLanguage;
    changeLanguage(lan);

    function changeLanguage(key) {
        $translate.use(key);
    }



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
    };
    function getUser() {
        $http.get('admin/ui/user').success(function (response) {
            $scope.userAgencyDetails = [];
            $scope.users = [];
            angular.forEach(response,function(val,key){
                $scope.users.unshift(val);
            });
            angular.forEach($scope.users, function (val, key) {
                console.log(val);
                if (!val.agencyId) {
                    return;
                }
                $scope.userAgencyDetails.push(val.agencyId.agencyName);
                $scope.userAgency = unique($scope.userAgencyDetails);
            });
        });

        $http.get('admin/user/account').success(function (response) {
            $scope.accounts = response;
        });
    }

    getUser();
    $scope.searchuserDetails = function (agencyUserName) {
        $scope.agencyListName = agencyUserName;
    };
    $scope.saveUser = function (user) {
        console.log("user-->");
        console.log(user);
//        var userData = {
//            id: user.id,
//            firstName: user.firstName,
//            lastName: user.lastName,
//            userName: user.userName,
//            email: user.email,
//            password: user.password,
//            primaryPhone: user.primaryPhone,
//            secondaryPhone: user.secondaryPhone,
//            agencyId: user.agencyId.id?user.agencyId.id:''
//        };
//        if ($scope.checkAdmin === 'admin') {
//            user.isAdmin = true;
//        }

        if (user.isAdmin === null || !user.isAdmin) {
            user.isAdmin = null;
        } else {
            user.isAdmin = true;
        }
        console.log(user.isAdmin);
        $http({method: user.id ? 'PUT' : 'POST', url: 'admin/ui/user', data: user}).success(function (response) {
            console.log(response);
            getUser();
            if (!response.message) {
                $scope.user = "";
                $scope.selectedUser = null;
                return;
            }
            if (response.status == true) {
                $scope.user = "";
                $scope.selectedUser = null;
            } else {
                var dialog = bootbox.dialog({
                    title: 'Alert',
                    message: response.message
                });
                dialog.init(function () {
                    setTimeout(function () {
                        dialog.modal('hide');
                    }, 2000);
                });
            }
        });
    };

    $scope.clearUser = function () {
        $scope.user = "";
        $scope.selectedUser = null;
    };

    $scope.addUser = function () {
        $scope.user = '';
        $scope.selectedUser = null;
    };

    $scope.selectedUser = null;
    $scope.editUser = function (user, index) {
        getUserAccount(user);
        $scope.userId = user;
        var data = {
            id: user.id,
            firstName: user.firstName,
            lastName: user.lastName,
            userName: user.userName,
            email: user.email,
            password: user.password,
            primaryPhone: user.primaryPhone,
            secondaryPhone: user.secondaryPhone,
            agencyId: user.agencyId,
            isAdmin: user.isAdmin
        };
        $scope.user = data;
        $scope.selectedUser = index;
    };

    $scope.deleteUser = function (user, index) {
        console.log(index);
        $http({method: 'DELETE', url: 'admin/ui/user/' + user.id}).success(function (response) {
            $scope.users.splice(index, 1);
        });
        $scope.clearUser();
    };

    function getUserAccount(user) {
        $http.get('admin/ui/userAccount/' + user.id).success(function (response) {
            $scope.userAccounts = response;
        });
        $http.get('admin/ui/userPermission/' + user.id).success(function (response) {
            $scope.allPermissions = true;
            $scope.userPermissions = response;
            $http.get('admin/ui/permission').success(function (response1) {
                $scope.permissions = response1;
                angular.forEach($scope.permissions, function (permission) {
                    $scope.hasPermission(permission);
                });
                checkAllPermission(user);
            });

        });

    }

    $scope.hasPermission = function (permission) {
        for (var i = 0; i < $scope.userPermissions.length; i++) {
            if ($scope.userPermissions[i].permissionId.permissionName === permission.permissionName) {
                permission.status = ($scope.userPermissions[i].status === 1 || $scope.userPermissions[i].status === true) ? 1 : 0;
                return ($scope.userPermissions[i].status === 1 || $scope.userPermissions[i].status === true) ? true : false;
            }
        }
        permission.status = 0;
        return false;
    };

    $scope.hasData = function (permissionName) {
        for (var i = 0; i < $scope.userPermissions.length; i++) {
            if ($scope.userPermissions[i].permissionId.permissionName === permissionName) {
                var data = {
                    status: true,
                    id: $scope.userPermissions[i].id
                };
                return data;
            }
        }
        return false;
    };

    $scope.userAccounts = [];
    $scope.addUserAccount = function () {
        $scope.userAccounts.push({isEdit: true});
        $scope.selectedUser = null;
    };

    $scope.saveUserAccount = function (userAccount) {
        var currentUserId = $scope.userId;
        if (!currentUserId) {
            var dialog = bootbox.dialog({
                title: 'Alert',
                message: '<p>Select User</p>'
            });
            dialog.init(function () {
                setTimeout(function () {
                    dialog.modal('hide');
                }, 2000);
            });

        } else {
            var data = {
                id: userAccount.id,
                accountId: userAccount.accountId.id,
                userId: currentUserId.id
            };
            $http({method: userAccount.id ? 'PUT' : 'POST', url: 'admin/ui/userAccount', data: data}).success(function (response) {
                getUserAccount(currentUserId);
                //$scope.userAccounts = response.id;
//            userAccount(currentUserId);
            });
            userAccount.isEdit = false;
            $scope.selectedUser = null;
        }
    };

    $scope.deleteUserAccount = function (userAccount, index) {
        //if (userAccount.id) {
        $http({method: 'DELETE', url: 'admin/ui/userAccount/' + userAccount.id}).success(function (response) {

            $scope.userAccounts.splice(index, 1);
            //$scope.userAccounts = response;
        });
        // } else {
        //$scope.userAccounts.splice(index, 1);
        //}
    };

    $scope.removeUserAccount = function (index) {
        $scope.userAccounts.splice(index, 1);
        $scope.selectedUser = null;
    };

    $scope.setAllPermissions = function () {
        angular.forEach($scope.permissions, function (val, key) {
            if ($scope.allPermissionStatus === 1) {
                var permissionObject = {
                    permissionName: val.permissionName,
                    description: val.description,
                    id: val.id,
                    status: true
                };
            } else {
                var permissionObject = {
                    permissionName: val.permissionName,
                    description: val.description,
                    id: val.id,
                    status: 0
                };
            }
            var type = "allPermission";
            $scope.saveUserPermission(permissionObject, type);
        });
    };
    $scope.allPermissions = false;
    $scope.setUserPermission = function (permission, type) {
        $scope.saveUserPermission(permission, type);
    };

    //check for all permisssions for the user
    function checkAllPermission(user) {
        var statusCount = 0;
        var permissionLength = $scope.permissions.length;
        $http.get('admin/ui/userPermission/' + user.id).success(function (response) {
            $scope.userLevelPermission = response;
            var userPermissionLength = $scope.userLevelPermission.length;
            if (permissionLength > userPermissionLength) {
                $scope.allPermissionStatus = 0;
            } else {
                $scope.userLevelPermission.forEach(function (val, key) {
                    if (val.status === true) {
                        statusCount++;
                    }
                });
                if (permissionLength === statusCount) {
                    $scope.allPermissionStatus = 1;
                } else {
                    $scope.allPermissionStatus = 0;
                }
            }
        });
    }
    ;

    $scope.saveUserPermission = function (permission, type) {
        var userPermissionId = $scope.hasData(permission.permissionName).id;
        var currentUserId = $scope.userId;
        var data = {
            id: $scope.hasData(permission.permissionName).id,
            permissionId: permission.id,
            userId: currentUserId.id,
            status: permission.status
        };
        if (permission.status === 0) {
            $scope.allPermissionStatus = 0;
        }
        $http({method: userPermissionId ? 'PUT' : 'POST', url: 'admin/ui/userPermission', data: data}).success(function (response) {
            getUserAccount(currentUserId);
            if (type === 'individualPermission') {
                //check all permissions is set for the user. If set active all permisssions toggle to true else set to false
                checkAllPermission($scope.userId);

            }
        });
    };
});
app.directive("showPassword", function () {
    return {
        restrict: "EA",
        link: function (scope, element, attrs) {
            element.on('click', function () {
                var inputType = angular.element(angular.element(element[0]).parent().siblings()[0]).attr("type");
                if (inputType != null && inputType.toLowerCase() == "password") {
                    angular.element(angular.element(element[0]).parent().siblings()[0]).attr("type", "text");
                } else {
                    angular.element(angular.element(element[0]).parent().siblings()[0]).attr("type", "password");
                }
            });
        }
    };
});
