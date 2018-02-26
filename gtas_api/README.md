API Exposing App to connect to GTAS data stores

May need to follow special instructions to get mysql dialect working on windows.
https://github.com/nodejs/node-gyp

Modify config.js with implementation values before setting up.

How to obtain a JWT Token:
POST to /auth, inside the body using x-www-form-urlencoded and put username and password.

How to use GraphQL Endpoint:
GET to /graphql, inside header use
Key: Authorization
Value: Bearer YOUR_JWT_TOKEN

Current schema is available in ./data/schema.js
