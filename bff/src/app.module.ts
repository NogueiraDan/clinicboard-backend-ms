import { Module } from '@nestjs/common';
import { UsersModule } from './app/modules/user-serivce/user.module';
import { PatientModule } from './app/modules/business-service/patient/patient.module';
import { HttpModule } from '@nestjs/axios';
import { AuthModule } from './app/modules/auth/auth.module';
import { AppointmentModule } from './app/modules/business-service/appointment/appointment.module';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    UsersModule,
    PatientModule,
    HttpModule,
    AuthModule,
    AppointmentModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
