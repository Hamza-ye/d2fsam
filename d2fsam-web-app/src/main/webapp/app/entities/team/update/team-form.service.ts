import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITeam, NewTeam } from '../team.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeam for edit and NewTeamFormGroupInput for create.
 */
type TeamFormGroupInput = ITeam | PartialWithRequiredKeyOf<NewTeam>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITeam | NewTeam> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TeamFormRawValue = FormValueOf<ITeam>;

type NewTeamFormRawValue = FormValueOf<NewTeam>;

type TeamFormDefaults = Pick<NewTeam, 'id' | 'created' | 'updated' | 'inactive' | 'members' | 'managedTeams' | 'groups' | 'managedByTeams'>;

type TeamFormGroupContent = {
  id: FormControl<TeamFormRawValue['id'] | NewTeam['id']>;
  uid: FormControl<TeamFormRawValue['uid']>;
  code: FormControl<TeamFormRawValue['code']>;
  created: FormControl<TeamFormRawValue['created']>;
  updated: FormControl<TeamFormRawValue['updated']>;
  name: FormControl<TeamFormRawValue['name']>;
  description: FormControl<TeamFormRawValue['description']>;
  comments: FormControl<TeamFormRawValue['comments']>;
  rating: FormControl<TeamFormRawValue['rating']>;
  teamType: FormControl<TeamFormRawValue['teamType']>;
  inactive: FormControl<TeamFormRawValue['inactive']>;
  activity: FormControl<TeamFormRawValue['activity']>;
  createdBy: FormControl<TeamFormRawValue['createdBy']>;
  updatedBy: FormControl<TeamFormRawValue['updatedBy']>;
  members: FormControl<TeamFormRawValue['members']>;
  managedTeams: FormControl<TeamFormRawValue['managedTeams']>;
  groups: FormControl<TeamFormRawValue['groups']>;
  managedByTeams: FormControl<TeamFormRawValue['managedByTeams']>;
};

export type TeamFormGroup = FormGroup<TeamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamFormService {
  createTeamFormGroup(team: TeamFormGroupInput = { id: null }): TeamFormGroup {
    const teamRawValue = this.convertTeamToTeamRawValue({
      ...this.getFormDefaults(),
      ...team,
    });
    return new FormGroup<TeamFormGroupContent>({
      id: new FormControl(
        { value: teamRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(teamRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(teamRawValue.code),
      created: new FormControl(teamRawValue.created),
      updated: new FormControl(teamRawValue.updated),
      name: new FormControl(teamRawValue.name),
      description: new FormControl(teamRawValue.description),
      comments: new FormControl(teamRawValue.comments),
      rating: new FormControl(teamRawValue.rating, {
        validators: [Validators.min(0), Validators.max(100)],
      }),
      teamType: new FormControl(teamRawValue.teamType, {
        validators: [Validators.required],
      }),
      inactive: new FormControl(teamRawValue.inactive),
      activity: new FormControl(teamRawValue.activity, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(teamRawValue.createdBy),
      updatedBy: new FormControl(teamRawValue.updatedBy),
      members: new FormControl(teamRawValue.members ?? []),
      managedTeams: new FormControl(teamRawValue.managedTeams ?? []),
      groups: new FormControl(teamRawValue.groups ?? []),
      managedByTeams: new FormControl(teamRawValue.managedByTeams ?? []),
    });
  }

  getTeam(form: TeamFormGroup): ITeam | NewTeam {
    return this.convertTeamRawValueToTeam(form.getRawValue() as TeamFormRawValue | NewTeamFormRawValue);
  }

  resetForm(form: TeamFormGroup, team: TeamFormGroupInput): void {
    const teamRawValue = this.convertTeamToTeamRawValue({ ...this.getFormDefaults(), ...team });
    form.reset(
      {
        ...teamRawValue,
        id: { value: teamRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TeamFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      members: [],
      managedTeams: [],
      groups: [],
      managedByTeams: [],
    };
  }

  private convertTeamRawValueToTeam(rawTeam: TeamFormRawValue | NewTeamFormRawValue): ITeam | NewTeam {
    return {
      ...rawTeam,
      created: dayjs(rawTeam.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTeam.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTeamToTeamRawValue(
    team: ITeam | (Partial<NewTeam> & TeamFormDefaults)
  ): TeamFormRawValue | PartialWithRequiredKeyOf<NewTeamFormRawValue> {
    return {
      ...team,
      created: team.created ? team.created.format(DATE_TIME_FORMAT) : undefined,
      updated: team.updated ? team.updated.format(DATE_TIME_FORMAT) : undefined,
      members: team.members ?? [],
      managedTeams: team.managedTeams ?? [],
      groups: team.groups ?? [],
      managedByTeams: team.managedByTeams ?? [],
    };
  }
}
