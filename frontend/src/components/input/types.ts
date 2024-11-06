import { TTailwindString } from "@frontend/tailwindcss-classnames";

export type InputProps = {
  size: 'sm' | 'md' | 'lg'
  placeholder?: string
  onChange?: React.ChangeEventHandler<HTMLInputElement>
  type?: React.HTMLInputTypeAttribute
  classNames?: TTailwindString
  value?: string | number | readonly string[] | undefined
  icon?: any
  disabled?: boolean
  min?: number
  max?: number
  accept?: string
};
