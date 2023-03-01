import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';

import { ProgramTempOwnershipAuditRoutingResolveService } from './program-temp-ownership-audit-routing-resolve.service';

describe('ProgramTempOwnershipAudit routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProgramTempOwnershipAuditRoutingResolveService;
  let service: ProgramTempOwnershipAuditService;
  let resultProgramTempOwnershipAudit: IProgramTempOwnershipAudit | null | undefined;

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
    routingResolveService = TestBed.inject(ProgramTempOwnershipAuditRoutingResolveService);
    service = TestBed.inject(ProgramTempOwnershipAuditService);
    resultProgramTempOwnershipAudit = undefined;
  });

  describe('resolve', () => {
    it('should return IProgramTempOwnershipAudit returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTempOwnershipAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramTempOwnershipAudit).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTempOwnershipAudit = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProgramTempOwnershipAudit).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProgramTempOwnershipAudit>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProgramTempOwnershipAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProgramTempOwnershipAudit).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
