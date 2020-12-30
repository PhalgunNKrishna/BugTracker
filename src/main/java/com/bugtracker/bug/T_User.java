package com.bugtracker.bug;

import com.sun.istack.NotNull;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.HashSet;

@Entity
public class T_User {

    @Id
    private String user_id;

    @NotNull
    private String username;

    @OneToMany(mappedBy = "t_user", cascade = CascadeType.ALL)
    private Set<Ticket> tickets = new HashSet<>();

    public String getUser_id() {
        return this.user_id;
    }

    @GetMapping
    public ResponseEntity setUser_id(HttpServletRequest request) {

        // Below code taken from:
        // https://stackoverflow.com/questions/45802797/spring-keycloak-get-user-id/52700002

        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        this.user_id = principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject();
        return new ResponseEntity(HttpStatus.OK);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(HttpServletRequest request) {

        // Below code taken from:
        // https://stackoverflow.com/questions/49105290/how-to-get-userinfo-in-springboot-using-keycloak

        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        this.username = accessToken.getPreferredUsername();
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
        for (Ticket t : tickets) {
            t.setUser(this);
        }
    }
}
