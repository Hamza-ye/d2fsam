//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentity.TrackedEntityType;
//import org.nmcpye.am.trackedentity.TrackedEntityTypeRepository;
//import org.nmcpye.am.trackedentity.TrackedEntityTypeService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TrackedEntityType}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityTypeResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityTypeResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityType";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityTypeService trackedEntityTypeService;
//
//    private final TrackedEntityTypeRepository trackedEntityTypeRepository;
//
//    public TrackedEntityTypeResource(
//        TrackedEntityTypeService trackedEntityTypeService,
//        TrackedEntityTypeRepository trackedEntityTypeRepository
//    ) {
//        this.trackedEntityTypeService = trackedEntityTypeService;
//        this.trackedEntityTypeRepository = trackedEntityTypeRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-types} : Create a new trackedEntityType.
//     *
//     * @param trackedEntityType the trackedEntityType to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityType, or with status {@code 400 (Bad Request)} if the trackedEntityType has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-types")
//    public ResponseEntity<TrackedEntityType> createTrackedEntityType(@Valid @RequestBody TrackedEntityType trackedEntityType)
//        throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityType : {}", trackedEntityType);
//        if (trackedEntityType.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityType cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityType result = trackedEntityTypeService.save(trackedEntityType);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-types/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-types/:id} : Updates an existing trackedEntityType.
//     *
//     * @param id the id of the trackedEntityType to save.
//     * @param trackedEntityType the trackedEntityType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityType,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityType is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-types/{id}")
//    public ResponseEntity<TrackedEntityType> updateTrackedEntityType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityType trackedEntityType
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityType : {}, {}", id, trackedEntityType);
//        if (trackedEntityType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityType result = trackedEntityTypeService.update(trackedEntityType);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityType.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-types/:id} : Partial updates given fields of an existing trackedEntityType, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityType to save.
//     * @param trackedEntityType the trackedEntityType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityType,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityType is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityType is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityType> partialUpdateTrackedEntityType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityType trackedEntityType
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityType partially : {}, {}", id, trackedEntityType);
//        if (trackedEntityType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityType> result = trackedEntityTypeService.partialUpdate(trackedEntityType);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityType.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-types} : get all the trackedEntityTypes.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityTypes in body.
//     */
//    @GetMapping("/tracked-entity-types")
//    public ResponseEntity<List<TrackedEntityType>> getAllTrackedEntityTypes(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of TrackedEntityTypes");
//        Page<TrackedEntityType> page;
//        if (eagerload) {
//            page = trackedEntityTypeService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = trackedEntityTypeService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /tracked-entity-types/:id} : get the "id" trackedEntityType.
//     *
//     * @param id the id of the trackedEntityType to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityType, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-types/{id}")
//    public ResponseEntity<TrackedEntityType> getTrackedEntityType(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityType : {}", id);
//        Optional<TrackedEntityType> trackedEntityType = trackedEntityTypeService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityType);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-types/:id} : delete the "id" trackedEntityType.
//     *
//     * @param id the id of the trackedEntityType to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-types/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityType(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityType : {}", id);
//        trackedEntityTypeService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
