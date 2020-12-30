package com.bugtracker.bug.controller;

import com.bugtracker.bug.*;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("user")
public class UserController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserController(TicketRepository ticketRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Ticket> create(@Valid @RequestBody Ticket ticket, HttpServletRequest request) {

        // Variables that show whether current user and user's group are in repository
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        String curr_user_id = principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject();
        Optional<T_User> existUser = userRepository.findById(curr_user_id);
        System.out.println("user is " + existUser);

        KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext)(request.getAttribute(KeycloakSecurityContext.class.getName()));
        AccessToken token = keycloakSecurityContext.getToken();
        Map<String, Object> other_claims = token.getOtherClaims();
        String curr_group_id = String.valueOf(other_claims.get("group"));
        Optional<T_Group> existGroup = groupRepository.findById(curr_group_id);

        // if user in user repo, set user to that user in repo
        // if user not in user repo, add user to user repo and set user to that user in repo
        if (existUser.isPresent()) {
            ticket.setUser(existUser.get());
        } else {
            T_User t_user = new T_User();
            userRepository.save(t_user);
            Optional<T_User> new_user = userRepository.findById(ticket.getUser().getUser_id());
            ticket.setUser(new_user.get());
        }

        // same as above but with group, not user
        if (existGroup.isPresent()) {
            ticket.setGroup(existGroup.get());
        } else {
            T_Group t_group = new T_Group();
            groupRepository.save(t_group);
            Optional<T_Group> new_group = groupRepository.findById(ticket.getGroup().getGroup());
            ticket.setGroup(new_group.get());
        }

        // save ticket into ticket repo
        Ticket savedTicket = ticketRepository.save(ticket);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTicket.getId()).toUri();

        return ResponseEntity.created(location).body(savedTicket);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<T_User> getAllTickets(HttpServletRequest request) {

        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        String curr_user_id = principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject();
        Optional<T_User> optionalUser = userRepository.findById(curr_user_id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalUser.get());
    }

    @GetMapping("index")
    public String index(){
        return "user/index";
    }
}