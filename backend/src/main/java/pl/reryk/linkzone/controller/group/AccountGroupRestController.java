package pl.reryk.linkzone.controller.group;

import pl.reryk.linkzone.dto.GroupResponse;
import pl.reryk.linkzone.dto.IGroupResponseDto;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.AccountService;
import pl.reryk.linkzone.service.GroupMembershipService;
import pl.reryk.linkzone.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users/{username}/groups")
public class AccountGroupRestController {

    private final AccountService accountService;
    private final GroupMembershipService groupMembershipService;
    private final GroupService groupService;

    @Autowired
    public AccountGroupRestController(AccountService accountService,
                                      GroupMembershipService groupMembershipService,
                                      GroupService groupService) {
        this.accountService = accountService;
        this.groupMembershipService = groupMembershipService;
        this.groupService = groupService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> listGroupsSubscribedByUser(@PathVariable String username, @CurrentUser UserPrincipal currentUser) {
        Long requestUserId = currentUser != null ? currentUser.getAccount().getId() : - 1;
        List<IGroupResponseDto> dtos = groupService.getSubscribedGroups(accountService.findByUsername(username).getId(), requestUserId);
        List<GroupResponse> response = dtos.stream().map(GroupResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/managed/")
    public ResponseEntity<?> listManagedGroups(@PathVariable String username, @CurrentUser UserPrincipal currentUser) {
        Long requestUserId = currentUser != null ? currentUser.getAccount().getId() : - 1;
        List<GroupResponse> response = groupService
                .getManagedGroups(accountService.findByUsername(username).getId(), requestUserId)
                .stream().map(GroupResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}