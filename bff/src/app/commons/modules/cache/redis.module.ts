import { Module } from '@nestjs/common';
import { RedisModule } from '@nestjs-modules/ioredis';

@Module({
  imports: [
    RedisModule.forRoot({
      type: 'single',
      options: {
        host: 'redis', // Nome do serviço no docker-compose.yml ou localhost caso não esteja usando Docker
        port: 6379,
      },
    }),
  ],
  exports: [RedisModule],
})
export class RedisCacheModule {}
