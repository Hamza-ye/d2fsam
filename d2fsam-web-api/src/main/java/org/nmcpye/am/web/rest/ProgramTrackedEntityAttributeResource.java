//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramTrackedEntityAttribute;
//import org.nmcpye.am.program.ProgramTrackedEntityAttributeRepository;
//import org.nmcpye.am.program.ProgramTrackedEntityAttributeService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramTrackedEntityAttribute}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramTrackedEntityAttributeResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramTrackedEntityAttributeResource.class);
//
//    private static final String ENTITY_NAME = "programTrackedEntityAttribute";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramTrackedEntityAttributeService programTrackedEntityAttributeService;
//
//    private final ProgramTrackedEntityAttributeRepository programTrackedEntityAttributeRepository;
//
//    public ProgramTrackedEntityAttributeResource(
//        ProgramTrackedEntityAttributeService programTrackedEntityAttributeService,
//        ProgramTrackedEntityAttributeRepository programTrackedEntityAttributeRepository
//    ) {
//        this.programTrackedEntityAttributeService = programTrackedEntityAttributeService;
//        this.programTrackedEntityAttributeRepository = programTrackedEntityAttributeRepository;
//    }
//
//    /**
//     * {@code POST  /program-tracked-entity-attributes} : Create a new programTrackedEntityAttribute.
//     *
//     * @param programTrackedEntityAttribute the programTrackedEntityAttribute to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programTrackedEntityAttribute, or with status {@code 400 (Bad Request)} if the programTrackedEntityAttribute has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-tracked-entity-attributes")
//    public ResponseEntity<ProgramTrackedEntityAttribute> createProgramTrackedEntityAttribute(
//        @Valid @RequestBody ProgramTrackedEntityAttribute programTrackedEntityAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to save ProgramTrackedEntityAttribute : {}", programTrackedEntityAttribute);
//        if (programTrackedEntityAttribute.getId() != null) {
//            throw new BadRequestAlertException("A new programTrackedEntityAttribute cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramTrackedEntityAttribute result = programTrackedEntityAttributeService.save(programTrackedEntityAttribute);
//        return ResponseEntity
//            .created(new URI("/api/program-tracked-entity-attributes/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-tracked-entity-attributes/:id} : Updates an existing programTrackedEntityAttribute.
//     *
//     * @param id the id of the programTrackedEntityAttribute to save.
//     * @param programTrackedEntityAttribute the programTrackedEntityAttribute to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTrackedEntityAttribute,
//     * or with status {@code 400 (Bad Request)} if the programTrackedEntityAttribute is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programTrackedEntityAttribute couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-tracked-entity-attributes/{id}")
//    public ResponseEntity<ProgramTrackedEntityAttribute> updateProgramTrackedEntityAttribute(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramTrackedEntityAttribute programTrackedEntityAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramTrackedEntityAttribute : {}, {}", id, programTrackedEntityAttribute);
//        if (programTrackedEntityAttribute.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTrackedEntityAttribute.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTrackedEntityAttributeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramTrackedEntityAttribute result = programTrackedEntityAttributeService.update(programTrackedEntityAttribute);
//        return ResponseEntity
//            .ok()
//            .headers(
//                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTrackedEntityAttribute.getId().toString())
//            )
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-tracked-entity-attributes/:id} : Partial updates given fields of an existing programTrackedEntityAttribute, field will ignore if it is null
//     *
//     * @param id the id of the programTrackedEntityAttribute to save.
//     * @param programTrackedEntityAttribute the programTrackedEntityAttribute to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTrackedEntityAttribute,
//     * or with status {@code 400 (Bad Request)} if the programTrackedEntityAttribute is not valid,
//     * or with status {@code 404 (Not Found)} if the programTrackedEntityAttribute is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programTrackedEntityAttribute couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-tracked-entity-attributes/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramTrackedEntityAttribute> partialUpdateProgramTrackedEntityAttribute(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramTrackedEntityAttribute programTrackedEntityAttribute
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramTrackedEntityAttribute partially : {}, {}", id, programTrackedEntityAttribute);
//        if (programTrackedEntityAttribute.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTrackedEntityAttribute.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTrackedEntityAttributeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramTrackedEntityAttribute> result = programTrackedEntityAttributeService.partialUpdate(programTrackedEntityAttribute);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTrackedEntityAttribute.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-tracked-entity-attributes} : get all the programTrackedEntityAttributes.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programTrackedEntityAttributes in body.
//     */
//    @GetMapping("/program-tracked-entity-attributes")
//    public List<ProgramTrackedEntityAttribute> getAllProgramTrackedEntityAttributes(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ProgramTrackedEntityAttributes");
//        return programTrackedEntityAttributeService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-tracked-entity-attributes/:id} : get the "id" programTrackedEntityAttribute.
//     *
//     * @param id the id of the programTrackedEntityAttribute to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programTrackedEntityAttribute, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-tracked-entity-attributes/{id}")
//    public ResponseEntity<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttribute(@PathVariable Long id) {
//        log.debug("REST request to get ProgramTrackedEntityAttribute : {}", id);
//        Optional<ProgramTrackedEntityAttribute> programTrackedEntityAttribute = programTrackedEntityAttributeService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programTrackedEntityAttribute);
//    }
//
//    /**
//     * {@code DELETE  /program-tracked-entity-attributes/:id} : delete the "id" programTrackedEntityAttribute.
//     *
//     * @param id the id of the programTrackedEntityAttribute to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-tracked-entity-attributes/{id}")
//    public ResponseEntity<Void> deleteProgramTrackedEntityAttribute(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramTrackedEntityAttribute : {}", id);
//        programTrackedEntityAttributeService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
