import { Test, TestingModule } from '@nestjs/testing';
import { PatientController } from '../patient.controller';
import { PatientService } from '../patient.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';

describe('PatientController', () => {
  let controller: PatientController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PatientController],
      providers: [
        PatientService,
        {
          provide: HttpRequestService,
          useValue: {
            get: jest.fn(),
            post: jest.fn(),
            put: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: AppConfigService,
          useValue: {
            get: jest.fn(),
          },
        },
      ],
    }).compile();

    controller = module.get<PatientController>(PatientController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
