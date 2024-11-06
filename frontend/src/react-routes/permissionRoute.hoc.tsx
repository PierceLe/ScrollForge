import { useAuthContext } from '@frontend/modules/auth';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { PAGE_LINKS } from './permissionLink';
import { Fallback } from '@frontend/modules/fallback';

export const withPermissionRoute = (Component: React.FC) => {
  const PermissionRoute = (props: any) => {
    const { isLogged, isLoading, user } = useAuthContext();
    const navigate = useNavigate();

    React.useEffect(() => {
      if (!isLoading && !user?.isAdmin()) {
        navigate(PAGE_LINKS.HOME.path);
      }
    }, [isLogged, isLoading]);

    // If user is logged in, return original component
    return (
      <>
        {isLoading ? <Fallback /> : isLogged ? <Component {...props} /> : null}
      </>
    );
  };

  return PermissionRoute;
};
