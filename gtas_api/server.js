import express from 'express';
import { graphqlExpress, graphiqlExpress } from 'apollo-server-express';
import bodyParser from 'body-parser';
import schema from './data/schema';
import helmet from 'helmet';
import morgan from 'morgan';
import passport from 'passport';
import {serializeUser, generateToken, localResponse} from './auth.js';
import expressJwt from 'express-jwt';
import {jwtSecret, PORT} from './config.js';

const app = express();
const authenticateJwt = expressJwt({secret: jwtSecret});
//Security middleware that prevents certain types of requests
app.use(helmet());
//Logs all http requests to console
app.use(morgan('combined'));
app.use(bodyParser.urlencoded({ extended: true }) );
app.use(passport.initialize());
app.post('/auth',
  //Strategy verifies username/password in db returns User object
  //serializeUser searches db for user and stores id in request object
  //generateToken adds jwt tokent to request object
  //localResponse responds with modified request object
  passport.authenticate('local', {session: false}),
    serializeUser, generateToken, localResponse);
// curl -v -H "Authorization: Bearer 123456789" http://127.0.0.1:3000/
// curl -v http://127.0.0.1:3000/?access_token=123456789
app.use('/graphql',
  authenticateJwt,
  graphqlExpress({ schema })
);
app.get('/graphiql', graphiqlExpress({ endpointURL: '/graphql' }));

app.listen(PORT, () =>
  console.log(
    `GraphiQL is now running on http://localhost:${PORT}/graphiql`
  )
);
