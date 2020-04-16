import React, { useState, useRef } from "react";
import { Link } from "@reach/router";
import { Nav, Navbar, NavDropdown, Form, FormControl, Button } from "react-bootstrap";
import "./Header.scss";

const Header = () => {
  let user = JSON.parse(localStorage.getItem("user"));
  const loggedinUserName = user ? user.lastName + ", " + user.firstName : "User Name";

  const headerTabs = {
    DASHBOARD: "dashboard",
    FLIGHT: "flight",
    VETTING: "vetting",
    TOOLS: "tools",
    ADMIN: "admin"
  };

  const [activeTab, setActiveTab] = useState(headerTabs.DASHBOARD);
  const toggleRef = useRef();

  const clickTab = tabName => {
    if (toggleRef.current.clientHeight > 0) {
      toggleRef.current.click();
    }
    setActiveTab(tabName);
  };

  const getActiveClass = tabName => {
    return activeTab === tabName ? "active-tab" : "";
  };

  return (
    <Navbar sticky="top" expand="md" className="header-navbar">
      <Navbar.Brand>
        <Link
          to="/gtas"
          className="header-navbar-brand"
          onClick={() => setActiveTab(headerTabs.DASHBOARD)}
        >
          GTAS
        </Link>
      </Navbar.Brand>
      <Navbar.Toggle
        aria-controls="responsive-navbar-nav"
        className="navbar-toggler"
        ref={toggleRef}
      />
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav variant="tabs" className="mr-auto">
          <Nav.Link
            as={Link}
            to="dashboard"
            className={`${getActiveClass(headerTabs.DASHBOARD)}`}
            onClick={() => clickTab(headerTabs.DASHBOARD)}
          >
            Dashboard
          </Nav.Link>
          <Nav.Link
            as={Link}
            to="flights"
            className={`${getActiveClass(headerTabs.FLIGHT)}`}
            onClick={() => clickTab(headerTabs.FLIGHT)}
          >
            Flights
          </Nav.Link>
          <Nav.Link
            as={Link}
            to="vetting"
            className={`${getActiveClass(headerTabs.VETTING)}`}
            onClick={() => clickTab(headerTabs.VETTING)}
          >
            Vetting
          </Nav.Link>

          <NavDropdown title="Tools" id="nav-dropdown">
            <NavDropdown.Item as={Link} to="tools/queries" onClick={() => clickTab("")}>
              Queries
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="tools/rules" onClick={() => clickTab("")}>
              Rules
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="tools/watchlist" onClick={() => clickTab("")}>
              Watchlist
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="tools/neo4j" onClick={() => clickTab("")}>
              Neo4J Browser
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="tools/about" onClick={() => clickTab("")}>
              About
            </NavDropdown.Item>
          </NavDropdown>

          <Nav.Link
            as={Link}
            to="admin"
            className={`${getActiveClass(headerTabs.ADMIN)}`}
            onClick={() => clickTab(headerTabs.ADMIN)}
          >
            Admin{" "}
          </Nav.Link>
        </Nav>
        <Nav className="mr-auto">
          <Form inline>
            <FormControl type="text" placeholder="Search" className="mr-sm-2" />
            <Button variant="outline-light">Search</Button>
          </Form>
        </Nav>
        <Nav variant="tabs">
          <NavDropdown title={loggedinUserName} id="basic-nav-dropdown" className="right">
            <NavDropdown.Item as={Link} to="#" onClick={() => clickTab("")}>
              Change Password
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="#" onClick={() => clickTab("")}>
              Logout
            </NavDropdown.Item>
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default Header;
