import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ExternalFileResourceDetailComponent } from './external-file-resource-detail.component';

describe('ExternalFileResource Management Detail Component', () => {
  let comp: ExternalFileResourceDetailComponent;
  let fixture: ComponentFixture<ExternalFileResourceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExternalFileResourceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ externalFileResource: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ExternalFileResourceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExternalFileResourceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load externalFileResource on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.externalFileResource).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
