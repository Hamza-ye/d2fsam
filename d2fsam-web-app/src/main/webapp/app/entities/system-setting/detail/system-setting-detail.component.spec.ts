import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SystemSettingDetailComponent } from './system-setting-detail.component';

describe('SystemSetting Management Detail Component', () => {
  let comp: SystemSettingDetailComponent;
  let fixture: ComponentFixture<SystemSettingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SystemSettingDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ systemSetting: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SystemSettingDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SystemSettingDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load systemSetting on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.systemSetting).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
