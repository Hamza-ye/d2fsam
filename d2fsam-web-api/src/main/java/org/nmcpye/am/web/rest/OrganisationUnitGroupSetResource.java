//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupSetRepository;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupSetService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.OrganisationUnitGroupSet}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class OrganisationUnitGroupSetResource {
//
//    private final Logger log = LoggerFactory.getLogger(OrganisationUnitGroupSetResource.class);
//
//    private static final String ENTITY_NAME = "organisationUnitGroupSet";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final OrganisationUnitGroupSetService organisationUnitGroupSetService;
//
//    private final OrganisationUnitGroupSetRepository organisationUnitGroupSetRepository;
//
//    public OrganisationUnitGroupSetResource(
//        OrganisationUnitGroupSetService organisationUnitGroupSetService,
//        OrganisationUnitGroupSetRepository organisationUnitGroupSetRepository
//    ) {
//        this.organisationUnitGroupSetService = organisationUnitGroupSetService;
//        this.organisationUnitGroupSetRepository = organisationUnitGroupSetRepository;
//    }
//
//    /**
//     * {@code POST  /organisation-unit-group-sets} : Create a new organisationUnitGroupSet.
//     *
//     * @param organisationUnitGroupSet the organisationUnitGroupSet to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organisationUnitGroupSet, or with status {@code 400 (Bad Request)} if the organisationUnitGroupSet has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/organisation-unit-group-sets")
//    public ResponseEntity<OrganisationUnitGroupSet> createOrganisationUnitGroupSet(
//        @Valid @RequestBody OrganisationUnitGroupSet organisationUnitGroupSet
//    ) throws URISyntaxException {
//        log.debug("REST request to save OrganisationUnitGroupSet : {}", organisationUnitGroupSet);
//        if (organisationUnitGroupSet.getId() != null) {
//            throw new BadRequestAlertException("A new organisationUnitGroupSet cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        OrganisationUnitGroupSet result = organisationUnitGroupSetService.save(organisationUnitGroupSet);
//        return ResponseEntity
//            .created(new URI("/api/organisation-unit-group-sets/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /organisation-unit-group-sets/:id} : Updates an existing organisationUnitGroupSet.
//     *
//     * @param id the id of the organisationUnitGroupSet to save.
//     * @param organisationUnitGroupSet the organisationUnitGroupSet to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitGroupSet,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitGroupSet is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitGroupSet couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/organisation-unit-group-sets/{id}")
//    public ResponseEntity<OrganisationUnitGroupSet> updateOrganisationUnitGroupSet(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody OrganisationUnitGroupSet organisationUnitGroupSet
//    ) throws URISyntaxException {
//        log.debug("REST request to update OrganisationUnitGroupSet : {}, {}", id, organisationUnitGroupSet);
//        if (organisationUnitGroupSet.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitGroupSet.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitGroupSetRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        OrganisationUnitGroupSet result = organisationUnitGroupSetService.update(organisationUnitGroupSet);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitGroupSet.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /organisation-unit-group-sets/:id} : Partial updates given fields of an existing organisationUnitGroupSet, field will ignore if it is null
//     *
//     * @param id the id of the organisationUnitGroupSet to save.
//     * @param organisationUnitGroupSet the organisationUnitGroupSet to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisationUnitGroupSet,
//     * or with status {@code 400 (Bad Request)} if the organisationUnitGroupSet is not valid,
//     * or with status {@code 404 (Not Found)} if the organisationUnitGroupSet is not found,
//     * or with status {@code 500 (Internal Server Error)} if the organisationUnitGroupSet couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/organisation-unit-group-sets/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<OrganisationUnitGroupSet> partialUpdateOrganisationUnitGroupSet(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody OrganisationUnitGroupSet organisationUnitGroupSet
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update OrganisationUnitGroupSet partially : {}, {}", id, organisationUnitGroupSet);
//        if (organisationUnitGroupSet.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, organisationUnitGroupSet.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!organisationUnitGroupSetRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<OrganisationUnitGroupSet> result = organisationUnitGroupSetService.partialUpdate(organisationUnitGroupSet);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisationUnitGroupSet.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /organisation-unit-group-sets} : get all the organisationUnitGroupSets.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organisationUnitGroupSets in body.
//     */
//    @GetMapping("/organisation-unit-group-sets")
//    public ResponseEntity<List<OrganisationUnitGroupSet>> getAllOrganisationUnitGroupSets(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of OrganisationUnitGroupSets");
//        Page<OrganisationUnitGroupSet> page;
//        if (eagerload) {
//            page = organisationUnitGroupSetService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = organisationUnitGroupSetService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /organisation-unit-group-sets/:id} : get the "id" organisationUnitGroupSet.
//     *
//     * @param id the id of the organisationUnitGroupSet to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organisationUnitGroupSet, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/organisation-unit-group-sets/{id}")
//    public ResponseEntity<OrganisationUnitGroupSet> getOrganisationUnitGroupSet(@PathVariable Long id) {
//        log.debug("REST request to get OrganisationUnitGroupSet : {}", id);
//        Optional<OrganisationUnitGroupSet> organisationUnitGroupSet = organisationUnitGroupSetService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(organisationUnitGroupSet);
//    }
//
//    /**
//     * {@code DELETE  /organisation-unit-group-sets/:id} : delete the "id" organisationUnitGroupSet.
//     *
//     * @param id the id of the organisationUnitGroupSet to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/organisation-unit-group-sets/{id}")
//    public ResponseEntity<Void> deleteOrganisationUnitGroupSet(@PathVariable Long id) {
//        log.debug("REST request to delete OrganisationUnitGroupSet : {}", id);
//        organisationUnitGroupSetService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
