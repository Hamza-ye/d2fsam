//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramOwnershipHistory;
//import org.nmcpye.am.program.ProgramOwnershipHistoryRepository;
//import org.nmcpye.am.program.ProgramOwnershipHistoryService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramOwnershipHistory}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramOwnershipHistoryResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramOwnershipHistoryResource.class);
//
//    private static final String ENTITY_NAME = "programOwnershipHistory";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramOwnershipHistoryService programOwnershipHistoryService;
//
//    private final ProgramOwnershipHistoryRepository programOwnershipHistoryRepository;
//
//    public ProgramOwnershipHistoryResource(
//        ProgramOwnershipHistoryService programOwnershipHistoryService,
//        ProgramOwnershipHistoryRepository programOwnershipHistoryRepository
//    ) {
//        this.programOwnershipHistoryService = programOwnershipHistoryService;
//        this.programOwnershipHistoryRepository = programOwnershipHistoryRepository;
//    }
//
//    /**
//     * {@code POST  /program-ownership-histories} : Create a new programOwnershipHistory.
//     *
//     * @param programOwnershipHistory the programOwnershipHistory to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programOwnershipHistory, or with status {@code 400 (Bad Request)} if the programOwnershipHistory has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-ownership-histories")
//    public ResponseEntity<ProgramOwnershipHistory> createProgramOwnershipHistory(
//        @Valid @RequestBody ProgramOwnershipHistory programOwnershipHistory
//    ) throws URISyntaxException {
//        log.debug("REST request to save ProgramOwnershipHistory : {}", programOwnershipHistory);
//        if (programOwnershipHistory.getId() != null) {
//            throw new BadRequestAlertException("A new programOwnershipHistory cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramOwnershipHistory result = programOwnershipHistoryService.save(programOwnershipHistory);
//        return ResponseEntity
//            .created(new URI("/api/program-ownership-histories/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-ownership-histories/:id} : Updates an existing programOwnershipHistory.
//     *
//     * @param id the id of the programOwnershipHistory to save.
//     * @param programOwnershipHistory the programOwnershipHistory to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programOwnershipHistory,
//     * or with status {@code 400 (Bad Request)} if the programOwnershipHistory is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programOwnershipHistory couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-ownership-histories/{id}")
//    public ResponseEntity<ProgramOwnershipHistory> updateProgramOwnershipHistory(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramOwnershipHistory programOwnershipHistory
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramOwnershipHistory : {}, {}", id, programOwnershipHistory);
//        if (programOwnershipHistory.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programOwnershipHistory.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programOwnershipHistoryRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramOwnershipHistory result = programOwnershipHistoryService.update(programOwnershipHistory);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programOwnershipHistory.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-ownership-histories/:id} : Partial updates given fields of an existing programOwnershipHistory, field will ignore if it is null
//     *
//     * @param id the id of the programOwnershipHistory to save.
//     * @param programOwnershipHistory the programOwnershipHistory to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programOwnershipHistory,
//     * or with status {@code 400 (Bad Request)} if the programOwnershipHistory is not valid,
//     * or with status {@code 404 (Not Found)} if the programOwnershipHistory is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programOwnershipHistory couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-ownership-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramOwnershipHistory> partialUpdateProgramOwnershipHistory(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramOwnershipHistory programOwnershipHistory
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramOwnershipHistory partially : {}, {}", id, programOwnershipHistory);
//        if (programOwnershipHistory.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programOwnershipHistory.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programOwnershipHistoryRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramOwnershipHistory> result = programOwnershipHistoryService.partialUpdate(programOwnershipHistory);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programOwnershipHistory.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-ownership-histories} : get all the programOwnershipHistories.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programOwnershipHistories in body.
//     */
//    @GetMapping("/program-ownership-histories")
//    public List<ProgramOwnershipHistory> getAllProgramOwnershipHistories(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ProgramOwnershipHistories");
//        return programOwnershipHistoryService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-ownership-histories/:id} : get the "id" programOwnershipHistory.
//     *
//     * @param id the id of the programOwnershipHistory to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programOwnershipHistory, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-ownership-histories/{id}")
//    public ResponseEntity<ProgramOwnershipHistory> getProgramOwnershipHistory(@PathVariable Long id) {
//        log.debug("REST request to get ProgramOwnershipHistory : {}", id);
//        Optional<ProgramOwnershipHistory> programOwnershipHistory = programOwnershipHistoryService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programOwnershipHistory);
//    }
//
//    /**
//     * {@code DELETE  /program-ownership-histories/:id} : delete the "id" programOwnershipHistory.
//     *
//     * @param id the id of the programOwnershipHistory to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-ownership-histories/{id}")
//    public ResponseEntity<Void> deleteProgramOwnershipHistory(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramOwnershipHistory : {}", id);
//        programOwnershipHistoryService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
