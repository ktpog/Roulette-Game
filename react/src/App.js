import React, { Component } from 'react';
import classes from './App.css';
import Roulette from './components/Roulette/Roulette';
import { Card, Button, FormGroup, FormControl, ControlLabel } from "react-bootstrap";

import axios from 'axios';
/**
 * Base App Component which is rendered in the ReactDOM.render() function in index.js
 */

function App() {
  const [name, setName] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [newName, setNewName] = React.useState("");
  const [newPassword, setNewPassword] = React.useState("");
  const [message, setMessage] = React.useState("");
  const [loginMessage, setLoginMessage] = React.useState("");
  const [numPlayer, setNumPlayer] = React.useState(0);
  const [authorize, setAuthorize] = React.useState(false);
  const [playerName, setPlayerName] = React.useState("");
  let _id = {};
  const ws = React.useRef(new WebSocket('ws://localhost:1234/ws'));

  ws.current.onmessage = (message) => {
    setNumPlayer(Number(message.data));
  };

  window.addEventListener("beforeunload", (ev) => {
    if (authorize === true)
      ws.current.send(numPlayer - 1)
    //Update numPlayer when a close window event happens
  });


  const login = () => {
    axios.post('/login', null, {
      params: {
        name,
        password
      }
    })
      .then((res) => {
        console.log(res.data);
        if (res.data.responseCode == "Error") {
          setLoginMessage("Username or Password Mismatch");
        } else {
          setPlayerName(res.data.playerList[0].name);
          setAuthorize(true);
          ws.current.send(numPlayer + 1);
          console.log(_id);
        }
      });
  }

  const register = () => {
    axios.post('/createAccount', null, {
      params: {
        newName,
        newPassword
      }
    })
      .then((res) => {
        console.log(res.data);
        if (res.data.responseCode == "Error") {
          setMessage("Account Already Exist");
        } else {
          setMessage("Account Created");
        }
      });
  }

  const logout = () => {
    setAuthorize(false);
    console.log(authorize);
    ws.current.send(numPlayer - 1)

  }

  return (
    <div>

      {authorize === false &&
        <div style={{ maxWidth: "80vh" }}>
          <Card className="container-fluid">
            <h1 style={{ alignSelf: "center" }}>SIGN IN</h1>
            <div className="Login">
              <form >
                <FormGroup controlId="name" bsSize="large">
                  <h2>Playername</h2>
                  <FormControl
                    autoFocus
                    value={name}
                    onChange={e => setName(e.target.value)}
                  />
                </FormGroup>
                <FormGroup controlId="password" bsSize="large">
                  <h2>Password</h2>
                  <FormControl
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    type="password"
                  />
                </FormGroup>
                <Button onClick={login} block bsSize="large">
                  Login
   </Button> <a style={{ fontSize: "20px", color: "red" }}>{loginMessage}</a>
              </form>
            </div>
            <h3 style={{ alignSelf: "center" }}>OR </h3>
            <h1 style={{ alignSelf: "center" }}>SIGN UP</h1>
            <div className="Login">
              <form >
                <FormGroup bsSize="large">
                  <h2>Playername</h2>
                  <FormControl
                    autoFocus
                    value={newName}
                    onChange={e => setNewName(e.target.value)}
                  />
                </FormGroup>
                <FormGroup bsSize="large">
                  <h2>Password</h2>
                  <FormControl
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                    type="password"
                  />
                </FormGroup>
                <Button onClick={register} block bsSize="large">
                  Register
   </Button>
                < br />
                <a style={{ fontSize: "20px", color: "green" }}>{message}</a>
              </form>
            </div>
          </Card> </div>
      }

      {authorize === true &&
        <div>
          <h3>
            <a style={{ color: "white", float: "left", paddingLeft: "20px" }} >Active Players:
 <a style={{ color: "lightgreen" }}> {numPlayer} </a></a>
          </h3>
          <Button style={{ float: "right" }} onClick={logout} bsSize="small" type="submit">
            Log Out
   </Button>
          <div className={classes.App}>
            <Roulette  playerName= {playerName}/>
          </div>
        </div>
      }
    </div>
  );
}

export default App;
