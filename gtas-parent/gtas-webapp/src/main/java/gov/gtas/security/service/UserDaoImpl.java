/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security.service;

import gov.gtas.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
 

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;
 

@Repository
public class UserDaoImpl extends JdbcDaoSupport {

    

    private static final int MAX_ATTEMPTS = 3;
 
    @Autowired
    private DataSource dataSource;
 
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
 
/*  
    public void updateFailAttempts(String username) {
 
      UserAttempts user = getUserAttempts(username);
      if (user == null) {
        if (isUserExists(username)) {
            // if no record, insert a new
            getJdbcTemplate().update(SQL_USER_ATTEMPTS_INSERT, new Object[] { username, 1, new Date() });
        }
      } else {
 
        if (isUserExists(username)) {
            // update attempts count, +1
            getJdbcTemplate().update(SQL_USER_ATTEMPTS_UPDATE_ATTEMPTS, new Object[] { new Date(), username});
        }
 
        if (user.getAttempts() + 1 >= MAX_ATTEMPTS) {
            // locked user
            getJdbcTemplate().update(SQL_USERS_UPDATE_LOCKED, new Object[] { false, username });
            // throw exception
            throw new LockedException("User Account is locked!");
        }
 
      }
 
    }
 */
    
    public User getUserEntity(String username) {
 
      try {
 
        User user = getJdbcTemplate().queryForObject("",
            new Object[] { username }, new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
 
                User user = new User();
                user.setUserId(rs.getString("user_id"));
 
                return user;
            }
 
        });
        return user;
 
      } catch (EmptyResultDataAccessException e) {
        return null;
      }
 
    }
 

/*  private boolean isUserExists(String username) {
 
      boolean result = false;
 
      int count = getJdbcTemplate().queryForObject(
                            SQL_USERS_COUNT, new Object[] { username }, Integer.class);
      if (count > 0) {
        result = true;
      }
 
      return result;
    }
*/ 
    
    
}
