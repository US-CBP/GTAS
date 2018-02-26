import Sequelize from 'sequelize';
import {dbConfig} from './../config.js';
//operatorsAliases set to false due to deprecated operator string warning
const db = new Sequelize(dbConfig.name, dbConfig.user, dbConfig.pass, {
  dialect: 'mysql',
  operatorsAliases: false,
  define: {
    freezeTableName: true,
    underscored: true,
    timestamps: false
  }
});
db.authenticate()
  .then(()=>{
    console.log('Connection has been established successfully.');
  })
  .catch(err=> {
    console.error('Unable to connect to the database:', err);
  });

const PassengerModel = db.define('passenger', {
  firstName: { type: Sequelize.STRING, field: 'first_name'},
  lastName: { type: Sequelize.STRING, field: 'last_name'},
});
const FlightModel = db.define('flight', {
  flightNumber: { type: Sequelize.STRING, field: 'full_flight_number'},
  origin: { type: Sequelize.STRING },
  destination: { type: Sequelize.STRING },
});
const ApisModel = db.define('flight_pax', {
  embarkation: { type: Sequelize.STRING },
  debarkation: { type: Sequelize.STRING },
  refNumber: {type: Sequelize.STRING, field: 'ref_number'},
  passengerId: {type: Sequelize.BIGINT, field: 'passenger_id'},
  flightId: {type: Sequelize.BIGINT, field: 'flight_id'}
});
const PnrModel = db.define('pnr', {
  recordLocator: {type: Sequelize.STRING, field: 'record_locator'},
  formOfPayment: {type: Sequelize.STRING, field: 'form_of_payment'},
  baggageWeight: {type: Sequelize.DOUBLE, field: 'baggage_weight'},
  totalBagCount: {type: Sequelize.INTEGER, field: 'total_bag_count'},
  excessBagCount: {type: Sequelize.INTEGER, field: 'bag_count'}
});
const AddressModel = db.define('address', {
  line1: {type: Sequelize.STRING},
  line2: {type: Sequelize.STRING},
  line3: {type: Sequelize.STRING},
  city: {type: Sequelize.STRING},
  country: {type: Sequelize.STRING}
});
const BagModel = db.define('bag', {
  bagId: {type: Sequelize.STRING, field: 'bag_identification'},
  dataSource: {type: Sequelize.STRING, field: 'data_source'},
  destination: {type: Sequelize.STRING, field: 'destination_airport'},
  passengerId: {type: Sequelize.BIGINT, field: 'passenger_id'},
  flightId: {type: Sequelize.BIGINT, field: 'flight_id'}
});
const ApiAccessModel = db.define('api_access', {
  username: {type:Sequelize.STRING},
  password: {type:Sequelize.STRING},
  organization: {type:Sequelize.STRING},
  email: {type:Sequelize.STRING}
});

const PnrPassengerModel = db.define('pnr_passenger', {
  pnrId: {type: Sequelize.BIGINT, field: 'pnr_id', references: 'pnr'},
  passengerId: {type: Sequelize.BIGINT, field: 'passenger_id', references: 'passenger'}
});
const PnrFlightModel = db.define('pnr_flight', {
  pnrId: {type: Sequelize.BIGINT, field: 'pnr_id', references: 'pnr'},
  flightId: {type: Sequelize.BIGINT, field: 'flight_id', references: 'flight'}
});
const PnrAddressModel = db.define('pnr_address', {
  pnrId: {type: Sequelize.BIGINT, field: 'pnr_id', references: 'pnr'},
  addressId: {type: Sequelize.BIGINT, field: 'address_id', references: 'address'}
});

PassengerModel.hasMany(ApisModel);
FlightModel.hasMany(ApisModel);
ApisModel.belongsTo(PassengerModel);
ApisModel.belongsTo(FlightModel);

PassengerModel.hasMany(BagModel);
FlightModel.hasMany(BagModel);
BagModel.belongsTo(PassengerModel);
BagModel.belongsTo(FlightModel);

PassengerModel.belongsToMany(PnrModel, {through: PnrPassengerModel});
PnrModel.belongsToMany(PassengerModel,{through: PnrPassengerModel});
FlightModel.belongsToMany(PnrModel, {through: PnrFlightModel});
PnrModel.belongsToMany(FlightModel, {through: PnrFlightModel});
AddressModel.belongsToMany(PnrModel, {through: PnrAddressModel});
PnrModel.belongsToMany(AddressModel, {through: PnrAddressModel});

const Passenger = db.models.passenger;
const Flight = db.models.flight;
const Apis = db.models.flight_pax;
const Pnr = db.models.pnr;
const Address = db.models.address;
const Bag = db.models.bag;
const ApiAccess = db.models.api_access;

export {Passenger, Apis, Flight, Pnr, Address, Bag, ApiAccess};
