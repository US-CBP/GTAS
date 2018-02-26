import {Passenger, Flight, Apis, Pnr, Bag} from './connectors.js';
const resolvers = {
  Query: {
    passenger(_, args) {
      return Passenger.find({where: args});
    },
    allPassengers(_, args) {
      return Passenger.findAll();
    },
    apis(_,args) {
      return Apis.findAll({where: args});
    },
    pnr(_,args) {
      return Pnr.find({
        include: [
          {model: Passenger, where:{id: args.passengerId}},
          {model: Flight, where:{id: args.flightId}}
        ]
      });
    },
    bag(_,args) {
      return Bag.findAll({where: args});
    },
    allFlights(_,args) {
      return Flight.findAll();
    }
  },
  Passenger: {
    apis(passenger) {
      return Apis.findAll({where: {passengerId: passenger.id}});
    },
    bags(passenger){
      return passenger.getBags();
    }
  },
  Flight: {
    apis(flight) {
      return Apis.findAll({where: {flightId: flight.id}});
    },
    bags(flight) {
      return flight.getBags();
    }
  },
  Pnr: {
    passengers(pnr){
      return pnr.getPassengers();
    },
    flights(pnr){
      return pnr.getFlights();
    },
    addresses(pnr){
      return pnr.getAddresses();
    }
  },
  Apis: {
    flight(apis) {
      return apis.getFlight();
    },
    passenger(apis) {
      return apis.getPassenger();
    }
  },
};

export default resolvers;
