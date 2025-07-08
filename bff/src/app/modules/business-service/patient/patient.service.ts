import { Injectable } from '@nestjs/common';
import { PatientResponseDto } from './dto/patient-response.dto';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { PatientRequestDto } from './dto/patient-request.dto';
import { AppConfigService } from 'src/app/config/config.service';

@Injectable()
export class PatientService {
  constructor(
    private readonly httpRequestService: HttpRequestService,
    private readonly appConfigService: AppConfigService,
  ) {}

  async findProfessionalPatients(id: string): Promise<PatientResponseDto[]> {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/patients/professional/${id}`,
    );
  }
  async create(createPatientDto: PatientRequestDto) {
    return await this.httpRequestService.request(
      'POST',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/patients`,
      createPatientDto,
    );
  }

  async findOne(id: string) {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/patients/${id}`,
    );
  }

  async update(id: number, updatePatientDto: PatientRequestDto) {
    return await this.httpRequestService.request(
      'PUT',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/patients/${id}`,
      updatePatientDto,
    );
  }

  async remove(id: number) {
    return await this.httpRequestService.request(
      'DELETE',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/patients/${id}`,
    );
  }
}
