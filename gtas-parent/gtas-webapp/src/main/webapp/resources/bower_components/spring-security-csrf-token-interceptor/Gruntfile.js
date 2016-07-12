/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        jshint: {
            files: ["src/<%= pkg.name %>.js"]
        },
        ngmin: {
            default: {
                src: ['src/<%= pkg.name %>.js'],
                dest: 'generated/<%= pkg.name %>.js'
            }
        },
        uglify: {
            options: {
                mangle: false
            },
            default: {
                files:  {
                    'dist/<%= pkg.name %>.min.js': ['generated/<%= pkg.name %>.js']
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-ngmin');

    grunt.registerTask('default', ['jshint', 'ngmin', 'uglify']);
};