import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrganisationUnitGroupComponent } from './list/organisation-unit-group.component';
import { OrganisationUnitGroupDetailComponent } from './detail/organisation-unit-group-detail.component';
import { OrganisationUnitGroupUpdateComponent } from './update/organisation-unit-group-update.component';
import { OrganisationUnitGroupDeleteDialogComponent } from './delete/organisation-unit-group-delete-dialog.component';
import { OrganisationUnitGroupRoutingModule } from './route/organisation-unit-group-routing.module';

@NgModule({
  imports: [SharedModule, OrganisationUnitGroupRoutingModule],
  declarations: [
    OrganisationUnitGroupComponent,
    OrganisationUnitGroupDetailComponent,
    OrganisationUnitGroupUpdateComponent,
    OrganisationUnitGroupDeleteDialogComponent,
  ],
})
export class OrganisationUnitGroupModule {}
