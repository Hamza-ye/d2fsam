package org.nmcpye.am.user.dto;

import org.nmcpye.am.user.User;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with only the public attributes.
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    @Size(max = 100)
    private String name;

    private String imageUrl;

    private boolean disabled = false;

    @Size(min = 2, max = 10)
    private String langKey;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private Set<String> authorities;

    //    private Set<String> userAuthorities;
    //
    //    private Set<UserGroup> groups;
    //
    //    private Set<OrganisationUnit> organisationUnits;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.disabled = user.isDisabled();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        //        this.userAuthorities = user.getUserAuthorityGroups().stream()
        //            .map(UserAuthorityGroup::getAuthorities)
        //            .map(Set::stream).collect(Collectors.toSet())
        //            .stream().flatMap(item -> item).collect(Collectors.toSet());
        //
        //        this.groups = user.getGroups();
        //        this.organisationUnits = user.getGroups()
        //            .stream().map(UserGroup::getOrganisationUnits)
        //            .map(Set::stream).collect(Collectors.toSet())
        //            .stream().flatMap(item -> item).collect(Collectors.toSet());
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            "}";
    }
}
