import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrganisationUnitGroupSetComponent } from './list/organisation-unit-group-set.component';
import { OrganisationUnitGroupSetDetailComponent } from './detail/organisation-unit-group-set-detail.component';
import { OrganisationUnitGroupSetUpdateComponent } from './update/organisation-unit-group-set-update.component';
import { OrganisationUnitGroupSetDeleteDialogComponent } from './delete/organisation-unit-group-set-delete-dialog.component';
import { OrganisationUnitGroupSetRoutingModule } from './route/organisation-unit-group-set-routing.module';

@NgModule({
  imports: [SharedModule, OrganisationUnitGroupSetRoutingModule],
  declarations: [
    OrganisationUnitGroupSetComponent,
    OrganisationUnitGroupSetDetailComponent,
    OrganisationUnitGroupSetUpdateComponent,
    OrganisationUnitGroupSetDeleteDialogComponent,
  ],
})
export class OrganisationUnitGroupSetModule {}
