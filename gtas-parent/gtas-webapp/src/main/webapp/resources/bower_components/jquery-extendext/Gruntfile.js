module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        banner:
            '/*!\n'+
            ' * jQuery.extendext <%= pkg.version %>\n'+
            ' *\n'+
            ' * Copyright 2014-<%= grunt.template.today("yyyy") %> Damien "Mistic" Sorel (http://www.strangeplanet.fr)\n'+
            ' * Licensed under MIT (http://opensource.org/licenses/MIT)\n'+
            ' *\n'+
            ' * Based on jQuery.extend by jQuery Foundation, Inc. and other contributors\n'+
            ' */',

        // compress js
        uglify: {
            options: {
                banner: '<%= banner %>\n',
                mangle: { except: ['$'] }
            },
            dist: {
                files: {
                    'jQuery.extendext.min.js': [
                        'jQuery.extendext.js'
                    ]
                }
            }
        },

        // jshint tests
        jshint: {
            lib: {
                files: {
                    src: ['jQuery.extendext.js']
                }
            }
        },

        // qunit test suite
        qunit: {
            all: ['tests/*.html']
        }
    });

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-qunit');
    grunt.loadNpmTasks('grunt-contrib-jshint');

    grunt.registerTask('default', [
        'uglify'
    ]);

    grunt.registerTask('test', [
        'qunit',
        'jshint'
    ]);
};