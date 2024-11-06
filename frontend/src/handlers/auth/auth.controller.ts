import { AuthService } from '@frontend/api/auth/';
import { createAsyncThunk } from '@reduxjs/toolkit';

export class AuthController {
  private static instance: AuthController;
  private authService: AuthService = AuthService.getInstance();

  public static getInstance(): AuthController {
    if (!AuthController.instance) {
      AuthController.instance = new AuthController();
    }

    return AuthController.instance;
  }

  public register = createAsyncThunk<any, any>(
    'registerAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.authService.register({
        data,
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.message);
      }
    },
  );

  public login = createAsyncThunk<any, any>(
    'loginAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.authService.login({ data });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.message);
      }
    },
  );
}
