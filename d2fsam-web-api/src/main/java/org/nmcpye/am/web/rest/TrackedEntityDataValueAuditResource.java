//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAudit;
//import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditService;
//import org.nmcpye.am.trackedentityattributevalue.TrackedEntityDataValueAuditRepository;
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
// * REST controller for managing {@link org.nmcpye.am.domain.TrackedEntityDataValueAudit}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class TrackedEntityDataValueAuditResource {
//
//    private final Logger log = LoggerFactory.getLogger(TrackedEntityDataValueAuditResource.class);
//
//    private static final String ENTITY_NAME = "trackedEntityDataValueAudit";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final TrackedEntityDataValueAuditService trackedEntityDataValueAuditService;
//
//    private final TrackedEntityDataValueAuditRepository trackedEntityDataValueAuditRepository;
//
//    public TrackedEntityDataValueAuditResource(
//        TrackedEntityDataValueAuditService trackedEntityDataValueAuditService,
//        TrackedEntityDataValueAuditRepository trackedEntityDataValueAuditRepository
//    ) {
//        this.trackedEntityDataValueAuditService = trackedEntityDataValueAuditService;
//        this.trackedEntityDataValueAuditRepository = trackedEntityDataValueAuditRepository;
//    }
//
//    /**
//     * {@code POST  /tracked-entity-data-value-audits} : Create a new trackedEntityDataValueAudit.
//     *
//     * @param trackedEntityDataValueAudit the trackedEntityDataValueAudit to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackedEntityDataValueAudit, or with status {@code 400 (Bad Request)} if the trackedEntityDataValueAudit has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/tracked-entity-data-value-audits")
//    public ResponseEntity<TrackedEntityDataValueAudit> createTrackedEntityDataValueAudit(
//        @Valid @RequestBody TrackedEntityDataValueAudit trackedEntityDataValueAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to save TrackedEntityDataValueAudit : {}", trackedEntityDataValueAudit);
//        if (trackedEntityDataValueAudit.getId() != null) {
//            throw new BadRequestAlertException("A new trackedEntityDataValueAudit cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        TrackedEntityDataValueAudit result = trackedEntityDataValueAuditService.save(trackedEntityDataValueAudit);
//        return ResponseEntity
//            .created(new URI("/api/tracked-entity-data-value-audits/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /tracked-entity-data-value-audits/:id} : Updates an existing trackedEntityDataValueAudit.
//     *
//     * @param id the id of the trackedEntityDataValueAudit to save.
//     * @param trackedEntityDataValueAudit the trackedEntityDataValueAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityDataValueAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityDataValueAudit is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityDataValueAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/tracked-entity-data-value-audits/{id}")
//    public ResponseEntity<TrackedEntityDataValueAudit> updateTrackedEntityDataValueAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody TrackedEntityDataValueAudit trackedEntityDataValueAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to update TrackedEntityDataValueAudit : {}, {}", id, trackedEntityDataValueAudit);
//        if (trackedEntityDataValueAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityDataValueAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityDataValueAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        TrackedEntityDataValueAudit result = trackedEntityDataValueAuditService.update(trackedEntityDataValueAudit);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityDataValueAudit.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /tracked-entity-data-value-audits/:id} : Partial updates given fields of an existing trackedEntityDataValueAudit, field will ignore if it is null
//     *
//     * @param id the id of the trackedEntityDataValueAudit to save.
//     * @param trackedEntityDataValueAudit the trackedEntityDataValueAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackedEntityDataValueAudit,
//     * or with status {@code 400 (Bad Request)} if the trackedEntityDataValueAudit is not valid,
//     * or with status {@code 404 (Not Found)} if the trackedEntityDataValueAudit is not found,
//     * or with status {@code 500 (Internal Server Error)} if the trackedEntityDataValueAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/tracked-entity-data-value-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<TrackedEntityDataValueAudit> partialUpdateTrackedEntityDataValueAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody TrackedEntityDataValueAudit trackedEntityDataValueAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update TrackedEntityDataValueAudit partially : {}, {}", id, trackedEntityDataValueAudit);
//        if (trackedEntityDataValueAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, trackedEntityDataValueAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!trackedEntityDataValueAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<TrackedEntityDataValueAudit> result = trackedEntityDataValueAuditService.partialUpdate(trackedEntityDataValueAudit);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackedEntityDataValueAudit.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /tracked-entity-data-value-audits} : get all the trackedEntityDataValueAudits.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackedEntityDataValueAudits in body.
//     */
//    @GetMapping("/tracked-entity-data-value-audits")
//    public ResponseEntity<List<TrackedEntityDataValueAudit>> getAllTrackedEntityDataValueAudits(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of TrackedEntityDataValueAudits");
//        Page<TrackedEntityDataValueAudit> page;
//        if (eagerload) {
//            page = trackedEntityDataValueAuditService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = trackedEntityDataValueAuditService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /tracked-entity-data-value-audits/:id} : get the "id" trackedEntityDataValueAudit.
//     *
//     * @param id the id of the trackedEntityDataValueAudit to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackedEntityDataValueAudit, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/tracked-entity-data-value-audits/{id}")
//    public ResponseEntity<TrackedEntityDataValueAudit> getTrackedEntityDataValueAudit(@PathVariable Long id) {
//        log.debug("REST request to get TrackedEntityDataValueAudit : {}", id);
//        Optional<TrackedEntityDataValueAudit> trackedEntityDataValueAudit = trackedEntityDataValueAuditService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(trackedEntityDataValueAudit);
//    }
//
//    /**
//     * {@code DELETE  /tracked-entity-data-value-audits/:id} : delete the "id" trackedEntityDataValueAudit.
//     *
//     * @param id the id of the trackedEntityDataValueAudit to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/tracked-entity-data-value-audits/{id}")
//    public ResponseEntity<Void> deleteTrackedEntityDataValueAudit(@PathVariable Long id) {
//        log.debug("REST request to delete TrackedEntityDataValueAudit : {}", id);
//        trackedEntityDataValueAuditService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
