import { Injectable } from '@nestjs/common';
import { HttpRequestService } from 'src/app/commons/modules/http/http-request.service';
import { AppointmentRequestDto } from './dto/appointment-request.dto';
import { AppConfigService } from 'src/app/config/config.service';

@Injectable()
export class AppointmentService {
  constructor(
    private readonly httpRequestService: HttpRequestService,
    private readonly appConfigService: AppConfigService,
  ) {}

  async findAppointment(id: string, date: string) {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/${id}/date?date=${date}`,
    );
  }

  async findAvailabeAppointments(id: string, date: any) {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/available-times?id=${id}&date=${date}`,
    );
  }

  async findProfessionalAppointment(id: string, date: string) {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/professional?id=${id}&date=${date}`,
    );
  }

  async create(appointmentRequestDto: AppointmentRequestDto) {
    return await this.httpRequestService.request(
      'POST',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments`,
      appointmentRequestDto,
    );
  }

  async findAll() {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments`,
    );
  }

  async findOne(id: string) {
    return await this.httpRequestService.request(
      'GET',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/${id}`,
    );
  }

  async update(id: string, appointmentRequestDto: AppointmentRequestDto) {
    return await this.httpRequestService.request(
      'PUT',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/${id}`,
      appointmentRequestDto,
    );
  }

  async remove(id: string) {
    return await this.httpRequestService.request(
      'DELETE',
      `${this.appConfigService.baseUrls.BUSINESS_SERVICE}/appointments/${id}`,
    );
  }
}
