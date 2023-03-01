import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MetadataVersionDetailComponent } from './metadata-version-detail.component';

describe('MetadataVersion Management Detail Component', () => {
  let comp: MetadataVersionDetailComponent;
  let fixture: ComponentFixture<MetadataVersionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MetadataVersionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ metadataVersion: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MetadataVersionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MetadataVersionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load metadataVersion on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.metadataVersion).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
