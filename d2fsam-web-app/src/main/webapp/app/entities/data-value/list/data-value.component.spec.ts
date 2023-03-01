import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DataValueService } from '../service/data-value.service';

import { DataValueComponent } from './data-value.component';

describe('DataValue Management Component', () => {
  let comp: DataValueComponent;
  let fixture: ComponentFixture<DataValueComponent>;
  let service: DataValueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'data-value', component: DataValueComponent }]), HttpClientTestingModule],
      declarations: [DataValueComponent],
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
      .overrideTemplate(DataValueComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DataValueComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DataValueService);

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
    expect(comp.dataValues?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to dataValueService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDataValueIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDataValueIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
