import React, { useState, useEffect, useContext } from "react";
import Form from "../../components/form/Form";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import { logins, loggedinUser } from "../../services/serviceWrapper";
import "./Login.css";
import { Alert, Card } from "react-bootstrap";
import { navigate } from "@reach/router";
import { store } from "../../appContext";

const Login = () => {
  const [authenticate, setAuthenticate] = useState(false);
  const [failedLogin, setFailedLogIn] = useState(false);

  const globalState = useContext(store);
  const { dispatch } = globalState;

  useEffect(() => {
    if (authenticate) {
      loggedinUser
        .get()
        .then(response => {
          dispatch({
            type: "login",
            user: response
          });
          navigate(`/gtas/flights`, true);
        })
        .catch(error => {
          //todo: error handling
          console.log(error);
        });
    }
  }, [authenticate, failedLogin, dispatch]);

  const loginCallback = input => {
    return input
      .then(response => {
        if (response.authenticated) {
          setAuthenticate(true);
        } else {
          setAuthenticate(false);
          setFailedLogIn(true);
        }
      })
      .catch(reason => {
        console.log(reason);
        //todo: make a toast that sets error to false when done letting the user know why the login failed.
      });
  };

  return (
    <div className="login-page-container">
      <Card className="transparent-white-card text-center mx-auto">
        <div className="login-logo"></div>
        <div className="login-brand">GTAS</div>
        <Card.Body>
          <Form
            title=""
            submitText="LOG IN"
            submitService={logins.authPost}
            action="auth"
            afterProcessed={loginCallback}
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
        <Alert
          variant="danger"
          show={failedLogin}
          onClose={() => setFailedLogIn(false)}
          dismissible
          className="login-alert"
        >
          Incorrect username or password
        </Alert>
      </Card>
    </div>
  );
};

export default Login;
