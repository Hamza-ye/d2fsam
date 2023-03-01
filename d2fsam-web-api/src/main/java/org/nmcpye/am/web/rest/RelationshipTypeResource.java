//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.relationship.RelationshipType;
//import org.nmcpye.am.relationship.RelationshipTypeRepository;
//import org.nmcpye.am.relationship.RelationshipTypeService;
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
// * REST controller for managing {@link org.nmcpye.am.relationship.RelationshipType}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class RelationshipTypeResource {
//
//    private final Logger log = LoggerFactory.getLogger(RelationshipTypeResource.class);
//
//    private static final String ENTITY_NAME = "relationshipType";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final RelationshipTypeService relationshipTypeService;
//
//    private final RelationshipTypeRepository relationshipTypeRepository;
//
//    public RelationshipTypeResource(
//        RelationshipTypeService relationshipTypeService,
//        RelationshipTypeRepository relationshipTypeRepository
//    ) {
//        this.relationshipTypeService = relationshipTypeService;
//        this.relationshipTypeRepository = relationshipTypeRepository;
//    }
//
//    /**
//     * {@code POST  /relationship-types} : Create a new relationshipType.
//     *
//     * @param relationshipType the relationshipType to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relationshipType, or with status {@code 400 (Bad Request)} if the relationshipType has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/relationship-types")
//    public ResponseEntity<RelationshipType> createRelationshipType(@Valid @RequestBody RelationshipType relationshipType)
//        throws URISyntaxException {
//        log.debug("REST request to save RelationshipType : {}", relationshipType);
//        if (relationshipType.getId() != null) {
//            throw new BadRequestAlertException("A new relationshipType cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        RelationshipType result = relationshipTypeService.save(relationshipType);
//        return ResponseEntity
//            .created(new URI("/api/relationship-types/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /relationship-types/:id} : Updates an existing relationshipType.
//     *
//     * @param id the id of the relationshipType to save.
//     * @param relationshipType the relationshipType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relationshipType,
//     * or with status {@code 400 (Bad Request)} if the relationshipType is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the relationshipType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/relationship-types/{id}")
//    public ResponseEntity<RelationshipType> updateRelationshipType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody RelationshipType relationshipType
//    ) throws URISyntaxException {
//        log.debug("REST request to update RelationshipType : {}, {}", id, relationshipType);
//        if (relationshipType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relationshipType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relationshipTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        RelationshipType result = relationshipTypeService.update(relationshipType);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relationshipType.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /relationship-types/:id} : Partial updates given fields of an existing relationshipType, field will ignore if it is null
//     *
//     * @param id the id of the relationshipType to save.
//     * @param relationshipType the relationshipType to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relationshipType,
//     * or with status {@code 400 (Bad Request)} if the relationshipType is not valid,
//     * or with status {@code 404 (Not Found)} if the relationshipType is not found,
//     * or with status {@code 500 (Internal Server Error)} if the relationshipType couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/relationship-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<RelationshipType> partialUpdateRelationshipType(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody RelationshipType relationshipType
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update RelationshipType partially : {}, {}", id, relationshipType);
//        if (relationshipType.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relationshipType.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relationshipTypeRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<RelationshipType> result = relationshipTypeService.partialUpdate(relationshipType);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relationshipType.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /relationship-types} : get all the relationshipTypes.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relationshipTypes in body.
//     */
//    @GetMapping("/relationship-types")
//    public List<RelationshipType> getAllRelationshipTypes(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all RelationshipTypes");
//        return relationshipTypeService.findAll();
//    }
//
//    /**
//     * {@code GET  /relationship-types/:id} : get the "id" relationshipType.
//     *
//     * @param id the id of the relationshipType to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relationshipType, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/relationship-types/{id}")
//    public ResponseEntity<RelationshipType> getRelationshipType(@PathVariable Long id) {
//        log.debug("REST request to get RelationshipType : {}", id);
//        Optional<RelationshipType> relationshipType = relationshipTypeService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(relationshipType);
//    }
//
//    /**
//     * {@code DELETE  /relationship-types/:id} : delete the "id" relationshipType.
//     *
//     * @param id the id of the relationshipType to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/relationship-types/{id}")
//    public ResponseEntity<Void> deleteRelationshipType(@PathVariable Long id) {
//        log.debug("REST request to delete RelationshipType : {}", id);
//        relationshipTypeService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
