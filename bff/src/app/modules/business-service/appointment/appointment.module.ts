import { Module } from '@nestjs/common';
import { AppointmentService } from './appointment.service';
import { AppointmentController } from './appointment.controller';
import { HttpRequestModule } from 'src/app/commons/modules/http/http-request.module';
import { AppConfigService } from 'src/app/config/config.service';

@Module({
  imports: [HttpRequestModule],
  controllers: [AppointmentController],
  providers: [AppointmentService, AppConfigService],
})
export class AppointmentModule {}
