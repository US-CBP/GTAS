// @flow
import passport from 'passport';
import {Strategy as LocalStrategy} from 'passport-local';
import jwt from 'jsonwebtoken';
import {ApiAccess} from './data/connectors.js';
import bcrypt from 'bcrypt';
import {jwtSecret, jwtExpire} from './config.js';

// Local Strategy, used to initially get token
passport.use(new LocalStrategy(
  function(username:string, password:string, cb:any):void {
    findByCreds(username, password, function(err:string|null, user:any):any {
      if (err) { return cb(err); }
      if (!user) { return cb(null, false); }
      return cb(null, user);
    });
  }));
//Required because passport will not call default defaul when session:false
export function serializeUser(req: any, res:any, next:any):void {
  req.user = {
    id: req.user.id
  };
  next();
}
export function findByCreds(username:string, password:string, cb:(err:string|null,user:any)=>any):void {
  ApiAccess.find({where: {username:username}})
    .then(user=>{
        !user
          ?cb("User not found", false)
          :bcrypt.compare(password, user.dataValues.password)
            .then(res=> {
              res?cb(null, user):cb("Incorrect Password",false);
            });
      },
      reason=>cb(reason, false));
}
export function generateToken(req:any, res:any, next:any):void {
  //expiresIn seconds
  req.token = jwt.sign({id: req.user.id}, jwtSecret, {expiresIn: jwtExpire});
  next();
}
export function localResponse(req:any, res:any):void {
  res.status(200).json({
    user: req.user,
    token: req.token
  });
}
