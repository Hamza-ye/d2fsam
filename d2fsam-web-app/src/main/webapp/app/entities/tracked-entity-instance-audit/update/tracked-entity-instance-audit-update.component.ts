import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TrackedEntityInstanceAuditFormService, TrackedEntityInstanceAuditFormGroup } from './tracked-entity-instance-audit-form.service';
import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';
import { TrackedEntityInstanceAuditService } from '../service/tracked-entity-instance-audit.service';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

@Component({
  selector: 'app-tracked-entity-instance-audit-update',
  templateUrl: './tracked-entity-instance-audit-update.component.html',
})
export class TrackedEntityInstanceAuditUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityInstanceAudit: ITrackedEntityInstanceAudit | null = null;
  auditTypeValues = Object.keys(AuditType);

  editForm: TrackedEntityInstanceAuditFormGroup = this.trackedEntityInstanceAuditFormService.createTrackedEntityInstanceAuditFormGroup();

  constructor(
    protected trackedEntityInstanceAuditService: TrackedEntityInstanceAuditService,
    protected trackedEntityInstanceAuditFormService: TrackedEntityInstanceAuditFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstanceAudit }) => {
      this.trackedEntityInstanceAudit = trackedEntityInstanceAudit;
      if (trackedEntityInstanceAudit) {
        this.updateForm(trackedEntityInstanceAudit);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityInstanceAudit = this.trackedEntityInstanceAuditFormService.getTrackedEntityInstanceAudit(this.editForm);
    if (trackedEntityInstanceAudit.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityInstanceAuditService.update(trackedEntityInstanceAudit));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityInstanceAuditService.create(trackedEntityInstanceAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityInstanceAudit>>): void {
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

  protected updateForm(trackedEntityInstanceAudit: ITrackedEntityInstanceAudit): void {
    this.trackedEntityInstanceAudit = trackedEntityInstanceAudit;
    this.trackedEntityInstanceAuditFormService.resetForm(this.editForm, trackedEntityInstanceAudit);
  }
}
