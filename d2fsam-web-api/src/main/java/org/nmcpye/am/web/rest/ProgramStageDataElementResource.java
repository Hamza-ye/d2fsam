//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramStageDataElement;
//import org.nmcpye.am.program.ProgramStageDataElementRepository;
//import org.nmcpye.am.program.ProgramStageDataElementService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramStageDataElement}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramStageDataElementResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramStageDataElementResource.class);
//
//    private static final String ENTITY_NAME = "programStageDataElement";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramStageDataElementService programStageDataElementService;
//
//    private final ProgramStageDataElementRepository programStageDataElementRepository;
//
//    public ProgramStageDataElementResource(
//        ProgramStageDataElementService programStageDataElementService,
//        ProgramStageDataElementRepository programStageDataElementRepository
//    ) {
//        this.programStageDataElementService = programStageDataElementService;
//        this.programStageDataElementRepository = programStageDataElementRepository;
//    }
//
//    /**
//     * {@code POST  /program-stage-data-elements} : Create a new programStageDataElement.
//     *
//     * @param programStageDataElement the programStageDataElement to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programStageDataElement, or with status {@code 400 (Bad Request)} if the programStageDataElement has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-stage-data-elements")
//    public ResponseEntity<ProgramStageDataElement> createProgramStageDataElement(
//        @Valid @RequestBody ProgramStageDataElement programStageDataElement
//    ) throws URISyntaxException {
//        log.debug("REST request to save ProgramStageDataElement : {}", programStageDataElement);
//        if (programStageDataElement.getId() != null) {
//            throw new BadRequestAlertException("A new programStageDataElement cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramStageDataElement result = programStageDataElementService.save(programStageDataElement);
//        return ResponseEntity
//            .created(new URI("/api/program-stage-data-elements/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-stage-data-elements/:id} : Updates an existing programStageDataElement.
//     *
//     * @param id the id of the programStageDataElement to save.
//     * @param programStageDataElement the programStageDataElement to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageDataElement,
//     * or with status {@code 400 (Bad Request)} if the programStageDataElement is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programStageDataElement couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-stage-data-elements/{id}")
//    public ResponseEntity<ProgramStageDataElement> updateProgramStageDataElement(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramStageDataElement programStageDataElement
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramStageDataElement : {}, {}", id, programStageDataElement);
//        if (programStageDataElement.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageDataElement.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageDataElementRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramStageDataElement result = programStageDataElementService.update(programStageDataElement);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageDataElement.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-stage-data-elements/:id} : Partial updates given fields of an existing programStageDataElement, field will ignore if it is null
//     *
//     * @param id the id of the programStageDataElement to save.
//     * @param programStageDataElement the programStageDataElement to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageDataElement,
//     * or with status {@code 400 (Bad Request)} if the programStageDataElement is not valid,
//     * or with status {@code 404 (Not Found)} if the programStageDataElement is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programStageDataElement couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-stage-data-elements/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramStageDataElement> partialUpdateProgramStageDataElement(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramStageDataElement programStageDataElement
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramStageDataElement partially : {}, {}", id, programStageDataElement);
//        if (programStageDataElement.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageDataElement.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageDataElementRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramStageDataElement> result = programStageDataElementService.partialUpdate(programStageDataElement);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageDataElement.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-stage-data-elements} : get all the programStageDataElements.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programStageDataElements in body.
//     */
//    @GetMapping("/program-stage-data-elements")
//    public List<ProgramStageDataElement> getAllProgramStageDataElements(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ProgramStageDataElements");
//        return programStageDataElementService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-stage-data-elements/:id} : get the "id" programStageDataElement.
//     *
//     * @param id the id of the programStageDataElement to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programStageDataElement, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-stage-data-elements/{id}")
//    public ResponseEntity<ProgramStageDataElement> getProgramStageDataElement(@PathVariable Long id) {
//        log.debug("REST request to get ProgramStageDataElement : {}", id);
//        Optional<ProgramStageDataElement> programStageDataElement = programStageDataElementService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programStageDataElement);
//    }
//
//    /**
//     * {@code DELETE  /program-stage-data-elements/:id} : delete the "id" programStageDataElement.
//     *
//     * @param id the id of the programStageDataElement to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-stage-data-elements/{id}")
//    public ResponseEntity<Void> deleteProgramStageDataElement(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramStageDataElement : {}", id);
//        programStageDataElementService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
