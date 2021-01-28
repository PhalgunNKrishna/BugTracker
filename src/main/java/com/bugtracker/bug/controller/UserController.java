package com.bugtracker.bug.controller;

import com.bugtracker.bug.*;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
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
        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_user_id = (accessToken.getOtherClaims().get("user_id").toString());
        Optional<T_User> optionalUser = userRepository.findById(curr_user_id);

        String curr_group_id = (accessToken.getOtherClaims().get("groups").toString());
        Optional<T_Group> optionalGroup = groupRepository.findById(curr_group_id);

        // if user in user repo, set user to that user in repo
        // if user not in user repo, add user to user repo and set user to that user in repo
        if (optionalUser.isPresent()) {
            ticket.setUser(optionalUser.get());
        } else {
            T_User t_user = new T_User();
            t_user.setUser_id(curr_user_id);
            userRepository.save(t_user);
            Optional<T_User> new_user = userRepository.findById(curr_user_id);
            ticket.setUser(t_user);
        }

        // same as above if-else block but with group, not user
        if (optionalGroup.isPresent()) {
            ticket.setGroup(optionalGroup.get());
        } else {
            T_Group t_group = new T_Group();
            t_group.setGroup(curr_group_id);
            groupRepository.save(t_group);
            Optional<T_Group> new_group = groupRepository.findById(curr_group_id);
            ticket.setGroup(t_group);
        }

        // Setting Date
        Date t_date = new Date();
        ticket.setDate(t_date);

        // Setting resolved
        ticket.setResolved(false);

        // Setting admin message
        ticket.setAdmin_message(null);

        // save ticket into ticket repo
        Ticket savedTicket = ticketRepository.save(ticket);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTicket.getId()).toUri();

        return ResponseEntity.created(location).body(savedTicket);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<T_User> getAllTickets(HttpServletRequest request) {
        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_user_id = (accessToken.getOtherClaims().get("user_id").toString());
        Optional<T_User> optionalUser = userRepository.findById(curr_user_id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalUser.get());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ticket> getTicket(HttpServletRequest request, @PathVariable int id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        return ResponseEntity.ok(optionalTicket.get());
    }

    @GetMapping("index")
    public String index(){
        return "user/index";
    }
}