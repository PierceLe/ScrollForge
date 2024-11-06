import { PAGE_LINKS } from '@frontend/react-routes/permissionLink';
import classnames, {
  display,
  justifyContent,
  sizing,
  spacing,
  alignItems,
  typography,
  backgroundColor,
  borderRadius,
} from '@frontend/tailwindcss-classnames';
import { Link } from 'react-router-dom';
import { useAuthContext } from '@frontend/modules/auth';

export const Navigation = () => {
  const styles = useStyles();
  const { user } = useAuthContext();

  const pages = [PAGE_LINKS.HOME, PAGE_LINKS.USER_MANAGEMENT, PAGE_LINKS.LOG_MANAGEMENT];
  return (
    <div className={styles.navigation}>
      {pages.map(page => {
        return page.roles === 'all' ||
          (user?.role ? page.roles.includes(user.role) : false) ? (
          <NavigationItem title={page.title} path={page.path} />
        ) : null;
      })}
    </div>
  );
};

const NavigationItem = (props: { title: string; path: string }) => {
  const { title, path } = props;
  const styles = useNavigationItemStyles();

  return (
    <Link className={classnames(styles.root(path === window.location.pathname))} to={path}>
      {title}
    </Link>
  );
};

const useStyles = () => {
  return {
    navigation: classnames(
      display('flex'),
      justifyContent('justify-center'),
      sizing('h-full'),
      alignItems('items-center'),
    ),
  };
};

const useNavigationItemStyles = () => {
  return {
    root: (isActive: boolean) =>
      classnames(
        spacing('py-2', 'px-4'),
        typography(
          'text-gray-600',
          'text-tx18',
          'hover:text-primary-color',
          isActive ? 'text-primary-color' : null,
        ),
        backgroundColor('hover:bg-gray-100'),
        borderRadius('rounded-3xl'),
      ),
  };
};
