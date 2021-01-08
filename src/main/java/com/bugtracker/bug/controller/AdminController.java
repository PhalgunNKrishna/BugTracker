package com.bugtracker.bug.controller;

import com.bugtracker.bug.GroupRepository;
import com.bugtracker.bug.T_Group;
import com.bugtracker.bug.TicketRepository;
import com.bugtracker.bug.UserRepository;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.acl.Group;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public AdminController(TicketRepository ticketRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<T_Group> getAllTickets(HttpServletRequest request) {

        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_group_id = (accessToken.getOtherClaims().get("groups").toString());
        Optional<T_Group> optGroup = groupRepository.findById(curr_group_id);

        return ResponseEntity.ok(optGroup.get());
    }

    @GetMapping("index")
    public String index(){
        return "admin/index";
    }
}
