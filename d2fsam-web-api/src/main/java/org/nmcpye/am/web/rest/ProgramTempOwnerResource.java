//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.program.ProgramTempOwner;
//import org.nmcpye.am.program.ProgramTempOwnerRepository;
//import org.nmcpye.am.program.ProgramTempOwnerService;
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
// * REST controller for managing {@link org.nmcpye.am.program.ProgramTempOwner}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ProgramTempOwnerResource {
//
//    private final Logger log = LoggerFactory.getLogger(ProgramTempOwnerResource.class);
//
//    private static final String ENTITY_NAME = "programTempOwner";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ProgramTempOwnerService programTempOwnerService;
//
//    private final ProgramTempOwnerRepository programTempOwnerRepository;
//
//    public ProgramTempOwnerResource(
//        ProgramTempOwnerService programTempOwnerService,
//        ProgramTempOwnerRepository programTempOwnerRepository
//    ) {
//        this.programTempOwnerService = programTempOwnerService;
//        this.programTempOwnerRepository = programTempOwnerRepository;
//    }
//
//    /**
//     * {@code POST  /program-temp-owners} : Create a new programTempOwner.
//     *
//     * @param programTempOwner the programTempOwner to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programTempOwner, or with status {@code 400 (Bad Request)} if the programTempOwner has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/program-temp-owners")
//    public ResponseEntity<ProgramTempOwner> createProgramTempOwner(@Valid @RequestBody ProgramTempOwner programTempOwner)
//        throws URISyntaxException {
//        log.debug("REST request to save ProgramTempOwner : {}", programTempOwner);
//        if (programTempOwner.getId() != null) {
//            throw new BadRequestAlertException("A new programTempOwner cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ProgramTempOwner result = programTempOwnerService.save(programTempOwner);
//        return ResponseEntity
//            .created(new URI("/api/program-temp-owners/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /program-temp-owners/:id} : Updates an existing programTempOwner.
//     *
//     * @param id the id of the programTempOwner to save.
//     * @param programTempOwner the programTempOwner to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTempOwner,
//     * or with status {@code 400 (Bad Request)} if the programTempOwner is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the programTempOwner couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/program-temp-owners/{id}")
//    public ResponseEntity<ProgramTempOwner> updateProgramTempOwner(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ProgramTempOwner programTempOwner
//    ) throws URISyntaxException {
//        log.debug("REST request to update ProgramTempOwner : {}, {}", id, programTempOwner);
//        if (programTempOwner.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTempOwner.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTempOwnerRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ProgramTempOwner result = programTempOwnerService.update(programTempOwner);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTempOwner.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /program-temp-owners/:id} : Partial updates given fields of an existing programTempOwner, field will ignore if it is null
//     *
//     * @param id the id of the programTempOwner to save.
//     * @param programTempOwner the programTempOwner to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programTempOwner,
//     * or with status {@code 400 (Bad Request)} if the programTempOwner is not valid,
//     * or with status {@code 404 (Not Found)} if the programTempOwner is not found,
//     * or with status {@code 500 (Internal Server Error)} if the programTempOwner couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/program-temp-owners/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ProgramTempOwner> partialUpdateProgramTempOwner(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ProgramTempOwner programTempOwner
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ProgramTempOwner partially : {}, {}", id, programTempOwner);
//        if (programTempOwner.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, programTempOwner.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!programTempOwnerRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ProgramTempOwner> result = programTempOwnerService.partialUpdate(programTempOwner);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, programTempOwner.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /program-temp-owners} : get all the programTempOwners.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programTempOwners in body.
//     */
//    @GetMapping("/program-temp-owners")
//    public List<ProgramTempOwner> getAllProgramTempOwners(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all ProgramTempOwners");
//        return programTempOwnerService.findAll();
//    }
//
//    /**
//     * {@code GET  /program-temp-owners/:id} : get the "id" programTempOwner.
//     *
//     * @param id the id of the programTempOwner to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programTempOwner, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/program-temp-owners/{id}")
//    public ResponseEntity<ProgramTempOwner> getProgramTempOwner(@PathVariable Long id) {
//        log.debug("REST request to get ProgramTempOwner : {}", id);
//        Optional<ProgramTempOwner> programTempOwner = programTempOwnerService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(programTempOwner);
//    }
//
//    /**
//     * {@code DELETE  /program-temp-owners/:id} : delete the "id" programTempOwner.
//     *
//     * @param id the id of the programTempOwner to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/program-temp-owners/{id}")
//    public ResponseEntity<Void> deleteProgramTempOwner(@PathVariable Long id) {
//        log.debug("REST request to delete ProgramTempOwner : {}", id);
//        programTempOwnerService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
