//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramInstance;
//import org.nmcpye.am.program.ProgramInstanceRepository;
//import org.nmcpye.am.program.ProgramInstanceService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramInstance}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramInstanceResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramInstanceResource.class);
//
//    private static final String ENTITY_NAME = "programInstance";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramInstanceService programInstanceService;
//
//    private final ProgramInstanceRepository programInstanceRepository;
//
//    public ProgramInstanceResource(ProgramInstanceService programInstanceService, ProgramInstanceRepository programInstanceRepository) {
//        this.programInstanceService = programInstanceService;
//        this.programInstanceRepository = programInstanceRepository;
//    }
//
//    /**
//     * {@code POST  /program-instances} : Create a new programInstance.
//     *
//     * @param programInstance the programInstance to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programInstance, or with status {@code 400 (Bad Request)} if the programInstance has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-instances")
//    public ResponseEntity<ProgramInstance> createProgramInstance(@Valid @RequestBody ProgramInstance programInstance)
//        throws URISyntaxException {
//        log.debug("REST request to save ProgramInstance : {}", programInstance);
//        if (programInstance.getId() != null) {
//            throw new BadRequestAlertException("A new programInstance cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramInstance result = programInstanceService.save(programInstance);
//        return ResponseEntity
//            .created(new URI("/api/program-instances/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-instances/:id} : Updates an existing programInstance.
//     *
//     * @param id the id of the programInstance to save.
//     * @param programInstance the programInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programInstance,
//     * or with status {@code 400 (Bad Request)} if the programInstance is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-instances/{id}")
//    public ResponseEntity<ProgramInstance> updateProgramInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramInstance programInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramInstance : {}, {}", id, programInstance);
//        if (programInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramInstance result = programInstanceService.update(programInstance);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programInstance.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-instances/:id} : Partial updates given fields of an existing programInstance, field will ignore if it is null
//     *
//     * @param id the id of the programInstance to save.
//     * @param programInstance the programInstance to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programInstance,
//     * or with status {@code 400 (Bad Request)} if the programInstance is not valid,
//     * or with status {@code 404 (Not Found)} if the programInstance is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programInstance couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-instances/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramInstance> partialUpdateProgramInstance(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramInstance programInstance
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramInstance partially : {}, {}", id, programInstance);
//        if (programInstance.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programInstance.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programInstanceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramInstance> result = programInstanceService.partialUpdate(programInstance);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programInstance.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-instances} : get all the programInstances.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programInstances in body.
//     */
//    @GetMapping("/program-instances")
//    public ResponseEntity<List<ProgramInstance>> getAllProgramInstances(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of ProgramInstances");
//        Page<ProgramInstance> page;
//        if (eagerload) {
//            page = programInstanceService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = programInstanceService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /program-instances/:id} : get the "id" programInstance.
//     *
//     * @param id the id of the programInstance to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programInstance, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-instances/{id}")
//    public ResponseEntity<ProgramInstance> getProgramInstance(@PathVariable Long id) {
//        log.debug("REST request to get ProgramInstance : {}", id);
//        Optional<ProgramInstance> programInstance = programInstanceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programInstance);
//    }
//
//    /**
//     * {@code DELETE  /program-instances/:id} : delete the "id" programInstance.
//     *
//     * @param id the id of the programInstance to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-instances/{id}")
//    public ResponseEntity<Void> deleteProgramInstance(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramInstance : {}", id);
//        programInstanceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
