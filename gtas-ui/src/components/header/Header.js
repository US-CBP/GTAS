import React, { useContext, useState, useRef } from "react";
import { Link } from "@reach/router";
import { Nav, Navbar, NavDropdown, Form, FormControl, Button } from "react-bootstrap";
import { navigate, useLocation } from "@reach/router";
import { UserContext } from "../../context/user/UserContext";
import RoleAuthenticator from "../../context/roleAuthenticator/RoleAuthenticator";
import { ROLE } from "../../utils/constants";
import "./Header.scss";
import wcoLogo from "../../images/WCO_GTAS_header_brand.svg";

const Header = () => {
  const { getUserState, userAction } = useContext(UserContext);

  const user = getUserState();
  const currentPath = useLocation();

  const logout = () => {
    userAction({ type: "logoff" });

    navigate("/login");
  };

  if (user === undefined) logout();

  const userFullName = user?.fullName || "";

  const headerTabs = {
    DASHBOARD: "/gtas/dashboard",
    FLIGHT: "/gtas/flights",
    VETTING: "/gtas/vetting",
    TOOLS: "/gtas/tools",
    ADMIN: "/gtas/admin"
  };

  const toggleRef = useRef();

  const clickTab = tabName => {
    if (toggleRef.current.clientHeight > 0) {
      toggleRef.current.click();
    }
  };

  const getActiveClass = tabName => {
    return currentPath.pathname === tabName ? "active-tab" : "";
  };

  return (
    <Navbar sticky="top" expand="md" className="header-navbar" variant="light">
      <Navbar.Brand className="header-navbar-brand">
        <Link to="dashboard" onClick={() => clickTab(headerTabs.DASHBOARD)}>
          <img src={wcoLogo} />
        </Link>
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="responsive-navbar-nav" ref={toggleRef} />
      <Navbar.Collapse>
        <Nav variant="tabs" className="left-nav">
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
          <Nav.Link
            as={Link}
            to="admin"
            className={`${getActiveClass(headerTabs.ADMIN)}`}
            onClick={() => clickTab(headerTabs.ADMIN)}
          >
            Admin
          </Nav.Link>

          <NavDropdown title="Tools" id="nav-dropdown">
            <NavDropdown.Item
              as={Link}
              to="tools/queries"
              className="fa fa-filter"
              onClick={() => clickTab("")}
            >
              Queries
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item
              as={Link}
              to="tools/rules"
              className="fa fa-flag"
              onClick={() => clickTab("")}
            >
              Rules
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item
              as={Link}
              to="tools/watchlist"
              className="fa fa-eye"
              onClick={() => clickTab("")}
            >
              Watchlist
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item
              as={Link}
              to="tools/neo4j"
              className="fa fa-filter"
              onClick={() => clickTab("")}
            >
              Neo4J Browser
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item
              as={Link}
              to="tools/about"
              className="fa fa-info-circle"
              onClick={() => clickTab("")}
            >
              About
            </NavDropdown.Item>
          </NavDropdown>
        </Nav>
        <Nav className="navbar-search">
          <Form inline>
            <FormControl type="text" placeholder="Search" className="mr-sm-2" />
            <Button variant="outline-light">Search</Button>
          </Form>
        </Nav>
        <Nav variant="tabs" className="ml-auto">
          <NavDropdown title={userFullName} id="basic-nav-dropdown" className="right">
            <NavDropdown.Item as={Link} to="#" onClick={() => clickTab("")}>
              Change Password
            </NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item as={Link} to="#" onClick={logout}>
              Logout
            </NavDropdown.Item>
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default Header;
