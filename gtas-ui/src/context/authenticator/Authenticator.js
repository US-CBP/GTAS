import React, { useContext } from "react";
import { navigate } from "@reach/router";
import { UserContext } from "../user/UserContext";
import { hasData } from "../../utils/utils";

const Authenticator = props => {
  const { getUserState } = useContext(UserContext);
  const user = getUserState() || {};

  if (!user.authenticated) {
    navigate("/login");
    return null;
  }

  return <>{props.children}</>;
};

export default Authenticator;
