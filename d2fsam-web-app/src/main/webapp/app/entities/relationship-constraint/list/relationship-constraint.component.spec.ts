import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelationshipConstraintService } from '../service/relationship-constraint.service';

import { RelationshipConstraintComponent } from './relationship-constraint.component';

describe('RelationshipConstraint Management Component', () => {
  let comp: RelationshipConstraintComponent;
  let fixture: ComponentFixture<RelationshipConstraintComponent>;
  let service: RelationshipConstraintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'relationship-constraint', component: RelationshipConstraintComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RelationshipConstraintComponent],
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
      .overrideTemplate(RelationshipConstraintComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipConstraintComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelationshipConstraintService);

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
    expect(comp.relationshipConstraints?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to relationshipConstraintService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRelationshipConstraintIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRelationshipConstraintIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
