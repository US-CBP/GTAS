import React from "react";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";

const Page404 = () => {
  return (
    <div className="container">
      <Title title="Page Not found"></Title>

      <div className="columns">
        <div className="column">
          <div className="box2">
            <div className="top">
              <Link to="/gtas/flights">Flights</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Page404;
