var globalUserRoles = [ 
  { name: 'normal', id: 'normal' },
  { name: 'admin', id : 'admin' }
  ];

userControllers.controller('UsersListCtrl', [
		'$scope',
		'$http',
		'$timeout',
		'$rootScope',
		'$route',
		'$location',
		function($scope, $http, $timeout, $rootScope, $route, $location) {
			$scope.searchText = lastUserSearch;
			
			initListNavigationCommons($scope, $timeout,	10, 'username', USER_TABLE_HEADERS);
            $scope.userRoles = globalUserRoles;

			$http.get('/users/list').success(function(data) {
				$scope.users = data;
				$timeout(function() {
					if ($scope.filtered !== undefined) {
						$scope.updateCurrentPageSelector($scope.filtered.length);
					} else {
						$scope.updateCurrentPageSelector($scope.solutions.length);
					}
					;
				});
			});

			$scope.$watch('searchText', function(term) {
				$timeout(function() {
					$scope.currentPage = 0;
					$scope.currentPageSelector = 1;
					if ($scope.filtered !== undefined) {
						$scope.updateCurrentPageSelector($scope.filtered.length);
					}
					;
					lastUserSearch = $scope.searchText;
				});
			});

			$scope.search = function(entry) {
				if ($scope.searchText) {
					var searchTextLowercase = angular.lowercase($scope.searchText);
					return angular.lowercase(entry.username ? entry.username : "").indexOf(searchTextLowercase) >= 0 
							|| angular.lowercase(entry.fullName ? entry.fullName : "").indexOf(searchTextLowercase) >= 0;
				} else {
					return true;
				}
				;
			};
			
			$scope.viewEntry = function(entryId) {
				$location.path('/users/' + entryId);
			};
			
			$scope.removeUser = function(userId) {
				$rootScope.removeEntry(userId, "users", "User");
			};			
		} ]);

userControllers.controller('UsersDetailCtrl', [ '$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {
	$scope.userId = $routeParams.userId;
	var f = "/users/" + $routeParams.userId;
	$scope.url = f;
	$http.get(f).success(function(data) {
		$scope.user = data;
        $scope.userRoles = globalUserRoles;
		$scope.userDetailsFields = [{ field: $scope.user.username, label: "Username", id: "userUsername", isDate: false, "labelclass": "noLeftBorder borderBottom", "valueclass": "borderBottom" },
                                    { field: $scope.user.fullName, label: "Full name", id: "userFullname", isDate: false, "labelclass": "noLeftBorder borderBottom", "valueclass": "borderBottom" },
                                    { field: $scope.user.role, label: "Role", id: "userRole", isDate: false, "labelclass": "noLeftBorder borderBottom", "valueclass": "borderBottom" }
		                            ];
	});

} ]);

userControllers.controller('UsersNewCtrl', [ '$scope', '$http', '$location', function($scope, $http, $location) {
	$scope.pageTitle = "New user";
	$scope.buttonText = "Create";
	$scope.isPasswordRequired = true;
	$scope.disableUsername = false;
	$scope.isNew = true;
    $scope.userRoles = globalUserRoles;

	$scope.submit = function() {
		$scope.form.id = 0;
		$http.post('/users/create', $scope.form).success(function(newUser) {
			$location.path('/users/' + newUser.id);
		});
	};
} ]);

userControllers.controller('UsersEditCtrl', [ '$scope', '$routeParams', '$http', '$location', '$timeout',
		function($scope, $routeParams, $http, $location, $timeout) {
			$scope.pageTitle = "Edit user";
			$scope.buttonText = "Update";
			$scope.isPasswordRequired = false;
			$scope.disableUsername = true;
			$scope.isNew = false;
            $scope.userRoles = globalUserRoles;

			$scope.userId = $routeParams.userId;
			var f = "/users/" + $routeParams.userId;
			$scope.url = f;
			$http.get(f).success(function(data) {
				$scope.form = data;
			});

			$scope.submit = function() {
				$http.put('/users/' + $scope.form.id, $scope.form).success(function(updatedUser) {
					$location.path('/users/' + updatedUser.id);
				});
			};
	} ]);

userControllers.directive('confirmPass', [function () {
	return {
		require: 'ngModel',
		link: function (scope, element, attrs, ctrl) {
			var pwdCheck = function () {
				var confirmPass = scope.$eval(attrs.ngModel); 
				var initialPass = scope.$eval(attrs.confirmPass);
				scope.$apply(function () {
					if (angular.isUndefined(confirmPass) && angular.isUndefined(initialPass)) {
						ctrl.$setValidity('passwordsmatch', true);
					} else {
						var v = initialPass === confirmPass;
						ctrl.$setValidity('passwordsmatch', v);
					}
				});
			};			
			element.bind('keyup', pwdCheck); 
			//the difference between this function and pwdCheck is in scope.$apply
			scope.$watch('form.user.password', function() {
				var confirmPass = scope.$eval(attrs.ngModel); 
				var initialPass = scope.$eval(attrs.confirmPass);
				if (angular.isUndefined(confirmPass) && angular.isUndefined(initialPass)) {
					ctrl.$setValidity('passwordsmatch', true);
				} else {
					var v = initialPass === confirmPass;
					ctrl.$setValidity('passwordsmatch', v);
				}
			}); 
			
		}
	};
}]);