import React from "react";
import { Link } from "@reach/router";
import { Nav, Navbar, Container, NavDropdown, Form, FormControl, Button } from "react-bootstrap";

const Header = () => {
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
              <NavDropdown.Item as={Link} to="tools/rules">Rules</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="tools/watchlist">Watchlist</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="tools/neo4j">Neo4J Browser</NavDropdown.Item>
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
          <Nav.Link href="admin.html"> User</Nav.Link>
        </Nav>
        </Navbar.Collapse>
       
    </Navbar>

  );
};

export default Header;
