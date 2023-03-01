//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.datavalue.DataValue;
//import org.nmcpye.am.datavalue.DataValueRepository;
//import org.nmcpye.am.datavalue.DataValueService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.DataValue}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class DataValueResource {
//
//    private final Logger log = LoggerFactory.getLogger(DataValueResource.class);
//
//    private static final String ENTITY_NAME = "dataValue";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final DataValueService dataValueService;
//
//    private final DataValueRepository dataValueRepository;
//
//    public DataValueResource(DataValueService dataValueService, DataValueRepository dataValueRepository) {
//        this.dataValueService = dataValueService;
//        this.dataValueRepository = dataValueRepository;
//    }
//
//    /**
//     * {@code POST  /data-values} : Create a new dataValue.
//     *
//     * @param dataValue the dataValue to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataValue, or with status {@code 400 (Bad Request)} if the dataValue has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/data-values")
//    public ResponseEntity<DataValue> createDataValue(@Valid @RequestBody DataValue dataValue) throws URISyntaxException {
//        log.debug("REST request to save DataValue : {}", dataValue);
//        if (dataValue.getId() != null) {
//            throw new BadRequestAlertException("A new dataValue cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DataValue result = dataValueService.save(dataValue);
//        return ResponseEntity
//            .created(new URI("/api/data-values/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /data-values/:id} : Updates an existing dataValue.
//     *
//     * @param id the id of the dataValue to save.
//     * @param dataValue the dataValue to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataValue,
//     * or with status {@code 400 (Bad Request)} if the dataValue is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the dataValue couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/data-values/{id}")
//    public ResponseEntity<DataValue> updateDataValue(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody DataValue dataValue
//    ) throws URISyntaxException {
//        log.debug("REST request to update DataValue : {}, {}", id, dataValue);
//        if (dataValue.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataValue.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataValueRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        DataValue result = dataValueService.update(dataValue);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataValue.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /data-values/:id} : Partial updates given fields of an existing dataValue, field will ignore if it is null
//     *
//     * @param id the id of the dataValue to save.
//     * @param dataValue the dataValue to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataValue,
//     * or with status {@code 400 (Bad Request)} if the dataValue is not valid,
//     * or with status {@code 404 (Not Found)} if the dataValue is not found,
//     * or with status {@code 500 (Internal Server Error)} if the dataValue couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/data-values/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<DataValue> partialUpdateDataValue(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody DataValue dataValue
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update DataValue partially : {}, {}", id, dataValue);
//        if (dataValue.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataValue.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataValueRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<DataValue> result = dataValueService.partialUpdate(dataValue);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataValue.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /data-values} : get all the dataValues.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataValues in body.
//     */
//    @GetMapping("/data-values")
//    public List<DataValue> getAllDataValues(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all DataValues");
//        return dataValueService.findAll();
//    }
//
//    /**
//     * {@code GET  /data-values/:id} : get the "id" dataValue.
//     *
//     * @param id the id of the dataValue to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataValue, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/data-values/{id}")
//    public ResponseEntity<DataValue> getDataValue(@PathVariable Long id) {
//        log.debug("REST request to get DataValue : {}", id);
//        Optional<DataValue> dataValue = dataValueService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(dataValue);
//    }
//
//    /**
//     * {@code DELETE  /data-values/:id} : delete the "id" dataValue.
//     *
//     * @param id the id of the dataValue to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/data-values/{id}")
//    public ResponseEntity<Void> deleteDataValue(@PathVariable Long id) {
//        log.debug("REST request to delete DataValue : {}", id);
//        dataValueService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
