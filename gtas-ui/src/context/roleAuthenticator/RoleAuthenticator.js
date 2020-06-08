import React, { useContext } from "react";
import { UserContext } from "../user/UserContext";
import { asArray, titleCase } from "../../utils/utils";

const RoleAuthenticator = props => {
  const alt = props.alt ?? <></>;
  const { getUserState } = useContext(UserContext);

  let hasRole = false;

  const userRoles = getUserState().userRoles.map(item => titleCase(item));

  (asArray(props.roles) || []).forEach(element => {
    if (userRoles.includes(titleCase(element))) {
      hasRole = true;
      return;
    }
  });

  return hasRole ? <>{props.children}</> : alt;
};

export default RoleAuthenticator;
