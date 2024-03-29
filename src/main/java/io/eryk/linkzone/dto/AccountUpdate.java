package io.eryk.linkzone.dto;

import io.eryk.linkzone.config.AccountConstants;
import io.eryk.linkzone.validation.annotation.NoSpacesConstraint;
import io.eryk.linkzone.validation.annotation.PasswordMatches;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@PasswordMatches
public class AccountUpdate implements Password {

    @NoSpacesConstraint
    @Size(max = AccountConstants.USERNAME_MAX_LENGTH, min = AccountConstants.USERNAME_MIN_LENGTH,
            message = AccountConstants.INVALID_USERNAME)
    @NotNull
    private String username;

    @Size(max = AccountConstants.EMAIL_LENGTH)
    @Email
    private String email;

    @Size(min = AccountConstants.PASSWORD_MIN_LENGTH, max = AccountConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @Size(min = AccountConstants.PASSWORD_MIN_LENGTH, max = AccountConstants.PASSWORD_MAX_LENGTH)
    private String passwordConfirm;

    private String tagline;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTagline() {
        return Optional.ofNullable(tagline).map(String::trim).orElse(null);
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
