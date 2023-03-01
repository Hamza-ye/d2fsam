import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MetadataVersionComponent } from './list/metadata-version.component';
import { MetadataVersionDetailComponent } from './detail/metadata-version-detail.component';
import { MetadataVersionUpdateComponent } from './update/metadata-version-update.component';
import { MetadataVersionDeleteDialogComponent } from './delete/metadata-version-delete-dialog.component';
import { MetadataVersionRoutingModule } from './route/metadata-version-routing.module';

@NgModule({
  imports: [SharedModule, MetadataVersionRoutingModule],
  declarations: [
    MetadataVersionComponent,
    MetadataVersionDetailComponent,
    MetadataVersionUpdateComponent,
    MetadataVersionDeleteDialogComponent,
  ],
})
export class MetadataVersionModule {}
