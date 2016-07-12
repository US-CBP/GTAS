#spring-security-csrf-token-interceptor

An AngularJS interceptor that will include the CSRF token header in HTTP requests.

It does this by doing an AJAX HTTP HEAD call to /, and then retrieves the HTTP header 'X-CSRF-TOKEN' and sets this
same token on all HTTP requests.

#Installing
###Via Bower
````
$ bower install spring-security-csrf-token-interceptor
````
###Via NPM
````
$ npm install spring-security-csrf-token-interceptor
````

#Usage
Include this as a depenency on your application:

````javascript
angular.module('myApp', ['spring-security-csrf-token-interceptor']);
````