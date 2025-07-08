import { Module } from '@nestjs/common';
import { PatientService } from './patient.service';
import { PatientController } from './patient.controller';
import { HttpRequestModule } from 'src/app/commons/modules/http/http-request.module';
import { AppConfigService } from 'src/app/config/config.service';

@Module({
  imports: [HttpRequestModule],
  controllers: [PatientController],
  providers: [PatientService, AppConfigService],
})
export class PatientModule {}
