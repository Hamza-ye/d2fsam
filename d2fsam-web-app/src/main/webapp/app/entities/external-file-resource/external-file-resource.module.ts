import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExternalFileResourceComponent } from './list/external-file-resource.component';
import { ExternalFileResourceDetailComponent } from './detail/external-file-resource-detail.component';
import { ExternalFileResourceUpdateComponent } from './update/external-file-resource-update.component';
import { ExternalFileResourceDeleteDialogComponent } from './delete/external-file-resource-delete-dialog.component';
import { ExternalFileResourceRoutingModule } from './route/external-file-resource-routing.module';

@NgModule({
  imports: [SharedModule, ExternalFileResourceRoutingModule],
  declarations: [
    ExternalFileResourceComponent,
    ExternalFileResourceDetailComponent,
    ExternalFileResourceUpdateComponent,
    ExternalFileResourceDeleteDialogComponent,
  ],
})
export class ExternalFileResourceModule {}
