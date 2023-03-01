import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';
import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';

import { ProgramStageInstanceFilterRoutingResolveService } from './program-stage-instance-filter-routing-resolve.service';

describe('ProgramStageInstanceFilter routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProgramStageInstanceFilterRoutingResolveService;
  let service: ProgramStageInstanceFilterService;
  let resultProgramStageInstanceFilter: IProgramStageInstanceFilter | null | undefined;

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
    routingResolveService = TestBed.inject(ProgramStageInstanceFilterRoutingResolveService);
    service = TestBed.inject(ProgramStageInstanceFilterService);
    resultProgramStageInstanceFilter = undefined;
  });

  describe('resolve', () => {
    it('should return IProgramStageInstanceFilter returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageInstanceFilter = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramStageInstanceFilter).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageInstanceFilter = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProgramStageInstanceFilter).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProgramStageInstanceFilter>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageInstanceFilter = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramStageInstanceFilter).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
