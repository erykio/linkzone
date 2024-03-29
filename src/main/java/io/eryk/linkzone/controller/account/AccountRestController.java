package io.eryk.linkzone.controller.account;

import io.eryk.linkzone.dto.AccountCreate;
import io.eryk.linkzone.dto.AccountDetails;
import io.eryk.linkzone.dto.AccountGroupInfo;
import io.eryk.linkzone.dto.AccountSummary;
import io.eryk.linkzone.dto.AccountUpdate;
import io.eryk.linkzone.dto.IAccountStatsDto;
import io.eryk.linkzone.dto.IsAvailableResponse;
import io.eryk.linkzone.dto.JwtAuthenticationResponse;
import io.eryk.linkzone.dto.LoginRequest;
import io.eryk.linkzone.exception.EmailTakenException;
import io.eryk.linkzone.exception.NotFoundException;
import io.eryk.linkzone.exception.UserNotFoundException;
import io.eryk.linkzone.exception.UsernameTakenException;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.JwtTokenProvider;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.AccountService;
import io.eryk.linkzone.service.FileStorageService;
import io.eryk.linkzone.utils.ModelMapper;
import io.eryk.linkzone.utils.MultipartFileValidator;
import io.eryk.linkzone.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/api/users")
public class AccountRestController {

    private final AccountService accountService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final FileStorageService fileStorageService;

    @Autowired
    public AccountRestController(AccountService accountService,
                                 JwtTokenProvider tokenProvider,
                                 AuthenticationManager authenticationManager, FileStorageService fileStorageService) {
        this.accountService = accountService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/login/")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new NotFoundException();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new ResponseEntity<>(new JwtAuthenticationResponse(jwt), HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(Pageable pageable) {
        return new ResponseEntity<>(accountService.findAll(pageable).map(AccountSummary::new), HttpStatus.OK);
    }

    @GetMapping(value = "/findExact")
    public ResponseEntity<?> findByExactUsername(@RequestParam("username") @NotNull String username) {
        try {
            Account account = accountService.findByUsername(username);
            return new ResponseEntity<>(new AccountSummary(account), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(
            @Valid @RequestBody AccountCreate accountCreate,
            Errors errors) {
        validateUsername(accountCreate.getUsername());
//        validateEmail(accountCreate.getEmail());
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Account account = accountService.create(accountCreate);
        AccountSummary accountSummary = ModelMapper.mapAccountToSummary(account);
        return new ResponseEntity<>(accountSummary, HttpStatus.CREATED);
    }

    @GetMapping(value = "/details")
    public AccountDetails getCurrentUserInfo(@CurrentUser UserPrincipal currentUser) {
        return new AccountDetails(currentUser.getAccount());
    }

    @GetMapping(value = "/groupInfo/{username}")
    public ResponseEntity<?> getExtras(@PathVariable String username) {
        Account account = accountService.findByUsernameEager(username);
        return new ResponseEntity<>(new AccountGroupInfo(account), HttpStatus.OK);
    }

    @GetMapping(value = "/{username}")
    public AccountDetails detail(@PathVariable String username) {
        // TODO do not expose an email
        return new AccountDetails(accountService.findByUsername(username));
    }

    @GetMapping(value = "/{username}/stats")
    public IAccountStatsDto stats(@PathVariable String username) {
        return accountService.calculateStats(accountService.findByUsername(username).getId());
    }

    @PutMapping(value = "/{username}")
    public ResponseEntity<?> update(@PathVariable String username,
                                    @Valid @RequestBody AccountUpdate accountUpdate,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        Account account = accountService.findByUsername(username);
        // validate username only if it has changed
        if (!account.getUsername().equals(accountUpdate.getUsername())) {
            validateUsername(accountUpdate.getUsername());
        }
        if (!account.getEmail().equals(accountUpdate.getEmail())) {
//            validateEmail(accountUpdate.getEmail());
        }
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        accountService.update(account, accountUpdate);
        return new ResponseEntity<>(new AccountDetails(account), HttpStatus.OK);
    }

    @PostMapping(value = "/{username}/upload-avatar")
    public AccountDetails uploadAvatar(@RequestParam("data") MultipartFile file,
                                           @PathVariable("username") String username,
                                           @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 200, 200);
        MultipartFileValidator.validateImageSize(file, 100);
        String fileName = fileStorageService.storeFile(file);
        accountService.updateAvatar(fileName, currentUser.getId());
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(fileName)
                .toUriString();

        fileStorageService.removeFile(currentUser.getAccount().getAvatar()); //

        return detail(currentUser.getUsername());
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username,
                                       @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        Account account = accountService.findByUsername(username);
        accountService.delete(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/checkUsername/{username}/")
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable("username") String username) {
        boolean isAvailable = true;
        try {
            validateUsername(username);
        } catch (UsernameTakenException e) {
            isAvailable = false;
        }
        return new ResponseEntity<>(new IsAvailableResponse(isAvailable), HttpStatus.OK);
    }

    @GetMapping(value = "/checkEmail/{email}/")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable("email") String email) {
        boolean isAvailable = true;
        try {
            validateEmail(email);
        } catch (EmailTakenException e) {
            isAvailable = false;
        }
        return new ResponseEntity<>(new IsAvailableResponse(isAvailable), HttpStatus.OK);
    }

    private void validateUsername(String updatedUsername) {
        try {
            accountService.findByUsername(updatedUsername);
            throw new UsernameTakenException(updatedUsername);
        } catch (UserNotFoundException ignored) {
        }
    }

    private void validateEmail(String email) {
        try {
            accountService.findByEmail(email);
            throw new EmailTakenException(email);
        } catch (UserNotFoundException ignored) {
        }
    }
}
