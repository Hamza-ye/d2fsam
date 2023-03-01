import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SystemSettingFormService, SystemSettingFormGroup } from './system-setting-form.service';
import { ISystemSetting } from '../system-setting.model';
import { SystemSettingService } from '../service/system-setting.service';

@Component({
  selector: 'app-system-setting-update',
  templateUrl: './system-setting-update.component.html',
})
export class SystemSettingUpdateComponent implements OnInit {
  isSaving = false;
  systemSetting: ISystemSetting | null = null;

  editForm: SystemSettingFormGroup = this.systemSettingFormService.createSystemSettingFormGroup();

  constructor(
    protected systemSettingService: SystemSettingService,
    protected systemSettingFormService: SystemSettingFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemSetting }) => {
      this.systemSetting = systemSetting;
      if (systemSetting) {
        this.updateForm(systemSetting);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const systemSetting = this.systemSettingFormService.getSystemSetting(this.editForm);
    if (systemSetting.id !== null) {
      this.subscribeToSaveResponse(this.systemSettingService.update(systemSetting));
    } else {
      this.subscribeToSaveResponse(this.systemSettingService.create(systemSetting));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISystemSetting>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(systemSetting: ISystemSetting): void {
    this.systemSetting = systemSetting;
    this.systemSettingFormService.resetForm(this.editForm, systemSetting);
  }
}
