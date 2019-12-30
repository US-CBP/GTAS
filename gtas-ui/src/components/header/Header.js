import React from "react";
import { Link } from "@reach/router";
import { Nav, Navbar, NavDropdown, Form, FormControl, Button } from "react-bootstrap";
import { store } from '../../appContext';

const Header = () => {
  const globalState = React.useContext(store);
  const loggedinUserName = globalState.state.user ?
    globalState.state.user.lastName + ", " + globalState.state.user.firstName
    : "User Name";





  return (
    <Navbar sticky="top" expand="md">
        <Navbar.Brand><Link to="/gtas">GTAS</Link></Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse>
          <Nav variant="tabs">
            <Nav.Link as={Link} to="dashboard">Dashboard</Nav.Link>
            <Nav.Link as={Link} to="flights"> Flights</Nav.Link>
            <Nav.Link as={Link} to="vetting">Vetting</Nav.Link>

            <NavDropdown title="Tools" id="basic-nav-dropdown">
              <NavDropdown.Item as={Link} to="tools/queries">Queries</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="tools/rules">Rules</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="tools/watchlist">Watchlist</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="tools/neo4j">Neo4J Browser</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="tools/about">About</NavDropdown.Item>
            </NavDropdown>

            <Nav.Link as={Link} to="admin">Admin </Nav.Link>
          </Nav>
          </Navbar.Collapse>

          <Navbar.Collapse>
        <Form inline>
          <FormControl type="text" placeholder="Search" className="mr-sm-2" />
          <Button variant="outline-light">Search</Button>
        </Form>
        
        <Nav variant="tabs" className="ml-auto">
          <NavDropdown title={loggedinUserName} id="basic-nav-dropdown">
          <NavDropdown.Item as={Link} to="#">Change Password</NavDropdown.Item>
          <NavDropdown.Divider />
          <NavDropdown.Item as={Link} to="#">Logout</NavDropdown.Item>
          </NavDropdown>
        </Nav>
        </Navbar.Collapse>
       
    </Navbar>

  );
};

export default Header;
