import React from "react";
import Table from "../../../components/table/Table";
import { watchlistcats } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const WatchlistCats = ({ name }) => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title={name}></Title>

      <div className="columns">
        <div className="top">
          <Table service={watchlistcats.get} id="foo" callback={cb}></Table>
        </div>
      </div>
    </div>
  );
};

export default WatchlistCats;
