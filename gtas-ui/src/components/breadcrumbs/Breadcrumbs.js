import React from "react";
import { Link } from "@reach/router";
import { getCrumbs, titleCase } from "../../utils/text";

//APB - probably don't need this except possibly for Admin pages.

const Breadcrumbs = props => {
  const list = getCrumbs(props.uri);

  return (
    <nav className="breadcrumb is-centered" aria-label="breadcrumbs">
      <ul>
        {list.map(function(item) {
          return (
            <li>
              <Link to={`/${item}`}>{titleCase(item)}</Link>
            </li>
          );
        })}
      </ul>
      {props.children}
    </nav>
  );
};

export default Breadcrumbs;
