//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.fileresource.FileResource;
//import org.nmcpye.am.fileresource.FileResourceRepository;
//import org.nmcpye.am.fileresource.FileResourceService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.FileResource}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class FileResourceResource {
//
//    private final Logger log = LoggerFactory.getLogger(FileResourceResource.class);
//
//    private static final String ENTITY_NAME = "fileResource";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final FileResourceService fileResourceService;
//
//    private final FileResourceRepository fileResourceRepository;
//
//    public FileResourceResource(FileResourceService fileResourceService, FileResourceRepository fileResourceRepository) {
//        this.fileResourceService = fileResourceService;
//        this.fileResourceRepository = fileResourceRepository;
//    }
//
//    /**
//     * {@code POST  /file-resources} : Create a new fileResource.
//     *
//     * @param fileResource the fileResource to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileResource, or with status {@code 400 (Bad Request)} if the fileResource has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/file-resources")
//    public ResponseEntity<FileResource> createFileResource(@Valid @RequestBody FileResource fileResource) throws URISyntaxException {
//        log.debug("REST request to save FileResource : {}", fileResource);
//        if (fileResource.getId() != null) {
//            throw new BadRequestAlertException("A new fileResource cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        FileResource result = fileResourceService.save(fileResource);
//        return ResponseEntity
//            .created(new URI("/api/file-resources/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /file-resources/:id} : Updates an existing fileResource.
//     *
//     * @param id the id of the fileResource to save.
//     * @param fileResource the fileResource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileResource,
//     * or with status {@code 400 (Bad Request)} if the fileResource is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the fileResource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/file-resources/{id}")
//    public ResponseEntity<FileResource> updateFileResource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody FileResource fileResource
//    ) throws URISyntaxException {
//        log.debug("REST request to update FileResource : {}, {}", id, fileResource);
//        if (fileResource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, fileResource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!fileResourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        FileResource result = fileResourceService.update(fileResource);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileResource.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /file-resources/:id} : Partial updates given fields of an existing fileResource, field will ignore if it is null
//     *
//     * @param id the id of the fileResource to save.
//     * @param fileResource the fileResource to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileResource,
//     * or with status {@code 400 (Bad Request)} if the fileResource is not valid,
//     * or with status {@code 404 (Not Found)} if the fileResource is not found,
//     * or with status {@code 500 (Internal Server Error)} if the fileResource couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/file-resources/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<FileResource> partialUpdateFileResource(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody FileResource fileResource
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update FileResource partially : {}, {}", id, fileResource);
//        if (fileResource.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, fileResource.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!fileResourceRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<FileResource> result = fileResourceService.partialUpdate(fileResource);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileResource.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /file-resources} : get all the fileResources.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileResources in body.
//     */
//    @GetMapping("/file-resources")
//    public List<FileResource> getAllFileResources(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all FileResources");
//        return fileResourceService.findAll();
//    }
//
//    /**
//     * {@code GET  /file-resources/:id} : get the "id" fileResource.
//     *
//     * @param id the id of the fileResource to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileResource, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/file-resources/{id}")
//    public ResponseEntity<FileResource> getFileResource(@PathVariable Long id) {
//        log.debug("REST request to get FileResource : {}", id);
//        Optional<FileResource> fileResource = fileResourceService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(fileResource);
//    }
//
//    /**
//     * {@code DELETE  /file-resources/:id} : delete the "id" fileResource.
//     *
//     * @param id the id of the fileResource to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/file-resources/{id}")
//    public ResponseEntity<Void> deleteFileResource(@PathVariable Long id) {
//        log.debug("REST request to delete FileResource : {}", id);
//        fileResourceService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
