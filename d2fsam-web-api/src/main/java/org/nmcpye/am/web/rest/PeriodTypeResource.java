//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.period.PeriodType;
//import org.nmcpye.am.period.PeriodTypeRepository;
//import org.nmcpye.am.period.PeriodTypeService;
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
// * REST controller for managing {@link org.nmcpye.am.periodType}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class PeriodTypeResource {
//
//    private final Logger log = LoggerFactory.getLogger(PeriodTypeResource.class);
//
//    private static final String ENTITY_NAME = "periodType";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final PeriodTypeService periodTypeService;
//
//    private final PeriodTypeRepository periodTypeRepository;
//
//    public PeriodTypeResource(PeriodTypeService periodTypeService, PeriodTypeRepository periodTypeRepository) {
//        this.periodTypeService = periodTypeService;
//        this.periodTypeRepository = periodTypeRepository;
//    }
//
//    /**
//     * {@code POST  /period-types} : Create a new periodType.
//     *
//     * @param periodType the periodType to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new periodType, or with status {@code 400 (Bad Request)} if the periodType has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/period-types")
//    public ResponseEntity<PeriodType> createPeriodType(@RequestBody PeriodType periodType) throws URISyntaxException {
//        log.debug("REST request to save PeriodType : {}", periodType);
//        if (periodType.getId() != null) {
//            throw new BadRequestAlertException("A new periodType cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        PeriodType result = periodTypeService.save(periodType);
//        return ResponseEntity
//            .created(new URI("/api/period-types/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /period-types/:id} : Updates an existing periodType.
//     *
//     * @param id the id of the periodType to save.
//     * @param periodType the periodType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated periodType,
//     * or with status {@code 400 (Bad Request)} if the periodType is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the periodType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/period-types/{id}")
//    public ResponseEntity<PeriodType> updatePeriodType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody PeriodType periodType
//    ) throws URISyntaxException {
//        log.debug("REST request to update PeriodType : {}, {}", id, periodType);
//        if (periodType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, periodType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!periodTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        PeriodType result = periodTypeService.update(periodType);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, periodType.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /period-types/:id} : Partial updates given fields of an existing periodType, field will ignore if it is null
//     *
//     * @param id the id of the periodType to save.
//     * @param periodType the periodType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated periodType,
//     * or with status {@code 400 (Bad Request)} if the periodType is not valid,
//     * or with status {@code 404 (Not Found)} if the periodType is not found,
//     * or with status {@code 500 (Internal Server Error)} if the periodType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/period-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<PeriodType> partialUpdatePeriodType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody PeriodType periodType
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update PeriodType partially : {}, {}", id, periodType);
//        if (periodType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, periodType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!periodTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<PeriodType> result = periodTypeService.partialUpdate(periodType);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, periodType.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /period-types} : get all the periodTypes.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of periodTypes in body.
//     */
//    @GetMapping("/period-types")
//    public List<PeriodType> getAllPeriodTypes() {
//        log.debug("REST request to get all PeriodTypes");
//        return periodTypeService.findAll();
//    }
//
//    /**
//     * {@code GET  /period-types/:id} : get the "id" periodType.
//     *
//     * @param id the id of the periodType to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the periodType, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/period-types/{id}")
//    public ResponseEntity<PeriodType> getPeriodType(@PathVariable Long id) {
//        log.debug("REST request to get PeriodType : {}", id);
//        Optional<PeriodType> periodType = periodTypeService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(periodType);
//    }
//
//    /**
//     * {@code DELETE  /period-types/:id} : delete the "id" periodType.
//     *
//     * @param id the id of the periodType to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/period-types/{id}")
//    public ResponseEntity<Void> deletePeriodType(@PathVariable Long id) {
//        log.debug("REST request to delete PeriodType : {}", id);
//        periodTypeService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
