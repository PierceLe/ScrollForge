import { User } from '@frontend/repositories';

export type AuthState = {
  currentUser: {
    isLoading?: boolean;
    data?: User;
    error?: any;
  };

  error?: any;
};
