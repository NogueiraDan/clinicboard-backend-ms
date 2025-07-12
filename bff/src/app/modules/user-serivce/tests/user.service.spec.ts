/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { UsersService } from '../user.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';
import { UpdateUserDto } from 'src/app/commons/dto/request/update-user.dto';
import { UserRole } from 'src/app/commons/enums/user-role.enum';

describe('UsersService', () => {
  let service: UsersService;
  let httpRequestService: jest.Mocked<HttpRequestService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UsersService,
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
              USERS_SERVICE: 'http://localhost:3001',
            },
            get: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<UsersService>(UsersService);
    httpRequestService = module.get(HttpRequestService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('findByEmail', () => {
    it('should find user by email successfully', async () => {
      const email = 'joao@email.com';
      const expectedUser = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        role: 'PROFESSIONAL',
        active: true,
      };

      httpRequestService.request.mockResolvedValue(expectedUser);

      const result = await service.findByEmail(email);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/users/user/${email}`,
      );
      expect(result).toEqual(expectedUser);
    });

    it('should throw error when user with email is not found', async () => {
      const email = 'nonexistent@email.com';
      const notFoundError = new Error('User not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.findByEmail(email)).rejects.toThrow(
        'User not found',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/users/user/${email}`,
      );
    });

    it('should throw error when email format is invalid', async () => {
      const invalidEmail = 'invalid-email';
      const validationError = new Error('Invalid email format');

      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.findByEmail(invalidEmail)).rejects.toThrow(
        'Invalid email format',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const email = 'joao@email.com';
      const serviceError = new Error('Service unavailable');

      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.findByEmail(email)).rejects.toThrow(
        'Service unavailable',
      );
    });
  });

  describe('findAll', () => {
    it('should retrieve all users successfully', async () => {
      const expectedUsers = [
        {
          id: '1',
          name: 'João Silva',
          email: 'joao@email.com',
          role: 'PROFESSIONAL',
          active: true,
        },
        {
          id: '2',
          name: 'Maria Santos',
          email: 'maria@email.com',
          role: 'ADMIN',
          active: true,
        },
        {
          id: '3',
          name: 'Pedro Costa',
          email: 'pedro@email.com',
          role: 'PROFESSIONAL',
          active: false,
        },
      ];

      httpRequestService.request.mockResolvedValue(expectedUsers);

      const result = await service.findAll();

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        'http://localhost:3001/users',
      );
      expect(result).toEqual(expectedUsers);
    });

    it('should return empty array when no users found', async () => {
      httpRequestService.request.mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        'http://localhost:3001/users',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const serviceError = new Error('Service unavailable');

      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.findAll()).rejects.toThrow('Service unavailable');
    });

    it('should throw error when unauthorized access', async () => {
      const unauthorizedError = new Error('Unauthorized access');

      httpRequestService.request.mockRejectedValue(unauthorizedError);

      await expect(service.findAll()).rejects.toThrow('Unauthorized access');
    });
  });

  describe('findOne', () => {
    it('should find user by id successfully', async () => {
      const userId = '1';
      const expectedUser = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        role: 'PROFESSIONAL',
        active: true,
        createdAt: '2025-01-01T00:00:00Z',
        updatedAt: '2025-01-01T00:00:00Z',
      };

      httpRequestService.request.mockResolvedValue(expectedUser);

      const result = await service.findOne(userId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/users/${userId}`,
      );
      expect(result).toEqual(expectedUser);
    });

    it('should throw error when user is not found', async () => {
      const userId = 'non-existent';
      const notFoundError = new Error('User not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.findOne(userId)).rejects.toThrow('User not found');

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'GET',
        `http://localhost:3001/users/${userId}`,
      );
    });

    it('should throw error when user id format is invalid', async () => {
      const invalidId = 'invalid-id';
      const validationError = new Error('Invalid user ID format');

      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.findOne(invalidId)).rejects.toThrow(
        'Invalid user ID format',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const userId = '1';
      const serviceError = new Error('Service unavailable');

      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.findOne(userId)).rejects.toThrow(
        'Service unavailable',
      );
    });
  });

  describe('update', () => {
    it('should update user successfully', async () => {
      const userId = '1';
      const updateUserDto: UpdateUserDto = {
        name: 'João Silva Santos',
        email: 'joao.santos@email.com',
        role: UserRole.ADMIN,
      };

      const expectedResponse = {
        id: '1',
        name: 'João Silva Santos',
        email: 'joao.santos@email.com',
        role: 'ADMIN',
        active: true,
        updatedAt: '2025-07-12T15:00:00Z',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.update(userId, updateUserDto);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'PUT',
        `http://localhost:3001/users/${userId}`,
        updateUserDto,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when updating non-existent user', async () => {
      const userId = 'non-existent';
      const updateUserDto: UpdateUserDto = {
        name: 'João Silva',
        email: 'joao@email.com',
      };
      const notFoundError = new Error('User not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.update(userId, updateUserDto)).rejects.toThrow(
        'User not found',
      );
    });

    it('should throw error when update data is invalid', async () => {
      const userId = '1';
      const invalidData: UpdateUserDto = {
        name: '',
        email: 'invalid-email',
        role: 'INVALID_ROLE' as any,
      };
      const validationError = new Error('Invalid update data');

      httpRequestService.request.mockRejectedValue(validationError);

      await expect(service.update(userId, invalidData)).rejects.toThrow(
        'Invalid update data',
      );
    });

    it('should throw error when email already exists for another user', async () => {
      const userId = '1';
      const updateUserDto: UpdateUserDto = {
        name: 'João Silva',
        email: 'existing@email.com',
      };
      const conflictError = new Error('Email already exists');

      httpRequestService.request.mockRejectedValue(conflictError);

      await expect(service.update(userId, updateUserDto)).rejects.toThrow(
        'Email already exists',
      );
    });

    it('should throw error when unauthorized to update user', async () => {
      const userId = '1';
      const updateUserDto: UpdateUserDto = {
        name: 'João Silva',
        role: UserRole.ADMIN,
      };
      const unauthorizedError = new Error('Unauthorized to update user role');

      httpRequestService.request.mockRejectedValue(unauthorizedError);

      await expect(service.update(userId, updateUserDto)).rejects.toThrow(
        'Unauthorized to update user role',
      );
    });
  });

  describe('remove', () => {
    it('should delete user successfully', async () => {
      const userId = '1';
      const expectedResponse = { message: 'User deleted successfully' };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.remove(userId);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/users/${userId}`,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when deleting non-existent user', async () => {
      const userId = 'non-existent';
      const notFoundError = new Error('User not found');

      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.remove(userId)).rejects.toThrow('User not found');

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'DELETE',
        `http://localhost:3001/users/${userId}`,
      );
    });

    it('should throw error when user has active dependencies', async () => {
      const userId = '1';
      const businessError = new Error(
        'Cannot delete user with active patients or appointments',
      );

      httpRequestService.request.mockRejectedValue(businessError);

      await expect(service.remove(userId)).rejects.toThrow(
        'Cannot delete user with active patients or appointments',
      );
    });

    it('should throw error when trying to delete admin user', async () => {
      const userId = '1';
      const businessError = new Error('Cannot delete admin user');

      httpRequestService.request.mockRejectedValue(businessError);

      await expect(service.remove(userId)).rejects.toThrow(
        'Cannot delete admin user',
      );
    });

    it('should throw error when unauthorized to delete user', async () => {
      const userId = '1';
      const unauthorizedError = new Error('Unauthorized to delete user');

      httpRequestService.request.mockRejectedValue(unauthorizedError);

      await expect(service.remove(userId)).rejects.toThrow(
        'Unauthorized to delete user',
      );
    });

    it('should throw error when service is unavailable', async () => {
      const userId = '1';
      const serviceError = new Error('Service unavailable');

      httpRequestService.request.mockRejectedValue(serviceError);

      await expect(service.remove(userId)).rejects.toThrow(
        'Service unavailable',
      );
    });
  });
});
