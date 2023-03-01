//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroup;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupRepository;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.OrganisationUnitGroup}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class OrganisationUnitGroupResource {
//
//    private final Logger log = LoggerFactory.getLogger(OrganisationUnitGroupResource.class);
//
//    private static final String ENTITY_NAME = "organisationUnitGroup";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final OrganisationUnitGroupService organisationUnitGroupService;
//
//    private final OrganisationUnitGroupRepository organisationUnitGroupRepository;
//
//    public OrganisationUnitGroupResource(
//        OrganisationUnitGroupService organisationUnitGroupService,
//        OrganisationUnitGroupRepository organisationUnitGroupRepository
//    ) {
//        this.organisationUnitGroupService = organisationUnitGroupService;
//        this.organisationUnitGroupRepository = organisationUnitGroupRepository;
//    }
//
//    /**
//     * {@code POST  /organisation-unit-groups} : Create a new organisationUnitGroup.
//     *
//     * @param organisationUnitGroup the organisationUnitGroup to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organisationUnitGroup, or with status {@code 400 (Bad Request)} if the organisationUnitGroup has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/organisation-unit-groups")
//    public ResponseEntity<OrganisationUnitGroup> createOrganisationUnitGroup(
//        @Valid @RequestBody OrganisationUnitGroup organisationUnitGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to save OrganisationUnitGroup : {}", organisationUnitGroup);
//        if (organisationUnitGroup.getId() != null) {
//            throw new BadRequestAlertException("A new organisationUnitGroup cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        OrganisationUnitGroup result = organisationUnitGroupService.save(organisationUnitGroup);
//        return ResponseEntity
//            .created(new URI("/api/organisation-unit-groups/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /organisation-unit-groups/:id} : Updates an existing organisationUnitGroup.
//     *
//     * @param id the id of the organisationUnitGroup to save.
//     * @param organisationUnitGroup the organisationUnitGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitGroup,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitGroup is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/organisation-unit-groups/{id}")
//    public ResponseEntity<OrganisationUnitGroup> updateOrganisationUnitGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody OrganisationUnitGroup organisationUnitGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to update OrganisationUnitGroup : {}, {}", id, organisationUnitGroup);
//        if (organisationUnitGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        OrganisationUnitGroup result = organisationUnitGroupService.update(organisationUnitGroup);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitGroup.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /organisation-unit-groups/:id} : Partial updates given fields of an existing organisationUnitGroup, field will ignore if it is null
//     *
//     * @param id the id of the organisationUnitGroup to save.
//     * @param organisationUnitGroup the organisationUnitGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitGroup,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitGroup is not valid,
//     * or with status {@code 404 (Not Found)} if the organisationUnitGroup is not found,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/organisation-unit-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<OrganisationUnitGroup> partialUpdateOrganisationUnitGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody OrganisationUnitGroup organisationUnitGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update OrganisationUnitGroup partially : {}, {}", id, organisationUnitGroup);
//        if (organisationUnitGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<OrganisationUnitGroup> result = organisationUnitGroupService.partialUpdate(organisationUnitGroup);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitGroup.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /organisation-unit-groups} : get all the organisationUnitGroups.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organisationUnitGroups in body.
//     */
//    @GetMapping("/organisation-unit-groups")
//    public ResponseEntity<List<OrganisationUnitGroup>> getAllOrganisationUnitGroups(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of OrganisationUnitGroups");
//        Page<OrganisationUnitGroup> page;
//        if (eagerload) {
//            page = organisationUnitGroupService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = organisationUnitGroupService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /organisation-unit-groups/:id} : get the "id" organisationUnitGroup.
//     *
//     * @param id the id of the organisationUnitGroup to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organisationUnitGroup, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/organisation-unit-groups/{id}")
//    public ResponseEntity<OrganisationUnitGroup> getOrganisationUnitGroup(@PathVariable Long id) {
//        log.debug("REST request to get OrganisationUnitGroup : {}", id);
//        Optional<OrganisationUnitGroup> organisationUnitGroup = organisationUnitGroupService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(organisationUnitGroup);
//    }
//
//    /**
//     * {@code DELETE  /organisation-unit-groups/:id} : delete the "id" organisationUnitGroup.
//     *
//     * @param id the id of the organisationUnitGroup to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/organisation-unit-groups/{id}")
//    public ResponseEntity<Void> deleteOrganisationUnitGroup(@PathVariable Long id) {
//        log.debug("REST request to delete OrganisationUnitGroup : {}", id);
//        organisationUnitGroupService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
