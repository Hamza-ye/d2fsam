jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';

import { TrackedEntityAttributeValueAuditDeleteDialogComponent } from './tracked-entity-attribute-value-audit-delete-dialog.component';

describe('TrackedEntityAttributeValueAudit Management Delete Component', () => {
  let comp: TrackedEntityAttributeValueAuditDeleteDialogComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueAuditDeleteDialogComponent>;
  let service: TrackedEntityAttributeValueAuditService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TrackedEntityAttributeValueAuditDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TrackedEntityAttributeValueAuditDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityAttributeValueAuditDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrackedEntityAttributeValueAuditService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
