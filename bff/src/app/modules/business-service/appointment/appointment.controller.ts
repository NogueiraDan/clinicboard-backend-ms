import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Delete,
  Query,
  Put,
} from '@nestjs/common';
import { AppointmentService } from './appointment.service';
import { AppointmentRequestDto } from './dto/appointment-request.dto';

@Controller('appointment')
export class AppointmentController {
  constructor(private readonly appointmentService: AppointmentService) {}

  @Get('/available-times')
  findAvailabeAppointments(@Query('id') id: string, @Query('date') date: any) {
    return this.appointmentService.findAvailabeAppointments(id, date);
  }

  @Get('/professional')
  findProfessionalAppointment(
    @Query('id') id: string,
    @Query('date') date: any,
  ) {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
    return this.appointmentService.findProfessionalAppointment(id, date);
  }

  @Post()
  create(@Body() appointmentRequestDto: AppointmentRequestDto) {
    return this.appointmentService.create(appointmentRequestDto);
  }

  @Get()
  findAll() {
    return this.appointmentService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.appointmentService.findOne(id);
  }

  @Put(':id')
  update(
    @Param('id') id: string,
    @Body() appointmentRequestDto: AppointmentRequestDto,
  ) {
    return this.appointmentService.update(id, appointmentRequestDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.appointmentService.remove(id);
  }
}
