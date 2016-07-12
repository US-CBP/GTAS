/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security.service;


import gov.gtas.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Service;

import gov.gtas.security.AuthUser;


//@Service("userDetailsService")
public class AuthUserDetailsService extends JdbcDaoImpl {


    @Autowired
    private DataSource dataSource;
 
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
 
    @Override
    @Value("select * from gtas_users where user_id = ?")
    public void setUsersByUsernameQuery(String usersByUsernameQueryString) {
        super.setUsersByUsernameQuery(usersByUsernameQueryString);
    }
 
    @Override
    @Value("select role_id, role_description from gtas_roles where role_id =?")
    public void setAuthoritiesByUsernameQuery(String queryString) {
        super.setAuthoritiesByUsernameQuery(queryString);
    }
    
    
    
 
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
          
    //  return getJdbcTemplate().queryForObject(super.getUsersByUsernameQuery(), new String[] { username }, gov.gtas.model.Role.class)
        
        
        return getJdbcTemplate().queryForObject(super.getUsersByUsernameQuery(), new String[] { username }, new RowMapper<AuthUser>() {
              public AuthUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                String username = rs.getString("user_id");
                String password = rs.getString("password");
                /*  boolean enabled = rs.getBoolean("enabled");
                boolean accountNonExpired = rs.getBoolean("accountNonExpired");
                boolean credentialsNonExpired = rs.getBoolean("credentialsNonExpired");
                boolean accountNonLocked = rs.getBoolean("accountNonLocked");
     */
                ArrayList<GrantedAuthority> tempList = new ArrayList<GrantedAuthority>();
                
                tempList.add(AuthUser.MANAGE_QUERIES_AUTHORITY);
                
                return new AuthUser(username, password, true, true, true, true, tempList);
              }
     
          })
          ;
                  
                  
                
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {

    ArrayList<UserDetails> tempList = new ArrayList<UserDetails>();
            
    tempList.add(   getJdbcTemplate().queryForObject(super.getUsersByUsernameQuery(), new String[] { username }, new RowMapper<AuthUser>() {
              public AuthUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                String username = rs.getString("user_id");
                String password = rs.getString("password");
                /*  boolean enabled = rs.getBoolean("enabled");
                boolean accountNonExpired = rs.getBoolean("accountNonExpired");
                boolean credentialsNonExpired = rs.getBoolean("credentialsNonExpired");
                boolean accountNonLocked = rs.getBoolean("accountNonLocked");
     */
                ArrayList<GrantedAuthority> tempList = new ArrayList<GrantedAuthority>();
                                
                tempList.add(AuthUser.MANAGE_RULES_AUTHORITY);
                
                return new AuthUser(username, password, true, true, true, true, tempList);
              }
     
          })  
          )
          ;
         
         return tempList;
    }

    
    
    
    //override to get accountNonLocked  
/*  @Override
    public List<UserDetails> loadUsersByUsername(String username) {
      return getJdbcTemplate().query(super.getUsersByUsernameQuery(), new String[] { username },
        new RowMapper<AuthUser>() {
          public AuthUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            String username = rs.getString("user_id");
            String password = rs.getString("password");
            boolean enabled = rs.getBoolean("enabled");
            boolean accountNonExpired = rs.getBoolean("accountNonExpired");
            boolean credentialsNonExpired = rs.getBoolean("credentialsNonExpired");
            boolean accountNonLocked = rs.getBoolean("accountNonLocked");
 
            return new AuthUser(username, password, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
          }
 
      });
    }*/
 
    /*//override to pass accountNonLocked
    @Override
    public UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
            List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();
 
        if (super.isUsernameBasedPrimaryKey()) {
          returnUsername = username;
        }
 
        return new User(returnUsername, userFromUserQuery.getPassword(), 
                       userFromUserQuery.isEnabled(),
               userFromUserQuery.isAccountNonExpired(), 
                       userFromUserQuery.isCredentialsNonExpired(),
            userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
    }*/
 
    
    
}
