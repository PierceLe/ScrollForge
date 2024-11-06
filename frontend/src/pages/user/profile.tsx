import { Profile } from '@frontend/modules/user/profile';
import { withPrivateRoute } from '@frontend/react-routes/privateRoute.hoc';
import { classnames, typography } from '@frontend/tailwindcss-classnames';

const ProfilePage = () => {
  return <div className={classnames(typography('font-bold'))}>
    <Profile/>
  </div>;
};

const EnhancedProfilePage = withPrivateRoute(ProfilePage);
// const EnhancedProfilePage = ProfilePage;

export default EnhancedProfilePage;
