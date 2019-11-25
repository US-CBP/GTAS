import React from 'react';
import Table from '../../components/table/Table';
import {flights} from '../../services/serviceWrapper';
import Title from '../../components/title/Title';
import { Link } from "@reach/router";
import LabelledInput from '../../components/labelledInput/LabelledInput';

const Flights = (props) => {
  const onTextChange = () => {

  }
  /// APB - passing the uri here as a test, can we use context here??

  return (
    <div className='container'>
      <Title title='Flights' uri={props.uri}></Title>

      <div className='columns'>
      <div className='column is-3'>
        <div className='box2'>
            <aside className="menu">
                <p className="menu-label">Filters</p>
                <hr></hr>
                <LabelledInput labelText='Origin Airport' inputType='text' name='originAirport' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Destination Airport' inputType='text' name='destAirport' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Full Flight ID' inputType='text' name='fullFlightId' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Direction' inputType='text' name='direction' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Start Date' inputType='text' name='direction' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='End Date' inputType='text' name='direction' callback={onTextChange} alt='nothing' />
            </aside>
        </div>
      </div>

      <div className='column'>
        <div className='box2'>
        <Link to='../flightpax'>Flight Passengers</Link>
          <div className='card events-card'>
            <Table service={flights.get} id='foo' ></Table>
          </div>
        </div>
      </div>
    </div>
  </div>
)

}

export default Flights;