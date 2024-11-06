import { ROLE_TYPE } from "@frontend/repositories";

export const PAGE_LINKS = {
  LOGIN: {
    path: '/auth/login',
    title: 'Login',
    isWithLayout: false,
    roles: 'all'
  },
  REGISTER: {
    path: '/auth/register',
    title: 'Register',
    isWithLayout: false,
    roles: 'all'
  },
  NOT_FOUND: {
    path: '/not-found',
    title: 'Not Found',
    isWithLayout: true,
    roles: 'all'
  },
  HOME: {
    path: '/',
    title: 'Home',
    isWithLayout: true,
    roles: 'all'
  },
  PROFILE: {
    path: '/user/profile',
    title: 'Profile',
    isWithLayout: true,
    roles: 'all'
  },
  USER_MANAGEMENT: {
    path: '/user-management',
    title: 'User Management',
    isWithLayout: true,
    roles: [ROLE_TYPE.ADMIN]
  },
  LOG_MANAGEMENT: {
    path: '/logging',
    title: 'Log Management',
    isWithLayout: true,
    roles: [ROLE_TYPE.ADMIN]
  },
};
