//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.stock.StockItemGroup;
//import org.nmcpye.am.stock.StockItemGroupRepository;
//import org.nmcpye.am.stock.StockItemGroupService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.StockItemGroup}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class StockItemGroupResource {
//
//    private final Logger log = LoggerFactory.getLogger(StockItemGroupResource.class);
//
//    private static final String ENTITY_NAME = "stockItemGroup";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final StockItemGroupService stockItemGroupService;
//
//    private final StockItemGroupRepository stockItemGroupRepository;
//
//    public StockItemGroupResource(StockItemGroupService stockItemGroupService, StockItemGroupRepository stockItemGroupRepository) {
//        this.stockItemGroupService = stockItemGroupService;
//        this.stockItemGroupRepository = stockItemGroupRepository;
//    }
//
//    /**
//     * {@code POST  /stock-item-groups} : Create a new stockItemGroup.
//     *
//     * @param stockItemGroup the stockItemGroup to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockItemGroup, or with status {@code 400 (Bad Request)} if the stockItemGroup has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/stock-item-groups")
//    public ResponseEntity<StockItemGroup> createStockItemGroup(@Valid @RequestBody StockItemGroup stockItemGroup)
//        throws URISyntaxException {
//        log.debug("REST request to save StockItemGroup : {}", stockItemGroup);
//        if (stockItemGroup.getId() != null) {
//            throw new BadRequestAlertException("A new stockItemGroup cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        StockItemGroup result = stockItemGroupService.save(stockItemGroup);
//        return ResponseEntity
//            .created(new URI("/api/stock-item-groups/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /stock-item-groups/:id} : Updates an existing stockItemGroup.
//     *
//     * @param id the id of the stockItemGroup to save.
//     * @param stockItemGroup the stockItemGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItemGroup,
//     * or with status {@code 400 (Bad Request)} if the stockItemGroup is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the stockItemGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/stock-item-groups/{id}")
//    public ResponseEntity<StockItemGroup> updateStockItemGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody StockItemGroup stockItemGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to update StockItemGroup : {}, {}", id, stockItemGroup);
//        if (stockItemGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, stockItemGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!stockItemGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        StockItemGroup result = stockItemGroupService.update(stockItemGroup);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItemGroup.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /stock-item-groups/:id} : Partial updates given fields of an existing stockItemGroup, field will ignore if it is null
//     *
//     * @param id the id of the stockItemGroup to save.
//     * @param stockItemGroup the stockItemGroup to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItemGroup,
//     * or with status {@code 400 (Bad Request)} if the stockItemGroup is not valid,
//     * or with status {@code 404 (Not Found)} if the stockItemGroup is not found,
//     * or with status {@code 500 (Internal Server Error)} if the stockItemGroup couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/stock-item-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<StockItemGroup> partialUpdateStockItemGroup(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody StockItemGroup stockItemGroup
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update StockItemGroup partially : {}, {}", id, stockItemGroup);
//        if (stockItemGroup.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, stockItemGroup.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!stockItemGroupRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<StockItemGroup> result = stockItemGroupService.partialUpdate(stockItemGroup);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItemGroup.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /stock-item-groups} : get all the stockItemGroups.
//     *
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockItemGroups in body.
//     */
//    @GetMapping("/stock-item-groups")
//    public List<StockItemGroup> getAllStockItemGroups(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
//        log.debug("REST request to get all StockItemGroups");
//        return stockItemGroupService.findAll();
//    }
//
//    /**
//     * {@code GET  /stock-item-groups/:id} : get the "id" stockItemGroup.
//     *
//     * @param id the id of the stockItemGroup to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockItemGroup, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/stock-item-groups/{id}")
//    public ResponseEntity<StockItemGroup> getStockItemGroup(@PathVariable Long id) {
//        log.debug("REST request to get StockItemGroup : {}", id);
//        Optional<StockItemGroup> stockItemGroup = stockItemGroupService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(stockItemGroup);
//    }
//
//    /**
//     * {@code DELETE  /stock-item-groups/:id} : delete the "id" stockItemGroup.
//     *
//     * @param id the id of the stockItemGroup to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/stock-item-groups/{id}")
//    public ResponseEntity<Void> deleteStockItemGroup(@PathVariable Long id) {
//        log.debug("REST request to delete StockItemGroup : {}", id);
//        stockItemGroupService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
