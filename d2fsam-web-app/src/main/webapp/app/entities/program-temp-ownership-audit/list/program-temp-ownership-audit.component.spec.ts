import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';

import { ProgramTempOwnershipAuditComponent } from './program-temp-ownership-audit.component';

describe('ProgramTempOwnershipAudit Management Component', () => {
  let comp: ProgramTempOwnershipAuditComponent;
  let fixture: ComponentFixture<ProgramTempOwnershipAuditComponent>;
  let service: ProgramTempOwnershipAuditService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'program-temp-ownership-audit', component: ProgramTempOwnershipAuditComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProgramTempOwnershipAuditComponent],
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
      .overrideTemplate(ProgramTempOwnershipAuditComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTempOwnershipAuditComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgramTempOwnershipAuditService);

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
    expect(comp.programTempOwnershipAudits?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to programTempOwnershipAuditService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgramTempOwnershipAuditIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgramTempOwnershipAuditIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
