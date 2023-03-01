import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProject, NewProject } from '../project.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProject for edit and NewProjectFormGroupInput for create.
 */
type ProjectFormGroupInput = IProject | PartialWithRequiredKeyOf<NewProject>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProject | NewProject> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProjectFormRawValue = FormValueOf<IProject>;

type NewProjectFormRawValue = FormValueOf<NewProject>;

type ProjectFormDefaults = Pick<NewProject, 'id' | 'created' | 'updated' | 'hidden' | 'inactive'>;

type ProjectFormGroupContent = {
  id: FormControl<ProjectFormRawValue['id'] | NewProject['id']>;
  uid: FormControl<ProjectFormRawValue['uid']>;
  code: FormControl<ProjectFormRawValue['code']>;
  name: FormControl<ProjectFormRawValue['name']>;
  shortName: FormControl<ProjectFormRawValue['shortName']>;
  description: FormControl<ProjectFormRawValue['description']>;
  created: FormControl<ProjectFormRawValue['created']>;
  updated: FormControl<ProjectFormRawValue['updated']>;
  hidden: FormControl<ProjectFormRawValue['hidden']>;
  order: FormControl<ProjectFormRawValue['order']>;
  inactive: FormControl<ProjectFormRawValue['inactive']>;
  createdBy: FormControl<ProjectFormRawValue['createdBy']>;
  updatedBy: FormControl<ProjectFormRawValue['updatedBy']>;
};

export type ProjectFormGroup = FormGroup<ProjectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectFormService {
  createProjectFormGroup(project: ProjectFormGroupInput = { id: null }): ProjectFormGroup {
    const projectRawValue = this.convertProjectToProjectRawValue({
      ...this.getFormDefaults(),
      ...project,
    });
    return new FormGroup<ProjectFormGroupContent>({
      id: new FormControl(
        { value: projectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(projectRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(projectRawValue.code),
      name: new FormControl(projectRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(projectRawValue.shortName),
      description: new FormControl(projectRawValue.description),
      created: new FormControl(projectRawValue.created),
      updated: new FormControl(projectRawValue.updated),
      hidden: new FormControl(projectRawValue.hidden),
      order: new FormControl(projectRawValue.order),
      inactive: new FormControl(projectRawValue.inactive),
      createdBy: new FormControl(projectRawValue.createdBy),
      updatedBy: new FormControl(projectRawValue.updatedBy),
    });
  }

  getProject(form: ProjectFormGroup): IProject | NewProject {
    return this.convertProjectRawValueToProject(form.getRawValue() as ProjectFormRawValue | NewProjectFormRawValue);
  }

  resetForm(form: ProjectFormGroup, project: ProjectFormGroupInput): void {
    const projectRawValue = this.convertProjectToProjectRawValue({ ...this.getFormDefaults(), ...project });
    form.reset(
      {
        ...projectRawValue,
        id: { value: projectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      hidden: false,
      inactive: false,
    };
  }

  private convertProjectRawValueToProject(rawProject: ProjectFormRawValue | NewProjectFormRawValue): IProject | NewProject {
    return {
      ...rawProject,
      created: dayjs(rawProject.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProject.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProjectToProjectRawValue(
    project: IProject | (Partial<NewProject> & ProjectFormDefaults)
  ): ProjectFormRawValue | PartialWithRequiredKeyOf<NewProjectFormRawValue> {
    return {
      ...project,
      created: project.created ? project.created.format(DATE_TIME_FORMAT) : undefined,
      updated: project.updated ? project.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
