import { TTailwindString } from '@frontend/tailwindcss-classnames';

export type IconProps = {
  classNames?: TTailwindString;
  type: IconType;
  onClick?: React.MouseEventHandler<HTMLDivElement> | undefined;
};

export type IconType =
  | 'bell'
  | 'angle-down'
  | 'user'
  | 'website'
  | 'edit'
  | 'delete'
  | 'warning'
  | 'eye'
  | 'eye-slash'
  | 'download'
  | 'upload';
