import React, { Component } from "react";
import PropTypes from "prop-types";
import { hasData, titleCase } from "../../utils/text";
import { useTable, usePagination, useSortBy, useFilters } from "react-table";
// import matchSorter from "match-sorter";
import "./Table.css";
import { withTranslation } from "react-i18next";
import Xl8 from "../xl8/Xl8";
import BTable from 'react-bootstrap/Table';

//Will auto-populate with data retrieved from the given uri
//Attempts to format the header from the column names, but can be passed a header array instead.

class Table extends Component {
  constructor(props) {
    super(props);

    this.validateProps();

    this.state = {
      data: [],
      header: [],
      columns: []
    };
  }

  RTable({ columns, data }) {
    const {
      getTableProps,
      getTableBodyProps,
      headerGroups,
      page,
      prepareRow,
      canPreviousPage,
      canNextPage,
      pageOptions,
      pageCount,
      gotoPage,
      nextPage,
      previousPage,
      setPageSize,
      state: { pageIndex, pageSize }
    } = useTable(
      {
        columns,
        data,
        initialState: { pageIndex: 1 }
      },
      useFilters,
      useSortBy,
      usePagination
      // useRowSelect,
      // hooks => {
      //   hooks.flatColumns.push(columns => [
      //     // Let's make a column for selection
      //     {
      //       id: "selection",
      //       // The header can use the table's getToggleAllRowsSelectedProps method
      //       // to render a checkbox
      //       Header: ({ getToggleAllRowsSelectedProps }) => (
      //         <div>
      //           <ICheckbox {...getToggleAllRowsSelectedProps()} />
      //         </div>
      //       ),
      //       // The cell can use the individual row's getToggleRowSelectedProps method
      //       // to the render a checkbox
      //       Cell: ({ row }) => (
      //         <div>
      //           <ICheckbox {...row.getToggleRowSelectedProps()} />
      //         </div>
      //       )
      //     },
      //     ...columns
      //   ]);
      // }
    );

    const sortIcon = column => {
      const icon = column.isSorted ? (column.isSortedDesc ? "🔽" : "🔼") : " ";
      return icon;
    };

    const fullWidth = {
      width: "-webkit-fill-available",
      padding: "5px"
    };
    const zeroPad = {
      padding: "0px"
    };
    return (
      <>
        <BTable {...getTableProps()} responsive striped bordered hover>
          <thead>
            {headerGroups.map(headerGroup => (
              <tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map(column => (
                  <th
                    {...column.getHeaderProps(column.getSortByToggleProps())}
                    style={zeroPad}
                  >
                    <input
                      style={fullWidth}
                      type="button"
                      value={`${column.render("Header")} ${sortIcon(column)}`}
                    ></input>
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
            {page.map((row, i) => {
              prepareRow(row);
              return (
                <tr {...row.getRowProps()}>
                  {row.cells.map(cell => {
                    return <td {...cell.getCellProps()}>{cell.render("Cell")}</td>;
                  })}
                </tr>
              );
            })}
          </tbody>
        </BTable>

        <div className="pagination">
          <button onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
            {"<<"}
          </button>{" "}
          <button onClick={() => previousPage()} disabled={!canPreviousPage}>
            {"<"}
          </button>{" "}
          <button onClick={() => nextPage()} disabled={!canNextPage}>
            {">"}
          </button>{" "}
          <button onClick={() => gotoPage(pageCount - 1)} disabled={!canNextPage}>
            {">>"}
          </button>{" "}
          <span>
            Page{" "}
            <strong>
              {pageIndex + 1} of {pageOptions.length}
            </strong>{" "}
          </span>
          <span>
            | Go to page:{" "}
            <input
              type="number"
              // defaultValue={pageIndex + 0}
              onChange={e => {
                const page = e.target.value ? Number(e.target.value) - 1 : 0;
                gotoPage(page);
              }}
              style={{ width: "100px" }}
            />
          </span>{" "}
          <select
            value={pageSize}
            onChange={e => {
              setPageSize(Number(e.target.value));
            }}
          >
            {[10, 25, 50].map(pageSize => (
              <option key={pageSize} value={pageSize}>
                Show {pageSize}
              </option>
            ))}
          </select>
        </div>
      </>
    );
  }

  validateProps() {
    // Allow empty arrays for the data prop, only verify that its an array.
    // Uri and service props are tested for truthy values.
    if (
      !hasData(this.props.uri) &&
      !Array.isArray(this.props.data) &&
      !hasData(this.props.service)
    ) {
      const err = new Error("Table requires a uri, service (func), or data prop");
      throw err;
    }
  }

  // shouldComponentUpdate(nextProps, nextState, nextContext) {
  //   return this.props.data !== nextProps.data;
  // }

  componentDidMount() {
    if (!Array.isArray(this.props.data)) this.getData();
    else this.parseData(this.props.data);
  }

  parseData(data) {
    let noDataObj = [{}];
    noDataObj[0][this.props.id] = "No Data Found";

    let dataArray = Array.isArray(data) ? data : [data];
    const isPopulated = hasData(dataArray);
    const sdata = isPopulated ? dataArray : noDataObj;
    const sheader = isPopulated
      ? this.props.header || Object.keys(dataArray[0])
      : [this.props.id];

    let columns = [];

    sheader.forEach(element => {
      const acc = element.Accessor || element;
      let xl8Title = undefined;
      const trans = this.props.t(element.xid);
      // TODO - refac. - logic shd be exposed by xl8 or a util.
      // How to set the class on translation fail for a direct translation?
      if (element.xid !== undefined) {
        xl8Title = trans !== element.xid ? trans : undefined;
      }

      if (!(this.props.ignoredFields || []).includes(acc)) {
        const title = titleCase(xl8Title || element.Header || acc);
        let cellconfig = {
          Header: title,
          accessor: acc
        };

        if (element.Cell !== undefined) {
          cellconfig.Cell = element.Cell;
        }

        columns.push(cellconfig);
      }
    });

    this.setState({
      data: sdata,
      header: sheader,
      columns: columns
    });
  }

  getData(params = null) {
    //Assumption is that we are performing a GET only - just populating the table
    this.props
      .service(params)
      .then(response => {
        this.parseData(response);
      })
      .catch(error => {
        console.log(error);
        this.parseData([]);
      });
  }

  render() {
    return (
      <div>
        <div>
          {this.props.title !== undefined && (
            <h4 className={`title ${this.props.style}`}>{this.props.title}</h4>
          )}
          {this.props.smalltext !== undefined && <small>{this.props.smalltext}</small>}
          <div className="content">
            <Xl8>
              <this.RTable
                columns={this.state.columns}
                data={this.state.data}
              ></this.RTable>
            </Xl8>
          </div>
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
  style: PropTypes.string,
  ignoredFields: PropTypes.arrayOf(PropTypes.string)
};

export default withTranslation()(Table);
