import React from 'react';

import { classnames } from '@frontend/tailwindcss-classnames';
import { UserController } from '@frontend/handlers/user';
import { useReduxDispatch } from '@frontend/redux/hooks';
import { UserManagement } from '@frontend/modules/user/user-management';
import { withPrivateRoute } from '@frontend/react-routes/privateRoute.hoc';
import { withPermissionRoute } from '@frontend/react-routes/permissionRoute.hoc';

const UserManagementPage = () => {
  const userController = UserController.getInstance();
  const dispatch = useReduxDispatch();

  React.useEffect(() => {
    dispatch(userController.getUsers({}));
  }, []);

  return <div className={classnames()}>
    <UserManagement/>

  </div>;
};

const EnhancedUserManagementPage = withPrivateRoute(withPermissionRoute(UserManagementPage));
export default EnhancedUserManagementPage;
