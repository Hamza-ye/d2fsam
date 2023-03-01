import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DatastoreEntryDetailComponent } from './datastore-entry-detail.component';

describe('DatastoreEntry Management Detail Component', () => {
  let comp: DatastoreEntryDetailComponent;
  let fixture: ComponentFixture<DatastoreEntryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DatastoreEntryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ datastoreEntry: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DatastoreEntryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DatastoreEntryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load datastoreEntry on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.datastoreEntry).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
