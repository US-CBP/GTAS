import React, { useState, useEffect } from "react";
import Form from "../../components/form/Form";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import { logins } from "../../services/serviceWrapper";
import logo from "../../images/GTAS Logo blue 2.png";
import "./Login.css";
import { navigate } from "@reach/router";
import { Container, Figure, Jumbotron, Card, Badge } from "react-bootstrap"

const Login = () => {

    const [authenticate, setAuthenticate] = useState(false);
    const [failedLogin, setFailedLogIn] = useState(false);

    useEffect(() => {
        if (authenticate) {
            navigate(`/gtas/flights`, true);
        }
    }, [authenticate, failedLogin]);

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
        <br />
        <p>Incorrect username or password</p>
    </div>;

    return (
        <Jumbotron fluid>
                <Card className="transparent-white text-center login-card mx-auto">
                    <Figure className="avatar">
                        <Figure.Image
                            width={100}
                            height={100}
                            alt="logo"
                            src={logo} />
                    </Figure>
                    <Container>
                        <h3><Badge >GTAS</Badge></h3>
                        {failedLogin ? failedLoginDiv : null}
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
                    </Container>
                </Card>
        </Jumbotron>
    );
};

export default Login;
