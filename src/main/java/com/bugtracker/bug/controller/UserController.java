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
        System.out.println("create");
        // Variables that show whether current user and user's group are in repository
//        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
//        String curr_user_id = principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject();
//        System.out.println(curr_user_id);
//        Optional<T_User> existUser = userRepository.findById(curr_user_id);
        //System.out.println("user is " + existUser);
        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_user_id = (accessToken.getOtherClaims().get("user_id").toString());
        System.out.println("user_id = " + curr_user_id);
        Optional<T_User> optionalUser = userRepository.findById(curr_user_id);

        String curr_group_id = (accessToken.getOtherClaims().get("groups").toString());
        System.out.println("group_id = " + curr_group_id);
        Optional<T_Group> optionalGroup = groupRepository.findById(curr_group_id);

        // if user in user repo, set user to that user in repo
        // if user not in user repo, add user to user repo and set user to that user in repo
        if (optionalUser.isPresent()) {
            ticket.setUser(optionalUser.get());
        } else {
            System.out.println("65");
            T_User t_user = new T_User();
            t_user.setUser_id(curr_user_id);
            System.out.println("this user's id = " + t_user.getUser_id());
            userRepository.save(t_user);
            System.out.println("70");
            Optional<T_User> new_user = userRepository.findById(curr_user_id);
            ticket.setUser(t_user);
            System.out.println("this ticket's user = " + ticket.getUser());
        }

        // same as above but with group, not user
        if (optionalGroup.isPresent()) {
            ticket.setGroup(optionalGroup.get());
        } else {
            T_Group t_group = new T_Group();
            t_group.setGroup(curr_group_id);
            System.out.println("this user's group name = " + t_group.getGroup());
            // issue comes b/c group_id column in mysql t_group table is not set
            // delete group_id column or make the group_id column an @Id generated value
            groupRepository.save(t_group);
            Optional<T_Group> new_group = groupRepository.findById(curr_group_id);
            ticket.setGroup(t_group);
            System.out.println("this ticket's group = " + ticket.getGroup());
        }

        // Setting Date
        Date t_date = new Date();
        ticket.setDate(t_date);

        // save ticket into ticket repo
        System.out.println("before saving");
        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println("after saving");

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTicket.getId()).toUri();

        return ResponseEntity.created(location).body(savedTicket);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<T_User> getAllTickets(HttpServletRequest request) {
        System.out.println("got to all");
//        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
//        String curr_user_id = principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject();
//        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
//        KeycloakPrincipal principal = (KeycloakPrincipal)token.getPrincipal();
//        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
//        AccessToken accessToken = session.getToken();
//        String curr_user_id = accessToken.getPreferredUsername();
        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_user_id = (accessToken.getOtherClaims().get("user_id").toString());
        System.out.println("curr user id = " + curr_user_id);
        Optional<T_User> optionalUser = userRepository.findById(curr_user_id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalUser.get());
    }

    @GetMapping("index")
    public String index(){
        System.out.println("hi");
        return "user/index";
    }
}