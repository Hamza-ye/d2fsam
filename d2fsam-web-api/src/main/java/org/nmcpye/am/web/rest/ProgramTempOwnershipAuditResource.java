//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramTempOwnershipAudit;
//import org.nmcpye.am.program.ProgramTempOwnershipAuditRepository;
//import org.nmcpye.am.program.ProgramTempOwnershipAuditService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramTempOwnershipAudit}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramTempOwnershipAuditResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramTempOwnershipAuditResource.class);
//
//    private static final String ENTITY_NAME = "programTempOwnershipAudit";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramTempOwnershipAuditService programTempOwnershipAuditService;
//
//    private final ProgramTempOwnershipAuditRepository programTempOwnershipAuditRepository;
//
//    public ProgramTempOwnershipAuditResource(
//        ProgramTempOwnershipAuditService programTempOwnershipAuditService,
//        ProgramTempOwnershipAuditRepository programTempOwnershipAuditRepository
//    ) {
//        this.programTempOwnershipAuditService = programTempOwnershipAuditService;
//        this.programTempOwnershipAuditRepository = programTempOwnershipAuditRepository;
//    }
//
//    /**
//     * {@code POST  /program-temp-ownership-audits} : Create a new programTempOwnershipAudit.
//     *
//     * @param programTempOwnershipAudit the programTempOwnershipAudit to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programTempOwnershipAudit, or with status {@code 400 (Bad Request)} if the programTempOwnershipAudit has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-temp-ownership-audits")
//    public ResponseEntity<ProgramTempOwnershipAudit> createProgramTempOwnershipAudit(
//        @Valid @RequestBody ProgramTempOwnershipAudit programTempOwnershipAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to save ProgramTempOwnershipAudit : {}", programTempOwnershipAudit);
//        if (programTempOwnershipAudit.getId() != null) {
//            throw new BadRequestAlertException("A new programTempOwnershipAudit cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramTempOwnershipAudit result = programTempOwnershipAuditService.save(programTempOwnershipAudit);
//        return ResponseEntity
//            .created(new URI("/api/program-temp-ownership-audits/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-temp-ownership-audits/:id} : Updates an existing programTempOwnershipAudit.
//     *
//     * @param id the id of the programTempOwnershipAudit to save.
//     * @param programTempOwnershipAudit the programTempOwnershipAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTempOwnershipAudit,
//     * or with status {@code 400 (Bad Request)} if the programTempOwnershipAudit is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programTempOwnershipAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-temp-ownership-audits/{id}")
//    public ResponseEntity<ProgramTempOwnershipAudit> updateProgramTempOwnershipAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramTempOwnershipAudit programTempOwnershipAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramTempOwnershipAudit : {}, {}", id, programTempOwnershipAudit);
//        if (programTempOwnershipAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTempOwnershipAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTempOwnershipAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramTempOwnershipAudit result = programTempOwnershipAuditService.update(programTempOwnershipAudit);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTempOwnershipAudit.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-temp-ownership-audits/:id} : Partial updates given fields of an existing programTempOwnershipAudit, field will ignore if it is null
//     *
//     * @param id the id of the programTempOwnershipAudit to save.
//     * @param programTempOwnershipAudit the programTempOwnershipAudit to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTempOwnershipAudit,
//     * or with status {@code 400 (Bad Request)} if the programTempOwnershipAudit is not valid,
//     * or with status {@code 404 (Not Found)} if the programTempOwnershipAudit is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programTempOwnershipAudit couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-temp-ownership-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramTempOwnershipAudit> partialUpdateProgramTempOwnershipAudit(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramTempOwnershipAudit programTempOwnershipAudit
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramTempOwnershipAudit partially : {}, {}", id, programTempOwnershipAudit);
//        if (programTempOwnershipAudit.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTempOwnershipAudit.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTempOwnershipAuditRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramTempOwnershipAudit> result = programTempOwnershipAuditService.partialUpdate(programTempOwnershipAudit);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTempOwnershipAudit.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-temp-ownership-audits} : get all the programTempOwnershipAudits.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programTempOwnershipAudits in body.
//     */
//    @GetMapping("/program-temp-ownership-audits")
//    public List<ProgramTempOwnershipAudit> getAllProgramTempOwnershipAudits(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ProgramTempOwnershipAudits");
//        return programTempOwnershipAuditService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-temp-ownership-audits/:id} : get the "id" programTempOwnershipAudit.
//     *
//     * @param id the id of the programTempOwnershipAudit to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programTempOwnershipAudit, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-temp-ownership-audits/{id}")
//    public ResponseEntity<ProgramTempOwnershipAudit> getProgramTempOwnershipAudit(@PathVariable Long id) {
//        log.debug("REST request to get ProgramTempOwnershipAudit : {}", id);
//        Optional<ProgramTempOwnershipAudit> programTempOwnershipAudit = programTempOwnershipAuditService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programTempOwnershipAudit);
//    }
//
//    /**
//     * {@code DELETE  /program-temp-ownership-audits/:id} : delete the "id" programTempOwnershipAudit.
//     *
//     * @param id the id of the programTempOwnershipAudit to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-temp-ownership-audits/{id}")
//    public ResponseEntity<Void> deleteProgramTempOwnershipAudit(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramTempOwnershipAudit : {}", id);
//        programTempOwnershipAuditService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
