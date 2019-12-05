import React from "react";
import { Link } from "@reach/router";

const Header = () => {
  return (
    <div>
      <nav className="navbar is-white on-top top">
        <div className="container">
          <div className="navbar-brand">
            <Link to="/gtas" className="navbar-item brand-text">
              GTAS
            </Link>
            <div className="navbar-burger burger" data-target="navMenu">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
          <div id="navMenu" className="navbar-menu">
            <div className="navbar-start">
              <Link to="dashboard" className="navbar-item">
                Dashboard
              </Link>
              <Link to="flights" className="navbar-item">
                Flights
              </Link>
              <Link to="vetting" className="navbar-item">
                Vetting
              </Link>
              <div className="navbar-item has-dropdown is-hoverable">
                <div className="navbar-link">Tools</div>
                <div className="navbar-dropdown">
                  <Link to="tools/queries" className="navbar-item">
                    Queries
                  </Link>
                  <Link to="tools/rules" className="navbar-item">
                    Rules
                  </Link>
                  <Link to="tools/watchlist" className="navbar-item">
                    Watchlist
                  </Link>
                  <Link to="tools/neo4j" className="navbar-item">
                    Neo4J Browser
                  </Link>
                  <Link to="tools/about" className="navbar-item">
                    About
                  </Link>
                </div>
              </div>

              <Link to="admin" className="navbar-item">
                Admin
              </Link>
              <i className="fa fa-bell-o column"></i>
            </div>
            <div className="navbar-end">
              <div className="navbar-item is-right" href="index.html">
                searchbar
              </div>
              <div className="navbar-item is-right" href="admin.html">
                User
              </div>
            </div>
          </div>
        </div>
      </nav>
    </div>
  );
};

export default Header;
