//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentity.TrackedEntityInstance;
//import org.nmcpye.am.trackedentity.TrackedEntityInstanceRepository;
//import org.nmcpye.am.trackedentity.TrackedEntityInstanceService;
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
// * REST controller for managing {@link org.nmcpye.am.trackedentity.TrackedEntityInstance}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityInstanceResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityInstanceResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityInstance";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityInstanceService trackedEntityInstanceService;
//
//    private final TrackedEntityInstanceRepository trackedEntityInstanceRepository;
//
//    public TrackedEntityInstanceResource(
//        TrackedEntityInstanceService trackedEntityInstanceService,
//        TrackedEntityInstanceRepository trackedEntityInstanceRepository
//    ) {
//        this.trackedEntityInstanceService = trackedEntityInstanceService;
//        this.trackedEntityInstanceRepository = trackedEntityInstanceRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-instances} : Create a new trackedEntityInstance.
//     *
//     * @param trackedEntityInstance the trackedEntityInstance to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityInstance, or with status {@code 400 (Bad Request)} if the trackedEntityInstance has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-instances")
//    public ResponseEntity<TrackedEntityInstance> createTrackedEntityInstance(
//        @Valid @RequestBody TrackedEntityInstance trackedEntityInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityInstance : {}", trackedEntityInstance);
//        if (trackedEntityInstance.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityInstance cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityInstance result = trackedEntityInstanceService.save(trackedEntityInstance);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-instances/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-instances/:id} : Updates an existing trackedEntityInstance.
//     *
//     * @param id the id of the trackedEntityInstance to save.
//     * @param trackedEntityInstance the trackedEntityInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstance,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstance is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-instances/{id}")
//    public ResponseEntity<TrackedEntityInstance> updateTrackedEntityInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityInstance trackedEntityInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityInstance : {}, {}", id, trackedEntityInstance);
//        if (trackedEntityInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityInstance result = trackedEntityInstanceService.update(trackedEntityInstance);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstance.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-instances/:id} : Partial updates given fields of an existing trackedEntityInstance, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityInstance to save.
//     * @param trackedEntityInstance the trackedEntityInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstance,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstance is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityInstance is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-instances/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityInstance> partialUpdateTrackedEntityInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityInstance trackedEntityInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityInstance partially : {}, {}", id, trackedEntityInstance);
//        if (trackedEntityInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityInstance> result = trackedEntityInstanceService.partialUpdate(trackedEntityInstance);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstance.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instances} : get all the trackedEntityInstances.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityInstances in body.
//     */
//    @GetMapping("/tracked-entity-instances")
//    public ResponseEntity<List<TrackedEntityInstance>> getAllTrackedEntityInstances(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of TrackedEntityInstances");
//        Page<TrackedEntityInstance> page;
//        if (eagerload) {
//            page = trackedEntityInstanceService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = trackedEntityInstanceService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instances/:id} : get the "id" trackedEntityInstance.
//     *
//     * @param id the id of the trackedEntityInstance to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityInstance, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-instances/{id}")
//    public ResponseEntity<TrackedEntityInstance> getTrackedEntityInstance(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityInstance : {}", id);
//        Optional<TrackedEntityInstance> trackedEntityInstance = trackedEntityInstanceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityInstance);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-instances/:id} : delete the "id" trackedEntityInstance.
//     *
//     * @param id the id of the trackedEntityInstance to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-instances/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityInstance(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityInstance : {}", id);
//        trackedEntityInstanceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
