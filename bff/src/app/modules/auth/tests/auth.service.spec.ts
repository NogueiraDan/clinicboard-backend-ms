/* eslint-disable @typescript-eslint/unbound-method */
import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from '../auth.service';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppConfigService } from 'src/app/config/config.service';
import { TokenStorageService } from 'src/app/commons/modules/token/token-storage.service';
import { AuthLoginDto } from '../dto/auth-login.dto';
import { AuthLoginRepsonseDto } from '../dto/auth-token.dto';
import { UserRequestDto } from 'src/app/commons/dto/request/user-request.dto';
import { UserResponseDto } from 'src/app/commons/dto/response/user-response.dto';
import { UserRole } from 'src/app/commons/enums/user-role.enum';

describe('AuthService', () => {
  let service: AuthService;
  let httpRequestService: jest.Mocked<HttpRequestService>;
  let tokenStorageService: jest.Mocked<TokenStorageService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: HttpRequestService,
          useValue: {
            request: jest.fn(),
          },
        },
        {
          provide: AppConfigService,
          useValue: {
            baseUrls: {
              USERS_SERVICE: 'http://localhost:3001',
            },
          },
        },
        {
          provide: TokenStorageService,
          useValue: {
            setAccessToken: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    httpRequestService = module.get(HttpRequestService);
    tokenStorageService = module.get(TokenStorageService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    it('should create user successfully', async () => {
      const userRequest: UserRequestDto = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123',
        contact: '1234567890',
        role: UserRole.PROFESSIONAL,
      };

      const expectedResponse: UserResponseDto = {
        id: '1',
        name: 'Test User',
        email: 'test@example.com',
        role: UserRole.ADMIN,
        contact: '1234567890',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);

      const result = await service.create(userRequest);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/auth/register',
        userRequest,
      );
      expect(result).toEqual(expectedResponse);
    });

    it('should throw error when user already exists', async () => {
      const userRequest: UserRequestDto = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123',
        contact: '1234567890',
        role: UserRole.PROFESSIONAL,
      };

      const error = new Error('User already exists');
      httpRequestService.request.mockRejectedValue(error);

      await expect(service.create(userRequest)).rejects.toThrow(
        'User already exists',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/auth/register',
        userRequest,
      );
    });

    it('should throw error when HTTP request fails', async () => {
      const userRequest: UserRequestDto = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123',
        contact: '1234567890',
        role: UserRole.PROFESSIONAL,
      };

      const networkError = new Error('Network error');
      httpRequestService.request.mockRejectedValue(networkError);

      await expect(service.create(userRequest)).rejects.toThrow(
        'Network error',
      );
    });
  });

  describe('login', () => {
    it('should login successfully and store access token', async () => {
      const loginRequest: AuthLoginDto = {
        email: 'test@example.com',
        password: 'password123',
      };
      const expectedResponse: AuthLoginRepsonseDto = {
        access_token: 'jwt-token-123',
        id: '1',
        email: 'test@example.com',
        name: 'Test User',
        role: 'user',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);
      tokenStorageService.setAccessToken.mockResolvedValue(undefined);

      const result = await service.login(loginRequest);

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/auth/login',
        loginRequest,
      );
      expect(tokenStorageService.setAccessToken).toHaveBeenCalledWith(
        'jwt-token-123',
      );
      expect(result).toEqual(expectedResponse);
    });
    it('should throw error when credentials are invalid', async () => {
      const loginRequest: AuthLoginDto = {
        email: 'test@example.com',
        password: 'wrongpassword',
      };

      const authError = new Error('Invalid credentials');
      httpRequestService.request.mockRejectedValue(authError);

      await expect(service.login(loginRequest)).rejects.toThrow(
        'Invalid credentials',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/auth/login',
        loginRequest,
      );
      expect(tokenStorageService.setAccessToken).not.toHaveBeenCalled();
    });

    it('should throw error when user is not found', async () => {
      const loginRequest: AuthLoginDto = {
        email: 'nonexistent@example.com',
        password: 'password123',
      };

      const notFoundError = new Error('User not found');
      httpRequestService.request.mockRejectedValue(notFoundError);

      await expect(service.login(loginRequest)).rejects.toThrow(
        'User not found',
      );

      expect(tokenStorageService.setAccessToken).not.toHaveBeenCalled();
    });

    it('should complete login even if token storage fails', async () => {
      const loginRequest: AuthLoginDto = {
        email: 'test@example.com',
        password: 'password123',
      };
      const expectedResponse: AuthLoginRepsonseDto = {
        access_token: 'jwt-token-123',
        id: '1',
        email: 'test@example.com',
        name: 'Test User',
        role: 'user',
      };

      httpRequestService.request.mockResolvedValue(expectedResponse);
      tokenStorageService.setAccessToken.mockRejectedValue(
        new Error('Storage failed'),
      );

      // O login deve falhar se o token não puder ser armazenado
      await expect(service.login(loginRequest)).rejects.toThrow(
        'Storage failed',
      );

      expect(httpRequestService.request).toHaveBeenCalledWith(
        'POST',
        'http://localhost:3001/auth/login',
        loginRequest,
      );
      expect(tokenStorageService.setAccessToken).toHaveBeenCalledWith(
        'jwt-token-123',
      );
    });
  });
});
