import React from "react";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";

const PageUnauthorized = () => {
  return (
    <div className="container">
      <Title title="Custom Unauthorized Page"></Title>

      <div className="columns">
        <div className="column">
          <div className="box2">
            <div className="top">
              <Link to="/flights">Flights</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PageUnauthorized;
