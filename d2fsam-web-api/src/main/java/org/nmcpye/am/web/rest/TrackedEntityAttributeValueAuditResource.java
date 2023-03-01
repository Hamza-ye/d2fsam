//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAudit;
//import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditRepository;
//import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TrackedEntityAttributeValueAudit}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityAttributeValueAuditResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityAttributeValueAuditResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityAttributeValueAudit";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityAttributeValueAuditService trackedEntityAttributeValueAuditService;
//
//    private final TrackedEntityAttributeValueAuditRepository trackedEntityAttributeValueAuditRepository;
//
//    public TrackedEntityAttributeValueAuditResource(
//        TrackedEntityAttributeValueAuditService trackedEntityAttributeValueAuditService,
//        TrackedEntityAttributeValueAuditRepository trackedEntityAttributeValueAuditRepository
//    ) {
//        this.trackedEntityAttributeValueAuditService = trackedEntityAttributeValueAuditService;
//        this.trackedEntityAttributeValueAuditRepository = trackedEntityAttributeValueAuditRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-attribute-value-audits} : Create a new trackedEntityAttributeValueAudit.
//     *
//     * @param trackedEntityAttributeValueAudit the trackedEntityAttributeValueAudit to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityAttributeValueAudit, or with status {@code 400 (Bad Request)} if the trackedEntityAttributeValueAudit has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-attribute-value-audits")
//    public ResponseEntity<TrackedEntityAttributeValueAudit> createTrackedEntityAttributeValueAudit(
//        @Valid @RequestBody TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityAttributeValueAudit : {}", trackedEntityAttributeValueAudit);
//        if (trackedEntityAttributeValueAudit.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityAttributeValueAudit cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityAttributeValueAudit result = trackedEntityAttributeValueAuditService.save(trackedEntityAttributeValueAudit);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-attribute-value-audits/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-attribute-value-audits/:id} : Updates an existing trackedEntityAttributeValueAudit.
//     *
//     * @param id the id of the trackedEntityAttributeValueAudit to save.
//     * @param trackedEntityAttributeValueAudit the trackedEntityAttributeValueAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityAttributeValueAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityAttributeValueAudit is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityAttributeValueAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-attribute-value-audits/{id}")
//    public ResponseEntity<TrackedEntityAttributeValueAudit> updateTrackedEntityAttributeValueAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityAttributeValueAudit : {}, {}", id, trackedEntityAttributeValueAudit);
//        if (trackedEntityAttributeValueAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityAttributeValueAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityAttributeValueAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityAttributeValueAudit result = trackedEntityAttributeValueAuditService.update(trackedEntityAttributeValueAudit);
//        return ResponseEntity
//            .ok()
//            .headers(
//                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityAttributeValueAudit.getId().toString())
//            )
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-attribute-value-audits/:id} : Partial updates given fields of an existing trackedEntityAttributeValueAudit, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityAttributeValueAudit to save.
//     * @param trackedEntityAttributeValueAudit the trackedEntityAttributeValueAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityAttributeValueAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityAttributeValueAudit is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityAttributeValueAudit is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityAttributeValueAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-attribute-value-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityAttributeValueAudit> partialUpdateTrackedEntityAttributeValueAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit
//    ) throws URISyntaxException {
//        log.debug(
//            "REST request to partial update TrackedEntityAttributeValueAudit partially : {}, {}",
//            id,
//            trackedEntityAttributeValueAudit
//        );
//        if (trackedEntityAttributeValueAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityAttributeValueAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityAttributeValueAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityAttributeValueAudit> result = trackedEntityAttributeValueAuditService.partialUpdate(
//            trackedEntityAttributeValueAudit
//        );
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityAttributeValueAudit.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-attribute-value-audits} : get all the trackedEntityAttributeValueAudits.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityAttributeValueAudits in body.
//     */
//    @GetMapping("/tracked-entity-attribute-value-audits")
//    public List<TrackedEntityAttributeValueAudit> getAllTrackedEntityAttributeValueAudits(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all TrackedEntityAttributeValueAudits");
//        return trackedEntityAttributeValueAuditService.findAll();
//    }
//
//    /**
//     * {@code GET  /tracked-entity-attribute-value-audits/:id} : get the "id" trackedEntityAttributeValueAudit.
//     *
//     * @param id the id of the trackedEntityAttributeValueAudit to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityAttributeValueAudit, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-attribute-value-audits/{id}")
//    public ResponseEntity<TrackedEntityAttributeValueAudit> getTrackedEntityAttributeValueAudit(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityAttributeValueAudit : {}", id);
//        Optional<TrackedEntityAttributeValueAudit> trackedEntityAttributeValueAudit = trackedEntityAttributeValueAuditService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityAttributeValueAudit);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-attribute-value-audits/:id} : delete the "id" trackedEntityAttributeValueAudit.
//     *
//     * @param id the id of the trackedEntityAttributeValueAudit to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-attribute-value-audits/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityAttributeValueAudit(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityAttributeValueAudit : {}", id);
//        trackedEntityAttributeValueAuditService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
