//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilter;
//import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilterRepository;
//import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilterService;
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
// * REST controller for managing {@link TrackedEntityInstanceFilter}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityInstanceFilterResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityInstanceFilterResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityInstanceFilter";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityInstanceFilterService trackedEntityInstanceFilterService;
//
//    private final TrackedEntityInstanceFilterRepository trackedEntityInstanceFilterRepository;
//
//    public TrackedEntityInstanceFilterResource(
//        TrackedEntityInstanceFilterService trackedEntityInstanceFilterService,
//        TrackedEntityInstanceFilterRepository trackedEntityInstanceFilterRepository
//    ) {
//        this.trackedEntityInstanceFilterService = trackedEntityInstanceFilterService;
//        this.trackedEntityInstanceFilterRepository = trackedEntityInstanceFilterRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-instance-filters} : Create a new trackedEntityInstanceFilter.
//     *
//     * @param trackedEntityInstanceFilter the trackedEntityInstanceFilter to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityInstanceFilter, or with status {@code 400 (Bad Request)} if the trackedEntityInstanceFilter has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-instance-filters")
//    public ResponseEntity<TrackedEntityInstanceFilter> createTrackedEntityInstanceFilter(
//        @Valid @RequestBody TrackedEntityInstanceFilter trackedEntityInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityInstanceFilter : {}", trackedEntityInstanceFilter);
//        if (trackedEntityInstanceFilter.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityInstanceFilter cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityInstanceFilter result = trackedEntityInstanceFilterService.save(trackedEntityInstanceFilter);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-instance-filters/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-instance-filters/:id} : Updates an existing trackedEntityInstanceFilter.
//     *
//     * @param id the id of the trackedEntityInstanceFilter to save.
//     * @param trackedEntityInstanceFilter the trackedEntityInstanceFilter to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstanceFilter,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstanceFilter is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstanceFilter couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-instance-filters/{id}")
//    public ResponseEntity<TrackedEntityInstanceFilter> updateTrackedEntityInstanceFilter(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityInstanceFilter trackedEntityInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityInstanceFilter : {}, {}", id, trackedEntityInstanceFilter);
//        if (trackedEntityInstanceFilter.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstanceFilter.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceFilterRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityInstanceFilter result = trackedEntityInstanceFilterService.update(trackedEntityInstanceFilter);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstanceFilter.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-instance-filters/:id} : Partial updates given fields of an existing trackedEntityInstanceFilter, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityInstanceFilter to save.
//     * @param trackedEntityInstanceFilter the trackedEntityInstanceFilter to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstanceFilter,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstanceFilter is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityInstanceFilter is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstanceFilter couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-instance-filters/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityInstanceFilter> partialUpdateTrackedEntityInstanceFilter(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityInstanceFilter trackedEntityInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityInstanceFilter partially : {}, {}", id, trackedEntityInstanceFilter);
//        if (trackedEntityInstanceFilter.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstanceFilter.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceFilterRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityInstanceFilter> result = trackedEntityInstanceFilterService.partialUpdate(trackedEntityInstanceFilter);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstanceFilter.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instance-filters} : get all the trackedEntityInstanceFilters.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityInstanceFilters in body.
//     */
//    @GetMapping("/tracked-entity-instance-filters")
//    public List<TrackedEntityInstanceFilter> getAllTrackedEntityInstanceFilters(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all TrackedEntityInstanceFilters");
//        return trackedEntityInstanceFilterService.findAll();
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instance-filters/:id} : get the "id" trackedEntityInstanceFilter.
//     *
//     * @param id the id of the trackedEntityInstanceFilter to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityInstanceFilter, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-instance-filters/{id}")
//    public ResponseEntity<TrackedEntityInstanceFilter> getTrackedEntityInstanceFilter(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityInstanceFilter : {}", id);
//        Optional<TrackedEntityInstanceFilter> trackedEntityInstanceFilter = trackedEntityInstanceFilterService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityInstanceFilter);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-instance-filters/:id} : delete the "id" trackedEntityInstanceFilter.
//     *
//     * @param id the id of the trackedEntityInstanceFilter to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-instance-filters/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityInstanceFilter(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityInstanceFilter : {}", id);
//        trackedEntityInstanceFilterService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
