import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IComment, NewComment } from '../comment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IComment for edit and NewCommentFormGroupInput for create.
 */
type CommentFormGroupInput = IComment | PartialWithRequiredKeyOf<NewComment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IComment | NewComment> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type CommentFormRawValue = FormValueOf<IComment>;

type NewCommentFormRawValue = FormValueOf<NewComment>;

type CommentFormDefaults = Pick<NewComment, 'id' | 'created' | 'updated' | 'programInstances' | 'programStageInstances'>;

type CommentFormGroupContent = {
  id: FormControl<CommentFormRawValue['id'] | NewComment['id']>;
  uid: FormControl<CommentFormRawValue['uid']>;
  code: FormControl<CommentFormRawValue['code']>;
  created: FormControl<CommentFormRawValue['created']>;
  updated: FormControl<CommentFormRawValue['updated']>;
  commentText: FormControl<CommentFormRawValue['commentText']>;
  creator: FormControl<CommentFormRawValue['creator']>;
  createdBy: FormControl<CommentFormRawValue['createdBy']>;
  updatedBy: FormControl<CommentFormRawValue['updatedBy']>;
  programInstances: FormControl<CommentFormRawValue['programInstances']>;
  programStageInstances: FormControl<CommentFormRawValue['programStageInstances']>;
};

export type CommentFormGroup = FormGroup<CommentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CommentFormService {
  createCommentFormGroup(comment: CommentFormGroupInput = { id: null }): CommentFormGroup {
    const commentRawValue = this.convertCommentToCommentRawValue({
      ...this.getFormDefaults(),
      ...comment,
    });
    return new FormGroup<CommentFormGroupContent>({
      id: new FormControl(
        { value: commentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(commentRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(commentRawValue.code),
      created: new FormControl(commentRawValue.created),
      updated: new FormControl(commentRawValue.updated),
      commentText: new FormControl(commentRawValue.commentText),
      creator: new FormControl(commentRawValue.creator),
      createdBy: new FormControl(commentRawValue.createdBy),
      updatedBy: new FormControl(commentRawValue.updatedBy),
      programInstances: new FormControl(commentRawValue.programInstances ?? []),
      programStageInstances: new FormControl(commentRawValue.programStageInstances ?? []),
    });
  }

  getComment(form: CommentFormGroup): IComment | NewComment {
    return this.convertCommentRawValueToComment(form.getRawValue() as CommentFormRawValue | NewCommentFormRawValue);
  }

  resetForm(form: CommentFormGroup, comment: CommentFormGroupInput): void {
    const commentRawValue = this.convertCommentToCommentRawValue({ ...this.getFormDefaults(), ...comment });
    form.reset(
      {
        ...commentRawValue,
        id: { value: commentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CommentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      programInstances: [],
      programStageInstances: [],
    };
  }

  private convertCommentRawValueToComment(rawComment: CommentFormRawValue | NewCommentFormRawValue): IComment | NewComment {
    return {
      ...rawComment,
      created: dayjs(rawComment.created, DATE_TIME_FORMAT),
      updated: dayjs(rawComment.updated, DATE_TIME_FORMAT),
    };
  }

  private convertCommentToCommentRawValue(
    comment: IComment | (Partial<NewComment> & CommentFormDefaults)
  ): CommentFormRawValue | PartialWithRequiredKeyOf<NewCommentFormRawValue> {
    return {
      ...comment,
      created: comment.created ? comment.created.format(DATE_TIME_FORMAT) : undefined,
      updated: comment.updated ? comment.updated.format(DATE_TIME_FORMAT) : undefined,
      programInstances: comment.programInstances ?? [],
      programStageInstances: comment.programStageInstances ?? [],
    };
  }
}
