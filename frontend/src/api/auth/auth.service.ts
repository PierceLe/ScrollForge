import { AxiosRequestConfig } from 'axios';
import { BaseService } from '../base/base.service';

export class AuthService extends BaseService {
  private static instance: AuthService;

  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }

    return AuthService.instance;
  }

  public register(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.post('/v1/auth/register', options);
  }

  public login(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.post('/v1/auth/login', options);
  }
}
