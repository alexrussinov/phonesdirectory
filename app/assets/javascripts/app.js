/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 17/08/14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */

var phonesBook = angular.module('phonesBook', ['ui.bootstrap']);

phonesBook.controller('PhoneListCtrl', function ($scope,$http) {
    $scope.currentPage = 1;
    $scope.maxSize = 5;
    $scope.numPages =5;
    $scope.q ={
        query : "",
        page: 1,
        size: 10
    };

    // default object for phone entry item
    $scope.entry = {
        id: 0,
        name: "",
        phone_number: ""
    };


    $scope.editMode = false;
    $scope.entryExist = false;
    $scope.entryInserted = false;


    $http.post('/entries', $scope.q).success(function(dat){
        $scope.entries = dat.data;
        $scope.totalItems = dat.numberOfPages;
    });


    $scope.pageChanged = function() {
        console.log('Page changed to: ' + $scope.currentPage);
        $scope.q.page=$scope.currentPage;
        $http.post('/entries', $scope.q).success(function(dat){
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
        }).error(function(data, status, headers, config){
                console.log(data);
            });
    };

    $scope.search = function(){
        console.log('Query: ' + $scope.query);
        if (typeof $scope.query != 'undefined')
        $scope.q.query = $scope.query;
        else
            $scope.q.query = "";

        $http.post('/entries', $scope.q).success(function(dat){
            console.log(dat);
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
        }).error(function(data, status, headers, config){
                console.log(data);
            });
    };

    $scope.deleteEntry = function(entry){
        $http.post('/delete', entry).success(function(dat){
            console.log(dat);
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
        }).error(function(data, status, headers, config){
                console.log(data);
            });
    };

    $scope.updateEntry = function(entry){
        $http.post('/update', entry).success(function(dat){
            console.log(dat);
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
        }).error(function(data, status, headers, config){
                console.log(data);
            });
    };

    $scope.cancel= function(){
        $scope.editMode = false;
        $http.post('/entries', $scope.q).success(function(dat){
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
        });
    };

    $scope.insert = function(){
        $scope.entry.name=$scope.entryName;
        $scope.entry.phone_number=$scope.entryPhone;
        $http.post('/insert', $scope.entry).success(function(dat){
            $scope.entries = dat.data;
            $scope.totalItems = dat.numberOfPages;
            $scope.entryInserted = true;
            $scope.entry.name="";
            $scope.entry.phone_number="";
        }).error(function(data, status, headers, config){
                console.log(data);
                $scope.entryExist = true;
            });
    };
});

phonesBook.directive('editablePhone', function(){

    return {
        restrict : 'E',
        replace : true,
        templateUrl: "/assets/fragments/editable_phone.html"
        };
});

phonesBook.directive('editableName', function(){

    return {
        restrict : 'E',
        replace : true,
        templateUrl: "/assets/fragments/editable_name.html"
        };
});
