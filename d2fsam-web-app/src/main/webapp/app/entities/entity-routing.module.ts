import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'demographic-data',
        data: { pageTitle: 'amSystemBackApp.demographicData.home.title' },
        loadChildren: () => import('./demographic-data/demographic-data.module').then(m => m.DemographicDataModule),
      },
      {
        path: 'demographic-data-source',
        data: { pageTitle: 'amSystemBackApp.demographicDataSource.home.title' },
        loadChildren: () => import('./demographic-data-source/demographic-data-source.module').then(m => m.DemographicDataSourceModule),
      },
      {
        path: 'user-data',
        data: { pageTitle: 'amSystemBackApp.userData.home.title' },
        loadChildren: () => import('./user-data/user-data.module').then(m => m.UserDataModule),
      },
      {
        path: 'period-type',
        data: { pageTitle: 'amSystemBackApp.periodType.home.title' },
        loadChildren: () => import('./period-type/period-type.module').then(m => m.PeriodTypeModule),
      },
      {
        path: 'period',
        data: { pageTitle: 'amSystemBackApp.period.home.title' },
        loadChildren: () => import('./period/period.module').then(m => m.PeriodModule),
      },
      {
        path: 'chv',
        data: { pageTitle: 'amSystemBackApp.chv.home.title' },
        loadChildren: () => import('./chv/chv.module').then(m => m.ChvModule),
      },
      {
        path: 'organisation-unit',
        data: { pageTitle: 'amSystemBackApp.organisationUnit.home.title' },
        loadChildren: () => import('./organisation-unit/organisation-unit.module').then(m => m.OrganisationUnitModule),
      },
      {
        path: 'organisation-unit-group',
        data: { pageTitle: 'amSystemBackApp.organisationUnitGroup.home.title' },
        loadChildren: () => import('./organisation-unit-group/organisation-unit-group.module').then(m => m.OrganisationUnitGroupModule),
      },
      {
        path: 'organisation-unit-group-set',
        data: { pageTitle: 'amSystemBackApp.organisationUnitGroupSet.home.title' },
        loadChildren: () =>
          import('./organisation-unit-group-set/organisation-unit-group-set.module').then(m => m.OrganisationUnitGroupSetModule),
      },
      {
        path: 'organisation-unit-level',
        data: { pageTitle: 'amSystemBackApp.organisationUnitLevel.home.title' },
        loadChildren: () => import('./organisation-unit-level/organisation-unit-level.module').then(m => m.OrganisationUnitLevelModule),
      },
      {
        path: 'user-group',
        data: { pageTitle: 'amSystemBackApp.userGroup.home.title' },
        loadChildren: () => import('./user-group/user-group.module').then(m => m.UserGroupModule),
      },
      {
        path: 'data-input-period',
        data: { pageTitle: 'amSystemBackApp.dataInputPeriod.home.title' },
        loadChildren: () => import('./data-input-period/data-input-period.module').then(m => m.DataInputPeriodModule),
      },
      {
        path: 'relative-periods',
        data: { pageTitle: 'amSystemBackApp.relativePeriods.home.title' },
        loadChildren: () => import('./relative-periods/relative-periods.module').then(m => m.RelativePeriodsModule),
      },
      {
        path: 'stock-item',
        data: { pageTitle: 'amSystemBackApp.stockItem.home.title' },
        loadChildren: () => import('./stock-item/stock-item.module').then(m => m.StockItemModule),
      },
      {
        path: 'stock-item-group',
        data: { pageTitle: 'amSystemBackApp.stockItemGroup.home.title' },
        loadChildren: () => import('./stock-item-group/stock-item-group.module').then(m => m.StockItemGroupModule),
      },
      {
        path: 'user-authority-group',
        data: { pageTitle: 'amSystemBackApp.userAuthorityGroup.home.title' },
        loadChildren: () => import('./user-authority-group/user-authority-group.module').then(m => m.UserAuthorityGroupModule),
      },
      {
        path: 'team',
        data: { pageTitle: 'amSystemBackApp.team.home.title' },
        loadChildren: () => import('./team/team.module').then(m => m.TeamModule),
      },
      {
        path: 'malaria-case',
        data: { pageTitle: 'amSystemBackApp.malariaCase.home.title' },
        loadChildren: () => import('./malaria-case/malaria-case.module').then(m => m.MalariaCaseModule),
      },
      {
        path: 'project',
        data: { pageTitle: 'amSystemBackApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'comment',
        data: { pageTitle: 'amSystemBackApp.comment.home.title' },
        loadChildren: () => import('./comment/comment.module').then(m => m.CommentModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'amSystemBackApp.activity.home.title' },
        loadChildren: () => import('./activity/activity.module').then(m => m.ActivityModule),
      },
      {
        path: 'tracked-entity-type',
        data: { pageTitle: 'amSystemBackApp.trackedEntityType.home.title' },
        loadChildren: () => import('./tracked-entity-type/tracked-entity-type.module').then(m => m.TrackedEntityTypeModule),
      },
      {
        path: 'program',
        data: { pageTitle: 'amSystemBackApp.program.home.title' },
        loadChildren: () => import('./program/program.module').then(m => m.ProgramModule),
      },
      {
        path: 'program-stage',
        data: { pageTitle: 'amSystemBackApp.programStage.home.title' },
        loadChildren: () => import('./program-stage/program-stage.module').then(m => m.ProgramStageModule),
      },
      {
        path: 'program-instance',
        data: { pageTitle: 'amSystemBackApp.programInstance.home.title' },
        loadChildren: () => import('./program-instance/program-instance.module').then(m => m.ProgramInstanceModule),
      },
      {
        path: 'tracked-entity-instance',
        data: { pageTitle: 'amSystemBackApp.trackedEntityInstance.home.title' },
        loadChildren: () => import('./tracked-entity-instance/tracked-entity-instance.module').then(m => m.TrackedEntityInstanceModule),
      },
      {
        path: 'program-stage-instance',
        data: { pageTitle: 'amSystemBackApp.programStageInstance.home.title' },
        loadChildren: () => import('./program-stage-instance/program-stage-instance.module').then(m => m.ProgramStageInstanceModule),
      },
      {
        path: 'program-stage-data-element',
        data: { pageTitle: 'amSystemBackApp.programStageDataElement.home.title' },
        loadChildren: () =>
          import('./program-stage-data-element/program-stage-data-element.module').then(m => m.ProgramStageDataElementModule),
      },
      {
        path: 'assignment',
        data: { pageTitle: 'amSystemBackApp.assignment.home.title' },
        loadChildren: () => import('./assignment/assignment.module').then(m => m.AssignmentModule),
      },
      {
        path: 'data-element',
        data: { pageTitle: 'amSystemBackApp.dataElement.home.title' },
        loadChildren: () => import('./data-element/data-element.module').then(m => m.DataElementModule),
      },
      {
        path: 'tracked-entity-attribute-value',
        data: { pageTitle: 'amSystemBackApp.trackedEntityAttributeValue.home.title' },
        loadChildren: () =>
          import('./tracked-entity-attribute-value/tracked-entity-attribute-value.module').then(m => m.TrackedEntityAttributeValueModule),
      },
      {
        path: 'tracked-entity-data-value-audit',
        data: { pageTitle: 'amSystemBackApp.trackedEntityDataValueAudit.home.title' },
        loadChildren: () =>
          import('./tracked-entity-data-value-audit/tracked-entity-data-value-audit.module').then(m => m.TrackedEntityDataValueAuditModule),
      },
      {
        path: 'tracked-entity-attribute-value-audit',
        data: { pageTitle: 'amSystemBackApp.trackedEntityAttributeValueAudit.home.title' },
        loadChildren: () =>
          import('./tracked-entity-attribute-value-audit/tracked-entity-attribute-value-audit.module').then(
            m => m.TrackedEntityAttributeValueAuditModule
          ),
      },
      {
        path: 'tracked-entity-instance-audit',
        data: { pageTitle: 'amSystemBackApp.trackedEntityInstanceAudit.home.title' },
        loadChildren: () =>
          import('./tracked-entity-instance-audit/tracked-entity-instance-audit.module').then(m => m.TrackedEntityInstanceAuditModule),
      },
      {
        path: 'tracked-entity-program-owner',
        data: { pageTitle: 'amSystemBackApp.trackedEntityProgramOwner.home.title' },
        loadChildren: () =>
          import('./tracked-entity-program-owner/tracked-entity-program-owner.module').then(m => m.TrackedEntityProgramOwnerModule),
      },
      {
        path: 'file-resource',
        data: { pageTitle: 'amSystemBackApp.fileResource.home.title' },
        loadChildren: () => import('./file-resource/file-resource.module').then(m => m.FileResourceModule),
      },
      {
        path: 'external-file-resource',
        data: { pageTitle: 'amSystemBackApp.externalFileResource.home.title' },
        loadChildren: () => import('./external-file-resource/external-file-resource.module').then(m => m.ExternalFileResourceModule),
      },
      {
        path: 'document',
        data: { pageTitle: 'amSystemBackApp.document.home.title' },
        loadChildren: () => import('./document/document.module').then(m => m.DocumentModule),
      },
      {
        path: 'program-tracked-entity-attribute',
        data: { pageTitle: 'amSystemBackApp.programTrackedEntityAttribute.home.title' },
        loadChildren: () =>
          import('./program-tracked-entity-attribute/program-tracked-entity-attribute.module').then(
            m => m.ProgramTrackedEntityAttributeModule
          ),
      },
      {
        path: 'option',
        data: { pageTitle: 'amSystemBackApp.option.home.title' },
        loadChildren: () => import('./option/option.module').then(m => m.OptionModule),
      },
      {
        path: 'option-set',
        data: { pageTitle: 'amSystemBackApp.optionSet.home.title' },
        loadChildren: () => import('./option-set/option-set.module').then(m => m.OptionSetModule),
      },
      {
        path: 'tracked-entity-instance-filter',
        data: { pageTitle: 'amSystemBackApp.trackedEntityInstanceFilter.home.title' },
        loadChildren: () =>
          import('./tracked-entity-instance-filter/tracked-entity-instance-filter.module').then(m => m.TrackedEntityInstanceFilterModule),
      },
      {
        path: 'program-stage-instance-filter',
        data: { pageTitle: 'amSystemBackApp.programStageInstanceFilter.home.title' },
        loadChildren: () =>
          import('./program-stage-instance-filter/program-stage-instance-filter.module').then(m => m.ProgramStageInstanceFilterModule),
      },
      {
        path: 'data-value',
        data: { pageTitle: 'amSystemBackApp.dataValue.home.title' },
        loadChildren: () => import('./data-value/data-value.module').then(m => m.DataValueModule),
      },
      {
        path: 'program-temp-owner',
        data: { pageTitle: 'amSystemBackApp.programTempOwner.home.title' },
        loadChildren: () => import('./program-temp-owner/program-temp-owner.module').then(m => m.ProgramTempOwnerModule),
      },
      {
        path: 'program-ownership-history',
        data: { pageTitle: 'amSystemBackApp.programOwnershipHistory.home.title' },
        loadChildren: () =>
          import('./program-ownership-history/program-ownership-history.module').then(m => m.ProgramOwnershipHistoryModule),
      },
      {
        path: 'program-temp-ownership-audit',
        data: { pageTitle: 'amSystemBackApp.programTempOwnershipAudit.home.title' },
        loadChildren: () =>
          import('./program-temp-ownership-audit/program-temp-ownership-audit.module').then(m => m.ProgramTempOwnershipAuditModule),
      },
      {
        path: 'team-group',
        data: { pageTitle: 'amSystemBackApp.teamGroup.home.title' },
        loadChildren: () => import('./team-group/team-group.module').then(m => m.TeamGroupModule),
      },
      {
        path: 'metadata-version',
        data: { pageTitle: 'amSystemBackApp.metadataVersion.home.title' },
        loadChildren: () => import('./metadata-version/metadata-version.module').then(m => m.MetadataVersionModule),
      },
      {
        path: 'datastore-entry',
        data: { pageTitle: 'amSystemBackApp.datastoreEntry.home.title' },
        loadChildren: () => import('./datastore-entry/datastore-entry.module').then(m => m.DatastoreEntryModule),
      },
      {
        path: 'system-setting',
        data: { pageTitle: 'amSystemBackApp.systemSetting.home.title' },
        loadChildren: () => import('./system-setting/system-setting.module').then(m => m.SystemSettingModule),
      },
      {
        path: 'sql-view',
        data: { pageTitle: 'amSystemBackApp.sqlView.home.title' },
        loadChildren: () => import('./sql-view/sql-view.module').then(m => m.SqlViewModule),
      },
      {
        path: 'relationship',
        data: { pageTitle: 'amSystemBackApp.relationship.home.title' },
        loadChildren: () => import('./relationship/relationship.module').then(m => m.RelationshipModule),
      },
      {
        path: 'relationship-type',
        data: { pageTitle: 'amSystemBackApp.relationshipType.home.title' },
        loadChildren: () => import('./relationship-type/relationship-type.module').then(m => m.RelationshipTypeModule),
      },
      {
        path: 'relationship-item',
        data: { pageTitle: 'amSystemBackApp.relationshipItem.home.title' },
        loadChildren: () => import('./relationship-item/relationship-item.module').then(m => m.RelationshipItemModule),
      },
      {
        path: 'relationship-constraint',
        data: { pageTitle: 'amSystemBackApp.relationshipConstraint.home.title' },
        loadChildren: () => import('./relationship-constraint/relationship-constraint.module').then(m => m.RelationshipConstraintModule),
      },
      {
        path: 'tracked-entity-type-attribute',
        data: { pageTitle: 'amSystemBackApp.trackedEntityTypeAttribute.home.title' },
        loadChildren: () =>
          import('./tracked-entity-type-attribute/tracked-entity-type-attribute.module').then(m => m.TrackedEntityTypeAttributeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
