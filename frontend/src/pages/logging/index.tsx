import React from 'react';

import { classnames } from '@frontend/tailwindcss-classnames';
import { useReduxDispatch } from '@frontend/redux/hooks';
import { withPrivateRoute } from '@frontend/react-routes/privateRoute.hoc';
import { LogController } from '@frontend/handlers/logging';
import { LoggingManagement } from '@frontend/modules/logging';
import { withPermissionRoute } from '@frontend/react-routes/permissionRoute.hoc';

const LoggingPage = () => {
  const logController = LogController.getInstance();
  const dispatch = useReduxDispatch();

  React.useEffect(() => {
    dispatch(logController.getLogs({}));
  }, []);

  return <div className={classnames()}>
    <LoggingManagement/>

  </div>;
};

const EnhancedLoggingPage = withPrivateRoute(withPermissionRoute(LoggingPage));
export default EnhancedLoggingPage;
