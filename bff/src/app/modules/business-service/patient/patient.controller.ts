import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
} from '@nestjs/common';
import { PatientService } from './patient.service';
import { PatientResponseDto } from './dto/patient-response.dto';
import { PatientRequestDto } from './dto/patient-request.dto';

@Controller('patient')
export class PatientController {
  constructor(private readonly patientService: PatientService) {}

  @Post()
  create(@Body() createPatientDto: PatientRequestDto) {
    return this.patientService.create(createPatientDto);
  }

  @Get('/professional/:id')
  findProfessionalPatients(
    @Param('id') id: string,
  ): Promise<PatientResponseDto[]> {
    return this.patientService.findProfessionalPatients(id);
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.patientService.findOne(id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updatePatientDto: PatientRequestDto) {
    return this.patientService.update(+id, updatePatientDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.patientService.remove(+id);
  }
}
