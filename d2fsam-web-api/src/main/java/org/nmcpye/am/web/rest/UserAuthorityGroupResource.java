//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.user.UserAuthorityGroup;
//import org.nmcpye.am.user.UserAuthorityGroupRepository;
//import org.nmcpye.am.user.UserAuthorityGroupService;
//import org.nmcpye.am.web.rest.errors.BadRequestAlertException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tech.jhipster.web.util.HeaderUtil;
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
// * REST controller for managing {@link org.nmcpye.am.user.UserAuthorityGroup}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class UserAuthorityGroupResource {
//
//    private final Logger log = LoggerFactory.getLogger(UserAuthorityGroupResource.class);
//
//    private static final String ENTITY_NAME = "userAuthorityGroup";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final UserAuthorityGroupService userAuthorityGroupService;
//
//    private final UserAuthorityGroupRepository userAuthorityGroupRepository;
//
//    public UserAuthorityGroupResource(
//        UserAuthorityGroupService userAuthorityGroupService,
//        UserAuthorityGroupRepository userAuthorityGroupRepository
//    ) {
//        this.userAuthorityGroupService = userAuthorityGroupService;
//        this.userAuthorityGroupRepository = userAuthorityGroupRepository;
//    }
//
//    /**
//     * {@code POST  /user-authority-groups} : Create a new userAuthorityGroup.
//     *
//     * @param userAuthorityGroup the userAuthorityGroup to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAuthorityGroup, or with status {@code 400 (Bad Request)} if the userAuthorityGroup has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/user-authority-groups")
//    public ResponseEntity<UserAuthorityGroup> createUserAuthorityGroup(@Valid @RequestBody UserAuthorityGroup userAuthorityGroup)
//        throws URISyntaxException {
//        log.debug("REST request to save UserAuthorityGroup : {}", userAuthorityGroup);
//        if (userAuthorityGroup.getId() != null) {
//            throw new BadRequestAlertException("A new userAuthorityGroup cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        UserAuthorityGroup result = userAuthorityGroupService.save(userAuthorityGroup);
//        return ResponseEntity
//            .created(new URI("/api/user-authority-groups/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /user-authority-groups/:id} : Updates an existing userAuthorityGroup.
//     *
//     * @param id the id of the userAuthorityGroup to save.
//     * @param userAuthorityGroup the userAuthorityGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAuthorityGroup,
//     * or with status {@code 400 (Bad Request)} if the userAuthorityGroup is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the userAuthorityGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/user-authority-groups/{id}")
//    public ResponseEntity<UserAuthorityGroup> updateUserAuthorityGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody UserAuthorityGroup userAuthorityGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to update UserAuthorityGroup : {}, {}", id, userAuthorityGroup);
//        if (userAuthorityGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, userAuthorityGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!userAuthorityGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        UserAuthorityGroup result = userAuthorityGroupService.update(userAuthorityGroup);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAuthorityGroup.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /user-authority-groups/:id} : Partial updates given fields of an existing userAuthorityGroup, field will ignore if it is null
//     *
//     * @param id the id of the userAuthorityGroup to save.
//     * @param userAuthorityGroup the userAuthorityGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAuthorityGroup,
//     * or with status {@code 400 (Bad Request)} if the userAuthorityGroup is not valid,
//     * or with status {@code 404 (Not Found)} if the userAuthorityGroup is not found,
//     * or with status {@code 500 (Internal Server Error)} if the userAuthorityGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/user-authority-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<UserAuthorityGroup> partialUpdateUserAuthorityGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody UserAuthorityGroup userAuthorityGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update UserAuthorityGroup partially : {}, {}", id, userAuthorityGroup);
//        if (userAuthorityGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, userAuthorityGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!userAuthorityGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<UserAuthorityGroup> result = userAuthorityGroupService.partialUpdate(userAuthorityGroup);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAuthorityGroup.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /user-authority-groups} : get all the userAuthorityGroups.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAuthorityGroups in body.
//     */
//    @GetMapping("/user-authority-groups")
//    public List<UserAuthorityGroup> getAllUserAuthorityGroups(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all UserAuthorityGroups");
//        return userAuthorityGroupService.findAll();
//    }
//
//    /**
//     * {@code GET  /user-authority-groups/:id} : get the "id" userAuthorityGroup.
//     *
//     * @param id the id of the userAuthorityGroup to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAuthorityGroup, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/user-authority-groups/{id}")
//    public ResponseEntity<UserAuthorityGroup> getUserAuthorityGroup(@PathVariable Long id) {
//        log.debug("REST request to get UserAuthorityGroup : {}", id);
//        Optional<UserAuthorityGroup> userAuthorityGroup = userAuthorityGroupService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(userAuthorityGroup);
//    }
//
//    /**
//     * {@code DELETE  /user-authority-groups/:id} : delete the "id" userAuthorityGroup.
//     *
//     * @param id the id of the userAuthorityGroup to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/user-authority-groups/{id}")
//    public ResponseEntity<Void> deleteUserAuthorityGroup(@PathVariable Long id) {
//        log.debug("REST request to delete UserAuthorityGroup : {}", id);
//        userAuthorityGroupService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
