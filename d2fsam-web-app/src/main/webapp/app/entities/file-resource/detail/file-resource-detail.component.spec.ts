import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FileResourceDetailComponent } from './file-resource-detail.component';

describe('FileResource Management Detail Component', () => {
  let comp: FileResourceDetailComponent;
  let fixture: ComponentFixture<FileResourceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FileResourceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ fileResource: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FileResourceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FileResourceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load fileResource on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.fileResource).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
