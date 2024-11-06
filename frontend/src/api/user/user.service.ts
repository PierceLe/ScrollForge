import { AxiosRequestConfig } from 'axios';
import { BaseService } from '../base/base.service';

export class UserService extends BaseService {
  private static instance: UserService;

  public static getInstance(): UserService {
    if (!UserService.instance) {
      UserService.instance = new UserService();
    }

    return UserService.instance;
  }

  public getCurrentUser(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.get('/v1/users/info', options);
  }

  public getUsers(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.get('/v1/users', options);
  }

  public updateUser(username: string, options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.put(`/v1/update/user/${username}`, options);
  }

  public updatePassword(id: string, options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.put(`/v1/update/password/${id}`, options);
  }

  public deleteUser(username: string, options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.delete(`/v1/users/${username}`, options);
  }
}
