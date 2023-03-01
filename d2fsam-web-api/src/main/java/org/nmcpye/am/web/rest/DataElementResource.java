//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.dataelement.DataElement;
//import org.nmcpye.am.dataelement.DataElementRepository;
//import org.nmcpye.am.dataelement.DataElementService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.DataElement}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class DataElementResource {
//
//    private final Logger log = LoggerFactory.getLogger(DataElementResource.class);
//
//    private static final String ENTITY_NAME = "dataElement";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final DataElementService dataElementService;
//
//    private final DataElementRepository dataElementRepository;
//
//    public DataElementResource(DataElementService dataElementService, DataElementRepository dataElementRepository) {
//        this.dataElementService = dataElementService;
//        this.dataElementRepository = dataElementRepository;
//    }
//
//    /**
//     * {@code POST  /data-elements} : Create a new dataElement.
//     *
//     * @param dataElement the dataElement to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataElement, or with status {@code 400 (Bad Request)} if the dataElement has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/data-elements")
//    public ResponseEntity<DataElement> createDataElement(@Valid @RequestBody DataElement dataElement) throws URISyntaxException {
//        log.debug("REST request to save DataElement : {}", dataElement);
//        if (dataElement.getId() != null) {
//            throw new BadRequestAlertException("A new dataElement cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DataElement result = dataElementService.save(dataElement);
//        return ResponseEntity
//            .created(new URI("/api/data-elements/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /data-elements/:id} : Updates an existing dataElement.
//     *
//     * @param id the id of the dataElement to save.
//     * @param dataElement the dataElement to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataElement,
//     * or with status {@code 400 (Bad Request)} if the dataElement is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the dataElement couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/data-elements/{id}")
//    public ResponseEntity<DataElement> updateDataElement(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody DataElement dataElement
//    ) throws URISyntaxException {
//        log.debug("REST request to update DataElement : {}, {}", id, dataElement);
//        if (dataElement.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataElement.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataElementRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        DataElement result = dataElementService.update(dataElement);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataElement.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /data-elements/:id} : Partial updates given fields of an existing dataElement, field will ignore if it is null
//     *
//     * @param id the id of the dataElement to save.
//     * @param dataElement the dataElement to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataElement,
//     * or with status {@code 400 (Bad Request)} if the dataElement is not valid,
//     * or with status {@code 404 (Not Found)} if the dataElement is not found,
//     * or with status {@code 500 (Internal Server Error)} if the dataElement couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/data-elements/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<DataElement> partialUpdateDataElement(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody DataElement dataElement
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update DataElement partially : {}, {}", id, dataElement);
//        if (dataElement.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, dataElement.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!dataElementRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<DataElement> result = dataElementService.partialUpdate(dataElement);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataElement.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /data-elements} : get all the dataElements.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataElements in body.
//     */
//    @GetMapping("/data-elements")
//    public ResponseEntity<List<DataElement>> getAllDataElements(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of DataElements");
//        Page<DataElement> page;
//        if (eagerload) {
//            page = dataElementService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = dataElementService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /data-elements/:id} : get the "id" dataElement.
//     *
//     * @param id the id of the dataElement to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataElement, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/data-elements/{id}")
//    public ResponseEntity<DataElement> getDataElement(@PathVariable Long id) {
//        log.debug("REST request to get DataElement : {}", id);
//        Optional<DataElement> dataElement = dataElementService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(dataElement);
//    }
//
//    /**
//     * {@code DELETE  /data-elements/:id} : delete the "id" dataElement.
//     *
//     * @param id the id of the dataElement to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/data-elements/{id}")
//    public ResponseEntity<Void> deleteDataElement(@PathVariable Long id) {
//        log.debug("REST request to delete DataElement : {}", id);
//        dataElementService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
