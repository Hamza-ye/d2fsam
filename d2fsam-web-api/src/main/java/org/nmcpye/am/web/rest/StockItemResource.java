//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.stock.StockItem;
//import org.nmcpye.am.stock.StockItemRepository;
//import org.nmcpye.am.stock.StockItemService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.StockItem}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class StockItemResource {
//
//    private final Logger log = LoggerFactory.getLogger(StockItemResource.class);
//
//    private static final String ENTITY_NAME = "stockItem";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final StockItemService stockItemService;
//
//    private final StockItemRepository stockItemRepository;
//
//    public StockItemResource(StockItemService stockItemService, StockItemRepository stockItemRepository) {
//        this.stockItemService = stockItemService;
//        this.stockItemRepository = stockItemRepository;
//    }
//
//    /**
//     * {@code POST  /stock-items} : Create a new stockItem.
//     *
//     * @param stockItem the stockItem to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockItem, or with status {@code 400 (Bad Request)} if the stockItem has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/stock-items")
//    public ResponseEntity<StockItem> createStockItem(@Valid @RequestBody StockItem stockItem) throws URISyntaxException {
//        log.debug("REST request to save StockItem : {}", stockItem);
//        if (stockItem.getId() != null) {
//            throw new BadRequestAlertException("A new stockItem cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        StockItem result = stockItemService.save(stockItem);
//        return ResponseEntity
//            .created(new URI("/api/stock-items/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /stock-items/:id} : Updates an existing stockItem.
//     *
//     * @param id the id of the stockItem to save.
//     * @param stockItem the stockItem to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItem,
//     * or with status {@code 400 (Bad Request)} if the stockItem is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the stockItem couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/stock-items/{id}")
//    public ResponseEntity<StockItem> updateStockItem(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody StockItem stockItem
//    ) throws URISyntaxException {
//        log.debug("REST request to update StockItem : {}, {}", id, stockItem);
//        if (stockItem.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, stockItem.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!stockItemRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        StockItem result = stockItemService.update(stockItem);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItem.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /stock-items/:id} : Partial updates given fields of an existing stockItem, field will ignore if it is null
//     *
//     * @param id the id of the stockItem to save.
//     * @param stockItem the stockItem to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItem,
//     * or with status {@code 400 (Bad Request)} if the stockItem is not valid,
//     * or with status {@code 404 (Not Found)} if the stockItem is not found,
//     * or with status {@code 500 (Internal Server Error)} if the stockItem couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/stock-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<StockItem> partialUpdateStockItem(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody StockItem stockItem
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update StockItem partially : {}, {}", id, stockItem);
//        if (stockItem.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, stockItem.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!stockItemRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<StockItem> result = stockItemService.partialUpdate(stockItem);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItem.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /stock-items} : get all the stockItems.
//     *
//     * @param pageable the pagination information.
//     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockItems in body.
//     */
//    @GetMapping("/stock-items")
//    public ResponseEntity<List<StockItem>> getAllStockItems(
//        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
//        @RequestParam(required = false, defaultValue = "false") boolean eagerload
//    ) {
//        log.debug("REST request to get a page of StockItems");
//        Page<StockItem> page;
//        if (eagerload) {
//            page = stockItemService.findAllWithEagerRelationships(pageable);
//        } else {
//            page = stockItemService.findAll(pageable);
//        }
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    /**
//     * {@code GET  /stock-items/:id} : get the "id" stockItem.
//     *
//     * @param id the id of the stockItem to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockItem, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/stock-items/{id}")
//    public ResponseEntity<StockItem> getStockItem(@PathVariable Long id) {
//        log.debug("REST request to get StockItem : {}", id);
//        Optional<StockItem> stockItem = stockItemService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(stockItem);
//    }
//
//    /**
//     * {@code DELETE  /stock-items/:id} : delete the "id" stockItem.
//     *
//     * @param id the id of the stockItem to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/stock-items/{id}")
//    public ResponseEntity<Void> deleteStockItem(@PathVariable Long id) {
//        log.debug("REST request to delete StockItem : {}", id);
//        stockItemService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
