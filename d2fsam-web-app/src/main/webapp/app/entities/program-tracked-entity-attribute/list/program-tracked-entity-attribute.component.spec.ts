import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';

import { ProgramTrackedEntityAttributeComponent } from './program-tracked-entity-attribute.component';

describe('ProgramTrackedEntityAttribute Management Component', () => {
  let comp: ProgramTrackedEntityAttributeComponent;
  let fixture: ComponentFixture<ProgramTrackedEntityAttributeComponent>;
  let service: ProgramTrackedEntityAttributeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'program-tracked-entity-attribute', component: ProgramTrackedEntityAttributeComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProgramTrackedEntityAttributeComponent],
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
      .overrideTemplate(ProgramTrackedEntityAttributeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTrackedEntityAttributeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgramTrackedEntityAttributeService);

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
    expect(comp.programTrackedEntityAttributes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to programTrackedEntityAttributeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgramTrackedEntityAttributeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgramTrackedEntityAttributeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
