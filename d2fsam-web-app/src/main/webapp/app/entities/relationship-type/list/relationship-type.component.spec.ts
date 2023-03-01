import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelationshipTypeService } from '../service/relationship-type.service';

import { RelationshipTypeComponent } from './relationship-type.component';

describe('RelationshipType Management Component', () => {
  let comp: RelationshipTypeComponent;
  let fixture: ComponentFixture<RelationshipTypeComponent>;
  let service: RelationshipTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'relationship-type', component: RelationshipTypeComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RelationshipTypeComponent],
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
      .overrideTemplate(RelationshipTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelationshipTypeService);

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
    expect(comp.relationshipTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to relationshipTypeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRelationshipTypeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRelationshipTypeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
