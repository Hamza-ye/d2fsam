//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.sqlview.SqlView;
//import org.nmcpye.am.sqlview.SqlViewRepository;
//import org.nmcpye.am.sqlview.SqlViewService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.SqlView}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class SqlViewResource {
//
//    private final Logger log = LoggerFactory.getLogger(SqlViewResource.class);
//
//    private static final String ENTITY_NAME = "sqlView";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final SqlViewService sqlViewService;
//
//    private final SqlViewRepository sqlViewRepository;
//
//    public SqlViewResource(SqlViewService sqlViewService, SqlViewRepository sqlViewRepository) {
//        this.sqlViewService = sqlViewService;
//        this.sqlViewRepository = sqlViewRepository;
//    }
//
//    /**
//     * {@code POST  /sql-views} : Create a new sqlView.
//     *
//     * @param sqlView the sqlView to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sqlView, or with status {@code 400 (Bad Request)} if the sqlView has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/sql-views")
//    public ResponseEntity<SqlView> createSqlView(@Valid @RequestBody SqlView sqlView) throws URISyntaxException {
//        log.debug("REST request to save SqlView : {}", sqlView);
//        if (sqlView.getId() != null) {
//            throw new BadRequestAlertException("A new sqlView cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        SqlView result = sqlViewService.save(sqlView);
//        return ResponseEntity
//            .created(new URI("/api/sql-views/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /sql-views/:id} : Updates an existing sqlView.
//     *
//     * @param id the id of the sqlView to save.
//     * @param sqlView the sqlView to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sqlView,
//     * or with status {@code 400 (Bad Request)} if the sqlView is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the sqlView couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/sql-views/{id}")
//    public ResponseEntity<SqlView> updateSqlView(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody SqlView sqlView
//    ) throws URISyntaxException {
//        log.debug("REST request to update SqlView : {}, {}", id, sqlView);
//        if (sqlView.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, sqlView.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!sqlViewRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        SqlView result = sqlViewService.update(sqlView);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sqlView.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /sql-views/:id} : Partial updates given fields of an existing sqlView, field will ignore if it is null
//     *
//     * @param id the id of the sqlView to save.
//     * @param sqlView the sqlView to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sqlView,
//     * or with status {@code 400 (Bad Request)} if the sqlView is not valid,
//     * or with status {@code 404 (Not Found)} if the sqlView is not found,
//     * or with status {@code 500 (Internal Server Error)} if the sqlView couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/sql-views/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<SqlView> partialUpdateSqlView(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody SqlView sqlView
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update SqlView partially : {}, {}", id, sqlView);
//        if (sqlView.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, sqlView.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!sqlViewRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<SqlView> result = sqlViewService.partialUpdate(sqlView);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sqlView.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /sql-views} : get all the sqlViews.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sqlViews in body.
//     */
//    @GetMapping("/sql-views")
//    public List<SqlView> getAllSqlViews(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all SqlViews");
//        return sqlViewService.findAll();
//    }
//
//    /**
//     * {@code GET  /sql-views/:id} : get the "id" sqlView.
//     *
//     * @param id the id of the sqlView to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sqlView, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/sql-views/{id}")
//    public ResponseEntity<SqlView> getSqlView(@PathVariable Long id) {
//        log.debug("REST request to get SqlView : {}", id);
//        Optional<SqlView> sqlView = sqlViewService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(sqlView);
//    }
//
//    /**
//     * {@code DELETE  /sql-views/:id} : delete the "id" sqlView.
//     *
//     * @param id the id of the sqlView to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/sql-views/{id}")
//    public ResponseEntity<Void> deleteSqlView(@PathVariable Long id) {
//        log.debug("REST request to delete SqlView : {}", id);
//        sqlViewService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
