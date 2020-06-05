import React, { Component } from 'react';
import BalancePanel from './BalancePanel/BalancePanel'
import classes from './BettingPanel.module.css';
import axios from 'axios';



class BettingPanel extends Component {
    state = {
        bet: 0,
        balance: 0,
        error: "",
        color: '',
        count: 30,
        message: 'Place your bet to dominate the leaderboard',
        lock: '',
        resultNum: 0,
    }
    /**
     * Used to get the score from the database
     */

    getScore = () => {
        axios.get('/getPlayer?name='.concat(this.props.playerName))
            .then((res) => {
                this.setState({ balance: res.data.playerList[0].score });
                console.log(res.data.playerList[0].score);
            });
    }

    /**
     * Used to handle the click event for betting buttons
     * 
     * @param {eventObject} event - Click event object
     */
    handleChange = (event) => {
        if (event.target.value > this.state.balance) {
            this.setState({ error: "You cannot bet more than your balance." })
        } else if (event.target.value <= 0) {
            this.setState({ error: "You cannot bet less than 0." })
        } else {
            this.setState({ bet: event.target.value, error: "" })
        }
    }
    /**
     * Object used to get the winning colour from a number
     * 
     * @param {integer} win - The winning number of the spin
     * @return {string} - The string of the winning colour ('red', 'green' or 'black')
     */
    getWinningColour = (win) => {
        let out;
        if (win % 2 === 0 && win !== 0) {
            out = 'red';
        } else if (win % 2 === 1) {
            out = 'black';
        } else if (win === 0) {
            out = "green";
        }
        return out
    }
    /**
     * Used to handle a bet
     * 
     * @param {string} colour - The winning colour usually from getWinningColour()
     * @param {integer} result - The integer result of the spin
     */
    betHandler = (colour, result) => {
        let mult = null;
        //let old_balance = this.state.balance;
        if (colour != '')
            this.getScore();
        //this.setState({ balance:old_balance - this.state.bet });
        axios.get('/resultInt')
            .then((res) => this.setState({ resultNum: res.data.previousResultInt }));
        let winning_colour = this.getWinningColour(this.state.resultNum);
        if (winning_colour === 'black' || winning_colour === 'red') {
            mult = 2;
        } else {
            mult = 14;
        }
        setTimeout(() => {
            if (colour === winning_colour) {
                //let current_balance = this.state.balance;
                //let current_bet = this.state.bet;
                //let newBalance = current_balance += (current_bet * mult);
                //this.setState({ balance: newBalance })
                this.getScore();
                this.setState({ message: "Last roll result: Congratulations!" });
                this.setState({ bet: 0 })
                this.setState({ color: '' })
                this.setState({ lock: '' })
            }
            else if (colour === '') {
                this.setState({ message: "Sometimes it takes courage to win it all" });
                this.setState({ bet: 0 })
                this.setState({ lock: '' })
            }
            else {
                this.setState({ message: "Last roll result: Better luck next time!" });
                this.setState({ bet: 0 })
                this.setState({ color: '' })
                this.setState({ lock: '' })
            }
        }, 8500)
    }

    /**
     * Function used to handle the cooldown on frontend
     * 
     */
    count = () => {
        this.inter = setInterval(() => {

            if (this.state.count <= 0) {
                const audioEl = document.getElementsByClassName("audio-element")[0]
                audioEl.play()
                document.getElementById("form-bet").reset();
                this.setState({ lock: 'disabled' })
                clearInterval(this.inter);
                let result = this.props.spin();
                let colour = this.state.color;
                this.betHandler(colour, result);
                this.reset();


            } else {
                this.setState((prevState) => ({ count: prevState.count - 1 }));
                //this.ws.send(this.state.count);
            }
        }, 1000);
    }
   
    /**
     * Function used to reset state after each round
     */
    reset = () => {
        console.log("Reset " + this.state.balance);
        this.setState({ message: 'Rolling... Good luck!' });
        axios.get('getRollTime')
            .then((res) => this.setState({ count: res.data.secondTillRoll },
                () => this.count()
            ));

        if (this.state.balance < 0) {
            this.setState({ balance: 0 })
            this.setState({ color: '' })
        }
    }

     /**
     * when first log in, get the score from database and the current clock count from the server.
     */
    componentDidMount() {
        this.getScore();
        axios.get('getRollTime')
            .then((res) => this.setState({ count: res.data.secondTillRoll }, () => this.count()))
    }


    /**
     * Send bet data to the server. Update message.
     */
    clickHandle = (colour) => {
        if (this.state.error === "" && this.state.balance >= this.state.bet) {
            this.setState({ color: colour });
            if (this.state.bet > 0)
                this.setState({ message: "Bet Placed: " + this.state.bet + " credits on " + colour });
        }
        axios.post(`/placebet?name=${this.props.playerName}&color=${colour}&amount=${this.state.bet}`)
            .then((res) => this.getScore())

    }

    render() {
        let error = null;
        if (this.state.error) {
            error = (
                <p className={classes.error}>{this.state.error}</p>
            )
        }
        return (
            <div className={classes.bettingPanel}>
                <h1>ROUND STARTS IN</h1>
                <p style={{ color: "yellow", fontSize: 50 }}>{this.state.count}</p>
                <p style={{ color: "orange", fontSize: 20 }}>
                    The round is starting soon! Place your bet below.
                </p>
                <BalancePanel>{this.state.balance.toLocaleString()}</BalancePanel>
                <fieldset disabled={this.state.lock}>
                    <form id="form-bet">
                        <input id="input" className={classes.betInput} type="number" placeholder="Bet..." onChange={this.handleChange}
                            onKeyPress={e => {
                                if (e.key === 'Enter') e.preventDefault();
                            }} />
                    </form>
                </fieldset>
                {error}
                <div className={classes.betButtons}>
                    <button className={classes.red} style={this.props.btn_dis_style} disabled={this.props.disabledBool} onClick={this.clickHandle.bind(this, 'red')}>Bet Red (X2)</button>
                    <button className={classes.green} style={this.props.btn_dis_style} disabled={this.props.disabledBool} onClick={this.clickHandle.bind(this, 'green')}>Bet Green (X14)</button>
                    <button className={classes.black} style={this.props.btn_dis_style} disabled={this.props.disabledBool} onClick={this.clickHandle.bind(this, 'black')}>Bet Black (X2)</button>
                </div>
                <p style={{ color: "orange", fontSize: 30 }}>{this.state.message}</p>
                <div>
                    <audio className="audio-element">
                        <source src="http://commondatastorage.googleapis.com/codeskulptor-assets/week7-brrring.m4a"></source>
                    </audio>
                </div>
            </div>
        )
    }
}

export default BettingPanel
