import { useState } from 'react';
import classnames, {
  alignItems,
  display,
  gap,
  sizing,
  spacing,
  typography,
} from '@frontend/tailwindcss-classnames';
import { Input } from '@frontend/components/input';
import { Button } from '@frontend/components/button';
import { UserController } from '@frontend/handlers/user';
import { useReduxDispatch } from '@frontend/redux/hooks';

export const FilterBlock = () => {
  const userController = UserController.getInstance();
  const dispatch = useReduxDispatch();

  const [email, setEmail] = useState<string>();
  const [username, setUsername] = useState<string>();
  const styles = useStyles();

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'email':
        setEmail(value);
        break;
      case 'username':
        setUsername(value);
        break;
    }
  };

  const handleSearch = () => {
    dispatch(
      userController.getUsers({
        email,
        username,
      }),
    );
  };

  return (
    <div className={classnames(styles.root)}>
      <div>
        <div className={classnames(styles.inputWrap)}>
          <div className={classnames(styles.inputLabel)}>Email</div>
          <Input
            size="md"
            placeholder="Enter email"
            type="email"
            classNames={classnames(styles.input)}
            onChange={handleInput('email')}
          />
        </div>
        <div className={classnames(styles.inputWrap)}>
          <div className={classnames(styles.inputLabel)}>Username</div>
          <Input
            size="md"
            placeholder="Enter username"
            classNames={classnames(styles.input)}
            onChange={handleInput('username')}
          />
        </div>
      </div>
      <div className={classnames(styles.buttonWrap)}>
        <Button
          variant="contained"
          size="md"
          classNames={classnames(styles.buttonSearch)}
          onClick={handleSearch}
        >
          Search
        </Button>
      </div>
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(spacing('mb-8'), display('flex'), gap('gap-14')),
    inputWrap: classnames(
      spacing('mb-2', 'last:!mb-0'),
      display('flex'),
      alignItems('items-center'),
    ),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(
      spacing('mb-1'),
      typography('text-tx14', 'md:text-tx16'),
      sizing('w-1/2'),
    ),
    buttonWrap: classnames(display('flex'), gap('gap-2')),
    buttonSearch: classnames(),
  };
};
