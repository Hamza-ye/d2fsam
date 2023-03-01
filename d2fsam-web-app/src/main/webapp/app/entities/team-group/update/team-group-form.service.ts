import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITeamGroup, NewTeamGroup } from '../team-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamGroup for edit and NewTeamGroupFormGroupInput for create.
 */
type TeamGroupFormGroupInput = ITeamGroup | PartialWithRequiredKeyOf<NewTeamGroup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITeamGroup | NewTeamGroup> = Omit<T, 'created' | 'updated' | 'activeFrom' | 'activeTo'> & {
  created?: string | null;
  updated?: string | null;
  activeFrom?: string | null;
  activeTo?: string | null;
};

type TeamGroupFormRawValue = FormValueOf<ITeamGroup>;

type NewTeamGroupFormRawValue = FormValueOf<NewTeamGroup>;

type TeamGroupFormDefaults = Pick<NewTeamGroup, 'id' | 'created' | 'updated' | 'activeFrom' | 'activeTo' | 'inactive' | 'members'>;

type TeamGroupFormGroupContent = {
  id: FormControl<TeamGroupFormRawValue['id'] | NewTeamGroup['id']>;
  uid: FormControl<TeamGroupFormRawValue['uid']>;
  code: FormControl<TeamGroupFormRawValue['code']>;
  name: FormControl<TeamGroupFormRawValue['name']>;
  uuid: FormControl<TeamGroupFormRawValue['uuid']>;
  created: FormControl<TeamGroupFormRawValue['created']>;
  updated: FormControl<TeamGroupFormRawValue['updated']>;
  activeFrom: FormControl<TeamGroupFormRawValue['activeFrom']>;
  activeTo: FormControl<TeamGroupFormRawValue['activeTo']>;
  inactive: FormControl<TeamGroupFormRawValue['inactive']>;
  members: FormControl<TeamGroupFormRawValue['members']>;
};

export type TeamGroupFormGroup = FormGroup<TeamGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamGroupFormService {
  createTeamGroupFormGroup(teamGroup: TeamGroupFormGroupInput = { id: null }): TeamGroupFormGroup {
    const teamGroupRawValue = this.convertTeamGroupToTeamGroupRawValue({
      ...this.getFormDefaults(),
      ...teamGroup,
    });
    return new FormGroup<TeamGroupFormGroupContent>({
      id: new FormControl(
        { value: teamGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(teamGroupRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(teamGroupRawValue.code),
      name: new FormControl(teamGroupRawValue.name, {
        validators: [Validators.required],
      }),
      uuid: new FormControl(teamGroupRawValue.uuid),
      created: new FormControl(teamGroupRawValue.created),
      updated: new FormControl(teamGroupRawValue.updated),
      activeFrom: new FormControl(teamGroupRawValue.activeFrom),
      activeTo: new FormControl(teamGroupRawValue.activeTo),
      inactive: new FormControl(teamGroupRawValue.inactive),
      members: new FormControl(teamGroupRawValue.members ?? []),
    });
  }

  getTeamGroup(form: TeamGroupFormGroup): ITeamGroup | NewTeamGroup {
    return this.convertTeamGroupRawValueToTeamGroup(form.getRawValue() as TeamGroupFormRawValue | NewTeamGroupFormRawValue);
  }

  resetForm(form: TeamGroupFormGroup, teamGroup: TeamGroupFormGroupInput): void {
    const teamGroupRawValue = this.convertTeamGroupToTeamGroupRawValue({ ...this.getFormDefaults(), ...teamGroup });
    form.reset(
      {
        ...teamGroupRawValue,
        id: { value: teamGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TeamGroupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      activeFrom: currentTime,
      activeTo: currentTime,
      inactive: false,
      members: [],
    };
  }

  private convertTeamGroupRawValueToTeamGroup(rawTeamGroup: TeamGroupFormRawValue | NewTeamGroupFormRawValue): ITeamGroup | NewTeamGroup {
    return {
      ...rawTeamGroup,
      created: dayjs(rawTeamGroup.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTeamGroup.updated, DATE_TIME_FORMAT),
      activeFrom: dayjs(rawTeamGroup.activeFrom, DATE_TIME_FORMAT),
      activeTo: dayjs(rawTeamGroup.activeTo, DATE_TIME_FORMAT),
    };
  }

  private convertTeamGroupToTeamGroupRawValue(
    teamGroup: ITeamGroup | (Partial<NewTeamGroup> & TeamGroupFormDefaults)
  ): TeamGroupFormRawValue | PartialWithRequiredKeyOf<NewTeamGroupFormRawValue> {
    return {
      ...teamGroup,
      created: teamGroup.created ? teamGroup.created.format(DATE_TIME_FORMAT) : undefined,
      updated: teamGroup.updated ? teamGroup.updated.format(DATE_TIME_FORMAT) : undefined,
      activeFrom: teamGroup.activeFrom ? teamGroup.activeFrom.format(DATE_TIME_FORMAT) : undefined,
      activeTo: teamGroup.activeTo ? teamGroup.activeTo.format(DATE_TIME_FORMAT) : undefined,
      members: teamGroup.members ?? [],
    };
  }
}
