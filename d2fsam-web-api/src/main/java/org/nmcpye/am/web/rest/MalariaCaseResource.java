//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.malariacase.MalariaCase;
//import org.nmcpye.am.malariacase.MalariaCaseRepository;
//import org.nmcpye.am.malariacase.MalariaCaseService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.MalariaCase}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class MalariaCaseResource {
//
//    private final Logger log = LoggerFactory.getLogger(MalariaCaseResource.class);
//
//    private static final String ENTITY_NAME = "malariaCase";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final MalariaCaseService malariaCaseService;
//
//    private final MalariaCaseRepository malariaCaseRepository;
//
//    public MalariaCaseResource(MalariaCaseService malariaCaseService, MalariaCaseRepository malariaCaseRepository) {
//        this.malariaCaseService = malariaCaseService;
//        this.malariaCaseRepository = malariaCaseRepository;
//    }
//
//    /**
//     * {@code POST  /malaria-cases} : Create a new malariaCase.
//     *
//     * @param malariaCase the malariaCase to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new malariaCase, or with status {@code 400 (Bad Request)} if the malariaCase has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/malaria-cases")
//    public ResponseEntity<MalariaCase> createMalariaCase(@Valid @RequestBody MalariaCase malariaCase) throws URISyntaxException {
//        log.debug("REST request to save MalariaCase : {}", malariaCase);
//        if (malariaCase.getId() != null) {
//            throw new BadRequestAlertException("A new malariaCase cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        MalariaCase result = malariaCaseService.save(malariaCase);
//        return ResponseEntity
//            .created(new URI("/api/malaria-cases/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /malaria-cases/:id} : Updates an existing malariaCase.
//     *
//     * @param id the id of the malariaCase to save.
//     * @param malariaCase the malariaCase to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated malariaCase,
//     * or with status {@code 400 (Bad Request)} if the malariaCase is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the malariaCase couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/malaria-cases/{id}")
//    public ResponseEntity<MalariaCase> updateMalariaCase(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody MalariaCase malariaCase
//    ) throws URISyntaxException {
//        log.debug("REST request to update MalariaCase : {}, {}", id, malariaCase);
//        if (malariaCase.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, malariaCase.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!malariaCaseRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        MalariaCase result = malariaCaseService.update(malariaCase);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, malariaCase.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /malaria-cases/:id} : Partial updates given fields of an existing malariaCase, field will ignore if it is null
//     *
//     * @param id the id of the malariaCase to save.
//     * @param malariaCase the malariaCase to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated malariaCase,
//     * or with status {@code 400 (Bad Request)} if the malariaCase is not valid,
//     * or with status {@code 404 (Not Found)} if the malariaCase is not found,
//     * or with status {@code 500 (Internal Server Error)} if the malariaCase couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/malaria-cases/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<MalariaCase> partialUpdateMalariaCase(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody MalariaCase malariaCase
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update MalariaCase partially : {}, {}", id, malariaCase);
//        if (malariaCase.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, malariaCase.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!malariaCaseRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<MalariaCase> result = malariaCaseService.partialUpdate(malariaCase);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, malariaCase.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /malaria-cases} : get all the malariaCases.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of malariaCases in body.
//     */
//    @GetMapping("/malaria-cases")
//    public ResponseEntity<List<MalariaCase>> getAllMalariaCases(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of MalariaCases");
//        Page<MalariaCase> page;
//        if (eagerload) {
//            page = malariaCaseService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = malariaCaseService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /malaria-cases/:id} : get the "id" malariaCase.
//     *
//     * @param id the id of the malariaCase to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the malariaCase, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/malaria-cases/{id}")
//    public ResponseEntity<MalariaCase> getMalariaCase(@PathVariable Long id) {
//        log.debug("REST request to get MalariaCase : {}", id);
//        Optional<MalariaCase> malariaCase = malariaCaseService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(malariaCase);
//    }
//
//    /**
//     * {@code DELETE  /malaria-cases/:id} : delete the "id" malariaCase.
//     *
//     * @param id the id of the malariaCase to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/malaria-cases/{id}")
//    public ResponseEntity<Void> deleteMalariaCase(@PathVariable Long id) {
//        log.debug("REST request to delete MalariaCase : {}", id);
//        malariaCaseService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
