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
        <div className="column is-3">
          <div className="box2">
            <aside className="menu">
              <p className="menu-label">General</p>
              <ul className="menu-list">
                <li>
                  <Link to="/gtas/dashboard">Dashboard</Link>
                </li>
                <li>Customers</li>
                <li>Other</li>
              </ul>
              <p className="menu-label">Administration</p>
              <ul className="menu-list">
                <li>Team Settings</li>
                <li>
                  Manage Your Team
                  <ul>
                    <li>Members</li>
                    <li>Plugins</li>
                    <li>Add a member</li>
                    <li>Remove a member</li>
                  </ul>
                </li>
                <li>Invitations</li>
                <li>Cloud Storage Environment Settings</li>
                <li>Authentication</li>
                <li>Payments</li>
              </ul>
              <p className="menu-label">Transactions</p>
              <ul className="menu-list">
                <li>Payments</li>
                <li>Transfers</li>
                <li>Balance</li>
                <li>Reports</li>
              </ul>
            </aside>
          </div>
        </div>

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
