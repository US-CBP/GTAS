import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {hasData, titleCase, isObject} from '../../utils/text';

//Will auto-populate with data retrieved from the given uri
//Attempts to format the header from the column names, but can be passed a header array instead.
class Table extends Component {
  constructor(props) {
    super(props);
    this.onSort = this.onSort.bind(this);

    this.validateProps();

    this.state = {
      data: [],
      header: [],
      // sortField: this.props.id,
      // sortDir: 'asc',
    };
  }

  validateProps() {
    //APB - hasData on func??
    if (!hasData(this.props.uri) && !hasData(this.props.data) && !hasData(this.props.service)) {
      const err = new Error('Table requires a uri, service func, or data prop');
      throw err;
    }
  }

  componentDidMount() {
    if (!hasData(this.props.data))
      this.getData();
    else
      this.parseData(this.props.data);
  }

  onSort(e) {
    console.log(e.target.innerHTML, ' header clicked');
    // text in innerHTML is titlecased, won't match the actual field names. How to get the key value?
    // const field = e.target.innerHTML;

//     this.setState({
//       sortField: field,
// //      params: 
//     });

//    this.getData()
  }

  parseData(data) {
    let noDataObj = {};
    noDataObj[this.props.id] = "No Data Found";

    let dataArray = Array.isArray(data) ? data : [data];
    const sdata = hasData(dataArray) ? dataArray : [noDataObj];
    const sheader = hasData(dataArray) ? (this.props.header || Object.keys(dataArray[0])) : [this.props.id];

    this.setState({
      data: sdata,
      header: sheader,
    });
  }

  getData(params = null) {
    //Assumption is that we are performing a GET only - just populating the table
    this.props.service(params)
    .then(response => {
      this.parseData(response);
    })
    .catch(error => {
      console.log(error);
      this.parseData([]);
    });
  }

  //APB - needs error handling. Error boundary??S
  //APB - optional buttons, links ??
  createTable = () => {
    let table = [];
    let header = [];
    const stateData = this.state.data;

    // header
    for (let field of this.state.header) {
      header.push(<th scope="col" key={field} onClick={this.onSort}>{titleCase(field)}</th>);
    }
    table.push(<thead key={'header'}><tr>{header}</tr></thead>);

    // rows
    for (let raw of stateData) {
      //ensure data field order matches the header field order. Fixes issue with json-server data.
      let rec = JSON.parse(JSON.stringify(raw, this.state.header, 4));

      let children = [];
      for (let field in rec) {
        children.push(<td key={field}>{rec[field]}</td>);
      }
      table.push(<tbody key={rec[this.props.id]}><tr>{children}</tr></tbody>);
    }
    return table
  }

  render() {
    return (
      <div className="card-table">
        <div className="content">
          { (this.props.title !== undefined && <h4 className={`title ${this.props.style}`}>{this.props.title}</h4> ) }
          { (this.props.smalltext !== undefined && <small>{this.props.smalltext}</small>) }
          
          <table className="table is-fullwidth is-striped">
            { this.createTable() }
          </table>
        </div>
      </div>
    );
  }
}

Table.propTypes = {
  id: PropTypes.string.isRequired,
  callback: PropTypes.func.isRequired,
  service: PropTypes.func,
  uri: PropTypes.string,
  data: PropTypes.array,
  header: PropTypes.array,
  title: PropTypes.string,
  smalltext: PropTypes.string,
  style: PropTypes.string
}

export default Table;
