import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FileResourceComponent } from './list/file-resource.component';
import { FileResourceDetailComponent } from './detail/file-resource-detail.component';
import { FileResourceUpdateComponent } from './update/file-resource-update.component';
import { FileResourceDeleteDialogComponent } from './delete/file-resource-delete-dialog.component';
import { FileResourceRoutingModule } from './route/file-resource-routing.module';

@NgModule({
  imports: [SharedModule, FileResourceRoutingModule],
  declarations: [FileResourceComponent, FileResourceDetailComponent, FileResourceUpdateComponent, FileResourceDeleteDialogComponent],
})
export class FileResourceModule {}
