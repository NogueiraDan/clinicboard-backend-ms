import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AppConfigService {
  constructor(private configService: ConfigService) {}

  get baseUrls() {
    return {
      USERS_SERVICE: `${this.configService.get<string>('GATEWAY_URL')}/user-service`,
      BUSINESS_SERVICE: `${this.configService.get<string>('GATEWAY_URL')}/business-service`,
    };
  }
}
