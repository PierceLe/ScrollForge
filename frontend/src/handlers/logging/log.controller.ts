import { LogService } from '@frontend/api/logging';
import { createAsyncThunk } from '@reduxjs/toolkit';

export class LogController {
  private static instance: LogController;
  private logService: LogService = LogService.getInstance();

  public static getInstance(): LogController {
    if (!LogController.instance) {
      LogController.instance = new LogController();
    }

    return LogController.instance;
  }


  public getLogs = createAsyncThunk<any, any>(
    'getLogsAPI',
    async (params, { rejectWithValue }) => {
      const fetchFn = this.logService.getLogs({
        params
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.message);
      }
    },
  );
}
