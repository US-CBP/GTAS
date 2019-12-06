import React from "react";
import Table from "../../components/table/Table";
import { hacks } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";

const FlightPax = () => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title="Flight Passengers"></Title>

      <div className="columns">
        <div className="column">
          <div className="box2">
            <div className="top">
              <Link to="/gtas/paxdetail">Passenger Details</Link>
              <Table service={hacks.get} id="foo" callback={cb}></Table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FlightPax;
