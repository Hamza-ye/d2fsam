//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.assignment.Assignment;
//import org.nmcpye.am.assignment.AssignmentRepository;
//import org.nmcpye.am.assignment.AssignmentService;
//import org.nmcpye.am.common.OpenApi;
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
// * REST controller for managing {@link org.nmcpye.am.domain.Assignment}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class AssignmentResource {
//
//    private final Logger log = LoggerFactory.getLogger(AssignmentResource.class);
//
//    private static final String ENTITY_NAME = "assignment";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final AssignmentService assignmentService;
//
//    private final AssignmentRepository assignmentRepository;
//
//    public AssignmentResource(AssignmentService assignmentService, AssignmentRepository assignmentRepository) {
//        this.assignmentService = assignmentService;
//        this.assignmentRepository = assignmentRepository;
//    }
//
//    /**
//     * {@code POST  /assignments} : Create a new assignment.
//     *
//     * @param assignment the assignment to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assignment, or with status {@code 400 (Bad Request)} if the assignment has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/assignments")
//    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment) throws URISyntaxException {
//        log.debug("REST request to save Assignment : {}", assignment);
//        if (assignment.getId() != null) {
//            throw new BadRequestAlertException("A new assignment cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        Assignment result = assignmentService.save(assignment);
//        return ResponseEntity
//            .created(new URI("/api/assignments/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /assignments/:id} : Updates an existing assignment.
//     *
//     * @param id the id of the assignment to save.
//     * @param assignment the assignment to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignment,
//     * or with status {@code 400 (Bad Request)} if the assignment is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the assignment couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/assignments/{id}")
//    public ResponseEntity<Assignment> updateAssignment(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody Assignment assignment
//    ) throws URISyntaxException {
//        log.debug("REST request to update Assignment : {}, {}", id, assignment);
//        if (assignment.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, assignment.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!assignmentRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Assignment result = assignmentService.update(assignment);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignment.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /assignments/:id} : Partial updates given fields of an existing assignment, field will ignore if it is null
//     *
//     * @param id the id of the assignment to save.
//     * @param assignment the assignment to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignment,
//     * or with status {@code 400 (Bad Request)} if the assignment is not valid,
//     * or with status {@code 404 (Not Found)} if the assignment is not found,
//     * or with status {@code 500 (Internal Server Error)} if the assignment couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/assignments/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<Assignment> partialUpdateAssignment(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody Assignment assignment
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update Assignment partially : {}, {}", id, assignment);
//        if (assignment.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, assignment.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!assignmentRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<Assignment> result = assignmentService.partialUpdate(assignment);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignment.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /assignments} : get all the assignments.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assignments in body.
//     */
//    @GetMapping("/assignments")
//    public ResponseEntity<List<Assignment>> getAllAssignments(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of Assignments");
//        Page<Assignment> page;
//        if (eagerload) {
//            page = assignmentService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = assignmentService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /assignments/:id} : get the "id" assignment.
//     *
//     * @param id the id of the assignment to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assignment, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/assignments/{id}")
//    public ResponseEntity<Assignment> getAssignment(@PathVariable Long id) {
//        log.debug("REST request to get Assignment : {}", id);
//        Optional<Assignment> assignment = assignmentService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(assignment);
//    }
//
//    /**
//     * {@code DELETE  /assignments/:id} : delete the "id" assignment.
//     *
//     * @param id the id of the assignment to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/assignments/{id}")
//    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
//        log.debug("REST request to delete Assignment : {}", id);
//        assignmentService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
