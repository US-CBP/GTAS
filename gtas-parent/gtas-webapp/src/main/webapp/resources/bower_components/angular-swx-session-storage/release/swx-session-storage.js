/**
 * angular-swx-session-storage - $sessionStorage service for use in your AngularJS applications.
 * @author Paul Massey, paul.massey@scriptwerx.io
 * @version v1.0.2
 * @build 31 - Mon Feb 29 2016 12:06:44 GMT+0000 (GMT)
 * @link http://www.scriptwerx.io
 * @license MIT
 */
(function(angular) {

    'use strict';

    /**
     * @ngdoc service
     * @name $sessionStorage
     *
     * @requires $window
     * @requires $location
     * @requires $cacheFactory
     *
     * @description Provides a key-value (string-object) session storage with expiry option (in minutes).
     *
     * @param {service} $window The $window service.
     * @param {service} $location The $location service.
     * @param {service} $cacheFactory The $cacheFactory service.
     *
     * @example
     * ```js
     * myApp.$inject = ['$sessionStorage'];
     * function myApp($sessionStorage) {
   *   // Your app code...
   * }
     *
     * angular
     *   .module('myApp', ['swxSessionStorage']);
     * ```
     *
     * @ngInject
     */
    $sessionStorage.$inject = ['$window', '$location', '$cacheFactory'];
    function $sessionStorage($window, $location, $cacheFactory) {

        var prefix = $location.host().substring(0, $location.host().indexOf('.')) + '_',
            oneMinute = 60 * 1000,
            isSessionStorageAvailable = true,
            webStorage,
            cache = $cacheFactory('session-cache'),
            service = this;

        /**
         * @ngdoc method
         * @name $sessionStorage.prefix
         * @methodOf $sessionStorage
         *
         * @description
         * Overrides the default domain prefix.
         *
         * <strong>N.B. Destroys the existing cache.</strong>
         *
         * @param {string} val The string to add to the persistent data prefix.
         *
         * @example
         * ```js
         * $sessionStorage.prefix('myPrefix');
         * ```
         */
        service.prefix = function(val) {
            prefix = val + '_';
            cache.destroy();
            cache = $cacheFactory(prefix + 'cache');
        };

        /**
         * @ngdoc function
         * @name $sessionStorage.put
         * @methodOf $sessionStorage
         *
         * @description Add data to storage
         *
         * @param {string} key The key to store the data with.
         * @param {*} value The data to store.
         * [@param {number} expires] (expiry in minutes)
         */
        service.put = function(key, value) {

            var dataToStore = { data: value };

            if (arguments.length > 2 && angular.isNumber(arguments[2])) {
                dataToStore.expires = new Date().getTime() + (arguments[2] * oneMinute);
            }

            cache.put(key, dataToStore);

            if (isSessionStorageAvailable) {
                webStorage.setItem(prefix + key, angular.toJson(dataToStore, false));
            }

            return value;
        };

        /**
         * @ngdoc function
         * @name $sessionStorage.get
         * @methodOf $sessionStorage
         *
         * @description Get data from storage, will return from session cache if possible for greater performance.
         *
         * @param {String} key The key of the stored data to retrieve.
         * @returns {*} The value of the stored data or undefined.
         */
        service.get = function(key) {

            var item;

            if (cache.get(key)) {
                item = cache.get(key);
            }
            else if (isSessionStorageAvailable) {
                item = angular.fromJson(webStorage.getItem(prefix + key));
            }

            if (!item) {
                return void 0;
            }

            if (item.expires && item.expires < new Date().getTime()) {
                service.remove(key);
                return void 0;
            }

            cache.put(key, item);

            return item.data;
        };

        /**
         * @ngdoc function
         * @name $sessionStorage.remove
         * @methodOf $sessionStorage
         *
         * @descriotion Remove data from storage.
         *
         * @param {String} key The key of the stored data to remove.
         */
        service.remove = function(key) {
            service.put(key, void 0);
            if (isSessionStorageAvailable) {
                webStorage.removeItem(prefix + key);
            }
            cache.remove(key);
        };

        /**
         * @ngdoc function
         * @name $sessionStorage.empty
         * @methodOf $sessionStorage
         *
         * @description Delete all data from session storage and cookie.
         */
        service.empty = function() {
            if (isSessionStorageAvailable) {
                webStorage.clear();
            }
            cache.removeAll();
        };

        /**
         * @private
         * @description
         * Check for $window.localStorage availability and functionality
         */
        (function() {

            // Some browsers will return true when in private browsing mode so test to make sure it's functional.
            try {
                webStorage = $window.sessionStorage;
                var key = 'swxTest_' + Math.round(Math.random() * 1e7);
                webStorage.setItem(key, 'test');
                webStorage.removeItem(key);
            }
            catch (e) {
                isSessionStorageAvailable = false;
            }

        })();
    }

    /**
     * @ngdoc overview
     * @name swxSessionStorage
     *
     * @description
     * $sessionService service for use in your AngularJS applications.
     *
     * Provides a key-value (string-object) session storage with expiry option (in minutes).
     */
    angular
        .module('swxSessionStorage', [])
        .service('$sessionStorage', $sessionStorage);

})(window.angular);