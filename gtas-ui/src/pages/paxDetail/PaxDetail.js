import React from "react";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import Tabs from "../../components/tabs/Tabs";
import { Row } from "react-bootstrap";

const PaxDetail = props => {
  const tabcontent = props.children.props.children;
  const tabs = [
    { title: "Summary", link: tabcontent[0] },
    { title: "APIS", link: tabcontent[1] },
    { title: "PNR", link: tabcontent[2] },
    { title: "Flight History", link: tabcontent[3] },
    { title: "Link Analysis", link: tabcontent[4] }
  ];
  return (
    <div className="columns">
      <Title title="Passenger Detail"></Title>
      <Row>
        <div className="column col-lg-3 col-md-3 col-12">
          <div className="box2">
            <aside className="menu">
              <p className="menu-label">General</p>
              <ul className="menu-list">
                <li>
                  <Link to="/Dashboard">Dashboard</Link>
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

        <div className="col-lg-9 col-md-9 col-12">
          <div className="box2">
            <div className="top">
              <Tabs tabs={tabs} />
              <div></div>
            </div>
          </div>
        </div>
      </Row>
    </div>
  );
};

export default PaxDetail;
