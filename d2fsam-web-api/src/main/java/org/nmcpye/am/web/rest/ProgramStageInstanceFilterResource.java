//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.programstagefilter.ProgramStageInstanceFilter;
//import org.nmcpye.am.programstagefilter.ProgramStageInstanceFilterRepository;
//import org.nmcpye.am.programstagefilter.ProgramStageInstanceFilterService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramStageInstanceFilter}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramStageInstanceFilterResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramStageInstanceFilterResource.class);
//
//    private static final String ENTITY_NAME = "programStageInstanceFilter";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramStageInstanceFilterService programStageInstanceFilterService;
//
//    private final ProgramStageInstanceFilterRepository programStageInstanceFilterRepository;
//
//    public ProgramStageInstanceFilterResource(
//        ProgramStageInstanceFilterService programStageInstanceFilterService,
//        ProgramStageInstanceFilterRepository programStageInstanceFilterRepository
//    ) {
//        this.programStageInstanceFilterService = programStageInstanceFilterService;
//        this.programStageInstanceFilterRepository = programStageInstanceFilterRepository;
//    }
//
//    /**
//     * {@code POST  /program-stage-instance-filters} : Create a new programStageInstanceFilter.
//     *
//     * @param programStageInstanceFilter the programStageInstanceFilter to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programStageInstanceFilter, or with status {@code 400 (Bad Request)} if the programStageInstanceFilter has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-stage-instance-filters")
//    public ResponseEntity<ProgramStageInstanceFilter> createProgramStageInstanceFilter(
//        @Valid @RequestBody ProgramStageInstanceFilter programStageInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to save ProgramStageInstanceFilter : {}", programStageInstanceFilter);
//        if (programStageInstanceFilter.getId() != null) {
//            throw new BadRequestAlertException("A new programStageInstanceFilter cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramStageInstanceFilter result = programStageInstanceFilterService.save(programStageInstanceFilter);
//        return ResponseEntity
//            .created(new URI("/api/program-stage-instance-filters/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-stage-instance-filters/:id} : Updates an existing programStageInstanceFilter.
//     *
//     * @param id the id of the programStageInstanceFilter to save.
//     * @param programStageInstanceFilter the programStageInstanceFilter to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageInstanceFilter,
//     * or with status {@code 400 (Bad Request)} if the programStageInstanceFilter is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programStageInstanceFilter couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-stage-instance-filters/{id}")
//    public ResponseEntity<ProgramStageInstanceFilter> updateProgramStageInstanceFilter(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramStageInstanceFilter programStageInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramStageInstanceFilter : {}, {}", id, programStageInstanceFilter);
//        if (programStageInstanceFilter.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageInstanceFilter.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageInstanceFilterRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramStageInstanceFilter result = programStageInstanceFilterService.update(programStageInstanceFilter);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageInstanceFilter.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-stage-instance-filters/:id} : Partial updates given fields of an existing programStageInstanceFilter, field will ignore if it is null
//     *
//     * @param id the id of the programStageInstanceFilter to save.
//     * @param programStageInstanceFilter the programStageInstanceFilter to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programStageInstanceFilter,
//     * or with status {@code 400 (Bad Request)} if the programStageInstanceFilter is not valid,
//     * or with status {@code 404 (Not Found)} if the programStageInstanceFilter is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programStageInstanceFilter couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-stage-instance-filters/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramStageInstanceFilter> partialUpdateProgramStageInstanceFilter(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramStageInstanceFilter programStageInstanceFilter
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramStageInstanceFilter partially : {}, {}", id, programStageInstanceFilter);
//        if (programStageInstanceFilter.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programStageInstanceFilter.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programStageInstanceFilterRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramStageInstanceFilter> result = programStageInstanceFilterService.partialUpdate(programStageInstanceFilter);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programStageInstanceFilter.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-stage-instance-filters} : get all the programStageInstanceFilters.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programStageInstanceFilters in body.
//     */
//    @GetMapping("/program-stage-instance-filters")
//    public List<ProgramStageInstanceFilter> getAllProgramStageInstanceFilters(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ProgramStageInstanceFilters");
//        return programStageInstanceFilterService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-stage-instance-filters/:id} : get the "id" programStageInstanceFilter.
//     *
//     * @param id the id of the programStageInstanceFilter to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programStageInstanceFilter, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-stage-instance-filters/{id}")
//    public ResponseEntity<ProgramStageInstanceFilter> getProgramStageInstanceFilter(@PathVariable Long id) {
//        log.debug("REST request to get ProgramStageInstanceFilter : {}", id);
//        Optional<ProgramStageInstanceFilter> programStageInstanceFilter = programStageInstanceFilterService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programStageInstanceFilter);
//    }
//
//    /**
//     * {@code DELETE  /program-stage-instance-filters/:id} : delete the "id" programStageInstanceFilter.
//     *
//     * @param id the id of the programStageInstanceFilter to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-stage-instance-filters/{id}")
//    public ResponseEntity<Void> deleteProgramStageInstanceFilter(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramStageInstanceFilter : {}", id);
//        programStageInstanceFilterService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
