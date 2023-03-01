//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentity.TrackedEntityProgramOwner;
//import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerRepository;
//import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TrackedEntityProgramOwner}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityProgramOwnerResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityProgramOwnerResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityProgramOwner";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityProgramOwnerService trackedEntityProgramOwnerService;
//
//    private final TrackedEntityProgramOwnerRepository trackedEntityProgramOwnerRepository;
//
//    public TrackedEntityProgramOwnerResource(
//        TrackedEntityProgramOwnerService trackedEntityProgramOwnerService,
//        TrackedEntityProgramOwnerRepository trackedEntityProgramOwnerRepository
//    ) {
//        this.trackedEntityProgramOwnerService = trackedEntityProgramOwnerService;
//        this.trackedEntityProgramOwnerRepository = trackedEntityProgramOwnerRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-program-owners} : Create a new trackedEntityProgramOwner.
//     *
//     * @param trackedEntityProgramOwner the trackedEntityProgramOwner to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityProgramOwner, or with status {@code 400 (Bad Request)} if the trackedEntityProgramOwner has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-program-owners")
//    public ResponseEntity<TrackedEntityProgramOwner> createTrackedEntityProgramOwner(
//        @Valid @RequestBody TrackedEntityProgramOwner trackedEntityProgramOwner
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityProgramOwner : {}", trackedEntityProgramOwner);
//        if (trackedEntityProgramOwner.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityProgramOwner cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityProgramOwner result = trackedEntityProgramOwnerService.save(trackedEntityProgramOwner);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-program-owners/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-program-owners/:id} : Updates an existing trackedEntityProgramOwner.
//     *
//     * @param id the id of the trackedEntityProgramOwner to save.
//     * @param trackedEntityProgramOwner the trackedEntityProgramOwner to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityProgramOwner,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityProgramOwner is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityProgramOwner couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-program-owners/{id}")
//    public ResponseEntity<TrackedEntityProgramOwner> updateTrackedEntityProgramOwner(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityProgramOwner trackedEntityProgramOwner
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityProgramOwner : {}, {}", id, trackedEntityProgramOwner);
//        if (trackedEntityProgramOwner.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityProgramOwner.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityProgramOwnerRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityProgramOwner result = trackedEntityProgramOwnerService.update(trackedEntityProgramOwner);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityProgramOwner.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-program-owners/:id} : Partial updates given fields of an existing trackedEntityProgramOwner, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityProgramOwner to save.
//     * @param trackedEntityProgramOwner the trackedEntityProgramOwner to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityProgramOwner,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityProgramOwner is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityProgramOwner is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityProgramOwner couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-program-owners/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityProgramOwner> partialUpdateTrackedEntityProgramOwner(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityProgramOwner trackedEntityProgramOwner
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityProgramOwner partially : {}, {}", id, trackedEntityProgramOwner);
//        if (trackedEntityProgramOwner.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityProgramOwner.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityProgramOwnerRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityProgramOwner> result = trackedEntityProgramOwnerService.partialUpdate(trackedEntityProgramOwner);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityProgramOwner.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-program-owners} : get all the trackedEntityProgramOwners.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityProgramOwners in body.
//     */
//    @GetMapping("/tracked-entity-program-owners")
//    public ResponseEntity<List<TrackedEntityProgramOwner>> getAllTrackedEntityProgramOwners(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of TrackedEntityProgramOwners");
//        Page<TrackedEntityProgramOwner> page;
//        if (eagerload) {
//            page = trackedEntityProgramOwnerService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = trackedEntityProgramOwnerService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /tracked-entity-program-owners/:id} : get the "id" trackedEntityProgramOwner.
//     *
//     * @param id the id of the trackedEntityProgramOwner to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityProgramOwner, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-program-owners/{id}")
//    public ResponseEntity<TrackedEntityProgramOwner> getTrackedEntityProgramOwner(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityProgramOwner : {}", id);
//        Optional<TrackedEntityProgramOwner> trackedEntityProgramOwner = trackedEntityProgramOwnerService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityProgramOwner);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-program-owners/:id} : delete the "id" trackedEntityProgramOwner.
//     *
//     * @param id the id of the trackedEntityProgramOwner to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-program-owners/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityProgramOwner(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityProgramOwner : {}", id);
//        trackedEntityProgramOwnerService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
