import { createSlice } from '@reduxjs/toolkit';
import { AuthController } from './auth.controller';
import { AuthState } from './types';
import { clearCookie, setCookie } from '@frontend/helpers/cookie';
import { toast } from 'react-toastify';

const authController = AuthController.getInstance();

const initialState: AuthState = {
  currentUser: {},
};

export const authSlice = createSlice({
  name: 'authSlice',
  initialState,
  reducers: {
    logout: (state) => {
      clearCookie('Authentication');
      window.location.reload();

      return {
        ...state,
      };
    },
  },
  extraReducers: builder => {
    // RegisterAPI
    builder.addCase(authController.register.pending, state => {
      return {
        ...state,
      }
    });
    builder.addCase(
      authController.register.fulfilled,
      (state): AuthState => {
        toast.info('Register successfully!');

        return {
          ...state,
        };
      },
    );
    builder.addCase(
      authController.register.rejected,
      (state, action): AuthState => {
        toast.error('Register unsuccessfully!');

        return {
          ...state,
          currentUser: {
            isLoading: false,
            error: action.payload,
          },
        };
      },
    );

    // LoginAPI
    builder.addCase(
      authController.login.fulfilled,
      (state, action): AuthState => {
        const token = action.payload.token;
        setCookie('Authentication', token);

        // Reset login attempts
        localStorage.setItem('numOfAttemptsLogin', "0");
        
        return {
          ...state,
        };
      },
    );
    builder.addCase(
      authController.login.rejected,
      (state, action): AuthState => {
        toast.error('Login unsuccessfully!');

        // Increment password attempt
        const numOfAttemptsLogin = Number(localStorage.getItem('numOfAttemptsLogin'));
        const latestAttemptsLogin = Number(localStorage.getItem(
          'latestAttemptsLogin',
        ));

        // Reset count if latest attempt more than 15 minutes, else increse 1
        if (Date.now() - latestAttemptsLogin > 900000) {
          localStorage.setItem('numOfAttemptsLogin', "1");
        } else {
          localStorage.setItem('numOfAttemptsLogin', String(numOfAttemptsLogin+1));
        }
        localStorage.setItem('latestAttemptsLogin', String(Date.now()));


        return {
          ...state,
          error: action.payload,
        };
      },
    );
  },
});

// eslint-disable-next-line no-empty-pattern
export const { logout } = authSlice.actions;
export const { reducer: authReducer } = authSlice;
