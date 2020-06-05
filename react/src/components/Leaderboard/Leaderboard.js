import React, { useEffect } from 'react';
import axios from 'axios';
import { Card } from 'react-bootstrap';


import classes from './Leaderboard.module.css';


/**
 * Functional component for the panel whcih shows the users balance
 * 
 * @param {object} props - Props passed to the component 
 * @return {JSX} - Returned JSX for display
 */
function useInterval(callback, delay) {
    const savedCallback = React.useRef();
  
    // Remember the latest callback.
    React.useEffect(() => {
      savedCallback.current = callback;
    }, [callback]);
  
    // Set up the interval.
    React.useEffect(() => {
      function tick() {
        savedCallback.current();
      }
      if (delay !== null) {
        let id = setInterval(tick, delay);
        return () => clearInterval(id);
      }
    }, [delay]);
  }

const Leaderboard = () => {

    const [list, setList] = React.useState([]);

    const getList = () =>{
        axios.get('/leaderboard')
        .then((res) => {
            const temp = res.data.playerList.map(
                list => <div>
           <h2> {list.name}{'           '}{list.score}   </h2>         

            
            </div>
            )
            setList(temp)
          
         
        })
      }
      
    useEffect(getList, []);

    
    useInterval(() => {
    getList();
  }, 30000);



    return (
        <div className={classes.container}>
            <h2>LEADERBOARD</h2>
            {list}
        </div>
    )
}

export default Leaderboard