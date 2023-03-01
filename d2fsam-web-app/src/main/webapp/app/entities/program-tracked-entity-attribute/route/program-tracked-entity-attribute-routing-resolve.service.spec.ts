import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';

import { ProgramTrackedEntityAttributeRoutingResolveService } from './program-tracked-entity-attribute-routing-resolve.service';

describe('ProgramTrackedEntityAttribute routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProgramTrackedEntityAttributeRoutingResolveService;
  let service: ProgramTrackedEntityAttributeService;
  let resultProgramTrackedEntityAttribute: IProgramTrackedEntityAttribute | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ProgramTrackedEntityAttributeRoutingResolveService);
    service = TestBed.inject(ProgramTrackedEntityAttributeService);
    resultProgramTrackedEntityAttribute = undefined;
  });

  describe('resolve', () => {
    it('should return IProgramTrackedEntityAttribute returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTrackedEntityAttribute = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramTrackedEntityAttribute).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTrackedEntityAttribute = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProgramTrackedEntityAttribute).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProgramTrackedEntityAttribute>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTrackedEntityAttribute = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramTrackedEntityAttribute).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
