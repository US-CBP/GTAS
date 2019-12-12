import React, {useState} from "react";
import "bulma/css/bulma.css";
import Form from "../../components/form/Form";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import { logins } from "../../services/serviceWrapper";
import logo from "../../images/GTAS Logo blue 2.png";
import "./Login.css";
import {Redirect} from "@reach/router";

const Login = () => {

const [authenticate, setAuthenticate] = useState(false);

 const loginCallback = (input) => {
   return input.then(response => {
     if (response.authenticated) {
       setAuthenticate(true);
     } else {
       setAuthenticate(false);
     }
    }).catch(reason => {
      console.log(reason);
      //todo: make a toast that sets error to false when done letting the user know why the login failed.
   });
  };
    //todo: Update HACK to redirect to about without needing noThrow.
   let redirectAfterLogin =  <Redirect to="/gtas/tools/about" noThrow/>;
   //let redirectAfterLogin = () => navigate(`/gtas/tools/about`);

    let loginPage =  <section className="hero is-info is-fullheight">
    <div className="hero-body">
      <div className="container has-text-centered">
        <div className="column is-4 is-offset-4">
          <div className="box transparent-white">
            <figure className="avatar">
              <img src={logo} height="100" alt="logo" width="100"/>
            </figure>
            <h3 className="title2 has-text-black">GTAS, {authenticate ? "Logged In" : "Logged Out"}</h3>
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
  </section>;
  return (authenticate ? redirectAfterLogin : loginPage);
};

export default Login;
