//package org.nmcpye.am.config.jhipster;
//
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.jsr107.Eh107Configuration;
//import org.hibernate.cache.jcache.ConfigSettings;
//import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
//import org.nmcpye.am.hibernate.HibernateConfigurationProvider;
//import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAudit;
//import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
//import org.springframework.boot.info.BuildProperties;
//import org.springframework.boot.info.GitProperties;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.context.annotation.Bean;
//import tech.jhipster.config.JHipsterProperties;
//import tech.jhipster.config.cache.PrefixedKeyGenerator;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
////@Configuration
////@EnableCaching
//public class CacheConfiguration {
//
//    private GitProperties gitProperties;
//    private BuildProperties buildProperties;
//    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;
//
//    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
//        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();
//
//        jcacheConfiguration =
//            Eh107Configuration.fromEhcacheCacheConfiguration(
//                CacheConfigurationBuilder
//                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
//                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
//                    .build()
//            );
//    }
//
////    @Bean
////    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
////        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
////    }
//
//    // NMCP
//    @Bean
//    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
//        @Qualifier("hibernateConfigurationProvider")
//            HibernateConfigurationProvider hibernateConfigurationProvider,
//        javax.cache.CacheManager cacheManager) {
//        return hibernateProperties -> {
//            hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
//            hibernateProperties.putAll(
//                propertiesMap(hibernateConfigurationProvider.getConfiguration().getProperties())
//            );
//        };
//    }
//
//    public HashMap<String, Object> propertiesMap(Properties prop) {
//        return prop.entrySet().stream().collect(
//            Collectors.toMap(
//                e -> String.valueOf(e.getKey()),
//                Map.Entry::getValue,
//                (prev, next) -> next, HashMap::new
//            ));
//    }
//
//    @Bean
//    public JCacheManagerCustomizer cacheManagerCustomizer() {
//        return cm -> {
//            createCache(cm, org.nmcpye.am.user.UserRepositoryExt.USERS_BY_LOGIN_CACHE);
//            createCache(cm, org.nmcpye.am.user.UserRepositoryExt.USERS_BY_EMAIL_CACHE);
//            createCache(cm, org.nmcpye.am.user.UserRepositoryExt.USERS_BY_LOGIN_EXT);
//            createCache(cm, org.nmcpye.am.user.UserRepositoryExt.USERS_BY_EMAIL_CACHE_EXT);
//            createCache(cm, org.nmcpye.am.constant.Constant.class.getName());
//            createCache(cm, org.nmcpye.am.programrule.ProgramRule.class.getName());
//            createCache(cm, org.nmcpye.am.programrule.ProgramRuleAction.class.getName());
//            createCache(cm, org.nmcpye.am.programrule.ProgramRuleVariable.class.getName());
//            createCache(cm, org.nmcpye.am.programrule.ProgramRule.class.getName() + ".programRuleActions");
//
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitServiceExt.USERS_ACCESSIBLE_ORG_UNITS_CACHE);
//            createCache(cm, org.nmcpye.am.user.User.class.getName());
//            createCache(cm, org.nmcpye.am.user.Authority.class.getName());
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".authorities");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".organisationUnits");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".teiSearchOrganisationUnits");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".dataViewOrganisationUnits");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".userAuthorityGroups");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".groups");
//            createCache(cm, org.nmcpye.am.user.User.class.getName() + ".teams");
//            createCache(cm, org.nmcpye.am.demographicdata.DemographicData.class.getName());
//            createCache(cm, org.nmcpye.am.demographicdata.DemographicDataSource.class.getName());
//            createCache(cm, org.nmcpye.am.demographicdata.DemographicDataSource.class.getName() + ".demographicData");
//            createCache(cm, org.nmcpye.am.period.PeriodType.class.getName());
//            createCache(cm, org.nmcpye.am.period.Period.class.getName());
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName());
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".demographicData");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".assignments");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".children");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".programs");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".targetedInActivities");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".groups");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".users");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".searchingUsers");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnit.class.getName() + ".dataViewUsers");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitGroup.class.getName());
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitGroup.class.getName() + ".members");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitGroup.class.getName() + ".groupSets");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitGroupSet.class.getName());
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitGroupSet.class.getName() + ".organisationUnitGroups");
//            createCache(cm, org.nmcpye.am.organisationunit.OrganisationUnitLevel.class.getName());
//            createCache(cm, org.nmcpye.am.user.UserGroup.class.getName());
//            createCache(cm, org.nmcpye.am.user.UserGroup.class.getName() + ".members");
//            createCache(cm, org.nmcpye.am.user.UserGroup.class.getName() + ".managedGroups");
//            createCache(cm, org.nmcpye.am.user.UserGroup.class.getName() + ".managedByGroups");
//            createCache(cm, org.nmcpye.am.period.DataInputPeriod.class.getName());
//            createCache(cm, org.nmcpye.am.period.RelativePeriods.class.getName());
//            createCache(cm, org.nmcpye.am.stock.StockItem.class.getName());
//            createCache(cm, org.nmcpye.am.stock.StockItem.class.getName() + ".groups");
//            createCache(cm, org.nmcpye.am.stock.StockItemGroup.class.getName());
//            createCache(cm, org.nmcpye.am.stock.StockItemGroup.class.getName() + ".items");
//            createCache(cm, org.nmcpye.am.user.UserAuthorityGroup.class.getName());
//            createCache(cm, org.nmcpye.am.user.UserAuthorityGroup.class.getName() + ".members");
//            createCache(cm, org.nmcpye.am.team.Team.class.getName());
//            createCache(cm, org.nmcpye.am.team.Team.class.getName() + ".members");
//            createCache(cm, org.nmcpye.am.team.Team.class.getName() + ".managedTeams");
//            createCache(cm, org.nmcpye.am.team.Team.class.getName() + ".assignments");
//            createCache(cm, org.nmcpye.am.team.Team.class.getName() + ".groups");
//            createCache(cm, org.nmcpye.am.team.Team.class.getName() + ".managedByTeams");
//            createCache(cm, org.nmcpye.am.malariacase.MalariaCase.class.getName());
//            createCache(cm, org.nmcpye.am.project.Project.class.getName());
//            createCache(cm, org.nmcpye.am.project.Project.class.getName() + ".activities");
//            createCache(cm, org.nmcpye.am.comment.Comment.class.getName());
//            createCache(cm, org.nmcpye.am.comment.Comment.class.getName() + ".programInstances");
//            createCache(cm, org.nmcpye.am.comment.Comment.class.getName() + ".programStageInstances");
//            createCache(cm, org.nmcpye.am.activity.Activity.class.getName());
//            createCache(cm, org.nmcpye.am.activity.Activity.class.getName() + ".targetedOrganisationUnits");
//            createCache(cm, org.nmcpye.am.activity.Activity.class.getName() + ".teams");
//            createCache(cm, org.nmcpye.am.activity.Activity.class.getName() + ".assignments");
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityType.class.getName());
////            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityType.class.getName() + ".trackedEntityTypeAttributes");
//            createCache(cm, org.nmcpye.am.program.Program.class.getName());
//            createCache(cm, org.nmcpye.am.program.Program.class.getName() + ".organisationUnits");
////            createCache(cm, org.nmcpye.am.program.Program.class.getName() + ".programAttributes");
//            createCache(cm, org.nmcpye.am.program.Program.class.getName() + ".programStages");
//            createCache(cm, org.nmcpye.am.program.ProgramStage.class.getName());
////            createCache(cm, org.nmcpye.am.program.ProgramStage.class.getName() + ".programStageDataElements");
//            createCache(cm, org.nmcpye.am.program.ProgramInstance.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramInstance.class.getName() + ".comments");
//            createCache(cm, org.nmcpye.am.program.ProgramInstance.class.getName() + ".programStageInstances");
//            createCache(cm, org.nmcpye.am.program.ProgramInstance.class.getName() + ".relationshipItems");
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityInstance.class.getName());
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityInstance.class.getName() + ".programInstances");
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityInstance.class.getName() + ".programOwners");
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityInstance.class.getName() + ".trackedEntityAttributeValues");
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityInstance.class.getName() + ".relationshipItems");
//            createCache(cm, org.nmcpye.am.program.ProgramStageInstance.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramStageInstance.class.getName() + ".comments");
//            createCache(cm, org.nmcpye.am.program.ProgramStageInstance.class.getName() + ".relationshipItems");
//            createCache(cm, org.nmcpye.am.program.ProgramStageDataElement.class.getName());
//            createCache(cm, org.nmcpye.am.assignment.Assignment.class.getName());
//            createCache(cm, org.nmcpye.am.dataelement.DataElement.class.getName());
//            createCache(cm, org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue.class.getName());
//            createCache(cm, TrackedEntityDataValueAudit.class.getName());
//            createCache(cm, org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAudit.class.getName());
//            createCache(cm, TrackedEntityInstanceAudit.class.getName());
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityProgramOwner.class.getName());
//            createCache(cm, org.nmcpye.am.fileresource.FileResource.class.getName());
//            createCache(cm, org.nmcpye.am.fileresource.ExternalFileResource.class.getName());
//            createCache(cm, org.nmcpye.am.document.Document.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramTrackedEntityAttribute.class.getName());
//            createCache(cm, org.nmcpye.am.option.Option.class.getName());
//            createCache(cm, org.nmcpye.am.option.OptionSet.class.getName());
//            createCache(cm, org.nmcpye.am.option.OptionSet.class.getName() + ".options");
//            createCache(cm, TrackedEntityInstanceFilter.class.getName());
//            createCache(cm, org.nmcpye.am.programstagefilter.ProgramStageInstanceFilter.class.getName());
//            createCache(cm, org.nmcpye.am.datavalue.DataValue.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramTempOwner.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramOwnershipHistory.class.getName());
//            createCache(cm, org.nmcpye.am.program.ProgramTempOwnershipAudit.class.getName());
//            createCache(cm, org.nmcpye.am.team.TeamGroup.class.getName());
//            createCache(cm, org.nmcpye.am.team.TeamGroup.class.getName() + ".members");
//            createCache(cm, org.nmcpye.am.version.MetadataVersion.class.getName());
//            createCache(cm, org.nmcpye.am.datastore.DatastoreEntry.class.getName());
//            createCache(cm, org.nmcpye.am.setting.SystemSetting.class.getName());
//            createCache(cm, org.nmcpye.am.sqlview.SqlView.class.getName());
//            createCache(cm, org.nmcpye.am.relationship.Relationship.class.getName());
//            createCache(cm, org.nmcpye.am.relationship.RelationshipType.class.getName());
//            createCache(cm, org.nmcpye.am.relationship.RelationshipItem.class.getName());
//            createCache(cm, org.nmcpye.am.relationship.RelationshipConstraint.class.getName());
//            createCache(cm, org.nmcpye.am.trackedentity.TrackedEntityTypeAttribute.class.getName());
//            // jhipster-needle-ehcache-add-entry
//        };
//    }
//
//    private void createCache(javax.cache.CacheManager cm, String cacheName) {
//        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
//        if (cache != null) {
//            cache.clear();
//        } else {
//            cm.createCache(cacheName, jcacheConfiguration);
//        }
//    }
//
//    @Autowired(required = false)
//    public void setGitProperties(GitProperties gitProperties) {
//        this.gitProperties = gitProperties;
//    }
//
//    @Autowired(required = false)
//    public void setBuildProperties(BuildProperties buildProperties) {
//        this.buildProperties = buildProperties;
//    }
//
//    @Bean
//    public KeyGenerator keyGenerator() {
//        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
//    }
//}
