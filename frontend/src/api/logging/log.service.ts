import { AxiosRequestConfig } from 'axios';
import { BaseService } from '../base/base.service';

export class LogService extends BaseService {
  private static instance: LogService;

  public static getInstance(): LogService {
    if (!LogService.instance) {
      LogService.instance = new LogService();
    }

    return LogService.instance;
  }


  public getLogs(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.get('/v1/scroll/logs', options);
  }
}
