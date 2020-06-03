import React, { useState, useEffect, useContext } from "react";
import Form from "../../components/form/Form";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import { login } from "../../services/serviceWrapper";
import { Alert, Card } from "react-bootstrap";
import { navigate } from "@reach/router";
import { UserContext } from "../../context/user/UserContext";
import "./Login.scss";
import Logo from "../../images/WCO_GTAS_logo.svg";

const Login = () => {
  const ctx = useContext(UserContext);
  const [alertVis, setAlertVis] = useState(false);

  useEffect(() => {
    ctx.userAction({ type: "logoff" });
  }, []);

  const loginHandler = res => {
    console.log(res);
    // if (res?.authenticated && res?.userId) {
    //TODO - update the response to include user metadata

    // const roles = res.roles;

    // const newuser = {
    //   authenticated: true,
    //   fullName: res.fullName,
    //   userId: res.userId,
    //   userRoles: roles,
    //   userToken: res.token,
    //   queryPageSize: 25,
    //   userPageSize: 25,
    //   landingPage: undefined
    // };

    const roles = ["Admin"];

    const newuser = {
      authenticated: true,
      fullName: "Admin",
      userId: 7,
      userRoles: roles,
      userToken: "sdfsfsfd",
      queryPageSize: 25,
      userPageSize: 25,
      landingPage: undefined
    };

    ctx.userAction({ user: newuser, type: "login" });
    navigate("/gtas/flights");
    // }

    setAlertVis(true);
  };

  return (
    <div className="login-page-container">
      <Card className="transparent-white-card">
        <Card.Img variant="top" src={Logo} className="logo" />
        <div className="placeholder"></div>
        <Card.Body className="login-card-body">
          <br />
          <Form
            title=""
            submitText="LOGIN"
            submitService={login.post}
            callback={loginHandler}
            id="loginform"
          >
            <LabelledInput
              inputType="text"
              alt="Enter the user name"
              name="username"
              labelText=""
              placeholder="Username"
              datafield="username"
              required="required"
              inputVal=""
              autofocus="true"
              className="login-labeled-input"
            />
            <LabelledInput
              inputType="password"
              alt="Enter the password"
              name="password"
              labelText=""
              placeholder="Password"
              datafield="password"
              required="required"
              inputVal=""
              className="login-labeled-input"
            />
          </Form>
        </Card.Body>
      </Card>
      <div>
        {alertVis && (
          <Alert variant="danger" dismissible onClose={() => setAlertVis(false)}>
            Login failed.
          </Alert>
        )}
      </div>
    </div>
  );
};

export default Login;
