//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
//import org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditRepository;
//import org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditService;
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
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * REST controller for managing {@link TrackedEntityInstanceAudit}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityInstanceAuditResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityInstanceAuditResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityInstanceAudit";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityInstanceAuditService trackedEntityInstanceAuditService;
//
//    private final TrackedEntityInstanceAuditRepository trackedEntityInstanceAuditRepository;
//
//    public TrackedEntityInstanceAuditResource(
//        TrackedEntityInstanceAuditService trackedEntityInstanceAuditService,
//        TrackedEntityInstanceAuditRepository trackedEntityInstanceAuditRepository
//    ) {
//        this.trackedEntityInstanceAuditService = trackedEntityInstanceAuditService;
//        this.trackedEntityInstanceAuditRepository = trackedEntityInstanceAuditRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-instance-audits} : Create a new trackedEntityInstanceAudit.
//     *
//     * @param trackedEntityInstanceAudit the trackedEntityInstanceAudit to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityInstanceAudit, or with status {@code 400 (Bad Request)} if the trackedEntityInstanceAudit has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-instance-audits")
//    public ResponseEntity<TrackedEntityInstanceAudit> createTrackedEntityInstanceAudit(
//        @RequestBody TrackedEntityInstanceAudit trackedEntityInstanceAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityInstanceAudit : {}", trackedEntityInstanceAudit);
//        if (trackedEntityInstanceAudit.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityInstanceAudit cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityInstanceAudit result = trackedEntityInstanceAuditService.save(trackedEntityInstanceAudit);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-instance-audits/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-instance-audits/:id} : Updates an existing trackedEntityInstanceAudit.
//     *
//     * @param id the id of the trackedEntityInstanceAudit to save.
//     * @param trackedEntityInstanceAudit the trackedEntityInstanceAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstanceAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstanceAudit is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstanceAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-instance-audits/{id}")
//    public ResponseEntity<TrackedEntityInstanceAudit> updateTrackedEntityInstanceAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody TrackedEntityInstanceAudit trackedEntityInstanceAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityInstanceAudit : {}, {}", id, trackedEntityInstanceAudit);
//        if (trackedEntityInstanceAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstanceAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityInstanceAudit result = trackedEntityInstanceAuditService.update(trackedEntityInstanceAudit);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstanceAudit.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-instance-audits/:id} : Partial updates given fields of an existing trackedEntityInstanceAudit, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityInstanceAudit to save.
//     * @param trackedEntityInstanceAudit the trackedEntityInstanceAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityInstanceAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityInstanceAudit is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityInstanceAudit is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityInstanceAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-instance-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityInstanceAudit> partialUpdateTrackedEntityInstanceAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody TrackedEntityInstanceAudit trackedEntityInstanceAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityInstanceAudit partially : {}, {}", id, trackedEntityInstanceAudit);
//        if (trackedEntityInstanceAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityInstanceAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityInstanceAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityInstanceAudit> result = trackedEntityInstanceAuditService.partialUpdate(trackedEntityInstanceAudit);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityInstanceAudit.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instance-audits} : get all the trackedEntityInstanceAudits.
//     *
//     * @param pageable the pagination information.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityInstanceAudits in body.
//     */
//    @GetMapping("/tracked-entity-instance-audits")
//    public ResponseEntity<List<TrackedEntityInstanceAudit>> getAllTrackedEntityInstanceAudits(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable
//    ) {
//        log.debug("REST request to get a page of TrackedEntityInstanceAudits");
//        Page<TrackedEntityInstanceAudit> page = trackedEntityInstanceAuditService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /tracked-entity-instance-audits/:id} : get the "id" trackedEntityInstanceAudit.
//     *
//     * @param id the id of the trackedEntityInstanceAudit to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityInstanceAudit, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-instance-audits/{id}")
//    public ResponseEntity<TrackedEntityInstanceAudit> getTrackedEntityInstanceAudit(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityInstanceAudit : {}", id);
//        Optional<TrackedEntityInstanceAudit> trackedEntityInstanceAudit = trackedEntityInstanceAuditService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityInstanceAudit);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-instance-audits/:id} : delete the "id" trackedEntityInstanceAudit.
//     *
//     * @param id the id of the trackedEntityInstanceAudit to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-instance-audits/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityInstanceAudit(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityInstanceAudit : {}", id);
//        trackedEntityInstanceAuditService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
