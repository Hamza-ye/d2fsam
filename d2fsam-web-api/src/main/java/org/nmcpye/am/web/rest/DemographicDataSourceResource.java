//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.demographicdata.DemographicDataSource;
//import org.nmcpye.am.demographicdata.DemographicDataSourceRepository;
//import org.nmcpye.am.demographicdata.DemographicDataSourceService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.DemographicDataSource}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class DemographicDataSourceResource {
//
//    private final Logger log = LoggerFactory.getLogger(DemographicDataSourceResource.class);
//
//    private static final String ENTITY_NAME = "demographicDataSource";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final DemographicDataSourceService demographicDataSourceService;
//
//    private final DemographicDataSourceRepository demographicDataSourceRepository;
//
//    public DemographicDataSourceResource(
//        DemographicDataSourceService demographicDataSourceService,
//        DemographicDataSourceRepository demographicDataSourceRepository
//    ) {
//        this.demographicDataSourceService = demographicDataSourceService;
//        this.demographicDataSourceRepository = demographicDataSourceRepository;
//    }
//
//    /**
//     * {@code POST  /demographic-data-sources} : Create a new demographicDataSource.
//     *
//     * @param demographicDataSource the demographicDataSource to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new demographicDataSource, or with status {@code 400 (Bad Request)} if the demographicDataSource has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/demographic-data-sources")
//    public ResponseEntity<DemographicDataSource> createDemographicDataSource(
//        @Valid @RequestBody DemographicDataSource demographicDataSource
//    ) throws URISyntaxException {
//        log.debug("REST request to save DemographicDataSource : {}", demographicDataSource);
//        if (demographicDataSource.getId() != null) {
//            throw new BadRequestAlertException("A new demographicDataSource cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DemographicDataSource result = demographicDataSourceService.save(demographicDataSource);
//        return ResponseEntity
//            .created(new URI("/api/demographic-data-sources/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /demographic-data-sources/:id} : Updates an existing demographicDataSource.
//     *
//     * @param id the id of the demographicDataSource to save.
//     * @param demographicDataSource the demographicDataSource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demographicDataSource,
//     * or with status {@code 400 (Bad Request)} if the demographicDataSource is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the demographicDataSource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/demographic-data-sources/{id}")
//    public ResponseEntity<DemographicDataSource> updateDemographicDataSource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody DemographicDataSource demographicDataSource
//    ) throws URISyntaxException {
//        log.debug("REST request to update DemographicDataSource : {}, {}", id, demographicDataSource);
//        if (demographicDataSource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, demographicDataSource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!demographicDataSourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        DemographicDataSource result = demographicDataSourceService.update(demographicDataSource);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, demographicDataSource.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /demographic-data-sources/:id} : Partial updates given fields of an existing demographicDataSource, field will ignore if it is null
//     *
//     * @param id the id of the demographicDataSource to save.
//     * @param demographicDataSource the demographicDataSource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demographicDataSource,
//     * or with status {@code 400 (Bad Request)} if the demographicDataSource is not valid,
//     * or with status {@code 404 (Not Found)} if the demographicDataSource is not found,
//     * or with status {@code 500 (Internal Server Error)} if the demographicDataSource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/demographic-data-sources/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<DemographicDataSource> partialUpdateDemographicDataSource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody DemographicDataSource demographicDataSource
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update DemographicDataSource partially : {}, {}", id, demographicDataSource);
//        if (demographicDataSource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, demographicDataSource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!demographicDataSourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<DemographicDataSource> result = demographicDataSourceService.partialUpdate(demographicDataSource);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, demographicDataSource.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /demographic-data-sources} : get all the demographicDataSources.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of demographicDataSources in body.
//     */
//    @GetMapping("/demographic-data-sources")
//    public ResponseEntity<List<DemographicDataSource>> getAllDemographicDataSources(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of DemographicDataSources");
//        Page<DemographicDataSource> page;
//        if (eagerload) {
//            page = demographicDataSourceService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = demographicDataSourceService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /demographic-data-sources/:id} : get the "id" demographicDataSource.
//     *
//     * @param id the id of the demographicDataSource to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the demographicDataSource, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/demographic-data-sources/{id}")
//    public ResponseEntity<DemographicDataSource> getDemographicDataSource(@PathVariable Long id) {
//        log.debug("REST request to get DemographicDataSource : {}", id);
//        Optional<DemographicDataSource> demographicDataSource = demographicDataSourceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(demographicDataSource);
//    }
//
//    /**
//     * {@code DELETE  /demographic-data-sources/:id} : delete the "id" demographicDataSource.
//     *
//     * @param id the id of the demographicDataSource to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/demographic-data-sources/{id}")
//    public ResponseEntity<Void> deleteDemographicDataSource(@PathVariable Long id) {
//        log.debug("REST request to delete DemographicDataSource : {}", id);
//        demographicDataSourceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
