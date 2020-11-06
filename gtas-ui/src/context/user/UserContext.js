import React, { createContext, useReducer } from "react";
import Cookies from "js-cookie";
import { hasData } from "../../utils/utils";

const initialState = {
  authenticated: false,
  fullName: undefined,
  userId: undefined,
  userRoles: [],
  userToken: undefined,
  queryPageSize: 25,
  userPageSize: 25,
  landingPage: undefined,
  emailEnabled: undefined,
  highPriorityEmail: undefined
};

export const UserContext = createContext();

const setStorage = (key, val) => {
  sessionStorage.setItem(key, JSON.stringify(val));
};

const UserProvider = ({ children }) => {
  const { Provider } = UserContext;

  const UserReducer = (state, action) => {
    switch (action.type) {
      case "refresh":
      case "login": {
        setStorage("user", action.user);
        return action.user;
      }
      case "logoff": {
        sessionStorage.removeItem("user");
        Cookies.remove("JSESSIONID");
        setStorage("user", initialState);
        return initialState;
      }
      default:
        setStorage("user", initialState);
        return initialState;
    }
  };

  const [userState, userAction] = useReducer(
    UserReducer,
    sessionStorage.getItem("user") || initialState
  );

  const getUserState = () => {
    // if (hasData(userState.userToken)) return userState;
    if (userState.authenticated) return userState;

    const storedUser = JSON.parse(sessionStorage.getItem("user"));

    // if (hasData((storedUser || {}).userToken)) {
    if (storedUser.authenticated) {
      return storedUser;
    }
    return userAction({ type: "default" });
  };

  return <Provider value={{ getUserState, userAction }}>{children}</Provider>;
};

export default UserProvider;
