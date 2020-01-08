/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React from "react";

const GroupCheckBox = ({ value, onChange, id, name }) => {
    const handleChange = event => {
        const text = event.target.value;
        onChange(id, text);
    };

    return (
        <div>
            <label>
                {name}
            </label>
            <input
                type="checkbox"
                onChange={handleChange}
                checked={value}
                value={value}
            />
        </div>
    );
};



export default GroupCheckBox;