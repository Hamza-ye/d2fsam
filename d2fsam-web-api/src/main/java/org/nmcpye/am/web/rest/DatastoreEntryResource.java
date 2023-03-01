//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.datastore.DatastoreEntry;
//import org.nmcpye.am.datastore.DatastoreEntryRepository;
//import org.nmcpye.am.datastore.DatastoreEntryService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.DatastoreEntry}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class DatastoreEntryResource {
//
//    private final Logger log = LoggerFactory.getLogger(DatastoreEntryResource.class);
//
//    private static final String ENTITY_NAME = "datastoreEntry";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final DatastoreEntryService datastoreEntryService;
//
//    private final DatastoreEntryRepository datastoreEntryRepository;
//
//    public DatastoreEntryResource(DatastoreEntryService datastoreEntryService, DatastoreEntryRepository datastoreEntryRepository) {
//        this.datastoreEntryService = datastoreEntryService;
//        this.datastoreEntryRepository = datastoreEntryRepository;
//    }
//
//    /**
//     * {@code POST  /datastore-entries} : Create a new datastoreEntry.
//     *
//     * @param datastoreEntry the datastoreEntry to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new datastoreEntry, or with status {@code 400 (Bad Request)} if the datastoreEntry has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/datastore-entries")
//    public ResponseEntity<DatastoreEntry> createDatastoreEntry(@Valid @RequestBody DatastoreEntry datastoreEntry)
//        throws URISyntaxException {
//        log.debug("REST request to save DatastoreEntry : {}", datastoreEntry);
//        if (datastoreEntry.getId() != null) {
//            throw new BadRequestAlertException("A new datastoreEntry cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DatastoreEntry result = datastoreEntryService.save(datastoreEntry);
//        return ResponseEntity
//            .created(new URI("/api/datastore-entries/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /datastore-entries/:id} : Updates an existing datastoreEntry.
//     *
//     * @param id the id of the datastoreEntry to save.
//     * @param datastoreEntry the datastoreEntry to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated datastoreEntry,
//     * or with status {@code 400 (Bad Request)} if the datastoreEntry is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the datastoreEntry couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/datastore-entries/{id}")
//    public ResponseEntity<DatastoreEntry> updateDatastoreEntry(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody DatastoreEntry datastoreEntry
//    ) throws URISyntaxException {
//        log.debug("REST request to update DatastoreEntry : {}, {}", id, datastoreEntry);
//        if (datastoreEntry.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, datastoreEntry.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!datastoreEntryRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        DatastoreEntry result = datastoreEntryService.update(datastoreEntry);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, datastoreEntry.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /datastore-entries/:id} : Partial updates given fields of an existing datastoreEntry, field will ignore if it is null
//     *
//     * @param id the id of the datastoreEntry to save.
//     * @param datastoreEntry the datastoreEntry to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated datastoreEntry,
//     * or with status {@code 400 (Bad Request)} if the datastoreEntry is not valid,
//     * or with status {@code 404 (Not Found)} if the datastoreEntry is not found,
//     * or with status {@code 500 (Internal Server Error)} if the datastoreEntry couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/datastore-entries/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<DatastoreEntry> partialUpdateDatastoreEntry(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody DatastoreEntry datastoreEntry
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update DatastoreEntry partially : {}, {}", id, datastoreEntry);
//        if (datastoreEntry.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, datastoreEntry.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!datastoreEntryRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<DatastoreEntry> result = datastoreEntryService.partialUpdate(datastoreEntry);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, datastoreEntry.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /datastore-entries} : get all the datastoreEntries.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of datastoreEntries in body.
//     */
//    @GetMapping("/datastore-entries")
//    public List<DatastoreEntry> getAllDatastoreEntries(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all DatastoreEntries");
//        return datastoreEntryService.findAll();
//    }
//
//    /**
//     * {@code GET  /datastore-entries/:id} : get the "id" datastoreEntry.
//     *
//     * @param id the id of the datastoreEntry to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the datastoreEntry, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/datastore-entries/{id}")
//    public ResponseEntity<DatastoreEntry> getDatastoreEntry(@PathVariable Long id) {
//        log.debug("REST request to get DatastoreEntry : {}", id);
//        Optional<DatastoreEntry> datastoreEntry = datastoreEntryService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(datastoreEntry);
//    }
//
//    /**
//     * {@code DELETE  /datastore-entries/:id} : delete the "id" datastoreEntry.
//     *
//     * @param id the id of the datastoreEntry to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/datastore-entries/{id}")
//    public ResponseEntity<Void> deleteDatastoreEntry(@PathVariable Long id) {
//        log.debug("REST request to delete DatastoreEntry : {}", id);
//        datastoreEntryService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
