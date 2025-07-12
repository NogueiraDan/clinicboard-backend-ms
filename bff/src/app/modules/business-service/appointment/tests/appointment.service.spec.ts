/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { AppointmentService } from '../appointment.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';
import { AppointmentRequestDto } from '../dto/appointment-request.dto';
import { AppointmentResponseDto } from '../dto/appointment-response.dto';

describe('AppointmentService', () => {
  let service: AppointmentService;
  let httpRequestService: jest.Mocked<HttpRequestService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AppointmentService,
        {
          provide: HttpRequestService,
          useValue: {
            request: jest.fn(),
            get: jest.fn(),
            post: jest.fn(),
            put: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: AppConfigService,
          useValue: {
            baseUrls: {
              BUSINESS_SERVICE: 'http://localhost:3001',
            },
            get: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<AppointmentService>(AppointmentService);
    httpRequestService = module.get(HttpRequestService);
  });
  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    it('should create appointment successfully', async () => {
      const appointmentRequest: AppointmentRequestDto = {
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };

      const expectedResponse: AppointmentResponseDto = {
        id: '1',
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.create(appointmentRequest);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/appointments',
        appointmentRequest,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when appointment data is invalid', async () => {
      const appointmentRequest = {
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };

      const validationError = new Error('Invalid appointment data');
      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.create(appointmentRequest)).rejects.toThrow(
        'Invalid appointment data',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/appointments',
        appointmentRequest,
      );
    });

    it('should throw error when time slot is not available', async () => {
      const appointmentRequest = {
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };

      const conflictError = new Error('Time slot not available');
      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(service.create(appointmentRequest)).rejects.toThrow(
        'Time slot not available',
      );
    });
  });

  describe('findAll', () => {
    it('should retrieve all appointments successfully', async () => {
      const expectedAppointments = [
        {
          id: '1',
          patientId: '1',
          professionalId: '2',
          date: '2025-07-15',
          time: '10:00',
          status: 'SCHEDULED',
        },
        {
          id: '2',
          patientId: '3',
          professionalId: '2',
          date: '2025-07-15',
          time: '11:00',
          status: 'SCHEDULED',
        },
      ];

      httpRequestService.request.mockResolvedValue(expectedAppointments);

      const result = await service.findAll();

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        'http://localhost:3001/appointments',
      );
      expect(result).toEqual(expectedAppointments);
    });

    it('should return empty array when no appointments found', async () => {
      httpRequestService.request.mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        'http://localhost:3001/appointments',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const serviceError = new Error('Service unavailable');
      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.findAll()).rejects.toThrow('Service unavailable');
    });
  });

  describe('findById', () => {
    it('should find appointment by id successfully', async () => {
      const appointmentId = '1';
      const expectedAppointment = {
        id: '1',
        patientId: '1',
        professionalId: '2',
        serviceId: '3',
        date: '2025-07-15',
        time: '10:00',
        status: 'SCHEDULED',
      };

      httpRequestService.request.mockResolvedValue(expectedAppointment);

      const result = await service.findOne(appointmentId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/appointments/${appointmentId}`,
      );
      expect(result).toEqual(expectedAppointment);
    });

    it('should throw error when appointment is not found', async () => {
      const notFoundError = new Error('Appointment not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.findOne('1')).rejects.toThrow(
        'Appointment not found',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/appointments/${'1'}`,
      );
    });
  });

  describe('update', () => {
    it('should update appointment successfully', async () => {
      const appointmentUpdateRequest: AppointmentRequestDto = {
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };
      const appointmentId = '1';
      const expectedResponse = {
        id: '1',
        patientId: '1',
        professionalId: '2',
        serviceId: '3',
        date: '2025-07-16',
        time: '14:00',
        status: 'SCHEDULED',
        notes: 'Reagendamento solicitado pelo paciente',
        updatedAt: '2025-07-12T15:00:00Z',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.update(
        appointmentId,
        appointmentUpdateRequest,
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'PUT',
        `http://localhost:3001/appointments/${appointmentId}`,
        appointmentUpdateRequest,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when updating non-existent appointment', async () => {
      const appointmentId = 'non-existent';
      const appointmentUpdateRequest: AppointmentRequestDto = {
        date: '2025-07-15',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };
      const notFoundError = new Error('Appointment not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(
        service.update(appointmentId, appointmentUpdateRequest),
      ).rejects.toThrow('Appointment not found');
    });

    it('should throw error when update data is invalid', async () => {
      const appointmentId = '1';
      const invalidData: AppointmentRequestDto = {
        date: 'invalid-date',
        hour: '10:00',
        type: 'Consulta de rotina',
        user_id: '2',
        patient_id: '1',
      };
      const validationError = new Error('Invalid update data');

      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.update(appointmentId, invalidData)).rejects.toThrow(
        'Invalid update data',
      );
    });
  });

  describe('delete', () => {
    it('should delete appointment successfully', async () => {
      const appointmentId = '1';
      const expectedResponse = { message: 'Appointment deleted successfully' };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.remove(appointmentId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/appointments/${appointmentId}`,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when deleting non-existent appointment', async () => {
      const appointmentId = 'non-existent';
      const notFoundError = new Error('Appointment not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.remove(appointmentId)).rejects.toThrow(
        'Appointment not found',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/appointments/${appointmentId}`,
      );
    });

    it('should throw error when appointment cannot be deleted', async () => {
      const appointmentId = '1';
      const businessError = new Error('Cannot delete confirmed appointment');

      httpRequestService.request.mockRejectedValue(businessError);

      await expect(service.remove(appointmentId)).rejects.toThrow(
        'Cannot delete confirmed appointment',
      );
    });
  });
});
