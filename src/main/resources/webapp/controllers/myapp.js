var myApp = angular.module('myApp', [ 'ngRoute', 'ngSanitize', 'usersControllers' ]);

//Set up routing and controllers

myApp.config([ '$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
	$httpProvider.interceptors.push('httpInterceptor');
	$routeProvider.when('/users', {
		templateUrl : 'partials/users-list.html',
		controller : 'UsersListCtrl'
	}).when('/users/new', {
		templateUrl : 'partials/users-edit.html',
		controller : 'UsersNewCtrl'
	}).when('/users/:userId/edit', {
		templateUrl : 'partials/users-edit.html',
		controller : 'UsersEditCtrl'
	}).when('/users/:userId', {
		templateUrl : 'partials/users-details.html',
		controller : 'UsersDetailCtrl'
	}).otherwise({
		redirectTo : '/users'
	});
} ]);

var userControllers = angular.module('usersControllers', []);

//Global variables section

var lastUserSearch = "";

var USER_TABLE_HEADERS = [{ field: "username", text: "Username", id: "userUsernameHeader", width: "15%", sortable: true, "class": "noLeftBorder"},
                          { field: "fullName", text: "Full name", id: "userFullnameHeader", width: "35%", sortable: true, "class": ""},
                          { field: "role", text: "Role", id: "userRoleHeader", width: "35%", sortable: true, "class": ""},
                          { field: "", text: "", id: "userControlsHeader", width: "5%", sortable: false, "class": ""}];

// Interceptors
myApp.factory('httpInterceptor', function($rootScope, $q, $location, $window) {
  return {
    'request': function(config) {
    	config.headers = config.headers || {};
      if ($window.sessionStorage.token) {
        config.headers = {
        			'Accept'       : 'application/json, text/plain, */*',
        			'Content-Type' : 'application/json;charset=utf-8',
        			"x-auth-token" : $window.sessionStorage.token
        		};
      }
      return config || $q.when(config);
    },

   'requestError': function(rejection) {
  	 	$rootScope.error = "HTTP error status=" + rejection.status + "<br/>Description:<br/>- " + rejection.data.replace(/[\n\r]+/, '<br/>- ');
      return $q.reject(rejection);
    },

    'response': function(response) {
    	// do something on success
      return response || $q.when(response);
    },

   'responseError': function(rejection) {
			if (rejection.status === 400) {
				$rootScope.error = "- " + rejection.data.replace(/[\n\r]+/, '<br/>- ');
			} else {
				$rootScope.error = "HTTP error status=" + rejection.status + "<br/>Description:<br/>- " + rejection.data.replace(/[\n\r]+/, '<br/>- ');
			}
      return $q.reject(rejection);
    }
  };
});

//Global methods section

myApp.filter('startFrom', function() {
	return function(input, start) {
		if (input) {
			start = +start; // parse to int
			return input.slice(start);
		}
		return [];
	};
});

myApp.run(function($rootScope, $http, $route, $window, $location) {
	$rootScope.removeEntry = function(entryId, path, entryType) {
		if (confirm('Are you sure you want to delete ' + entryType.toLowerCase() + ' with id=' + entryId + '?')) {
			$http['delete']('/' + path + '/' + entryId).success(function(data) {
				if (data.status) {
					$route.reload();
					$rootScope.notice = entryType + " with id=" + entryId + " was successfully deleted";
				} else {
					$route.reload();
					$rootScope.error = entryType + " with id=" + entryId + " was not deleted";
				};
			});
		}
	};	
	$rootScope.hideNotifications = function() {
		$rootScope.error = "";
		$rootScope.notice = "";
	};
	$rootScope.isUserLoggedIn = function() {
		return ($window.sessionStorage.userId && $window.sessionStorage.username && $window.sessionStorage.token);
	};
	$rootScope.getUserDetails = function() {
		return {'id' : $window.sessionStorage.userId, 'username' : $window.sessionStorage.username};
	};
	$rootScope.xmlEditorOptions = {
      lineWrapping : true,
      lineNumbers: true,
      mode: 'xml',
  };
	$rootScope.xmlViewerOptions = {
      lineWrapping : true,
      lineNumbers: true,
      readOnly: 'nocursor',
      mode: 'xml',
  };
});

function initListNavigationCommons(scope, timeout,
		pSize, orderBy, ths) {
	timeout(function() {
		scope.$apply(function() {
			scope.currentPage = 0;
			scope.pageSize = pSize;
			scope.currentPageSelector = 1;
			scope.currentPageSelectorArray = [];
			scope.Math = window.Math;
			scope.orderByField = orderBy;
			scope.reverseSort = false;
			scope.tableHeaders = ths;
			
			scope.setCurrentPage = function(pageNumber) {
				scope.currentPage = pageNumber;
				scope.currentPageSelector = scope.currentPage + 1;
			};

			scope.updateCurrentPageSelector = function(numberOfRecords) {
				scope.currentPageSelectorArray = [];
				for (var i = 1; i <= Math.ceil(numberOfRecords / scope.pageSize); i++) {
					scope.currentPageSelectorArray.push(i);
				}
			};
			
			scope.reverseReverseSort = function() {
				scope.reverseSort = !scope.reverseSort;
			};
			
			scope.setOrderByField = function(sortField) {	
				scope.orderByField = sortField;
			};
		});
	});
};
