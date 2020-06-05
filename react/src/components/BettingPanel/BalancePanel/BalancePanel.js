import React from 'react';

/**
 * Functional component for the panel whcih shows the users balance
 * 
 * @param {object} props - Props passed to the component 
 * @return {JSX} - Returned JSX for display
 */
const BalancePanel = (props) => {
    return (
        <div>
            <p>Balance -
            <a style={{color: "#ffcde1"}}> {props.children} credits</a> 
            </p>
        </div>
    )
}

export default BalancePanel