import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProgramStageDataElement } from '../program-stage-data-element.model';
import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';

import { ProgramStageDataElementRoutingResolveService } from './program-stage-data-element-routing-resolve.service';

describe('ProgramStageDataElement routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProgramStageDataElementRoutingResolveService;
  let service: ProgramStageDataElementService;
  let resultProgramStageDataElement: IProgramStageDataElement | null | undefined;

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
    routingResolveService = TestBed.inject(ProgramStageDataElementRoutingResolveService);
    service = TestBed.inject(ProgramStageDataElementService);
    resultProgramStageDataElement = undefined;
  });

  describe('resolve', () => {
    it('should return IProgramStageDataElement returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageDataElement = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramStageDataElement).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageDataElement = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProgramStageDataElement).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProgramStageDataElement>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramStageDataElement = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramStageDataElement).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
