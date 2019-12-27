/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React, { useState } from "react";
import DatePicker from "react-datepicker";
import inputPasses from "../Inputs.css";

import "react-datepicker/dist/react-datepicker.css";
import PropTypes from "prop-types";
import classes from "./LabelledDateTimePicker.css";
const REQUIRED = "required";

const LabelledDateTimePicker = props => {

    const [startDate, setStartDate] = useState(props.startDate);
    const [endDate, setEndDate] = useState(props.endDate);

    const onChange = (event) => {
        if (props.end) {
            setEndDate(event);
        } else {
            setStartDate(event)
        }
        const update = {
            name: props.name,
            value: event.toISOString()
        };
        props.callback(update);
    };

    //TODO: correct date range on bad dates.
    const createDateProperties = (props) => {
        let dateProperties = {...props};
        if (dateProperties.dateRange) {
            let dateRange = dateProperties.dateRange;
            if (dateRange.position === 'start') {
                dateProperties = {...dateProperties, selectsStart: true};
                dateProperties = {...dateProperties, selected: startDate};
            } else {
                dateProperties = {...dateProperties, selectsEnd: true};
                dateProperties = {...dateProperties, selected: endDate};
            }
        } else {
            dateProperties = {...dateProperties, selected: startDate};
        }
        return dateProperties;
    };

    const totalClasses = {...classes, ...inputPasses};
    return  (
        <div
            className={`field ${props.visibleStyle}`}
            style={totalClasses}
        >
            <div className="control">
                <label className="txtlabel">{props.labelText}</label>
                <DatePicker
                    {...createDateProperties(props)}
                    className="form-input"
                    style={totalClasses}
                    onChange= {onChange}
                />
            </div>
        </div>
    );
};

LabelledDateTimePicker.propTypes = {
    name: PropTypes.string.isRequired,
    alt: PropTypes.string.isRequired,
    labelText: PropTypes.string.isRequired,
    inputType: PropTypes.oneOf([
        "dateTime",
        "date"
    ]).isRequired,
    callback: PropTypes.func.isRequired,
    inputVal: PropTypes.instanceOf(Date),
    startDate: PropTypes.instanceOf(Date),
    endDate: PropTypes.instanceOf(Date),
    placeholder: PropTypes.string,
    required: PropTypes.oneOf([REQUIRED, "", undefined]),
    isVisible: PropTypes.bool,
    readOnly: PropTypes.string,
    dateRange: PropTypes.object
};
export default React.memo(LabelledDateTimePicker, (oldProps, newProps) => {
    // True when an update should not happen.
    return oldProps === newProps || oldProps.children === newProps.children;
} );
