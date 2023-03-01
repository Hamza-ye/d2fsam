import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SystemSettingComponent } from './list/system-setting.component';
import { SystemSettingDetailComponent } from './detail/system-setting-detail.component';
import { SystemSettingUpdateComponent } from './update/system-setting-update.component';
import { SystemSettingDeleteDialogComponent } from './delete/system-setting-delete-dialog.component';
import { SystemSettingRoutingModule } from './route/system-setting-routing.module';

@NgModule({
  imports: [SharedModule, SystemSettingRoutingModule],
  declarations: [SystemSettingComponent, SystemSettingDetailComponent, SystemSettingUpdateComponent, SystemSettingDeleteDialogComponent],
})
export class SystemSettingModule {}
