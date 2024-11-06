import classnames, {
  borderRadius,
  borders,
  layout,
  position,
  sizing,
  spacing,
  typography,
} from '@frontend/tailwindcss-classnames';
import { InputProps } from './types';

export const Input = (props: InputProps) => {
  const {
    icon,
    size,
    placeholder,
    type,
    classNames,
    onChange,
    value,
    disabled,
    min,
    max,
    accept
  } = props;

  const styles = useStyles();

  return (
    <div className={classnames(classNames, styles.root)}>
      <input
        className={classnames(styles.common(type), styles.size(size))}
        placeholder={placeholder}
        type={type}
        onChange={onChange}
        value={value}
        disabled={disabled}
        min={min}
        max={max}
        accept={accept}
      />
      {icon ? <div className={classnames(styles.icon)}>{icon}</div> : null}
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(position('relative')),
    common: (type?: React.HTMLInputTypeAttribute) =>
      classnames(
        type !== 'file'
          ? classnames(
              spacing('p-2'),
              borderRadius('rounded-xl'),
              borders('border-2'),
            )
          : null,
        sizing('w-full'),
      ),
    size: (size: 'sm' | 'md' | 'lg') =>
      classnames(
        size === 'sm' ? classnames(sizing('h-4')) : null,
        size === 'md'
          ? classnames(
              sizing('h-8', 'md:h-10'),
              typography('text-tx14', 'md:text-tx16'),
            )
          : null,
      ),
    icon: classnames(position('absolute'), layout('right-2', 'top-1/4')),
  };
};
