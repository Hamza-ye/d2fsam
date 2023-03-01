//package org.nmcpye.am.sqlview;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Service Implementation for managing {@link SqlView}.
// */
////@Service
////@Transactional
//public class SqlViewServiceImpl implements SqlViewService {
//
//    private final Logger log = LoggerFactory.getLogger(SqlViewServiceImpl.class);
//
//    private final SqlViewRepository sqlViewRepository;
//
//    public SqlViewServiceImpl(SqlViewRepository sqlViewRepository) {
//        this.sqlViewRepository = sqlViewRepository;
//    }
//
//    @Override
//    public SqlView save(SqlView sqlView) {
//        log.debug("Request to save SqlView : {}", sqlView);
//        return sqlViewRepository.save(sqlView);
//    }
//
//    @Override
//    public SqlView update(SqlView sqlView) {
//        log.debug("Request to update SqlView : {}", sqlView);
//        return sqlViewRepository.save(sqlView);
//    }
//
//    @Override
//    public Optional<SqlView> partialUpdate(SqlView sqlView) {
//        log.debug("Request to partially update SqlView : {}", sqlView);
//
//        return sqlViewRepository
//            .findById(sqlView.getId())
//            .map(existingSqlView -> {
//                if (sqlView.getUid() != null) {
//                    existingSqlView.setUid(sqlView.getUid());
//                }
//                if (sqlView.getCode() != null) {
//                    existingSqlView.setCode(sqlView.getCode());
//                }
//                if (sqlView.getName() != null) {
//                    existingSqlView.setName(sqlView.getName());
//                }
//                if (sqlView.getCreated() != null) {
//                    existingSqlView.setCreated(sqlView.getCreated());
//                }
//                if (sqlView.getUpdated() != null) {
//                    existingSqlView.setUpdated(sqlView.getUpdated());
//                }
//                if (sqlView.getDescription() != null) {
//                    existingSqlView.setDescription(sqlView.getDescription());
//                }
//                if (sqlView.getSqlQuery() != null) {
//                    existingSqlView.setSqlQuery(sqlView.getSqlQuery());
//                }
//                if (sqlView.getType() != null) {
//                    existingSqlView.setType(sqlView.getType());
//                }
//                if (sqlView.getCacheStrategy() != null) {
//                    existingSqlView.setCacheStrategy(sqlView.getCacheStrategy());
//                }
//
//                return existingSqlView;
//            })
//            .map(sqlViewRepository::save);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<SqlView> findAll() {
//        log.debug("Request to get all SqlViews");
//        return sqlViewRepository.findAll();
//    }
//
//    public Page<SqlView> findAllWithEagerRelationships(Pageable pageable) {
//        return sqlViewRepository.findAllWithEagerRelationships(pageable);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<SqlView> findOne(Long id) {
//        log.debug("Request to get SqlView : {}", id);
//        return sqlViewRepository.findOneWithEagerRelationships(id);
//    }
//
//    @Override
//    public void delete(Long id) {
//        log.debug("Request to delete SqlView : {}", id);
//        sqlViewRepository.deleteById(id);
//    }
//}
