import React, {useState, useEffect, useContext} from "react";
import "bulma/css/bulma.css";
import Form from "../../components/form/Form";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import { logins, userService } from "../../services/serviceWrapper";
import logo from "../../images/GTAS Logo blue 2.png";
import "./Login.css";
import {navigate} from "@reach/router";
import { store } from '../../appContext';

const Login = () => {

const [authenticate, setAuthenticate] = useState(false);
const [failedLogin, setFailedLogIn] = useState(false);

    const globalState = useContext(store);
    const { dispatch } = globalState;

    useEffect(() => {
        if (authenticate) {
                userService.get().then(response => {
                    dispatch({
                        type: 'login',
                        user : response
                    });
                    navigate(`/gtas/flights`, true);
                }).catch(error => {
                    //todo: error handling
                   console.log(error);
                });
        }
    }, [authenticate, failedLogin, dispatch]);

 const loginCallback = (input) => {
   return input.then(response => {
     if (response.authenticated) {
       setAuthenticate(true);
     } else {
       setAuthenticate(false);
       setFailedLogIn(true);
     }
    }).catch(reason => {
      console.log(reason);
      //todo: make a toast that sets error to false when done letting the user know why the login failed.
   });
  };

    let failedLoginDiv = <div className="failed-login"><p>Authorization failed!</p>
    <br/>
    <p>Incorrect username or password</p>
    </div> ;

  return (
      <section className="hero is-info is-fullheight">
          <div className="hero-body">
              <div className="container has-text-centered">
                  <div className="column is-4 is-offset-4">
                      <div className="box transparent-white">
                          <figure className="avatar">
                              <img src={logo} height="100" alt="logo" width="100"/>
                          </figure>
                          <h3 className="title2 has-text-black">GTAS </h3>
                          {failedLogin ? failedLoginDiv : null}
                          <div className="section">
                              <Form
                                  title=""
                                  submitText="LOG IN"
                                  service={logins}
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
                                  />
                              </Form>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
      </section>
  );
};

export default Login;
