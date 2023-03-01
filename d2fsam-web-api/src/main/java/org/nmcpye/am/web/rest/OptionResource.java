//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.option.Option;
//import org.nmcpye.am.option.OptionRepository;
//import org.nmcpye.am.option.OptionService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.Option}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class OptionResource {
//
//    private final Logger log = LoggerFactory.getLogger(OptionResource.class);
//
//    private static final String ENTITY_NAME = "option";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final OptionService optionService;
//
//    private final OptionRepository optionRepository;
//
//    public OptionResource(OptionService optionService, OptionRepository optionRepository) {
//        this.optionService = optionService;
//        this.optionRepository = optionRepository;
//    }
//
//    /**
//     * {@code POST  /options} : Create a new option.
//     *
//     * @param option the option to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new option, or with status {@code 400 (Bad Request)} if the option has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/options")
//    public ResponseEntity<Option> createOption(@Valid @RequestBody Option option) throws URISyntaxException {
//        log.debug("REST request to save Option : {}", option);
//        if (option.getId() != null) {
//            throw new BadRequestAlertException("A new option cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        Option result = optionService.save(option);
//        return ResponseEntity
//            .created(new URI("/api/options/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /options/:id} : Updates an existing option.
//     *
//     * @param id the id of the option to save.
//     * @param option the option to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated option,
//     * or with status {@code 400 (Bad Request)} if the option is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the option couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/options/{id}")
//    public ResponseEntity<Option> updateOption(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody Option option
//    ) throws URISyntaxException {
//        log.debug("REST request to update Option : {}, {}", id, option);
//        if (option.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, option.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!optionRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Option result = optionService.update(option);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, option.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /options/:id} : Partial updates given fields of an existing option, field will ignore if it is null
//     *
//     * @param id the id of the option to save.
//     * @param option the option to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated option,
//     * or with status {@code 400 (Bad Request)} if the option is not valid,
//     * or with status {@code 404 (Not Found)} if the option is not found,
//     * or with status {@code 500 (Internal Server Error)} if the option couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/options/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<Option> partialUpdateOption(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody Option option
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update Option partially : {}, {}", id, option);
//        if (option.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, option.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!optionRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<Option> result = optionService.partialUpdate(option);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, option.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /options} : get all the options.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of options in body.
//     */
//    @GetMapping("/options")
//    public List<Option> getAllOptions(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all Options");
//        return optionService.findAll();
//    }
//
//    /**
//     * {@code GET  /options/:id} : get the "id" option.
//     *
//     * @param id the id of the option to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the option, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/options/{id}")
//    public ResponseEntity<Option> getOption(@PathVariable Long id) {
//        log.debug("REST request to get Option : {}", id);
//        Optional<Option> option = optionService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(option);
//    }
//
//    /**
//     * {@code DELETE  /options/:id} : delete the "id" option.
//     *
//     * @param id the id of the option to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/options/{id}")
//    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
//        log.debug("REST request to delete Option : {}", id);
//        optionService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
