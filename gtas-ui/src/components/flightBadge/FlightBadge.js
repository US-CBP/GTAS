import React from "react";

const FlightBadge = props => {
  return (
    <div className="columns">
      <div className="column is-one-fifth">
        <h4 className="block-label margin-0 fa fa-plane"> {props.flightNumber}</h4>
      </div>
      <h6 className="block-label flex margin-0 ">
        <table className="table table-condensed table-borderless margin-0">
          <tbody>
            <tr>
              <td className="text-right">
                <span className="fa fa-arrow-circle-up"> {props.origin}</span>
              </td>
              <td>{props.etd}</td>
            </tr>
            <tr>
              <td className="text-right">
                <span className="fa fa-arrow-circle-down"> {props.destination}</span>
              </td>
              <td>{props.eta}</td>
            </tr>
          </tbody>
        </table>
      </h6>
    </div>
  );
};

export default FlightBadge;
