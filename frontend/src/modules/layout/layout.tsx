import { Header } from '@frontend/modules/header';
import { LayoutProps } from './types';
import classnames, {
  backgroundColor,
  boxShadow,
  layout,
  position,
  sizing,
  spacing,
  zIndex,
} from '@frontend/tailwindcss-classnames';

export const Layout = (props: LayoutProps) => {
  const styles = useStyles();

  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.content)}>
        <div className={classnames(styles.header)}>
          <Header />
        </div>
        <div className={classnames(styles.content)}>{props.children}</div>
      </div>
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(),
    header: classnames(
      spacing('mb-5'),
      backgroundColor('bg-white'),
      boxShadow('shadow-2xl'),
      position('sticky'),
      layout('top-0'),
      zIndex('z-10')
    ),
    content: classnames(backgroundColor('bg-gray-1'), sizing('min-h-screen')),
  };
};
