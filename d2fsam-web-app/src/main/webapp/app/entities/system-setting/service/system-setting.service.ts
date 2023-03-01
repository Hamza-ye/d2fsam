import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISystemSetting, NewSystemSetting } from '../system-setting.model';

export type PartialUpdateSystemSetting = Partial<ISystemSetting> & Pick<ISystemSetting, 'id'>;

export type EntityResponseType = HttpResponse<ISystemSetting>;
export type EntityArrayResponseType = HttpResponse<ISystemSetting[]>;

@Injectable({ providedIn: 'root' })
export class SystemSettingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/system-settings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(systemSetting: NewSystemSetting): Observable<EntityResponseType> {
    return this.http.post<ISystemSetting>(this.resourceUrl, systemSetting, { observe: 'response' });
  }

  update(systemSetting: ISystemSetting): Observable<EntityResponseType> {
    return this.http.put<ISystemSetting>(`${this.resourceUrl}/${this.getSystemSettingIdentifier(systemSetting)}`, systemSetting, {
      observe: 'response',
    });
  }

  partialUpdate(systemSetting: PartialUpdateSystemSetting): Observable<EntityResponseType> {
    return this.http.patch<ISystemSetting>(`${this.resourceUrl}/${this.getSystemSettingIdentifier(systemSetting)}`, systemSetting, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISystemSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISystemSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSystemSettingIdentifier(systemSetting: Pick<ISystemSetting, 'id'>): number {
    return systemSetting.id;
  }

  compareSystemSetting(o1: Pick<ISystemSetting, 'id'> | null, o2: Pick<ISystemSetting, 'id'> | null): boolean {
    return o1 && o2 ? this.getSystemSettingIdentifier(o1) === this.getSystemSettingIdentifier(o2) : o1 === o2;
  }

  addSystemSettingToCollectionIfMissing<Type extends Pick<ISystemSetting, 'id'>>(
    systemSettingCollection: Type[],
    ...systemSettingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const systemSettings: Type[] = systemSettingsToCheck.filter(isPresent);
    if (systemSettings.length > 0) {
      const systemSettingCollectionIdentifiers = systemSettingCollection.map(
        systemSettingItem => this.getSystemSettingIdentifier(systemSettingItem)!
      );
      const systemSettingsToAdd = systemSettings.filter(systemSettingItem => {
        const systemSettingIdentifier = this.getSystemSettingIdentifier(systemSettingItem);
        if (systemSettingCollectionIdentifiers.includes(systemSettingIdentifier)) {
          return false;
        }
        systemSettingCollectionIdentifiers.push(systemSettingIdentifier);
        return true;
      });
      return [...systemSettingsToAdd, ...systemSettingCollection];
    }
    return systemSettingCollection;
  }
}
