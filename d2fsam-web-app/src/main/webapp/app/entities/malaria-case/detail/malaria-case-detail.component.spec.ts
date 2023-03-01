import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MalariaCaseDetailComponent } from './malaria-case-detail.component';

describe('MalariaCase Management Detail Component', () => {
  let comp: MalariaCaseDetailComponent;
  let fixture: ComponentFixture<MalariaCaseDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MalariaCaseDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ malariaCase: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MalariaCaseDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MalariaCaseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load malariaCase on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.malariaCase).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
