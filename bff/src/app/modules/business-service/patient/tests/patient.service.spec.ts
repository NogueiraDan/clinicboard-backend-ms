/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { PatientService } from '../patient.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';
import { PatientRequestDto } from '../dto/patient-request.dto';
import { PatientResponseDto } from '../dto/patient-response.dto';

describe('PatientService', () => {
  let service: PatientService;
  let httpRequestService: jest.Mocked<HttpRequestService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PatientService,
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

    service = module.get<PatientService>(PatientService);
    httpRequestService = module.get(HttpRequestService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('findProfessionalPatients', () => {
    it('should retrieve professional patients successfully', async () => {
      const professionalId = '1';
      const expectedPatients: PatientResponseDto[] = [
        {
          id: '1',
          name: 'João Silva',
          email: 'joao@email.com',
          contact: '(11) 99999-9999',
          professionalId: '1',
        },
        {
          id: '2',
          name: 'Maria Santos',
          email: 'maria@email.com',
          contact: '(11) 99999-9999',
          professionalId: '1',
        },
      ];

      httpRequestService.request.mockResolvedValue(expectedPatients);

      const result = await service.findProfessionalPatients(professionalId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/patients/professional/${professionalId}`,
      );
      expect(result).toEqual(expectedPatients);
    });

    it('should return empty array when professional has no patients', async () => {
      const professionalId = '1';
      httpRequestService.request.mockResolvedValue([]);

      const result = await service.findProfessionalPatients(professionalId);

      expect(result).toEqual([]);
      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/patients/professional/${professionalId}`,
      );
    });

    it('should throw error when professional is not found', async () => {
      const professionalId = 'non-existent';
      const notFoundError = new Error('Professional not found');
      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(
        service.findProfessionalPatients(professionalId),
      ).rejects.toThrow('Professional not found');
    });

    it('should throw error when service is unavailable', async () => {
      const professionalId = '1';
      const serviceError = new Error('Service unavailable');
      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(
        service.findProfessionalPatients(professionalId),
      ).rejects.toThrow('Service unavailable');
    });
  });

  describe('create', () => {
    it('should create patient successfully', async () => {
      const patientRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      const expectedResponse: PatientResponseDto = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.create(patientRequest);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/patients',
        patientRequest,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when patient data is invalid', async () => {
      const patientRequest: PatientRequestDto = {
        name: '',
        email: 'invalid-email',
        contact: '123',
        professionalId: '1',
      };

      const validationError = new Error('Invalid patient data');
      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.create(patientRequest)).rejects.toThrow(
        'Invalid patient data',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/patients',
        patientRequest,
      );
    });

    it('should throw error when patient email already exists', async () => {
      const patientRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      const conflictError = new Error('Email already exists');
      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(service.create(patientRequest)).rejects.toThrow(
        'Email already exists',
      );
    });

    it('should throw error when patient CPF already exists', async () => {
      const patientRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      const conflictError = new Error('CPF already exists');
      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(service.create(patientRequest)).rejects.toThrow(
        'CPF already exists',
      );
    });
  });

  describe('findOne', () => {
    it('should find patient by id successfully', async () => {
      const patientId = '1';
      const expectedPatient: PatientResponseDto = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      httpRequestService.request.mockResolvedValue(expectedPatient);

      const result = await service.findOne(patientId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/patients/${patientId}`,
      );
      expect(result).toEqual(expectedPatient);
    });

    it('should throw error when patient is not found', async () => {
      const patientId = 'non-existent';
      const notFoundError = new Error('Patient not found');
      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.findOne(patientId)).rejects.toThrow(
        'Patient not found',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/patients/${patientId}`,
      );
    });

    it('should throw error when service is unavailable', async () => {
      const patientId = '1';
      const serviceError = new Error('Service unavailable');
      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.findOne(patientId)).rejects.toThrow(
        'Service unavailable',
      );
    });
  });

  describe('update', () => {
    it('should update patient successfully', async () => {
      const patientId = 1;
      const patientUpdateRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      const expectedResponse: PatientResponseDto = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.update(patientId, patientUpdateRequest);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'PUT',
        `http://localhost:3001/patients/${patientId}`,
        patientUpdateRequest,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when updating non-existent patient', async () => {
      const patientId = 999;
      const patientUpdateRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };
      const notFoundError = new Error('Patient not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(
        service.update(patientId, patientUpdateRequest),
      ).rejects.toThrow('Patient not found');
    });

    it('should throw error when update data is invalid', async () => {
      const patientId = 1;
      const invalidData: PatientRequestDto = {
        name: '',
        email: 'invalid-email',
        contact: '123',
        professionalId: '1',
      };
      const validationError = new Error('Invalid update data');

      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.update(patientId, invalidData)).rejects.toThrow(
        'Invalid update data',
      );
    });

    it('should throw error when email already exists for another patient', async () => {
      const patientId = 1;
      const patientUpdateRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'existing@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };
      const conflictError = new Error('Email already exists');

      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(
        service.update(patientId, patientUpdateRequest),
      ).rejects.toThrow('Email already exists');
    });

    it('should throw error when CPF already exists for another patient', async () => {
      const patientId = 1;
      const patientUpdateRequest: PatientRequestDto = {
        name: 'João Silva',
        email: 'joao@email.com',
        contact: '(11) 99999-9999',
        professionalId: '1',
      };
      const conflictError = new Error('CPF already exists');

      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(
        service.update(patientId, patientUpdateRequest),
      ).rejects.toThrow('CPF already exists');
    });
  });

  describe('remove', () => {
    it('should delete patient successfully', async () => {
      const patientId = 1;
      const expectedResponse = { message: 'Patient deleted successfully' };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.remove(patientId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/patients/${patientId}`,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when deleting non-existent patient', async () => {
      const patientId = 999;
      const notFoundError = new Error('Patient not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.remove(patientId)).rejects.toThrow(
        'Patient not found',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/patients/${patientId}`,
      );
    });

    it('should throw error when patient has active appointments', async () => {
      const patientId = 1;
      const businessError = new Error(
        'Cannot delete patient with active appointments',
      );

      httpRequestService.request.mockRejectedValue(businessError);

      await expect(service.remove(patientId)).rejects.toThrow(
        'Cannot delete patient with active appointments',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const patientId = 1;
      const serviceError = new Error('Service unavailable');

      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.remove(patientId)).rejects.toThrow(
        'Service unavailable',
      );
    });
  });
});
