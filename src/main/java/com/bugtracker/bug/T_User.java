package com.bugtracker.bug;

import com.sun.istack.NotNull;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
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
        return user_id;
    }

    @GetMapping
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
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
