/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
// Karma configuration
// Generated on Sat Nov 21 2015 15:28:58 GMT-0500 (Eastern Standard Time)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: './',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
     'src/main/webapp/resources/bower_components/angular/angular.js',
     'src/main/webapp/resources/bower_components/angular-ui-router/release/angular-ui-router.js',
     'src/main/webapp/resources/bower_components/angular-ui-grid/ui-grid.js',
     'src/main/webapp/resources/bower_components/angular-material/angular-material.js',
     'src/main/webapp/resources/bower_components/angular-messages/angular-messages.js',
     'src/main/webapp/resources/bower_components/angular-aria/angular-aria.js',
     'src/main/webapp/resources/bower_components/angular-animate/angular-animate.js',
     'src/main/webapp/resources/bower_components/angular-spinners/dist/angular-spinners.js',
     'src/main/webapp/resources/bower_components/ng-file-upload/ng-file-upload.js',
     'src/main/webapp/resources/bower_components/moment/moment.js',
     'src/main/webapp/resources/bower_components/jquery/dist/jquery.js',
     'src/main/webapp/resources/bower_components/spring-security-csrf-token-interceptor/dist/spring-security-csrf-token-interceptor.min.js',
     'src/main/webapp/resources/bower_components/angular-swx-session-storage/release/swx-session-storage.min.js',
     'src/main/webapp/resources/bower_components/angular-mocks/angular-mocks.js',
     'src/main/webapp/app.js',
     'src/main/webapp/login/AuthService.js',
     'src/main/webapp/factory/JqueryQueryBuilderWidget.js',
     'src/main/webapp/common/services.js',    
     'src/main/webapp/admin/AdminController.js',
     'src/main/webapp/build/BuildController.js',
     'src/main/webapp/watchlists/WatchListController.js',
     'src/main/webapp/test/unit/**/*.js'],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['IE'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultanous
    concurrency: Infinity
  })
}
