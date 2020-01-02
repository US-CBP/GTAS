import React, { Component } from "react";
import PropTypes from "prop-types";
import { hasData, titleCase } from "../../utils/text";

//Will auto-populate with data retrieved from the given uri
//Attempts to format the header from the column names, but can be passed a header array instead.
class SegmentTable extends Component {
  constructor(props) {
    super(props);
    this.onSort = this.onSort.bind(this);

    this.validateProps();

    this.state = {
      data: [],
      header: []
    };
  }

  validateProps() {
    if (!Array.isArray(this.props.data)) {
      const err = new Error("Table requires a data prop");
      throw err;
    }
  }

  componentDidMount() {
    if (!Array.isArray(this.props.data)) this.getData();
    else this.parseData(this.props.data);
  }

  parseData(data) {
    let noDataObj = {};
    noDataObj[this.props.id] = "No Data Found";

    let dataArray = Array.isArray(data) ? data : [data];
    const sdata = hasData(dataArray) ? dataArray : [noDataObj];
    const sheader = hasData(dataArray)
      ? this.props.header || Object.keys(dataArray[0])
      : [this.props.id];

    this.setState({
      data: sdata,
      header: sheader
    });
  }

  createTable = () => {
    let table = [];
    let header = [];

    // header
    for (let field of this.state.header) {
      header.push(
        <th scope="col" key={field} onClick={this.onSort}>
          {titleCase(field)}
        </th>
      );
    }
    table.push(
      <React.Fragment>
        <thead key={"header"}>
          <tr>{header}</tr>
        </thead>
        <tbody>{this.createRows()}</tbody>
      </React.Fragment>
    );

    return table;
  };

  createRows = () => {
    const stateData = this.state.data;
    let rows = [];
    for (let raw of stateData) {
      let rec = JSON.parse(JSON.stringify(raw, this.state.header, 4));

      let children = [];
      rows.push(<tr key={rec[this.props.id]}>{children}</tr>);
    }
    return rows;
  };

  render() {
    return (
      <div className="card-table">
        <div className="content">
          {this.props.title !== undefined && (
            <h4 className={`title ${this.props.style}`}>{this.props.title}</h4>
          )}
          {this.props.smalltext !== undefined && <small>{this.props.smalltext}</small>}

          <table className="table is-fullwidth is-striped">{this.createTable()}</table>
        </div>
      </div>
    );
  }
}

SegmentTable.propTypes = {
  id: PropTypes.string.isRequired,
  callback: PropTypes.func.isRequired,
  service: PropTypes.func,
  uri: PropTypes.string,
  data: PropTypes.array,
  header: PropTypes.array,
  title: PropTypes.string,
  smalltext: PropTypes.string,
  style: PropTypes.string
};
