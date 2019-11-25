const path = require('path')
module.exports = {
  require: [
    'babel-polyfill',
    path.join(__dirname, './src/App.css'),
    path.join(__dirname, './node_modules/bulma/css/bulma.min.css')
  ]
}