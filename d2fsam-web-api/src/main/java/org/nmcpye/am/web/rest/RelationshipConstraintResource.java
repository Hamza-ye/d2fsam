//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.relationship.RelationshipConstraint;
//import org.nmcpye.am.relationship.RelationshipConstraintRepository;
//import org.nmcpye.am.relationship.RelationshipConstraintService;
//import org.nmcpye.am.web.rest.errors.BadRequestAlertException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tech.jhipster.web.util.HeaderUtil;
//import tech.jhipster.web.util.ResponseUtil;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * REST controller for managing {@link org.nmcpye.am.relationship.RelationshipConstraint}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class RelationshipConstraintResource {
//
//    private final Logger log = LoggerFactory.getLogger(RelationshipConstraintResource.class);
//
//    private static final String ENTITY_NAME = "relationshipConstraint";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final RelationshipConstraintService relationshipConstraintService;
//
//    private final RelationshipConstraintRepository relationshipConstraintRepository;
//
//    public RelationshipConstraintResource(
//        RelationshipConstraintService relationshipConstraintService,
//        RelationshipConstraintRepository relationshipConstraintRepository
//    ) {
//        this.relationshipConstraintService = relationshipConstraintService;
//        this.relationshipConstraintRepository = relationshipConstraintRepository;
//    }
//
//    /**
//     * {@code POST  /relationship-constraints} : Create a new relationshipConstraint.
//     *
//     * @param relationshipConstraint the relationshipConstraint to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relationshipConstraint, or with status {@code 400 (Bad Request)} if the relationshipConstraint has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/relationship-constraints")
//    public ResponseEntity<RelationshipConstraint> createRelationshipConstraint(@RequestBody RelationshipConstraint relationshipConstraint)
//        throws URISyntaxException {
//        log.debug("REST request to save RelationshipConstraint : {}", relationshipConstraint);
//        if (relationshipConstraint.getId() != null) {
//            throw new BadRequestAlertException("A new relationshipConstraint cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        RelationshipConstraint result = relationshipConstraintService.save(relationshipConstraint);
//        return ResponseEntity
//            .created(new URI("/api/relationship-constraints/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /relationship-constraints/:id} : Updates an existing relationshipConstraint.
//     *
//     * @param id the id of the relationshipConstraint to save.
//     * @param relationshipConstraint the relationshipConstraint to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relationshipConstraint,
//     * or with status {@code 400 (Bad Request)} if the relationshipConstraint is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the relationshipConstraint couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/relationship-constraints/{id}")
//    public ResponseEntity<RelationshipConstraint> updateRelationshipConstraint(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody RelationshipConstraint relationshipConstraint
//    ) throws URISyntaxException {
//        log.debug("REST request to update RelationshipConstraint : {}, {}", id, relationshipConstraint);
//        if (relationshipConstraint.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relationshipConstraint.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relationshipConstraintRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        RelationshipConstraint result = relationshipConstraintService.update(relationshipConstraint);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relationshipConstraint.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /relationship-constraints/:id} : Partial updates given fields of an existing relationshipConstraint, field will ignore if it is null
//     *
//     * @param id the id of the relationshipConstraint to save.
//     * @param relationshipConstraint the relationshipConstraint to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relationshipConstraint,
//     * or with status {@code 400 (Bad Request)} if the relationshipConstraint is not valid,
//     * or with status {@code 404 (Not Found)} if the relationshipConstraint is not found,
//     * or with status {@code 500 (Internal Server Error)} if the relationshipConstraint couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/relationship-constraints/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<RelationshipConstraint> partialUpdateRelationshipConstraint(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody RelationshipConstraint relationshipConstraint
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update RelationshipConstraint partially : {}, {}", id, relationshipConstraint);
//        if (relationshipConstraint.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, relationshipConstraint.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!relationshipConstraintRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<RelationshipConstraint> result = relationshipConstraintService.partialUpdate(relationshipConstraint);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relationshipConstraint.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /relationship-constraints} : get all the relationshipConstraints.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relationshipConstraints in body.
//     */
//    @GetMapping("/relationship-constraints")
//    public List<RelationshipConstraint> getAllRelationshipConstraints(
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get all RelationshipConstraints");
//        return relationshipConstraintService.findAll();
//    }
//
//    /**
//     * {@code GET  /relationship-constraints/:id} : get the "id" relationshipConstraint.
//     *
//     * @param id the id of the relationshipConstraint to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relationshipConstraint, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/relationship-constraints/{id}")
//    public ResponseEntity<RelationshipConstraint> getRelationshipConstraint(@PathVariable Long id) {
//        log.debug("REST request to get RelationshipConstraint : {}", id);
//        Optional<RelationshipConstraint> relationshipConstraint = relationshipConstraintService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(relationshipConstraint);
//    }
//
//    /**
//     * {@code DELETE  /relationship-constraints/:id} : delete the "id" relationshipConstraint.
//     *
//     * @param id the id of the relationshipConstraint to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/relationship-constraints/{id}")
//    public ResponseEntity<Void> deleteRelationshipConstraint(@PathVariable Long id) {
//        log.debug("REST request to delete RelationshipConstraint : {}", id);
//        relationshipConstraintService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
