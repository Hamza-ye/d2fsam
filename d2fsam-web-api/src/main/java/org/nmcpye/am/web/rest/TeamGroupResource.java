//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.team.TeamGroup;
//import org.nmcpye.am.team.TeamGroupRepository;
//import org.nmcpye.am.team.TeamGroupService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TeamGroup}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TeamGroupResource {
//
//    private final Logger log = LoggerFactory.getLogger(TeamGroupResource.class);
//
//    private static final String ENTITY_NAME = "teamGroup";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TeamGroupService teamGroupService;
//
//    private final TeamGroupRepository teamGroupRepository;
//
//    public TeamGroupResource(TeamGroupService teamGroupService, TeamGroupRepository teamGroupRepository) {
//        this.teamGroupService = teamGroupService;
//        this.teamGroupRepository = teamGroupRepository;
//    }
//
//    /**
//     * {@code POST  /team-groups} : Create a new teamGroup.
//     *
//     * @param teamGroup the teamGroup to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamGroup, or with status {@code 400 (Bad Request)} if the teamGroup has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/team-groups")
//    public ResponseEntity<TeamGroup> createTeamGroup(@Valid @RequestBody TeamGroup teamGroup) throws URISyntaxException {
//        log.debug("REST request to save TeamGroup : {}", teamGroup);
//        if (teamGroup.getId() != null) {
//            throw new BadRequestAlertException("A new teamGroup cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TeamGroup result = teamGroupService.save(teamGroup);
//        return ResponseEntity
//            .created(new URI("/api/team-groups/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /team-groups/:id} : Updates an existing teamGroup.
//     *
//     * @param id the id of the teamGroup to save.
//     * @param teamGroup the teamGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamGroup,
//     * or with status {@code 400 (Bad Request)} if the teamGroup is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the teamGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/team-groups/{id}")
//    public ResponseEntity<TeamGroup> updateTeamGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TeamGroup teamGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to update TeamGroup : {}, {}", id, teamGroup);
//        if (teamGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, teamGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!teamGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TeamGroup result = teamGroupService.update(teamGroup);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamGroup.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /team-groups/:id} : Partial updates given fields of an existing teamGroup, field will ignore if it is null
//     *
//     * @param id the id of the teamGroup to save.
//     * @param teamGroup the teamGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamGroup,
//     * or with status {@code 400 (Bad Request)} if the teamGroup is not valid,
//     * or with status {@code 404 (Not Found)} if the teamGroup is not found,
//     * or with status {@code 500 (Internal Server Error)} if the teamGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/team-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TeamGroup> partialUpdateTeamGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TeamGroup teamGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TeamGroup partially : {}, {}", id, teamGroup);
//        if (teamGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, teamGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!teamGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TeamGroup> result = teamGroupService.partialUpdate(teamGroup);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamGroup.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /team-groups} : get all the teamGroups.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamGroups in body.
//     */
//    @GetMapping("/team-groups")
//    public List<TeamGroup> getAllTeamGroups(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all TeamGroups");
//        return teamGroupService.findAll();
//    }
//
//    /**
//     * {@code GET  /team-groups/:id} : get the "id" teamGroup.
//     *
//     * @param id the id of the teamGroup to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamGroup, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/team-groups/{id}")
//    public ResponseEntity<TeamGroup> getTeamGroup(@PathVariable Long id) {
//        log.debug("REST request to get TeamGroup : {}", id);
//        Optional<TeamGroup> teamGroup = teamGroupService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(teamGroup);
//    }
//
//    /**
//     * {@code DELETE  /team-groups/:id} : delete the "id" teamGroup.
//     *
//     * @param id the id of the teamGroup to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/team-groups/{id}")
//    public ResponseEntity<Void> deleteTeamGroup(@PathVariable Long id) {
//        log.debug("REST request to delete TeamGroup : {}", id);
//        teamGroupService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
