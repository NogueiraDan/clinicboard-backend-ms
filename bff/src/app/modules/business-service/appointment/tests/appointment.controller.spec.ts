import { Test, TestingModule } from '@nestjs/testing';
import { AppointmentController } from '../appointment.controller';
import { AppointmentService } from '../appointment.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';

describe('AppointmentController', () => {
  let controller: AppointmentController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AppointmentController],
      providers: [
        AppointmentService,
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

    controller = module.get<AppointmentController>(AppointmentController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
