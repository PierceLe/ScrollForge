import { createAsyncThunk } from '@reduxjs/toolkit';
import { UserService } from '@frontend/api/user';
import { AuthService } from '@frontend/api/auth';

export class UserController {
  private static instance: UserController;
  private userService: UserService = UserService.getInstance();
  private authService: AuthService = AuthService.getInstance();

  public static getInstance(): UserController {
    if (!UserController.instance) {
      UserController.instance = new UserController();
    }

    return UserController.instance;
  }

  public getCurrentUser = createAsyncThunk(
    'getCurrentUserAPI',
    async (_, { rejectWithValue }) => {
      const fetchFn = this.userService.getCurrentUser({});

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public getUsers = createAsyncThunk<any, any>(
    'getUsersAPI',
    async (params, { rejectWithValue }) => {
      const fetchFn = this.userService.getUsers({
        params
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public updateUser = createAsyncThunk<any, any>(
    'updateUserAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.userService.updateUser(data.username, {
        data: {
          email: data?.email,
          firstName: data?.firstName,
          lastName: data?.lastName,
          phoneNumber: data?.phoneNumber,
        },
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public updateUsername = createAsyncThunk<any, any>(
    'updateUsernameAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.userService.updateUser(data.currentUsername, {
        data: {
          username: data?.newUsername,
        },
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public updatePassword = createAsyncThunk<any, any>(
    'updatePasswordAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.userService.updatePassword(data.id, {
        data: {
          oldPassword: data?.oldPassword,
          newPassword: data?.newPassword,
        },
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public createUser = createAsyncThunk<any, any>(
    'createUserAPI',
    async (data, { rejectWithValue }) => {
      const fetchFn = this.authService.register({
        data
      });

      try {
        const response = await fetchFn();
        return response;
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );

  public deleteUser = createAsyncThunk<any, any>(
    'deleteUserAPI',
    async (username, { rejectWithValue }) => {
      const fetchFn = this.userService.deleteUser(username, {});

      try {
        await fetchFn();
        return { username };
      } catch (err: any) {
        return rejectWithValue(err.response.data);
      }
    },
  );
}
