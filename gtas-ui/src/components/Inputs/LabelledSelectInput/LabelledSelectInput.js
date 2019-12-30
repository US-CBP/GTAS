/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React from 'react'
import Select from 'react-select'
import inputPasses from "../Inputs.css";
import {Row} from "react-bootstrap";

const options = [
    { value: 'foo', label: 'foo' },
    { value: 'bar', label: 'bar' },
    { value: 'baz', label: 'baz' }
];
const LabelledSelectInput = props => {
    let foo = {'min-width': '100%'};
    const finalStyles = {...foo, ...inputPasses};

    return (
        <div
            className={`field ${props.visibleStyle}`}
            style={finalStyles}
        >
            <Row className="control" style={finalStyles}>
                <label>Origin Airport</label>
            </Row>
            <Row style={finalStyles}>
                <div style={finalStyles}>
                <Select
                    style={finalStyles}
                    options={options}
                    {...props}
                />
                </div>
            </Row>
        </div>
    )
};

export default LabelledSelectInput;