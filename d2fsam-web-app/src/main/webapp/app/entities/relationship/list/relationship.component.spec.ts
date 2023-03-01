import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelationshipService } from '../service/relationship.service';

import { RelationshipComponent } from './relationship.component';

describe('Relationship Management Component', () => {
  let comp: RelationshipComponent;
  let fixture: ComponentFixture<RelationshipComponent>;
  let service: RelationshipService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'relationship', component: RelationshipComponent }]), HttpClientTestingModule],
      declarations: [RelationshipComponent],
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
      .overrideTemplate(RelationshipComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelationshipService);

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
    expect(comp.relationships?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to relationshipService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRelationshipIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRelationshipIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
