//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.period.DataInputPeriod;
//import org.nmcpye.am.period.DataInputPeriodRepository;
//import org.nmcpye.am.period.DataInputPeriodService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.DataInputPeriod}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class DataInputPeriodResource {
//
//    private final Logger log = LoggerFactory.getLogger(DataInputPeriodResource.class);
//
//    private static final String ENTITY_NAME = "dataInputPeriod";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final DataInputPeriodService dataInputPeriodService;
//
//    private final DataInputPeriodRepository dataInputPeriodRepository;
//
//    public DataInputPeriodResource(DataInputPeriodService dataInputPeriodService, DataInputPeriodRepository dataInputPeriodRepository) {
//        this.dataInputPeriodService = dataInputPeriodService;
//        this.dataInputPeriodRepository = dataInputPeriodRepository;
//    }
//
//    /**
//     * {@code POST  /data-input-periods} : Create a new dataInputPeriod.
//     *
//     * @param dataInputPeriod the dataInputPeriod to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataInputPeriod, or with status {@code 400 (Bad Request)} if the dataInputPeriod has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/data-input-periods")
//    public ResponseEntity<DataInputPeriod> createDataInputPeriod(@RequestBody DataInputPeriod dataInputPeriod) throws URISyntaxException {
//        log.debug("REST request to save DataInputPeriod : {}", dataInputPeriod);
//        if (dataInputPeriod.getId() != null) {
//            throw new BadRequestAlertException("A new dataInputPeriod cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DataInputPeriod result = dataInputPeriodService.save(dataInputPeriod);
//        return ResponseEntity
//            .created(new URI("/api/data-input-periods/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /data-input-periods/:id} : Updates an existing dataInputPeriod.
//     *
//     * @param id the id of the dataInputPeriod to save.
//     * @param dataInputPeriod the dataInputPeriod to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataInputPeriod,
//     * or with status {@code 400 (Bad Request)} if the dataInputPeriod is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the dataInputPeriod couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/data-input-periods/{id}")
//    public ResponseEntity<DataInputPeriod> updateDataInputPeriod(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody DataInputPeriod dataInputPeriod
//    ) throws URISyntaxException {
//        log.debug("REST request to update DataInputPeriod : {}, {}", id, dataInputPeriod);
//        if (dataInputPeriod.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataInputPeriod.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataInputPeriodRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        DataInputPeriod result = dataInputPeriodService.update(dataInputPeriod);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataInputPeriod.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /data-input-periods/:id} : Partial updates given fields of an existing dataInputPeriod, field will ignore if it is null
//     *
//     * @param id the id of the dataInputPeriod to save.
//     * @param dataInputPeriod the dataInputPeriod to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataInputPeriod,
//     * or with status {@code 400 (Bad Request)} if the dataInputPeriod is not valid,
//     * or with status {@code 404 (Not Found)} if the dataInputPeriod is not found,
//     * or with status {@code 500 (Internal Server Error)} if the dataInputPeriod couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/data-input-periods/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<DataInputPeriod> partialUpdateDataInputPeriod(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody DataInputPeriod dataInputPeriod
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update DataInputPeriod partially : {}, {}", id, dataInputPeriod);
//        if (dataInputPeriod.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataInputPeriod.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataInputPeriodRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<DataInputPeriod> result = dataInputPeriodService.partialUpdate(dataInputPeriod);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataInputPeriod.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /data-input-periods} : get all the dataInputPeriods.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataInputPeriods in body.
//     */
//    @GetMapping("/data-input-periods")
//    public List<DataInputPeriod> getAllDataInputPeriods(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all DataInputPeriods");
//        return dataInputPeriodService.findAll();
//    }
//
//    /**
//     * {@code GET  /data-input-periods/:id} : get the "id" dataInputPeriod.
//     *
//     * @param id the id of the dataInputPeriod to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataInputPeriod, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/data-input-periods/{id}")
//    public ResponseEntity<DataInputPeriod> getDataInputPeriod(@PathVariable Long id) {
//        log.debug("REST request to get DataInputPeriod : {}", id);
//        Optional<DataInputPeriod> dataInputPeriod = dataInputPeriodService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(dataInputPeriod);
//    }
//
//    /**
//     * {@code DELETE  /data-input-periods/:id} : delete the "id" dataInputPeriod.
//     *
//     * @param id the id of the dataInputPeriod to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/data-input-periods/{id}")
//    public ResponseEntity<Void> deleteDataInputPeriod(@PathVariable Long id) {
//        log.debug("REST request to delete DataInputPeriod : {}", id);
//        dataInputPeriodService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
