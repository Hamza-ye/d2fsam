//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramStageInstance;
//import org.nmcpye.am.program.ProgramStageInstanceRepository;
//import org.nmcpye.am.program.ProgramStageInstanceService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramStageInstance}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramStageInstanceResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramStageInstanceResource.class);
//
//    private static final String ENTITY_NAME = "programStageInstance";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramStageInstanceService programStageInstanceService;
//
//    private final ProgramStageInstanceRepository programStageInstanceRepository;
//
//    public ProgramStageInstanceResource(
//        ProgramStageInstanceService programStageInstanceService,
//        ProgramStageInstanceRepository programStageInstanceRepository
//    ) {
//        this.programStageInstanceService = programStageInstanceService;
//        this.programStageInstanceRepository = programStageInstanceRepository;
//    }
//
//    /**
//     * {@code POST  /program-stage-instances} : Create a new programStageInstance.
//     *
//     * @param programStageInstance the programStageInstance to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programStageInstance, or with status {@code 400 (Bad Request)} if the programStageInstance has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-stage-instances")
//    public ResponseEntity<ProgramStageInstance> createProgramStageInstance(@Valid @RequestBody ProgramStageInstance programStageInstance)
//        throws URISyntaxException {
//        log.debug("REST request to save ProgramStageInstance : {}", programStageInstance);
//        if (programStageInstance.getId() != null) {
//            throw new BadRequestAlertException("A new programStageInstance cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramStageInstance result = programStageInstanceService.save(programStageInstance);
//        return ResponseEntity
//            .created(new URI("/api/program-stage-instances/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-stage-instances/:id} : Updates an existing programStageInstance.
//     *
//     * @param id the id of the programStageInstance to save.
//     * @param programStageInstance the programStageInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageInstance,
//     * or with status {@code 400 (Bad Request)} if the programStageInstance is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programStageInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-stage-instances/{id}")
//    public ResponseEntity<ProgramStageInstance> updateProgramStageInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramStageInstance programStageInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramStageInstance : {}, {}", id, programStageInstance);
//        if (programStageInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramStageInstance result = programStageInstanceService.update(programStageInstance);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageInstance.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-stage-instances/:id} : Partial updates given fields of an existing programStageInstance, field will ignore if it is null
//     *
//     * @param id the id of the programStageInstance to save.
//     * @param programStageInstance the programStageInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageInstance,
//     * or with status {@code 400 (Bad Request)} if the programStageInstance is not valid,
//     * or with status {@code 404 (Not Found)} if the programStageInstance is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programStageInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-stage-instances/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramStageInstance> partialUpdateProgramStageInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramStageInstance programStageInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramStageInstance partially : {}, {}", id, programStageInstance);
//        if (programStageInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramStageInstance> result = programStageInstanceService.partialUpdate(programStageInstance);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageInstance.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-stage-instances} : get all the programStageInstances.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programStageInstances in body.
//     */
//    @GetMapping("/program-stage-instances")
//    public ResponseEntity<List<ProgramStageInstance>> getAllProgramStageInstances(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of ProgramStageInstances");
//        Page<ProgramStageInstance> page;
//        if (eagerload) {
//            page = programStageInstanceService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = programStageInstanceService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /program-stage-instances/:id} : get the "id" programStageInstance.
//     *
//     * @param id the id of the programStageInstance to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programStageInstance, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-stage-instances/{id}")
//    public ResponseEntity<ProgramStageInstance> getProgramStageInstance(@PathVariable Long id) {
//        log.debug("REST request to get ProgramStageInstance : {}", id);
//        Optional<ProgramStageInstance> programStageInstance = programStageInstanceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programStageInstance);
//    }
//
//    /**
//     * {@code DELETE  /program-stage-instances/:id} : delete the "id" programStageInstance.
//     *
//     * @param id the id of the programStageInstance to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-stage-instances/{id}")
//    public ResponseEntity<Void> deleteProgramStageInstance(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramStageInstance : {}", id);
//        programStageInstanceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
