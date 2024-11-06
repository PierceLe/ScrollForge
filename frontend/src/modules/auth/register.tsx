import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@frontend/components/button';
import { Input } from '@frontend/components/input';
import classnames, {
  backgroundColor,
  display,
  grid,
  sizing,
  spacing,
  borderRadius,
  borders,
  typography,
  alignItems,
  justifyContent,
} from '@frontend/tailwindcss-classnames';
import { useReduxDispatch } from '@frontend/redux/hooks';
import { AuthController } from '@frontend/handlers/auth';
import { PAGE_LINKS } from '@frontend/react-routes/permissionLink';
import { Icon } from '@frontend/components/icon';
import { toast } from 'react-toastify';
import { sleep } from '@frontend/helpers/sleep';
import { object, string } from 'yup';

export const Register = () => {
  const navigate = useNavigate();
  const dispatch = useReduxDispatch();
  const authController = AuthController.getInstance();

  const styles = useStyles();
  const [email, setEmail] = useState<string>();
  const [password, setPassword] = useState<string>();
  const [firstName, setFirstName] = useState<string>();
  const [lastName, setLastName] = useState<string>();
  const [username, setUsername] = useState<string>();
  const [phoneNumber, setPhoneNumber] = useState<string>();
  const [isShowPassword, setIsShowPassword] = useState<boolean>();

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'email':
        setEmail(value);
        break;
      case 'firstName':
        setFirstName(value);
        break;
      case 'lastName':
        setLastName(value);
        break;
      case 'username':
        setUsername(value);
        break;
      case 'phoneNumber':
        setPhoneNumber(value);
        break;
      case 'password':
        setPassword(value);
        break;
    }
  };

  const handleLogin = () => {
    navigate(PAGE_LINKS.LOGIN.path);
  };

  const handleSignUp = async () => {
    const userSchema = object({
      firstName: string()
      .matches(
        /^[A-Za-z\s]+$/,
        'First name must contain only letters'
      )
      .required(),
      lastName: string()
      .matches(
        /^[A-Za-z\s]+$/,
        'Last name must contain only letters'
      )
      .required(),
      username: string()
        .length(8)
        .matches(
          /^[a-z0-9]+/,
          'Your username must only contain digits and lowercase letters',
        )
        .required(),
      password: string()
        .min(8)
        .max(16)
        .matches(
          /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_\s\t]).{8,16}$/,
          'Password must contain at least one digit, one uppercase and lowercase letter, one special character, and may include spaces or tabs',
        )
        .required(),
      phone: string().required(),
      email: string().email().required(),
    });

    
    const userLogin = {
      email,
      password,
      firstName,
      lastName,
      phone: phoneNumber,
      username,
    };

    try {
      await userSchema.validate(userLogin);
    } catch (error: any) {
      toast.error(error.message);
      return;
    }

    const data = await dispatch(
      authController.register({
        email,
        password,
        firstName,
        lastName,
        phone: phoneNumber,
        username,
      }),
    );
    if (data?.payload?.token) {
      toast.info('Register successfully!');
      await sleep(1000);
      navigate(PAGE_LINKS.LOGIN.path);
    }
  };

  const handleShowPassword = () => {
    setIsShowPassword(!isShowPassword);
  };

  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.leftSide)}>
        <div className={classnames(styles.leftSideBody)}>
          <div className={classnames(styles.title)}>Join Scrolling System</div>
          <div className={classnames(styles.form)}>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>First Name</div>
              <Input
                size="md"
                placeholder="Enter your first name"
                classNames={classnames(styles.input)}
                onChange={handleInput('firstName')}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Last Name</div>
              <Input
                size="md"
                placeholder="Enter your last name"
                classNames={classnames(styles.input)}
                onChange={handleInput('lastName')}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Email</div>
              <Input
                size="md"
                placeholder="Enter your email"
                type="email"
                classNames={classnames(styles.input)}
                onChange={handleInput('email')}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Username</div>
              <Input
                size="md"
                placeholder="Enter your username"
                classNames={classnames(styles.input)}
                onChange={handleInput('username')}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Phone Number</div>
              <Input
                size="md"
                placeholder="Enter your phone number"
                classNames={classnames(styles.input)}
                onChange={handleInput('phoneNumber')}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Password</div>
              <Input
                size="md"
                placeholder="Enter your password"
                type={isShowPassword ? '' : 'password'}
                classNames={classnames(styles.input)}
                onChange={handleInput('password')}
                icon={
                  <Icon
                    type={isShowPassword ? 'eye' : 'eye-slash'}
                    classNames={styles.icon}
                    onClick={handleShowPassword}
                  />
                }
              />
            </div>
          </div>
          <Button
            variant="contained"
            size="md"
            classNames={classnames(styles.buttonSignUp)}
            onClick={handleSignUp}
          >
            Create Account
          </Button>
          <div className={classnames(styles.loginWrap)}>
            <div className={classnames(styles.loginTitle)}>
              Already a member?
            </div>
            <Button variant="text" size="md" onClick={handleLogin}>
              Click here to login
            </Button>
          </div>
        </div>
      </div>
      <div className={classnames(styles.rightSide)}></div>
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(
      display('grid'),
      grid('md:grid-cols-2'),
      sizing('min-h-screen'),
    ),
    leftSide: classnames(),
    rightSide: classnames(backgroundColor('bg-primary-color')),
    leftSideBody: classnames(
      spacing('mt-1/10-screen', 'mx-10', 'px-10', 'py-5'),
      backgroundColor('bg-white'),
      borderRadius('rounded-2xl'),
      borders('border-2'),
    ),
    title: classnames(typography('text-tx22'), spacing('mb-4')),
    form: classnames(spacing('mb-4')),
    inputWrap: classnames(spacing('mb-2', 'last:!mb-0')),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(
      spacing('mb-1'),
      typography('text-tx14', 'md:text-tx16'),
    ),

    buttonSignUp: classnames(sizing('w-full'), spacing('mb-2')),
    loginWrap: classnames(
      sizing('w-full'),
      display('flex'),
      alignItems('items-center'),
      justifyContent('justify-start'),
    ),
    loginTitle: classnames(
      typography('text-tx14', 'md:text-tx16'),
      spacing('mr-1'),
    ),
    icon: classnames(sizing('w-5', 'h-5')),
  };
};
