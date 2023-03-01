//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.period.RelativePeriods;
//import org.nmcpye.am.period.RelativePeriodsRepository;
//import org.nmcpye.am.period.RelativePeriodsService;
//import org.nmcpye.am.web.rest.errors.BadRequestAlertException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tech.jhipster.web.util.HeaderUtil;
//import tech.jhipster.web.util.ResponseUtil;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * REST controller for managing {@link org.nmcpye.am.domain.RelativePeriods}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class RelativePeriodsResource {
//
//    private final Logger log = LoggerFactory.getLogger(RelativePeriodsResource.class);
//
//    private static final String ENTITY_NAME = "relativePeriods";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final RelativePeriodsService relativePeriodsService;
//
//    private final RelativePeriodsRepository relativePeriodsRepository;
//
//    public RelativePeriodsResource(RelativePeriodsService relativePeriodsService, RelativePeriodsRepository relativePeriodsRepository) {
//        this.relativePeriodsService = relativePeriodsService;
//        this.relativePeriodsRepository = relativePeriodsRepository;
//    }
//
//    /**
//     * {@code POST  /relative-periods} : Create a new relativePeriods.
//     *
//     * @param relativePeriods the relativePeriods to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relativePeriods, or with status {@code 400 (Bad Request)} if the relativePeriods has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/relative-periods")
//    public ResponseEntity<RelativePeriods> createRelativePeriods(@RequestBody RelativePeriods relativePeriods) throws URISyntaxException {
//        log.debug("REST request to save RelativePeriods : {}", relativePeriods);
//        if (relativePeriods.getId() != null) {
//            throw new BadRequestAlertException("A new relativePeriods cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        RelativePeriods result = relativePeriodsService.save(relativePeriods);
//        return ResponseEntity
//            .created(new URI("/api/relative-periods/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /relative-periods/:id} : Updates an existing relativePeriods.
//     *
//     * @param id the id of the relativePeriods to save.
//     * @param relativePeriods the relativePeriods to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relativePeriods,
//     * or with status {@code 400 (Bad Request)} if the relativePeriods is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the relativePeriods couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/relative-periods/{id}")
//    public ResponseEntity<RelativePeriods> updateRelativePeriods(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody RelativePeriods relativePeriods
//    ) throws URISyntaxException {
//        log.debug("REST request to update RelativePeriods : {}, {}", id, relativePeriods);
//        if (relativePeriods.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relativePeriods.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relativePeriodsRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        RelativePeriods result = relativePeriodsService.update(relativePeriods);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relativePeriods.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /relative-periods/:id} : Partial updates given fields of an existing relativePeriods, field will ignore if it is null
//     *
//     * @param id the id of the relativePeriods to save.
//     * @param relativePeriods the relativePeriods to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relativePeriods,
//     * or with status {@code 400 (Bad Request)} if the relativePeriods is not valid,
//     * or with status {@code 404 (Not Found)} if the relativePeriods is not found,
//     * or with status {@code 500 (Internal Server Error)} if the relativePeriods couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/relative-periods/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<RelativePeriods> partialUpdateRelativePeriods(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody RelativePeriods relativePeriods
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update RelativePeriods partially : {}, {}", id, relativePeriods);
//        if (relativePeriods.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relativePeriods.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relativePeriodsRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<RelativePeriods> result = relativePeriodsService.partialUpdate(relativePeriods);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relativePeriods.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /relative-periods} : get all the relativePeriods.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relativePeriods in body.
//     */
//    @GetMapping("/relative-periods")
//    public List<RelativePeriods> getAllRelativePeriods() {
//        log.debug("REST request to get all RelativePeriods");
//        return relativePeriodsService.findAll();
//    }
//
//    /**
//     * {@code GET  /relative-periods/:id} : get the "id" relativePeriods.
//     *
//     * @param id the id of the relativePeriods to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relativePeriods, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/relative-periods/{id}")
//    public ResponseEntity<RelativePeriods> getRelativePeriods(@PathVariable Long id) {
//        log.debug("REST request to get RelativePeriods : {}", id);
//        Optional<RelativePeriods> relativePeriods = relativePeriodsService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(relativePeriods);
//    }
//
//    /**
//     * {@code DELETE  /relative-periods/:id} : delete the "id" relativePeriods.
//     *
//     * @param id the id of the relativePeriods to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/relative-periods/{id}")
//    public ResponseEntity<Void> deleteRelativePeriods(@PathVariable Long id) {
//        log.debug("REST request to delete RelativePeriods : {}", id);
//        relativePeriodsService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
