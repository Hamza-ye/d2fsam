import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { OrganisationUnitLevelFormService, OrganisationUnitLevelFormGroup } from './organisation-unit-level-form.service';
import { IOrganisationUnitLevel } from '../organisation-unit-level.model';
import { OrganisationUnitLevelService } from '../service/organisation-unit-level.service';

@Component({
  selector: 'app-organisation-unit-level-update',
  templateUrl: './organisation-unit-level-update.component.html',
})
export class OrganisationUnitLevelUpdateComponent implements OnInit {
  isSaving = false;
  organisationUnitLevel: IOrganisationUnitLevel | null = null;

  editForm: OrganisationUnitLevelFormGroup = this.organisationUnitLevelFormService.createOrganisationUnitLevelFormGroup();

  constructor(
    protected organisationUnitLevelService: OrganisationUnitLevelService,
    protected organisationUnitLevelFormService: OrganisationUnitLevelFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitLevel }) => {
      this.organisationUnitLevel = organisationUnitLevel;
      if (organisationUnitLevel) {
        this.updateForm(organisationUnitLevel);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const organisationUnitLevel = this.organisationUnitLevelFormService.getOrganisationUnitLevel(this.editForm);
    if (organisationUnitLevel.id !== null) {
      this.subscribeToSaveResponse(this.organisationUnitLevelService.update(organisationUnitLevel));
    } else {
      this.subscribeToSaveResponse(this.organisationUnitLevelService.create(organisationUnitLevel));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrganisationUnitLevel>>): void {
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

  protected updateForm(organisationUnitLevel: IOrganisationUnitLevel): void {
    this.organisationUnitLevel = organisationUnitLevel;
    this.organisationUnitLevelFormService.resetForm(this.editForm, organisationUnitLevel);
  }
}
