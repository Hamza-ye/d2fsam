//package org.nmcpye.am.web.rest;
//
//import org.nmcpye.am.common.OpenApi;
//import org.nmcpye.am.setting.SystemSetting;
//import org.nmcpye.am.setting.SystemSettingRepository;
//import org.nmcpye.am.setting.SystemSettingService;
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
// * REST controller for managing {@link org.nmcpye.am.domain.SystemSetting}.
// */
//@OpenApi.Ignore
////@RestController
//@RequestMapping("/api")
//public class SystemSettingResource {
//
//    private final Logger log = LoggerFactory.getLogger(SystemSettingResource.class);
//
//    private static final String ENTITY_NAME = "systemSetting";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final SystemSettingService systemSettingService;
//
//    private final SystemSettingRepository systemSettingRepository;
//
//    public SystemSettingResource(SystemSettingService systemSettingService, SystemSettingRepository systemSettingRepository) {
//        this.systemSettingService = systemSettingService;
//        this.systemSettingRepository = systemSettingRepository;
//    }
//
//    /**
//     * {@code POST  /system-settings} : Create a new systemSetting.
//     *
//     * @param systemSetting the systemSetting to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemSetting, or with status {@code 400 (Bad Request)} if the systemSetting has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/system-settings")
//    public ResponseEntity<SystemSetting> createSystemSetting(@Valid @RequestBody SystemSetting systemSetting) throws URISyntaxException {
//        log.debug("REST request to save SystemSetting : {}", systemSetting);
//        if (systemSetting.getId() != null) {
//            throw new BadRequestAlertException("A new systemSetting cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        SystemSetting result = systemSettingService.save(systemSetting);
//        return ResponseEntity
//            .created(new URI("/api/system-settings/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /system-settings/:id} : Updates an existing systemSetting.
//     *
//     * @param id the id of the systemSetting to save.
//     * @param systemSetting the systemSetting to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemSetting,
//     * or with status {@code 400 (Bad Request)} if the systemSetting is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the systemSetting couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/system-settings/{id}")
//    public ResponseEntity<SystemSetting> updateSystemSetting(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody SystemSetting systemSetting
//    ) throws URISyntaxException {
//        log.debug("REST request to update SystemSetting : {}, {}", id, systemSetting);
//        if (systemSetting.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, systemSetting.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!systemSettingRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        SystemSetting result = systemSettingService.update(systemSetting);
//        return ResponseEntity
//            .ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemSetting.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PATCH  /system-settings/:id} : Partial updates given fields of an existing systemSetting, field will ignore if it is null
//     *
//     * @param id the id of the systemSetting to save.
//     * @param systemSetting the systemSetting to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemSetting,
//     * or with status {@code 400 (Bad Request)} if the systemSetting is not valid,
//     * or with status {@code 404 (Not Found)} if the systemSetting is not found,
//     * or with status {@code 500 (Internal Server Error)} if the systemSetting couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PatchMapping(value = "/system-settings/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<SystemSetting> partialUpdateSystemSetting(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody SystemSetting systemSetting
//    ) throws URISyntaxException {
//        log.debug("REST request to partial update SystemSetting partially : {}, {}", id, systemSetting);
//        if (systemSetting.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, systemSetting.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!systemSettingRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<SystemSetting> result = systemSettingService.partialUpdate(systemSetting);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemSetting.getId().toString())
//        );
//    }
//
//    /**
//     * {@code GET  /system-settings} : get all the systemSettings.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemSettings in body.
//     */
//    @GetMapping("/system-settings")
//    public List<SystemSetting> getAllSystemSettings() {
//        log.debug("REST request to get all SystemSettings");
//        return systemSettingService.findAll();
//    }
//
//    /**
//     * {@code GET  /system-settings/:id} : get the "id" systemSetting.
//     *
//     * @param id the id of the systemSetting to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemSetting, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/system-settings/{id}")
//    public ResponseEntity<SystemSetting> getSystemSetting(@PathVariable Long id) {
//        log.debug("REST request to get SystemSetting : {}", id);
//        Optional<SystemSetting> systemSetting = systemSettingService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(systemSetting);
//    }
//
//    /**
//     * {@code DELETE  /system-settings/:id} : delete the "id" systemSetting.
//     *
//     * @param id the id of the systemSetting to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/system-settings/{id}")
//    public ResponseEntity<Void> deleteSystemSetting(@PathVariable Long id) {
//        log.debug("REST request to delete SystemSetting : {}", id);
//        systemSettingService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//}
