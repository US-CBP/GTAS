/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;


import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public final class AuthUser extends User{

    
    private static final long serialVersionUID = 1L;
    private final String lastName;
    private final String firstName;
    private final String emailAddress;
    private final String userLevelAccess;
    public static final GrantedAuthority MANAGE_RULES_AUTHORITY = new SimpleGrantedAuthority("MANAGE_RULES");
    public static final GrantedAuthority MANAGE_QUERIES_AUTHORITY = new SimpleGrantedAuthority("MANAGE_QUERIES");
    public static final GrantedAuthority MANAGE_USERS_AUTHORITY = new SimpleGrantedAuthority("MANAGE_USERS");
    public static final GrantedAuthority MANAGE_WATCHLIST_AUTHORITY = new SimpleGrantedAuthority("MANAGE_WATCHLIST");
    public static final GrantedAuthority VIEW_FLIGHTS_PASSENGERS_AUTHORITY = new SimpleGrantedAuthority("VIEW_FLIGHTS_PASSENGERS");

            
    public AuthUser(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities, String lastName, 
            String firstName, String emailAddress, 
            String userLevelAccess) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.userLevelAccess = userLevelAccess;
        
    }
    
    public AuthUser(String username, String password, boolean b, boolean c,
            boolean d, boolean e, List<GrantedAuthority> noAuthorities) {

        super(username, password, b, c, d,
                e, noAuthorities);

        this.firstName = "";
        this.lastName = "";
        this.emailAddress = "";
        this.userLevelAccess = "";

    }

    public static AuthUser getUser() {
        return (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AuthUser))
            return false;
        AuthUser castOther = (AuthUser) other;
        return new EqualsBuilder().append(lastName, castOther.lastName).append(firstName, castOther.firstName)
                .append(emailAddress, castOther.emailAddress).append(getUsername(), castOther.getUsername()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lastName).append(firstName).append(emailAddress).append(getUsername())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("lastName", lastName).append("firstName", firstName)
                .append("emailAddress", emailAddress).append("userName", getUsername()).toString();
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getUserLevelAccess() {
        return userLevelAccess;
    }


    public boolean isQueryBuilderPageEnabled() {
        return (getAuthorities().contains(MANAGE_QUERIES_AUTHORITY));
    }

    public boolean isAdminPageEnabled(){
        return (getAuthorities().contains(MANAGE_QUERIES_AUTHORITY)&&
                getAuthorities().contains(MANAGE_RULES_AUTHORITY)&&
                getAuthorities().contains(MANAGE_USERS_AUTHORITY)&&
                getAuthorities().contains(MANAGE_WATCHLIST_AUTHORITY)&&
                getAuthorities().contains(VIEW_FLIGHTS_PASSENGERS_AUTHORITY));
    }

}
