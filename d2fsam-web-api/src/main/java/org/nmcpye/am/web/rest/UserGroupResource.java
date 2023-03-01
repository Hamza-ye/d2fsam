//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.user.UserGroup;
//import org.nmcpye.am.user.UserGroupRepository;
//import org.nmcpye.am.user.UserGroupService;
//import org.nmcpye.am.web.rest.errors.BadRequestAlertException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import tech.jhipster.web.util.HeaderUtil;
//import tech.jhipster.web.util.PaginationUtil;
//import tech.jhipster.web.util.ResponseUtil;
//
//import javax.validation.Valid;
//import javax.validation.constraints.NotNull;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * REST controller for managing {@link org.nmcpye.am.user.UserGroup}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class UserGroupResource {
//
//    private final Logger log = LoggerFactory.getLogger(UserGroupResource.class);
//
//    private static final String ENTITY_NAME = "userGroup";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final UserGroupService userGroupService;
//
//    private final UserGroupRepository userGroupRepository;
//
//    public UserGroupResource(UserGroupService userGroupService, UserGroupRepository userGroupRepository) {
//        this.userGroupService = userGroupService;
//        this.userGroupRepository = userGroupRepository;
//    }
//
//    /**
//     * {@code POST  /user-groups} : Create a new userGroup.
//     *
//     * @param userGroup the userGroup to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userGroup, or with status {@code 400 (Bad Request)} if the userGroup has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/user-groups")
//    public ResponseEntity<UserGroup> createUserGroup(@Valid @RequestBody UserGroup userGroup) throws URISyntaxException {
//        log.debug("REST request to save UserGroup : {}", userGroup);
//        if (userGroup.getId() != null) {
//            throw new BadRequestAlertException("A new userGroup cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        UserGroup result = userGroupService.save(userGroup);
//        return ResponseEntity
//            .created(new URI("/api/user-groups/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /user-groups/:id} : Updates an existing userGroup.
//     *
//     * @param id the id of the userGroup to save.
//     * @param userGroup the userGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userGroup,
//     * or with status {@code 400 (Bad Request)} if the userGroup is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the userGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/user-groups/{id}")
//    public ResponseEntity<UserGroup> updateUserGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody UserGroup userGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to update UserGroup : {}, {}", id, userGroup);
//        if (userGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, userGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!userGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        UserGroup result = userGroupService.update(userGroup);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userGroup.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /user-groups/:id} : Partial updates given fields of an existing userGroup, field will ignore if it is null
//     *
//     * @param id the id of the userGroup to save.
//     * @param userGroup the userGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userGroup,
//     * or with status {@code 400 (Bad Request)} if the userGroup is not valid,
//     * or with status {@code 404 (Not Found)} if the userGroup is not found,
//     * or with status {@code 500 (Internal Server Error)} if the userGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/user-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<UserGroup> partialUpdateUserGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody UserGroup userGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update UserGroup partially : {}, {}", id, userGroup);
//        if (userGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, userGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!userGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<UserGroup> result = userGroupService.partialUpdate(userGroup);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userGroup.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /user-groups} : get all the userGroups.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userGroups in body.
//     */
//    @GetMapping("/user-groups")
//    public ResponseEntity<List<UserGroup>> getAllUserGroups(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of UserGroups");
//        Page<UserGroup> page;
//        if (eagerload) {
//            page = userGroupService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = userGroupService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /user-groups/:id} : get the "id" userGroup.
//     *
//     * @param id the id of the userGroup to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userGroup, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/user-groups/{id}")
//    public ResponseEntity<UserGroup> getUserGroup(@PathVariable Long id) {
//        log.debug("REST request to get UserGroup : {}", id);
//        Optional<UserGroup> userGroup = userGroupService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(userGroup);
//    }
//
//    /**
//     * {@code DELETE  /user-groups/:id} : delete the "id" userGroup.
//     *
//     * @param id the id of the userGroup to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/user-groups/{id}")
//    public ResponseEntity<Void> deleteUserGroup(@PathVariable Long id) {
//        log.debug("REST request to delete UserGroup : {}", id);
//        userGroupService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
