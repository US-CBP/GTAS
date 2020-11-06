import React from "react";
import { Link } from "@reach/router";
import { getCrumbs, titleCase } from "../../utils/text";
import { NO_URI } from "../../utils/constants";
import Breadcrumb from "react-bootstrap/Breadcrumb";
//APB - probably don't need this except possibly for Admin pages.

const Breadcrumbs = props => {
  const list = getCrumbs(props.uri);

  return (
    <Breadcrumb>
      {list.map(function(item) {
        if (item === NO_URI) return <li>{NO_URI}</li>;
        return (
          <Breadcrumb.Item>
            <Link to={`${item}`}>{titleCase(item)}</Link>
          </Breadcrumb.Item>
        );
      })}
    </Breadcrumb>
  );
};

export default Breadcrumbs;
