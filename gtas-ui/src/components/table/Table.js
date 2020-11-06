import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { hasData, titleCase, asArray, altObj, isObject } from "../../utils/utils";
import { useTable, usePagination, useSortBy } from "react-table";
import { navigate } from "@reach/router";
// import { withTranslation } from 'react-i18next';
// import Xl8 from '../xl8/Xl8';
import { Table as RBTable, Col, Pagination } from "react-bootstrap";
import "./Table.css";

//Will auto-populate with data retrieved from the given uri
//Attempts to format the header from the column names, but can be passed a header array instead.

const Table = props => {
  const [data, setData] = useState(props.data || []);
  const [header, setHeader] = useState(props.header || []);
  const [columns, setColumns] = useState([]);
  const [rowcount, setRowcount] = useState("");
  const stateVals = props.hasOwnProperty("stateVals") ? altObj(props.stateVals()) : {};

  useEffect(() => {
    validateProps();
    if (!Array.isArray(props.data)) getData();
    else parseData(props.data);
  }, []);

  const RTable = ({ columns, data }) => {
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
      state: { pageIndex, pageSize, sortBy }
    } = useTable(
      {
        columns,
        data,
        initialState: {
          pageIndex: stateVals.pageIndex || 0,
          pageSize: stateVals.pageSize || 25,
          sortBy: stateVals.sortBy || []
        }
      },
      useSortBy,
      usePagination
    );

    const sortIcon = column => {
      if (hasData(props.stateCb))
        props.stateCb({ pageSize: pageSize, pageIndex: pageIndex, sortBy: sortBy });

      const icon = column.isSorted ? (
        column.isSortedDesc ? (
          <i className="fa fa-sort-down p-2" />
        ) : (
          <i className="fa fa-sort-up p-2" />
        )
      ) : (
        <i className="fa fa-sort p-2" />
      );
      return icon;
    };
    // const collator = new Intl.Collator(undefined, {
    //   numeric: true,
    //   sensitivity: 'base',
    // });

    // this is a hack. Need access to the record set displayed in outbound queries as it is sorted so we can scroll
    // through it on the query detail page **in the same order**. Issue is that the react-table version that exposes the sorted
    // recordset isn't working, so I have to mimmick the additional sorting by applying the current sort state to the data passed
    // in from the DB. Works mostly, but sorts alphanumerics differently from react-table. Can apply Intl.Collator to correct this
    // or just find the correct api to get the digested/sorted data directly from the react-table dep.
    const getLinkData = () => {
      let dt = data.slice();
      let result = dt;

      sortBy.forEach(sort => {
        // result = dt.sort(sortValues(sort.id, sort.desc));
      });

      let ids = result.map(item => {
        return [item.id];
      });

      return ids.filter(Boolean);
    };

    return (
      <>
        <Col lg="12" className="table-main">
          <RBTable {...getTableProps()} striped bordered hover>
            <thead>
              {headerGroups.map(headerGroup => {
                return (
                  <tr {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map(column => {
                      return (
                        <th
                          className="table-header"
                          {...column.getHeaderProps(column.getSortByToggleProps())}
                        >
                          {`${column.render("Header")} `} {sortIcon(column)}
                        </th>
                      );
                    })}
                  </tr>
                );
              })}
            </thead>
            <tbody {...getTableBodyProps()}>
              {page.map((row, i) => {
                prepareRow(row);
                const link = row.original.link;
                const linked = link ? "linked" : "";
                return (
                  <tr {...row.getRowProps()} className={linked}>
                    {row.cells.map(cell => {
                      const style = cell.column.className || "";
                      if (link) {
                        return (
                          <td
                            className={` p-1 ${style}`}
                            {...cell.getCellProps()}
                            onClick={() =>
                              navigate(link, {
                                state: { data: getLinkData() }
                              })
                            }
                          >
                            {cell.render("Cell")}
                          </td>
                        );
                      }
                      return (
                        <td className={` p-1 ${style}`} {...cell.getCellProps()}>
                          {cell.render("Cell")}
                        </td>
                      );
                    })}
                  </tr>
                );
              })}
            </tbody>
          </RBTable>

          <Pagination>
            <Pagination.First onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
              <i className="fa fa-fast-backward"></i>
            </Pagination.First>
            <Pagination.Prev onClick={() => previousPage()} disabled={!canPreviousPage}>
              <i className="fa fa-backward"></i>
            </Pagination.Prev>
            <Pagination.Next onClick={() => nextPage()} disabled={!canNextPage}>
              <i className="fa fa-forward"></i>
            </Pagination.Next>
            <Pagination.Last
              className="mr-10"
              onClick={() => gotoPage(pageCount - 1)}
              disabled={!canNextPage}
            >
              <i className="fa fa-fast-forward"></i>
            </Pagination.Last>
            <span className="pag-text mr-10">
              Page
              <strong className="pag-num">
                {pageIndex + 1} of {pageOptions.length}
              </strong>{" "}
            </span>
            {/* <span className="pag-text pag-goto">Go to page: </span>{' '}
            <input
              className="pag pag-input pag-goto mr-10"
              type="number"
              min="1"
              onChange={(e) => {
                const page = e.target.value ? Number(e.target.value) - 1 : 0;
                gotoPage(page);
                e.target.value = page;
              }}
              style={{ width: '100px' }}
            /> */}
            <select
              className="pag"
              value={pageSize}
              onChange={e => {
                setPageSize(Number(e.target.value));
              }}
            >
              {[10, 25, 50, 100].map(pageSize => (
                <option key={pageSize} value={pageSize}>
                  Show {pageSize}
                </option>
              ))}
            </select>
            <span className="tagrightpag">
              <h3 className="title-default">
                <i>{rowcount}</i>
              </h3>
            </span>
          </Pagination>
        </Col>
      </>
    );
  };

  const validateProps = () => {
    // Allow empty arrays for the data prop, only verify that its an array.
    // Uri and service props are tested for truthy values.
    if (!hasData(props.uri) && !Array.isArray(props.data) && !hasData(props.service)) {
      // const err = new Error("Table requires a uri, service (func), or data prop");
      console.log("Table requires a uri, service (func), or data prop");
      // throw err;
    }
  };

  const parseData = data => {
    const noDataFound = "No Data Found";
    let noDataObj = [{}];
    noDataObj[0][props.id] = noDataFound;

    let dataArray = asArray(data);
    const isPopulated = hasData(dataArray);
    const sdata = isPopulated ? dataArray : noDataObj;

    const sheader = isPopulated
      ? hasData(header)
        ? header
        : Object.keys(dataArray[0])
      : [props.id];

    let columns = [];

    (sheader || []).forEach(element => {
      const acc = element.Accessor || element;
      // let xl8Title = undefined;
      // const trans = props.t(element.xid);
      // TODO - refac. - logic shd be exposed by xl8 or a util.
      // How to set the class on translation fail for a direct translation?
      // if (element.xid !== undefined) {
      //   xl8Title = trans !== element.xid ? trans : undefined;
      // }

      if (!(props.ignoredFields || []).includes(acc) && !Array.isArray(acc)) {
        // const title = titleCase(xl8Title || element.Header || acc);
        const title = titleCase(element.Header || acc);
        let cellconfig = { Header: title, accessor: acc };

        if (element.Cell !== undefined) {
          cellconfig.Cell = element.Cell;
        }

        columns.push(cellconfig);
      }
    });

    setData(sdata);
    setHeader(sheader);
    setColumns(columns);

    //exclude the No-Data-Found row from the count
    if (dataArray.length === 1 && dataArray[0][props.id] == noDataFound) setRowcount(0);
    else setRowcount(dataArray.length);
  };

  const getData = (params = null) => {
    if (!hasData(props.service)) {
      parseData({});
      return;
    }
    props
      .service(params)
      .then(response => {
        parseData(response);
      })
      .catch(error => {
        console.log(error);
        parseData([]);
      });
  };

  return (
    <>
      {props.title !== undefined && (
        <h4 className={`title ${props.style}`}>{props.title}</h4>
      )}
      {props.smalltext !== undefined && <small>{props.smalltext}</small>}
      {/* <Xl8> */}
      <RTable
        columns={columns}
        data={data}
        rowcount={rowcount}
        // initSort={props.initSort || []}
      ></RTable>
      {/* </Xl8> */}
    </>
  );
};

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
  stateCb: PropTypes.func,
  stateVals: PropTypes.func,
  ignoredFields: PropTypes.arrayOf(PropTypes.string)
};

// export default withTranslation()(Table);
export default Table;
