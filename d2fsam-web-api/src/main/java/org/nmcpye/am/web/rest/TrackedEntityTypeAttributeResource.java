//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentity.TrackedEntityTypeAttribute;
//import org.nmcpye.am.trackedentity.TrackedEntityTypeAttributeRepository;
//import org.nmcpye.am.trackedentity.TrackedEntityTypeAttributeService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TrackedEntityTypeAttribute}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityTypeAttributeResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityTypeAttributeResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityTypeAttribute";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityTypeAttributeService trackedEntityTypeAttributeService;
//
//    private final TrackedEntityTypeAttributeRepository trackedEntityTypeAttributeRepository;
//
//    public TrackedEntityTypeAttributeResource(
//        TrackedEntityTypeAttributeService trackedEntityTypeAttributeService,
//        TrackedEntityTypeAttributeRepository trackedEntityTypeAttributeRepository
//    ) {
//        this.trackedEntityTypeAttributeService = trackedEntityTypeAttributeService;
//        this.trackedEntityTypeAttributeRepository = trackedEntityTypeAttributeRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-type-attributes} : Create a new trackedEntityTypeAttribute.
//     *
//     * @param trackedEntityTypeAttribute the trackedEntityTypeAttribute to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityTypeAttribute, or with status {@code 400 (Bad Request)} if the trackedEntityTypeAttribute has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-type-attributes")
//    public ResponseEntity<TrackedEntityTypeAttribute> createTrackedEntityTypeAttribute(
//        @Valid @RequestBody TrackedEntityTypeAttribute trackedEntityTypeAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityTypeAttribute : {}", trackedEntityTypeAttribute);
//        if (trackedEntityTypeAttribute.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityTypeAttribute cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityTypeAttribute result = trackedEntityTypeAttributeService.save(trackedEntityTypeAttribute);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-type-attributes/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-type-attributes/:id} : Updates an existing trackedEntityTypeAttribute.
//     *
//     * @param id the id of the trackedEntityTypeAttribute to save.
//     * @param trackedEntityTypeAttribute the trackedEntityTypeAttribute to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityTypeAttribute,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityTypeAttribute is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityTypeAttribute couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-type-attributes/{id}")
//    public ResponseEntity<TrackedEntityTypeAttribute> updateTrackedEntityTypeAttribute(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityTypeAttribute trackedEntityTypeAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityTypeAttribute : {}, {}", id, trackedEntityTypeAttribute);
//        if (trackedEntityTypeAttribute.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityTypeAttribute.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityTypeAttributeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityTypeAttribute result = trackedEntityTypeAttributeService.update(trackedEntityTypeAttribute);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityTypeAttribute.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-type-attributes/:id} : Partial updates given fields of an existing trackedEntityTypeAttribute, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityTypeAttribute to save.
//     * @param trackedEntityTypeAttribute the trackedEntityTypeAttribute to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityTypeAttribute,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityTypeAttribute is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityTypeAttribute is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityTypeAttribute couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-type-attributes/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityTypeAttribute> partialUpdateTrackedEntityTypeAttribute(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityTypeAttribute trackedEntityTypeAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityTypeAttribute partially : {}, {}", id, trackedEntityTypeAttribute);
//        if (trackedEntityTypeAttribute.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityTypeAttribute.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityTypeAttributeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityTypeAttribute> result = trackedEntityTypeAttributeService.partialUpdate(trackedEntityTypeAttribute);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityTypeAttribute.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-type-attributes} : get all the trackedEntityTypeAttributes.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityTypeAttributes in body.
//     */
//    @GetMapping("/tracked-entity-type-attributes")
//    public List<TrackedEntityTypeAttribute> getAllTrackedEntityTypeAttributes(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all TrackedEntityTypeAttributes");
//        return trackedEntityTypeAttributeService.findAll();
//    }
//
//    /**
//     * {@code GET  /tracked-entity-type-attributes/:id} : get the "id" trackedEntityTypeAttribute.
//     *
//     * @param id the id of the trackedEntityTypeAttribute to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityTypeAttribute, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-type-attributes/{id}")
//    public ResponseEntity<TrackedEntityTypeAttribute> getTrackedEntityTypeAttribute(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityTypeAttribute : {}", id);
//        Optional<TrackedEntityTypeAttribute> trackedEntityTypeAttribute = trackedEntityTypeAttributeService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityTypeAttribute);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-type-attributes/:id} : delete the "id" trackedEntityTypeAttribute.
//     *
//     * @param id the id of the trackedEntityTypeAttribute to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-type-attributes/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityTypeAttribute(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityTypeAttribute : {}", id);
//        trackedEntityTypeAttributeService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
