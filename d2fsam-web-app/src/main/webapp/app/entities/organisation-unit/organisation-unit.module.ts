import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrganisationUnitComponent } from './list/organisation-unit.component';
import { OrganisationUnitDetailComponent } from './detail/organisation-unit-detail.component';
import { OrganisationUnitUpdateComponent } from './update/organisation-unit-update.component';
import { OrganisationUnitDeleteDialogComponent } from './delete/organisation-unit-delete-dialog.component';
import { OrganisationUnitRoutingModule } from './route/organisation-unit-routing.module';

@NgModule({
  imports: [SharedModule, OrganisationUnitRoutingModule],
  declarations: [
    OrganisationUnitComponent,
    OrganisationUnitDetailComponent,
    OrganisationUnitUpdateComponent,
    OrganisationUnitDeleteDialogComponent,
  ],
})
export class OrganisationUnitModule {}
