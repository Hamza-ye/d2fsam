import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgramTempOwnerService } from '../service/program-temp-owner.service';

import { ProgramTempOwnerComponent } from './program-temp-owner.component';

describe('ProgramTempOwner Management Component', () => {
  let comp: ProgramTempOwnerComponent;
  let fixture: ComponentFixture<ProgramTempOwnerComponent>;
  let service: ProgramTempOwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'program-temp-owner', component: ProgramTempOwnerComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProgramTempOwnerComponent],
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
      .overrideTemplate(ProgramTempOwnerComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTempOwnerComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgramTempOwnerService);

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
    expect(comp.programTempOwners?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to programTempOwnerService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgramTempOwnerIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgramTempOwnerIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
