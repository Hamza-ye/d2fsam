//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.option.OptionSet;
//import org.nmcpye.am.option.OptionSetRepository;
//import org.nmcpye.am.option.OptionSetService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.OptionSet}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class OptionSetResource {
//
//    private final Logger log = LoggerFactory.getLogger(OptionSetResource.class);
//
//    private static final String ENTITY_NAME = "optionSet";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final OptionSetService optionSetService;
//
//    private final OptionSetRepository optionSetRepository;
//
//    public OptionSetResource(OptionSetService optionSetService, OptionSetRepository optionSetRepository) {
//        this.optionSetService = optionSetService;
//        this.optionSetRepository = optionSetRepository;
//    }
//
//    /**
//     * {@code POST  /option-sets} : Create a new optionSet.
//     *
//     * @param optionSet the optionSet to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new optionSet, or with status {@code 400 (Bad Request)} if the optionSet has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/option-sets")
//    public ResponseEntity<OptionSet> createOptionSet(@Valid @RequestBody OptionSet optionSet) throws URISyntaxException {
//        log.debug("REST request to save OptionSet : {}", optionSet);
//        if (optionSet.getId() != null) {
//            throw new BadRequestAlertException("A new optionSet cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        OptionSet result = optionSetService.save(optionSet);
//        return ResponseEntity
//            .created(new URI("/api/option-sets/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /option-sets/:id} : Updates an existing optionSet.
//     *
//     * @param id the id of the optionSet to save.
//     * @param optionSet the optionSet to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated optionSet,
//     * or with status {@code 400 (Bad Request)} if the optionSet is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the optionSet couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/option-sets/{id}")
//    public ResponseEntity<OptionSet> updateOptionSet(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody OptionSet optionSet
//    ) throws URISyntaxException {
//        log.debug("REST request to update OptionSet : {}, {}", id, optionSet);
//        if (optionSet.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, optionSet.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!optionSetRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        OptionSet result = optionSetService.update(optionSet);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, optionSet.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /option-sets/:id} : Partial updates given fields of an existing optionSet, field will ignore if it is null
//     *
//     * @param id the id of the optionSet to save.
//     * @param optionSet the optionSet to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated optionSet,
//     * or with status {@code 400 (Bad Request)} if the optionSet is not valid,
//     * or with status {@code 404 (Not Found)} if the optionSet is not found,
//     * or with status {@code 500 (Internal Server Error)} if the optionSet couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/option-sets/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<OptionSet> partialUpdateOptionSet(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody OptionSet optionSet
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update OptionSet partially : {}, {}", id, optionSet);
//        if (optionSet.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, optionSet.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!optionSetRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<OptionSet> result = optionSetService.partialUpdate(optionSet);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, optionSet.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /option-sets} : get all the optionSets.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of optionSets in body.
//     */
//    @GetMapping("/option-sets")
//    public List<OptionSet> getAllOptionSets(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all OptionSets");
//        return optionSetService.findAll();
//    }
//
//    /**
//     * {@code GET  /option-sets/:id} : get the "id" optionSet.
//     *
//     * @param id the id of the optionSet to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the optionSet, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/option-sets/{id}")
//    public ResponseEntity<OptionSet> getOptionSet(@PathVariable Long id) {
//        log.debug("REST request to get OptionSet : {}", id);
//        Optional<OptionSet> optionSet = optionSetService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(optionSet);
//    }
//
//    /**
//     * {@code DELETE  /option-sets/:id} : delete the "id" optionSet.
//     *
//     * @param id the id of the optionSet to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/option-sets/{id}")
//    public ResponseEntity<Void> deleteOptionSet(@PathVariable Long id) {
//        log.debug("REST request to delete OptionSet : {}", id);
//        optionSetService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
