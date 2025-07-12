/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { HttpService } from '@nestjs/axios';
import { HttpException, HttpStatus } from '@nestjs/common';
import { of, throwError } from 'rxjs';
import { HttpRequestService } from '../http-request.service';
import { TokenStorageService } from '../../token/token-storage.service';
import { AxiosResponse } from 'axios';

describe('HttpRequestService', () => {
  let service: HttpRequestService;
  let httpService: jest.Mocked<HttpService>;
  let tokenStorageService: jest.Mocked<TokenStorageService>;

  beforeEach(async () => {
    const mockHttpService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };

    const mockTokenStorageService = {
      getAccessToken: jest.fn(),
      setAccessToken: jest.fn(),
      clearAccessToken: jest.fn(),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        HttpRequestService,
        {
          provide: HttpService,
          useValue: mockHttpService,
        },
        {
          provide: TokenStorageService,
          useValue: mockTokenStorageService,
        },
      ],
    }).compile();

    service = module.get<HttpRequestService>(HttpRequestService);
    httpService = module.get(HttpService);
    tokenStorageService = module.get(TokenStorageService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('request - GET method', () => {
    it('should make GET request successfully with token', async () => {
      const url = 'http://localhost:3001/users';
      const token = 'valid-token';
      const expectedData = { users: ['user1', 'user2'] };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(token);
      httpService.get.mockReturnValue(of(mockResponse));

      const result = await service.request('GET', url);

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.get).toHaveBeenCalledWith(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      expect(result).toEqual(expectedData);
    });

    it('should make GET request successfully without token', async () => {
      const url = 'http://localhost:3001/public';
      const expectedData = { message: 'public data' };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(of(mockResponse));

      const result = await service.request('GET', url);

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.get).toHaveBeenCalledWith(url, {
        headers: {},
      });
      expect(result).toEqual(expectedData);
    });

    it('should throw HttpException when GET request fails with response', async () => {
      const url = 'http://localhost:3001/users';
      const errorResponse = {
        response: {
          data: { message: 'Not found' },
          status: 404,
        },
      };

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(throwError(() => errorResponse));

      await expect(service.request('GET', url)).rejects.toThrow(
        new HttpException({ message: 'Not found' }, 404),
      );
    });

    it('should throw HttpException when GET request fails without response', async () => {
      const url = 'http://localhost:3001/users';
      const error = new Error('Network error');

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(throwError(() => error));

      await expect(service.request('GET', url)).rejects.toThrow(
        new HttpException('Erro ao fazer a requisição', HttpStatus.BAD_REQUEST),
      );
    });
  });

  describe('request - POST method', () => {
    it('should make POST request successfully with token and data', async () => {
      const url = 'http://localhost:3001/users';
      const token = 'valid-token';
      const requestData = { name: 'João Silva', email: 'joao@email.com' };
      const expectedData = { id: '1', ...requestData };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(token);
      httpService.post.mockReturnValue(of(mockResponse));

      const result = await service.request('POST', url, requestData);

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.post).toHaveBeenCalledWith(url, requestData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      expect(result).toEqual(expectedData);
    });

    it('should make POST request successfully without token', async () => {
      const url = 'http://localhost:3001/auth/login';
      const requestData = { email: 'user@email.com', password: 'password' };
      const expectedData = { token: 'new-token' };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.post.mockReturnValue(of(mockResponse));

      const result = await service.request('POST', url, requestData);

      expect(httpService.post).toHaveBeenCalledWith(url, requestData, {
        headers: {},
      });
      expect(result).toEqual(expectedData);
    });

    it('should throw HttpException when POST request fails with validation error', async () => {
      const url = 'http://localhost:3001/users';
      const requestData = { name: '', email: 'invalid-email' };
      const errorResponse = {
        response: {
          data: { message: 'Validation failed' },
          status: 400,
        },
      };

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.post.mockReturnValue(throwError(() => errorResponse));

      await expect(service.request('POST', url, requestData)).rejects.toThrow(
        new HttpException({ message: 'Validation failed' }, 400),
      );
    });
  });

  describe('request - PUT method', () => {
    it('should make PUT request successfully with token and data', async () => {
      const url = 'http://localhost:3001/users/1';
      const token = 'valid-token';
      const requestData = { name: 'João Silva Santos' };
      const expectedData = { id: '1', ...requestData };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(token);
      httpService.put.mockReturnValue(of(mockResponse));

      const result = await service.request('PUT', url, requestData);

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.put).toHaveBeenCalledWith(url, requestData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      expect(result).toEqual(expectedData);
    });

    it('should throw HttpException when PUT request fails with unauthorized error', async () => {
      const url = 'http://localhost:3001/users/1';
      const requestData = { name: 'João Silva' };
      const errorResponse = {
        response: {
          data: { message: 'Unauthorized' },
          status: 401,
        },
      };

      tokenStorageService.getAccessToken.mockResolvedValue('invalid-token');
      httpService.put.mockReturnValue(throwError(() => errorResponse));

      await expect(service.request('PUT', url, requestData)).rejects.toThrow(
        new HttpException({ message: 'Unauthorized' }, 401),
      );
    });
  });

  describe('request - DELETE method', () => {
    it('should make DELETE request successfully with token', async () => {
      const url = 'http://localhost:3001/users/1';
      const token = 'valid-token';
      const expectedData = { message: 'User deleted successfully' };
      const mockResponse: AxiosResponse = {
        data: expectedData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          url,
          method: 'delete',
          headers: {},
        } as any,
      };

      tokenStorageService.getAccessToken.mockResolvedValue(token);
      httpService.delete.mockReturnValue(of(mockResponse));

      const result = await service.request('DELETE', url);

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.delete).toHaveBeenCalledWith(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      expect(result).toEqual(expectedData);
    });

    it('should throw HttpException when DELETE request fails with forbidden error', async () => {
      const url = 'http://localhost:3001/users/1';
      const errorResponse = {
        response: {
          data: { message: 'Forbidden' },
          status: 403,
        },
      };

      tokenStorageService.getAccessToken.mockResolvedValue('valid-token');
      httpService.delete.mockReturnValue(throwError(() => errorResponse));

      await expect(service.request('DELETE', url)).rejects.toThrow(
        new HttpException({ message: 'Forbidden' }, 403),
      );
    });
  });

  describe('request - Token retrieval errors', () => {
    it('should throw error when token storage service fails', async () => {
      const url = 'http://localhost:3001/users';
      const tokenError = new Error('Erro ao fazer a requisição');

      tokenStorageService.getAccessToken.mockRejectedValue(tokenError);

      await expect(service.request('GET', url)).rejects.toThrow(
        'Erro ao fazer a requisição',
      );

      expect(tokenStorageService.getAccessToken).toHaveBeenCalled();
      expect(httpService.get).not.toHaveBeenCalled();
    });
  });

  describe('request - Network and timeout errors', () => {
    it('should throw HttpException when network timeout occurs', async () => {
      const url = 'http://localhost:3001/users';
      const timeoutError = new Error('Timeout');

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(throwError(() => timeoutError));

      await expect(service.request('GET', url)).rejects.toThrow(
        new HttpException('Erro ao fazer a requisição', HttpStatus.BAD_REQUEST),
      );
    });

    it('should throw HttpException when server is unavailable', async () => {
      const url = 'http://localhost:3001/users';
      const errorResponse = {
        response: {
          data: { message: 'Service unavailable' },
          status: 503,
        },
      };

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(throwError(() => errorResponse));

      await expect(service.request('GET', url)).rejects.toThrow(
        new HttpException({ message: 'Service unavailable' }, 503),
      );
    });

    it('should throw HttpException when connection is refused', async () => {
      const url = 'http://localhost:3001/users';
      const connectionError = new Error('Connection refused');

      tokenStorageService.getAccessToken.mockResolvedValue(null);
      httpService.get.mockReturnValue(throwError(() => connectionError));

      await expect(service.request('GET', url)).rejects.toThrow(
        new HttpException('Erro ao fazer a requisição', HttpStatus.BAD_REQUEST),
      );
    });
  });
});
