jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TrackedEntityProgramOwnerService } from '../service/tracked-entity-program-owner.service';

import { TrackedEntityProgramOwnerDeleteDialogComponent } from './tracked-entity-program-owner-delete-dialog.component';

describe('TrackedEntityProgramOwner Management Delete Component', () => {
  let comp: TrackedEntityProgramOwnerDeleteDialogComponent;
  let fixture: ComponentFixture<TrackedEntityProgramOwnerDeleteDialogComponent>;
  let service: TrackedEntityProgramOwnerService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TrackedEntityProgramOwnerDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TrackedEntityProgramOwnerDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityProgramOwnerDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrackedEntityProgramOwnerService);
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
