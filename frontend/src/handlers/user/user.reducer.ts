import { createSlice } from '@reduxjs/toolkit';
import { UserController } from './user.controller';
import { UserState } from './types';
import { User } from '@frontend/repositories';
import { clearCookie } from '@frontend/helpers/cookie';
import { toast } from 'react-toastify';

const userController = UserController.getInstance();

const initialState: UserState = {
  currentUser: {},
  users: [],
  isChangeAvatarPending: false
};

export const userSlice = createSlice({
  name: 'userSlice',
  initialState,
  reducers: {
    changeAvatarPending: (state) => {
      return {
        ...state,
        isChangeAvatarPending: true
      };
    },
    changeAvatarSuccess: (state, action) => {
      return {
        ...state,
        isChangeAvatarPending: false,
        currentUser: {
          ...state.currentUser,
          data: User.buildUser({
            ...state.currentUser.data,
            avatarUrl: action.payload
          })
        },
      };
    },
    changeAvatarFailed: (state) => {
      return {
        ...state,
        isChangeAvatarPending: true
      };
    },
  },
  extraReducers: builder => {
    // GetCurrentUserAPI
    builder.addCase(userController.getCurrentUser.pending, state => {
      state.currentUser.isLoading = true;
    });
    builder.addCase(
      userController.getCurrentUser.fulfilled,
      (state, action): UserState => {
        return {
          ...state,
          currentUser: {
            ...state.currentUser,
            isLoading: false,
            data: User.buildUser(action.payload),
          },
        };
      },
    );
    builder.addCase(
      userController.getCurrentUser.rejected,
      (state, action): UserState => {
        return {
          ...state,
          currentUser: {
            isLoading: false,
            error: action.payload,
          },
        };
      },
    );

    // GetUsersAPI
    builder.addCase(
      userController.getUsers.fulfilled,
      (state, action): UserState => {
        // const { currentPage, pageSize, totalCount } = metadata;

        return {
          ...state,
          users: action.payload.map((user: any): User => User.buildUser(user)),
          // currentPage,
          // pageSize,
          // totalUsers: totalCount,
        };
      },
    );
    builder.addCase(
      userController.getUsers.rejected,
      (state, action): UserState => {
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // CreateUserAPI
    builder.addCase(
      userController.createUser.fulfilled,
      (state, action): UserState => {
        console.log(action.payload);
        toast.info(`Create user ${action.payload.firstName} ${action.payload.lastName} successfully!`);

        return {
          ...state,
          users: [User.buildUser(action.payload), ...(state.users ?? [])],
        };
      },
    );
    builder.addCase(
      userController.createUser.rejected,
      (state, action): UserState => {
        toast.error('Create user unsuccessfully!');
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // UpdateUserAPI
    builder.addCase(
      userController.updateUser.fulfilled,
      (state, action): UserState => {
        toast.info(`Update user ${action.payload.firstName} ${action.payload.lastName} successfully!`);

        const currentUser = state.currentUser?.data;
        if (currentUser) {
          currentUser.firstName = action.payload.firstName;
          currentUser.lastName = action.payload.lastName;
        }

        return {
          ...state,
          currentUser: {
            ...state.currentUser,
            data: currentUser,
          },
        };
      },
    );
    builder.addCase(
      userController.updateUser.rejected,
      (state, action: any): UserState => {
        toast.error(action.payload.message);
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // UpdateUsernameAPI
    builder.addCase(
      userController.updateUsername.fulfilled,
      (state, action): UserState => {
        console.log(action.payload);
        toast.info(`Update username for user ${action.payload.firstName} ${action.payload.lastName} successfully!`);
        clearCookie('Authentication');

        return {
          ...state,
        };
      },
    );
    builder.addCase(
      userController.updateUsername.rejected,
      (state, action: any): UserState => {
        console.log(action.payload)
        toast.error(action.payload.message);
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // UpdatePasswordAPI
    builder.addCase(
      userController.updatePassword.fulfilled,
      (state, action): UserState => {
        console.log(action.payload);
        toast.info(`Update password for user ${action.payload.firstName} ${action.payload.lastName} successfully!`);

        // Reset change password attempts
        localStorage.setItem('numOfAttemptsChangePassword', "0");

        // Logout
        clearCookie('Authentication');

        return {
          ...state,
        };
      },
    );
    builder.addCase(
      userController.updatePassword.rejected,
      (state, action): UserState => {
        toast.error('Update password unsuccessfully!');

        // Increment password attempt
        const numOfAttemptsChangePassword = Number(localStorage.getItem('numOfAttemptsChangePassword'));
        const latestAttemptsChangePassword = Number(localStorage.getItem(
          'latestAttemptsChangePassword',
        ));

        // Reset count if latest attempt more than 15 minutes, else increse 1
        if (Date.now() - latestAttemptsChangePassword > 900000) {
          localStorage.setItem('numOfAttemptsChangePassword', "1");
        } else {
          localStorage.setItem('numOfAttemptsChangePassword', String(numOfAttemptsChangePassword+1));
        }
        localStorage.setItem('latestAttemptsChangePassword', String(Date.now()));

        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // DeleteUserAPI
    builder.addCase(
      userController.deleteUser.fulfilled,
      (state, action): UserState => {
        const users = state.users?.filter(
          item => item.username !== action.payload.username,
        );

        return {
          ...state,
          users,
        };
      },
    );
    builder.addCase(
      userController.deleteUser.rejected,
      (state, action): UserState => {
        return {
          ...state,
          error: action.payload,
        };
      },
    );
  },
});

// eslint-disable-next-line no-empty-pattern
export const { changeAvatarSuccess, changeAvatarPending, changeAvatarFailed } = userSlice.actions;
export const { reducer: userReducer } = userSlice;
