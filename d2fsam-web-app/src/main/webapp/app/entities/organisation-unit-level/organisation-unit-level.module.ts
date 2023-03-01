import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrganisationUnitLevelComponent } from './list/organisation-unit-level.component';
import { OrganisationUnitLevelDetailComponent } from './detail/organisation-unit-level-detail.component';
import { OrganisationUnitLevelUpdateComponent } from './update/organisation-unit-level-update.component';
import { OrganisationUnitLevelDeleteDialogComponent } from './delete/organisation-unit-level-delete-dialog.component';
import { OrganisationUnitLevelRoutingModule } from './route/organisation-unit-level-routing.module';

@NgModule({
  imports: [SharedModule, OrganisationUnitLevelRoutingModule],
  declarations: [
    OrganisationUnitLevelComponent,
    OrganisationUnitLevelDetailComponent,
    OrganisationUnitLevelUpdateComponent,
    OrganisationUnitLevelDeleteDialogComponent,
  ],
})
export class OrganisationUnitLevelModule {}
