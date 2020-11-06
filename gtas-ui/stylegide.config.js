const path = require('path')
module.exports = {
  require: [
    'babel-polyfill',
    path.join(__dirname, './src/App.css'),
  ]
}