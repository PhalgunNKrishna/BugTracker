package com.bugtracker.bug.controller;

import com.bugtracker.bug.*;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.acl.Group;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public AdminController(TicketRepository ticketRepository, UserRepository userRepository,
                           GroupRepository groupRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    // lists all the tickets from users in the group
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<T_Group> getAllTickets(HttpServletRequest request) {

        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = context.getToken();
        String curr_group_id = (accessToken.getOtherClaims().get("groups").toString());
        Optional<T_Group> optGroup = groupRepository.findById(curr_group_id);

        return ResponseEntity.ok(optGroup.get());
    }

    // update "resolved" data member of ticket objects
    @PatchMapping(value = "/resolve/{id}")
    public ResponseEntity<Void> resolve(@PathVariable String id,
                                        @RequestBody String admin_message) {
        System.out.println("49");
        String new_id = id.replace("{", "");
        String id_new = new_id.replace("}", "");
        Optional<Ticket> optionalTicket = ticketRepository.findById(Integer.parseInt(id_new));
        System.out.println("51");

        if (!optionalTicket.isPresent()) {
            return ResponseEntity.notFound().build();
        } Ticket resTick = optionalTicket.get();

        // if resolved, change to not resolved
        // if not resolved, change to resolved
        if (!resTick.getResolved()) {
            resTick.setResolved(true);
        } else {
            resTick.setResolved(false);
        }

        // set the admin message
        resTick.setAdmin_message(admin_message);

        ticketRepository.save(resTick);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("index")
    public String index(){
        return "admin/index";
    }
}
