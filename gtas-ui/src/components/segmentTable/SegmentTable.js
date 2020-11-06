import React from "react";
import PropTypes from "prop-types";
import { hasData } from "../../utils/utils";
import RBTable from "react-bootstrap/Table";
import "./SegmentTable.css";

/**
 * Table for PNR data segments. Highlights and scrolls to rows with a class matching
 * the string passed into setActiveKey by the parent container.
 */

// Pass in the color as a prop?
class SegmentTable extends React.Component {
  constructor(props) {
    super(props);

    let nodata = {};
    nodata[props.id] = "No Data Found";

    let dataArray = Array.isArray(props.data) ? props.data : [props.data];
    const sdata = hasData(dataArray) ? dataArray : [nodata];
    const sheader = hasData(dataArray) ? Object.keys(dataArray[0]) : [props.id];

    const refs = sdata.reduce((acc, rec) => {
      acc[rec.key] = React.createRef();
      return acc;
    }, {});

    this.state = {
      data: sdata,
      header: sheader,
      refs: refs
    };
  }

  setActiveKey = key => {
    this.setState({ activeKey: key });
    const row = this.state.refs[key];

    if (row) {
      row.current.scrollIntoView({
        behavior: "smooth",
        block: "nearest"
      });
    }
  };

  render() {
    let idx = 0;
    return (
      <div className="tableHeight">
        {this.props.title && (
          <h4 className={`title ${this.props.style}`}>{this.props.title}</h4>
        )}

        <RBTable striped bordered hover size="sm" responsive>
          <tbody>
            {this.state.data.map(raw => {
              const rec = JSON.parse(JSON.stringify(raw, this.state.header, 4));
              const active = this.state.activeKey === +rec.key ? " highlight" : "";

              return (
                <tr
                  key={idx}
                  className={`${rec.key}${active}`}
                  ref={this.state.refs[rec.key]}
                >
                  <td className={rec.key}>{idx++}</td>
                  <td className={rec.key}>{rec.value}</td>
                </tr>
              );
            })}
          </tbody>
        </RBTable>
      </div>
    );
  }
}

SegmentTable.propTypes = {
  id: PropTypes.string.isRequired,
  data: PropTypes.array.isRequired,
  title: PropTypes.string
};

export default SegmentTable;
