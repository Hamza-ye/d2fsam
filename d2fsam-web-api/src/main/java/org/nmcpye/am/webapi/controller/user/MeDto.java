/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.webapi.controller.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.security.acl.Access;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.UserGroup;
import org.nmcpye.am.user.sharing.Sharing;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class MeDto {
    public MeDto(User user, Map<String, Serializable> settings, List<String> programs, List<String> dataSets) {
        this.id = user.getUid();
        this.uuid = user.getUuid().toString();
        this.username = user.getUsername();
        this.lastName = user.getSurname();
//        this.externalAuth = user.getExternalAuth();;

//        this.openId = user.getOpenId();;

//        this.ldapId = user.getLdapId();;

        this.password = user.getPassword();
        ;

//        this.twoFA = user.getTwoFA();;

        this.passwordLastUpdated = user.getPasswordLastUpdated();
        ;
        this.code = user.getCode();

        this.langKey = user.getLangKey();
//    @JsonProperty
//    private Set<CategoryOptionGroupSet> cogsDimensionConstraints = new HashSet<>();
//
//    @JsonProperty
//    private Set<Category> catDimensionConstraints = new HashSet<>();

        this.previousPasswords = user.getPreviousPasswords();

        this.lastLogin = user.getLastLogin();
        ;

        this.restoreToken = user.getRestoreToken();
        ;

        this.idToken = user.getSurname();
        ;

        this.restoreExpiry = user.getRestoreExpiry();
        ;

        this.selfRegistered = user.isSelfRegistered();
        ;

//        this.invitation = user.getInvitation();;

        this.diabled = user.isDisabled();
        ;

        this.accountExpiry = user.getAccountExpiry();
        ;

        this.sharing = user.getSharing();
        ///////////////////////////
        this.firstName = user.getFirstName();
//        this.employer = user.getEmployer();
        this.languages = user.getLanguages();
        this.gender = user.getGender();
        this.jobTitle = user.getJobTitle();
        this.avatar = user.getAvatar();
        this.created = user.getCreated();
        this.lastUpdated = user.getUpdated();
        this.dataViewOrganisationUnits = user.getDataViewOrganisationUnits();
//        this.favorites = user.getFavorites();
        this.sharing = user.getSharing();
        this.userGroupAccesses = user.getUserGroupAccesses();
        this.userAccesses = user.getUserAccesses();
        this.userGroups = user.getGroups();
        this.teams = user.getTeams();
        this.translations = user.getTranslations();
        this.teiSearchOrganisationUnits = user.getTeiSearchOrganisationUnits();
        this.organisationUnits = user.getOrganisationUnits();
        this.externalAccess = user.getExternalAccess();
        this.displayName = user.getDisplayName();
        this.access = user.getAccess();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.introduction = user.getIntroduction();
        this.birthday = user.getBirthday();
//        this.nationality = user.getNationality();
//        this.education = user.getNationality();
        this.interests = user.getInterests();
        this.whatsApp = user.getWhatsApp();
//        this.facebookMessenger = user.getFacebookMessenger();
//        this.skype = user.getSkype();
        this.telegram = user.getTelegram();
//        this.twitter = user.getTwitter();
        this.userRoles = user.getUserAuthorityGroups();

        this.authorities = new ArrayList<>(user.getAllAuthorities());

        this.settings = settings;
        this.programs = programs;
        this.dataSets = dataSets;

    }

    // NMCP From CredentialDto /////////////////////////////
    @JsonProperty()
    private String code;

    @JsonProperty()
    private String langKey;

    @JsonProperty
    private String uuid;

    @JsonProperty()
    private String username;

//    @JsonProperty
//    private boolean externalAuth;

//    @JsonProperty
//    private String openId;

//    @JsonProperty
//    private String ldapId;

    @JsonProperty
    private String password;

//    @JsonProperty
//    private boolean twoFA;

    @JsonProperty
    private LocalDateTime passwordLastUpdated;

//    @JsonProperty
//    private Set<CategoryOptionGroupSet> cogsDimensionConstraints = new HashSet<>();
//
//    @JsonProperty
//    private Set<Category> catDimensionConstraints = new HashSet<>();

    @JsonProperty
    private List<String> previousPasswords = new ArrayList<>();

    @JsonProperty
    private LocalDateTime lastLogin;

    @JsonProperty
    private String restoreToken;

    @JsonProperty
    private String idToken;

    @JsonProperty
    private LocalDateTime restoreExpiry;

    @JsonProperty
    private boolean selfRegistered;

//    @JsonProperty
//    private boolean invitation;

    @JsonProperty
    private boolean diabled;

    @JsonProperty
    private LocalDateTime accountExpiry;

    @JsonProperty()
    private Sharing sharing = new Sharing();
    ////////////////////////////////////

    @JsonProperty()
    private String id;

    @JsonProperty()
    private String lastName;

    @JsonProperty()
    private String firstName;

    @JsonProperty()
    private String employer;

    @JsonProperty()
    private String languages;

    @JsonProperty()
    private String gender;

    @JsonProperty()
    private String jobTitle;

    @JsonProperty()
    private FileResource avatar;

    @JsonProperty()
    private Instant created;

    @JsonProperty()
    private Instant lastUpdated;

    @JsonProperty()
    private Set<OrganisationUnit> dataViewOrganisationUnits;

    @JsonProperty()
    protected Set<String> favorites;

    @JsonProperty()
    private Set<org.nmcpye.am.user.UserGroupAccess> userGroupAccesses;

    @JsonProperty()
    private Set<org.nmcpye.am.user.UserAccess> userAccesses;

    @JsonProperty()
    private Set<UserGroup> userGroups;

    @JsonProperty()
    private Set<Team> teams;

    @JsonProperty()
    private Set<Translation> translations;

    @JsonProperty()
    private Set<OrganisationUnit> teiSearchOrganisationUnits;

    @JsonProperty()
    private Set<OrganisationUnit> organisationUnits;

    @JsonProperty()
    private Boolean externalAccess;

    @JsonProperty()
    private String displayName;

    @JsonProperty()
    private Access access;

    @JsonProperty()
    private String name;

    @JsonProperty()
    private String email;

    @JsonProperty()
    private String phoneNumber;

    @JsonProperty()
    private String introduction;

    @JsonProperty()
    private LocalDate birthday;

    @JsonProperty()
    private String nationality;

    @JsonProperty()
    private String education;

    @JsonProperty()
    private String interests;

    @JsonProperty()
    private String whatsApp;

    @JsonProperty()
    private String facebookMessenger;

    @JsonProperty()
    private String skype;

    @JsonProperty()
    private String telegram;

    @JsonProperty()
    private String twitter;

    @JsonProperty
    private Set<UserAuthorityGroup> userRoles;

    @JsonProperty()
    private Map<String, Serializable> settings;

    @JsonProperty()
    private List<String> programs;

    @JsonProperty()
    private List<String> authorities;

    @JsonProperty()
    private List<String> dataSets;
}
