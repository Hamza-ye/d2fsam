//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.fileresource.ExternalFileResource;
//import org.nmcpye.am.fileresource.ExternalFileResourceRepository;
//import org.nmcpye.am.fileresource.ExternalFileResourceService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.ExternalFileResource}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class ExternalFileResourceResource {
//
//    private final Logger log = LoggerFactory.getLogger(ExternalFileResourceResource.class);
//
//    private static final String ENTITY_NAME = "externalFileResource";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final ExternalFileResourceService externalFileResourceService;
//
//    private final ExternalFileResourceRepository externalFileResourceRepository;
//
//    public ExternalFileResourceResource(
//        ExternalFileResourceService externalFileResourceService,
//        ExternalFileResourceRepository externalFileResourceRepository
//    ) {
//        this.externalFileResourceService = externalFileResourceService;
//        this.externalFileResourceRepository = externalFileResourceRepository;
//    }
//
//    /**
//     * {@code POST  /external-file-resources} : Create a new externalFileResource.
//     *
//     * @param externalFileResource the externalFileResource to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new externalFileResource, or with status {@code 400 (Bad Request)} if the externalFileResource has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/external-file-resources")
//    public ResponseEntity<ExternalFileResource> createExternalFileResource(@Valid @RequestBody ExternalFileResource externalFileResource)
//        throws URISyntaxException {
//        log.debug("REST request to save ExternalFileResource : {}", externalFileResource);
//        if (externalFileResource.getId() != null) {
//            throw new BadRequestAlertException("A new externalFileResource cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ExternalFileResource result = externalFileResourceService.save(externalFileResource);
//        return ResponseEntity
//            .created(new URI("/api/external-file-resources/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /external-file-resources/:id} : Updates an existing externalFileResource.
//     *
//     * @param id the id of the externalFileResource to save.
//     * @param externalFileResource the externalFileResource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalFileResource,
//     * or with status {@code 400 (Bad Request)} if the externalFileResource is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the externalFileResource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/external-file-resources/{id}")
//    public ResponseEntity<ExternalFileResource> updateExternalFileResource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody ExternalFileResource externalFileResource
//    ) throws URISyntaxException {
//        log.debug("REST request to update ExternalFileResource : {}, {}", id, externalFileResource);
//        if (externalFileResource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, externalFileResource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!externalFileResourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        ExternalFileResource result = externalFileResourceService.update(externalFileResource);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, externalFileResource.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /external-file-resources/:id} : Partial updates given fields of an existing externalFileResource, field will ignore if it is null
//     *
//     * @param id the id of the externalFileResource to save.
//     * @param externalFileResource the externalFileResource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalFileResource,
//     * or with status {@code 400 (Bad Request)} if the externalFileResource is not valid,
//     * or with status {@code 404 (Not Found)} if the externalFileResource is not found,
//     * or with status {@code 500 (Internal Server Error)} if the externalFileResource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/external-file-resources/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<ExternalFileResource> partialUpdateExternalFileResource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody ExternalFileResource externalFileResource
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update ExternalFileResource partially : {}, {}", id, externalFileResource);
//        if (externalFileResource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, externalFileResource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!externalFileResourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<ExternalFileResource> result = externalFileResourceService.partialUpdate(externalFileResource);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, externalFileResource.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /external-file-resources} : get all the externalFileResources.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of externalFileResources in body.
//     */
//    @GetMapping("/external-file-resources")
//    public List<ExternalFileResource> getAllExternalFileResources(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all ExternalFileResources");
//        return externalFileResourceService.findAll();
//    }
//
//    /**
//     * {@code GET  /external-file-resources/:id} : get the "id" externalFileResource.
//     *
//     * @param id the id of the externalFileResource to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the externalFileResource, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/external-file-resources/{id}")
//    public ResponseEntity<ExternalFileResource> getExternalFileResource(@PathVariable Long id) {
//        log.debug("REST request to get ExternalFileResource : {}", id);
//        Optional<ExternalFileResource> externalFileResource = externalFileResourceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(externalFileResource);
//    }
//
//    /**
//     * {@code DELETE  /external-file-resources/:id} : delete the "id" externalFileResource.
//     *
//     * @param id the id of the externalFileResource to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/external-file-resources/{id}")
//    public ResponseEntity<Void> deleteExternalFileResource(@PathVariable Long id) {
//        log.debug("REST request to delete ExternalFileResource : {}", id);
//        externalFileResourceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
