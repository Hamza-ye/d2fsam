//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.version.MetadataVersion;
//import org.nmcpye.am.version.MetadataVersionRepository;
//import org.nmcpye.am.version.MetadataVersionService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.MetadataVersion}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class MetadataVersionResource {
//
//    private final Logger log = LoggerFactory.getLogger(MetadataVersionResource.class);
//
//    private static final String ENTITY_NAME = "metadataVersion";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final MetadataVersionService metadataVersionService;
//
//    private final MetadataVersionRepository metadataVersionRepository;
//
//    public MetadataVersionResource(MetadataVersionService metadataVersionService, MetadataVersionRepository metadataVersionRepository) {
//        this.metadataVersionService = metadataVersionService;
//        this.metadataVersionRepository = metadataVersionRepository;
//    }
//
//    /**
//     * {@code POST  /metadata-versions} : Create a new metadataVersion.
//     *
//     * @param metadataVersion the metadataVersion to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metadataVersion, or with status {@code 400 (Bad Request)} if the metadataVersion has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/metadata-versions")
//    public ResponseEntity<MetadataVersion> createMetadataVersion(@Valid @RequestBody MetadataVersion metadataVersion)
//        throws URISyntaxException {
//        log.debug("REST request to save MetadataVersion : {}", metadataVersion);
//        if (metadataVersion.getId() != null) {
//            throw new BadRequestAlertException("A new metadataVersion cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        MetadataVersion result = metadataVersionService.save(metadataVersion);
//        return ResponseEntity
//            .created(new URI("/api/metadata-versions/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /metadata-versions/:id} : Updates an existing metadataVersion.
//     *
//     * @param id the id of the metadataVersion to save.
//     * @param metadataVersion the metadataVersion to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataVersion,
//     * or with status {@code 400 (Bad Request)} if the metadataVersion is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the metadataVersion couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/metadata-versions/{id}")
//    public ResponseEntity<MetadataVersion> updateMetadataVersion(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody MetadataVersion metadataVersion
//    ) throws URISyntaxException {
//        log.debug("REST request to update MetadataVersion : {}, {}", id, metadataVersion);
//        if (metadataVersion.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, metadataVersion.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!metadataVersionRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        MetadataVersion result = metadataVersionService.update(metadataVersion);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metadataVersion.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /metadata-versions/:id} : Partial updates given fields of an existing metadataVersion, field will ignore if it is null
//     *
//     * @param id the id of the metadataVersion to save.
//     * @param metadataVersion the metadataVersion to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataVersion,
//     * or with status {@code 400 (Bad Request)} if the metadataVersion is not valid,
//     * or with status {@code 404 (Not Found)} if the metadataVersion is not found,
//     * or with status {@code 500 (Internal Server Error)} if the metadataVersion couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/metadata-versions/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<MetadataVersion> partialUpdateMetadataVersion(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody MetadataVersion metadataVersion
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update MetadataVersion partially : {}, {}", id, metadataVersion);
//        if (metadataVersion.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, metadataVersion.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!metadataVersionRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<MetadataVersion> result = metadataVersionService.partialUpdate(metadataVersion);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metadataVersion.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /metadata-versions} : get all the metadataVersions.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metadataVersions in body.
//     */
//    @GetMapping("/metadata-versions")
//    public List<MetadataVersion> getAllMetadataVersions(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all MetadataVersions");
//        return metadataVersionService.findAll();
//    }
//
//    /**
//     * {@code GET  /metadata-versions/:id} : get the "id" metadataVersion.
//     *
//     * @param id the id of the metadataVersion to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataVersion, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/metadata-versions/{id}")
//    public ResponseEntity<MetadataVersion> getMetadataVersion(@PathVariable Long id) {
//        log.debug("REST request to get MetadataVersion : {}", id);
//        Optional<MetadataVersion> metadataVersion = metadataVersionService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(metadataVersion);
//    }
//
//    /**
//     * {@code DELETE  /metadata-versions/:id} : delete the "id" metadataVersion.
//     *
//     * @param id the id of the metadataVersion to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/metadata-versions/{id}")
//    public ResponseEntity<Void> deleteMetadataVersion(@PathVariable Long id) {
//        log.debug("REST request to delete MetadataVersion : {}", id);
//        metadataVersionService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
