//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.organisationunit.OrganisationUnitLevel;
//import org.nmcpye.am.organisationunit.OrganisationUnitLevelRepository;
//import org.nmcpye.am.organisationunit.OrganisationUnitLevelService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.OrganisationUnitLevel}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class OrganisationUnitLevelResource {
//
//    private final Logger log = LoggerFactory.getLogger(OrganisationUnitLevelResource.class);
//
//    private static final String ENTITY_NAME = "organisationUnitLevel";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final OrganisationUnitLevelService organisationUnitLevelService;
//
//    private final OrganisationUnitLevelRepository organisationUnitLevelRepository;
//
//    public OrganisationUnitLevelResource(
//        OrganisationUnitLevelService organisationUnitLevelService,
//        OrganisationUnitLevelRepository organisationUnitLevelRepository
//    ) {
//        this.organisationUnitLevelService = organisationUnitLevelService;
//        this.organisationUnitLevelRepository = organisationUnitLevelRepository;
//    }
//
//    /**
//     * {@code POST  /organisation-unit-levels} : Create a new organisationUnitLevel.
//     *
//     * @param organisationUnitLevel the organisationUnitLevel to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organisationUnitLevel, or with status {@code 400 (Bad Request)} if the organisationUnitLevel has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/organisation-unit-levels")
//    public ResponseEntity<OrganisationUnitLevel> createOrganisationUnitLevel(
//        @Valid @RequestBody OrganisationUnitLevel organisationUnitLevel
//    ) throws URISyntaxException {
//        log.debug("REST request to save OrganisationUnitLevel : {}", organisationUnitLevel);
//        if (organisationUnitLevel.getId() != null) {
//            throw new BadRequestAlertException("A new organisationUnitLevel cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        OrganisationUnitLevel result = organisationUnitLevelService.save(organisationUnitLevel);
//        return ResponseEntity
//            .created(new URI("/api/organisation-unit-levels/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /organisation-unit-levels/:id} : Updates an existing organisationUnitLevel.
//     *
//     * @param id the id of the organisationUnitLevel to save.
//     * @param organisationUnitLevel the organisationUnitLevel to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitLevel,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitLevel is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitLevel couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/organisation-unit-levels/{id}")
//    public ResponseEntity<OrganisationUnitLevel> updateOrganisationUnitLevel(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody OrganisationUnitLevel organisationUnitLevel
//    ) throws URISyntaxException {
//        log.debug("REST request to update OrganisationUnitLevel : {}, {}", id, organisationUnitLevel);
//        if (organisationUnitLevel.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitLevel.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitLevelRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        OrganisationUnitLevel result = organisationUnitLevelService.update(organisationUnitLevel);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitLevel.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /organisation-unit-levels/:id} : Partial updates given fields of an existing organisationUnitLevel, field will ignore if it is null
//     *
//     * @param id the id of the organisationUnitLevel to save.
//     * @param organisationUnitLevel the organisationUnitLevel to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitLevel,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitLevel is not valid,
//     * or with status {@code 404 (Not Found)} if the organisationUnitLevel is not found,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitLevel couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/organisation-unit-levels/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<OrganisationUnitLevel> partialUpdateOrganisationUnitLevel(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody OrganisationUnitLevel organisationUnitLevel
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update OrganisationUnitLevel partially : {}, {}", id, organisationUnitLevel);
//        if (organisationUnitLevel.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitLevel.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitLevelRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<OrganisationUnitLevel> result = organisationUnitLevelService.partialUpdate(organisationUnitLevel);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitLevel.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /organisation-unit-levels} : get all the organisationUnitLevels.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organisationUnitLevels in body.
//     */
//    @GetMapping("/organisation-unit-levels")
//    public List<OrganisationUnitLevel> getAllOrganisationUnitLevels() {
//        log.debug("REST request to get all OrganisationUnitLevels");
//        return organisationUnitLevelService.findAll();
//    }
//
//    /**
//     * {@code GET  /organisation-unit-levels/:id} : get the "id" organisationUnitLevel.
//     *
//     * @param id the id of the organisationUnitLevel to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organisationUnitLevel, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/organisation-unit-levels/{id}")
//    public ResponseEntity<OrganisationUnitLevel> getOrganisationUnitLevel(@PathVariable Long id) {
//        log.debug("REST request to get OrganisationUnitLevel : {}", id);
//        Optional<OrganisationUnitLevel> organisationUnitLevel = organisationUnitLevelService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(organisationUnitLevel);
//    }
//
//    /**
//     * {@code DELETE  /organisation-unit-levels/:id} : delete the "id" organisationUnitLevel.
//     *
//     * @param id the id of the organisationUnitLevel to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/organisation-unit-levels/{id}")
//    public ResponseEntity<Void> deleteOrganisationUnitLevel(@PathVariable Long id) {
//        log.debug("REST request to delete OrganisationUnitLevel : {}", id);
//        organisationUnitLevelService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
