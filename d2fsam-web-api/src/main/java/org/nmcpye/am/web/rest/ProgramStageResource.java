//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramStage;
//import org.nmcpye.am.program.ProgramStageRepository;
//import org.nmcpye.am.program.ProgramStageService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramStage}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramStageResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramStageResource.class);
//
//    private static final String ENTITY_NAME = "programStage";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramStageService programStageService;
//
//    private final ProgramStageRepository programStageRepository;
//
//    public ProgramStageResource(ProgramStageService programStageService, ProgramStageRepository programStageRepository) {
//        this.programStageService = programStageService;
//        this.programStageRepository = programStageRepository;
//    }
//
//    /**
//     * {@code POST  /program-stages} : Create a new programStage.
//     *
//     * @param programStage the programStage to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programStage, or with status {@code 400 (Bad Request)} if the programStage has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-stages")
//    public ResponseEntity<ProgramStage> createProgramStage(@Valid @RequestBody ProgramStage programStage) throws URISyntaxException {
//        log.debug("REST request to save ProgramStage : {}", programStage);
//        if (programStage.getId() != null) {
//            throw new BadRequestAlertException("A new programStage cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramStage result = programStageService.save(programStage);
//        return ResponseEntity
//            .created(new URI("/api/program-stages/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-stages/:id} : Updates an existing programStage.
//     *
//     * @param id the id of the programStage to save.
//     * @param programStage the programStage to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStage,
//     * or with status {@code 400 (Bad Request)} if the programStage is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programStage couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-stages/{id}")
//    public ResponseEntity<ProgramStage> updateProgramStage(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramStage programStage
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramStage : {}, {}", id, programStage);
//        if (programStage.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStage.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramStage result = programStageService.update(programStage);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStage.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-stages/:id} : Partial updates given fields of an existing programStage, field will ignore if it is null
//     *
//     * @param id the id of the programStage to save.
//     * @param programStage the programStage to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStage,
//     * or with status {@code 400 (Bad Request)} if the programStage is not valid,
//     * or with status {@code 404 (Not Found)} if the programStage is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programStage couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-stages/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramStage> partialUpdateProgramStage(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramStage programStage
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramStage partially : {}, {}", id, programStage);
//        if (programStage.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStage.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramStage> result = programStageService.partialUpdate(programStage);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStage.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-stages} : get all the programStages.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programStages in body.
//     */
//    @GetMapping("/program-stages")
//    public ResponseEntity<List<ProgramStage>> getAllProgramStages(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of ProgramStages");
//        Page<ProgramStage> page;
//        if (eagerload) {
//            page = programStageService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = programStageService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /program-stages/:id} : get the "id" programStage.
//     *
//     * @param id the id of the programStage to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programStage, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-stages/{id}")
//    public ResponseEntity<ProgramStage> getProgramStage(@PathVariable Long id) {
//        log.debug("REST request to get ProgramStage : {}", id);
//        Optional<ProgramStage> programStage = programStageService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programStage);
//    }
//
//    /**
//     * {@code DELETE  /program-stages/:id} : delete the "id" programStage.
//     *
//     * @param id the id of the programStage to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-stages/{id}")
//    public ResponseEntity<Void> deleteProgramStage(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramStage : {}", id);
//        programStageService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
