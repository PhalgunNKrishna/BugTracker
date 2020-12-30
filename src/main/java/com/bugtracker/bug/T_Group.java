package com.bugtracker.bug;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class T_Group {

    @Id
    private String t_group;

    @OneToMany(mappedBy = "t_group", cascade = CascadeType.ALL)
    private Set<Ticket> tickets = new HashSet<>();

    public String getGroup() {
        return this.t_group;
    }

    @GetMapping
    public ResponseEntity setGroup(HttpServletRequest request) {
        KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext)(request.getAttribute(KeycloakSecurityContext.class.getName()));
        AccessToken token = keycloakSecurityContext.getToken();

        // Below code ("other claims") taken from:
        // https://stackoverflow.com/questions/45802797/spring-keycloak-get-user-id/52700002
        // https://stackoverflow.com/questions/32678883/keycloak-retrieve-custom-attributes-to-keycloakprincipal

        Map<String, Object> other_claims = token.getOtherClaims();
        this.t_group = String.valueOf(other_claims.get("group"));
        return new ResponseEntity(HttpStatus.OK);
    }

    public Set<Ticket> getTickets() {
        return this.tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
        for (Ticket t : tickets) {
            t.setGroup(this);
        }
    }
}
