import { AxiosRequestConfig } from 'axios';
import { BaseService } from '../base/base.service';

export class ScrollingService extends BaseService {
  private static instance: ScrollingService;

  public static getInstance(): ScrollingService {
    if (!ScrollingService.instance) {
      ScrollingService.instance = new ScrollingService();
    }

    return ScrollingService.instance;
  }

  
  public getScrollings(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.get('/v1/scroll', options);
  }

  public uploadScrolling(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.post('/v1/scroll/upload', options);
  }

  public updateScrolling(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.put('/v1/scroll/update', options);
  }

  public deleteScrolling(id: number, options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.delete(`/v1/scroll/delete/${id}`, options);
  }

  public downloadScrolling(id: number, options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.get(`/v1/scroll/download/${id}`, options);
  }

  public changeDefaultTimerScrolling(options: AxiosRequestConfig<any>) {
    return this.serverCommunicate.put(`/v1/scroll/default-time`, options);
  }
}
