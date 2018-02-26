import { makeExecutableSchema, addMockFunctionsToSchema } from 'graphql-tools';
import resolvers from './resolvers.js';
const typeDefs = `
type Query {
  passenger(firstName: String, lastName: String): Passenger
  allPassengers: [Passenger]
  allFlights: [Flight]
  apis(passengerId: Int, flightId: Int, refNumber: String): [Apis]
  pnr(passengerId: Int!, flightId: Int!): Pnr
  bag(passengerId: Int, flightId: Int): [Bag]
}
type Passenger {
  id: Int
  firstName: String
  lastName: String
  apis: [Apis]
  bags: [Bag]
}
type Flight {
  id: Int
  flightNumber: String
  origin: String
  destination: String
  apis: [Apis]
  bags: [Bag]
}
type Pnr {
  id: Int
  recordLocator: String
  formOfPayment: String
  baggageWeight: Float
  totalBagCount: Int
  excessBagCount: Int
  passengers: [Passenger]
  flights: [Flight]
  addresses: [Address]
}
type Apis {
  id: Int
  passenger: Passenger
  flight: Flight
  embarkation: String
  debarkation: String
  refNumber: String
}
type Address {
  id: Int
  line1: String
  line2: String
  line3: String
  city: String
  country: String
}
type Bag {
  id: Int
  bagId: String
  dataSource: String
  destination: String
  passengerId: Int
  flightId: Int
}
`;

const schema = makeExecutableSchema({ typeDefs, resolvers });
export default schema;
