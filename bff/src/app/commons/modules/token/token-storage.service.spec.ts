/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { Redis } from 'ioredis';
import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;
  let redisClient: jest.Mocked<Redis>;

  beforeEach(async () => {
    const mockRedis = {
      set: jest.fn(),
      get: jest.fn(),
      del: jest.fn(),
    };
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TokenStorageService,
        {
          provide: 'default_IORedisModuleConnectionToken',
          useValue: mockRedis,
        },
      ],
    }).compile();

    service = module.get<TokenStorageService>(TokenStorageService);
    redisClient = module.get('default_IORedisModuleConnectionToken');
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('setAccessToken', () => {
    it('should store access token successfully', async () => {
      const token =
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ';

      redisClient.set.mockResolvedValue('OK');

      await service.setAccessToken(token);

      expect(redisClient.set).toHaveBeenCalledWith(
        'access_token',
        token,
        'EX',
        3600,
      );
    });

    it('should throw error when token is empty', async () => {
      const emptyToken = '';
      const validationError = new Error('Token cannot be empty');

      redisClient.set.mockRejectedValue(validationError);

      await expect(service.setAccessToken(emptyToken)).rejects.toThrow(
        'Token cannot be empty',
      );
    });

    it('should throw error when token format is invalid', async () => {
      const invalidToken = 'invalid-token-format';
      const validationError = new Error('Invalid token format');

      redisClient.set.mockRejectedValue(validationError);

      await expect(service.setAccessToken(invalidToken)).rejects.toThrow(
        'Invalid token format',
      );
    });

    it('should throw error when Redis connection fails', async () => {
      const token = 'valid-token';
      const connectionError = new Error('Redis connection failed');

      redisClient.set.mockRejectedValue(connectionError);

      await expect(service.setAccessToken(token)).rejects.toThrow(
        'Redis connection failed',
      );
    });

    it('should throw error when Redis server is unavailable', async () => {
      const token = 'valid-token';
      const serverError = new Error('Redis server unavailable');

      redisClient.set.mockRejectedValue(serverError);

      await expect(service.setAccessToken(token)).rejects.toThrow(
        'Redis server unavailable',
      );
    });
  });

  describe('getAccessToken', () => {
    it('should retrieve access token successfully', async () => {
      const expectedToken =
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ';

      redisClient.get.mockResolvedValue(expectedToken);

      const result = await service.getAccessToken();

      expect(redisClient.get).toHaveBeenCalledWith('access_token');
      expect(result).toEqual(expectedToken);
    });

    it('should return null when no token is stored', async () => {
      redisClient.get.mockResolvedValue(null);

      const result = await service.getAccessToken();

      expect(redisClient.get).toHaveBeenCalledWith('access_token');
      expect(result).toBeNull();
    });

    it('should return null when token has expired', async () => {
      redisClient.get.mockResolvedValue(null);

      const result = await service.getAccessToken();

      expect(redisClient.get).toHaveBeenCalledWith('access_token');
      expect(result).toBeNull();
    });

    it('should throw error when Redis connection fails', async () => {
      const connectionError = new Error('Redis connection failed');

      redisClient.get.mockRejectedValue(connectionError);

      await expect(service.getAccessToken()).rejects.toThrow(
        'Redis connection failed',
      );
    });

    it('should throw error when Redis server is unavailable', async () => {
      const serverError = new Error('Redis server unavailable');

      redisClient.get.mockRejectedValue(serverError);

      await expect(service.getAccessToken()).rejects.toThrow(
        'Redis server unavailable',
      );
    });

    it('should throw error when Redis timeout occurs', async () => {
      const timeoutError = new Error('Redis operation timeout');

      redisClient.get.mockRejectedValue(timeoutError);

      await expect(service.getAccessToken()).rejects.toThrow(
        'Redis operation timeout',
      );
    });
  });

  describe('clearAccessToken', () => {
    it('should clear access token successfully', async () => {
      redisClient.del.mockResolvedValue(1);

      await service.clearAccessToken();

      expect(redisClient.del).toHaveBeenCalledWith('access_token');
    });

    it('should handle clearing non-existent token gracefully', async () => {
      redisClient.del.mockResolvedValue(0);

      await service.clearAccessToken();

      expect(redisClient.del).toHaveBeenCalledWith('access_token');
    });

    it('should throw error when Redis connection fails', async () => {
      const connectionError = new Error('Redis connection failed');

      redisClient.del.mockRejectedValue(connectionError);

      await expect(service.clearAccessToken()).rejects.toThrow(
        'Redis connection failed',
      );
    });

    it('should throw error when Redis server is unavailable', async () => {
      const serverError = new Error('Redis server unavailable');

      redisClient.del.mockRejectedValue(serverError);

      await expect(service.clearAccessToken()).rejects.toThrow(
        'Redis server unavailable',
      );
    });

    it('should throw error when Redis operation fails', async () => {
      const operationError = new Error('Redis delete operation failed');

      redisClient.del.mockRejectedValue(operationError);

      await expect(service.clearAccessToken()).rejects.toThrow(
        'Redis delete operation failed',
      );
    });

    it('should throw error when Redis timeout occurs', async () => {
      const timeoutError = new Error('Redis operation timeout');

      redisClient.del.mockRejectedValue(timeoutError);

      await expect(service.clearAccessToken()).rejects.toThrow(
        'Redis operation timeout',
      );
    });
  });
});
