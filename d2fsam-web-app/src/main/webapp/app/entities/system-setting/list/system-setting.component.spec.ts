import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SystemSettingService } from '../service/system-setting.service';

import { SystemSettingComponent } from './system-setting.component';

describe('SystemSetting Management Component', () => {
  let comp: SystemSettingComponent;
  let fixture: ComponentFixture<SystemSettingComponent>;
  let service: SystemSettingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'system-setting', component: SystemSettingComponent }]), HttpClientTestingModule],
      declarations: [SystemSettingComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(SystemSettingComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SystemSettingComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SystemSettingService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.systemSettings?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to systemSettingService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSystemSettingIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSystemSettingIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
