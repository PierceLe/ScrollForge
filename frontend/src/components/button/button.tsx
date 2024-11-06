import classnames, {
  alignItems,
  backgroundColor,
  borderRadius,
  borders,
  cursor,
  display,
  justifyContent,
  sizing,
  spacing,
  typography,
} from '@frontend/tailwindcss-classnames';
import { ButtonProps, ButtonVariant } from './types';

export const Button = (props: ButtonProps) => {
  const {
    variant,
    size,
    children,
    classNames,
    onClick,
    disabled,
    color = 'primary',
  } = props;

  const styles = useStyles();

  return (
    <button
      className={classnames(
        classNames,
        styles.common,
        styles.variant(variant, disabled, color),
        styles.size(size, variant),
      )}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

const useStyles = () => {
  return {
    common: classnames(
      borderRadius('rounded-xl'),
      spacing('p-2'),
      display('flex'),
      alignItems('items-center'),
      justifyContent('justify-center'),
      cursor('cursor-pointer'),
    ),
    variant: (
      variant: ButtonVariant,
      disabled?: boolean,
      color?: 'success' | 'danger' | 'primary' | 'default',
    ) =>
      classnames(
        variant === 'contained'
          ? classnames(
              disabled
                ? backgroundColor('bg-gray-400')
                : classnames(
                    color === 'primary'
                      ? backgroundColor(
                          'bg-primary-color',
                          'hover:bg-secondary-color',
                        )
                      : null,
                    color === 'success'
                      ? backgroundColor('bg-green-600', 'hover:bg-green-500')
                      : null,
                    color === 'danger'
                      ? backgroundColor('bg-red-600', 'hover:bg-red-500')
                      : null,
                    color === 'default'
                      ? backgroundColor('bg-gray-400', 'hover:bg-gray-300')
                      : null,
                  ),
              typography('text-white'),
            )
          : null,
        variant === 'outlined'
          ? classnames(
              color === 'primary'
                ? classnames(
                    backgroundColor('hover:bg-primary-color'),
                    typography('hover:text-white'),
                    borders('border-2'),
                  )
                : null,
              color === 'default'
                ? classnames(
                    backgroundColor('hover:bg-gray-100'),
                    borders('border-2'),
                  )
                : null,
            )
          : null,
        variant === 'text'
          ? classnames(
              disabled
                ? typography('text-gray-3')
                : typography(
                    'text-primary-color',
                    'hover:text-secondary-color',
                  ),
            )
          : null,
      ),
    size: (size: 'sm' | 'md' | 'lg', variant: ButtonVariant) =>
      classnames(
        size === 'sm'
          ? classnames(
              variant !== 'text' ? sizing('h-8') : null,
              typography('text-tx12'),
            )
          : null,
        size === 'md'
          ? classnames(
              sizing('h-8', 'md:h-10'),
              typography('text-tx14', 'md:text-tx16'),
            )
          : null,
      ),
  };
};
