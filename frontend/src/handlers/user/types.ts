import { User } from '@frontend/repositories';

export type UserState = {
  users?: User[];
  currentPage?: number;
  pageSize?: number;
  totalUsers?: number;
  error?: any;
  isChangeAvatarPending: boolean;

  currentUser: {
    isLoading?: boolean;
    data?: User;
    error?: any;
  };
};
