import { Register } from '@frontend/modules/auth';
import { withAuthRoute } from '@frontend/react-routes/authRoute.hoc';
import { classnames, typography } from '@frontend/tailwindcss-classnames';

const RegisterPage = () => {
  return <div className={classnames(typography('font-bold'))}>
    <Register/>
  </div>;
};

const EnhancedRegisterPage = withAuthRoute(RegisterPage);

export default EnhancedRegisterPage;
