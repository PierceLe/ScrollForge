import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { object, string } from 'yup';

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
} from '@frontend/tailwindcss-classnames';
import { PAGE_LINKS } from '@frontend/react-routes/permissionLink';
import { AuthController } from '@frontend/handlers/auth';
import { useReduxDispatch } from '@frontend/redux/hooks';
import { Icon } from '@frontend/components/icon';
import { toast } from 'react-toastify';
import { sleep } from '@frontend/helpers/sleep';

export const Login = () => {
  const navigate = useNavigate();
  const dispatch = useReduxDispatch();
  const authController = AuthController.getInstance();

  const styles = useStyles();
  const [username, setUsername] = useState();
  const [password, setPassword] = useState();
  const [isShowPassword, setIsShowPassword] = useState<boolean>();

  const [isReachableMaxAttempts, setIsReachableMaxAttempts] =
    useState<boolean>();

  useEffect(() => {
    checkIsReachableMaxAttempts();
  }, []);

  const checkIsReachableMaxAttempts = () => {
    const numOfAttemptsChangePassword = localStorage.getItem(
      'numOfAttemptsLogin',
    );
    const latestAttemptsChangePassword = localStorage.getItem(
      'latestAttemptsLogin',
    );

    const now = Date.now();

    // Check if attempt login more than 3 in 15 minutes
    if (
      Number(numOfAttemptsChangePassword) >= 3 &&
      now - Number(latestAttemptsChangePassword) < 900000
    ) {
      setIsReachableMaxAttempts(true);
    }
  };

  const handleUsername = (e: any) => {
    setUsername(e.target.value);
  };

  const handlePassword = (e: any) => {
    setPassword(e.target.value);
  };

  const handleShowPassword = () => {
    setIsShowPassword(!isShowPassword);
  };

  const handleLogin = async () => {
    const userSchema = object({
      username: string().required(),
      password: string().required(),
    });

    const userLogin = {
      username,
      password,
    };

    try {
      await userSchema.validate(userLogin);
    } catch (error: any) {
      toast.error(error.message);
      return;
    }

    const data = await dispatch(
      authController.login({
        username,
        password,
      }),
    );

    //Verify for password authentication
    if (data?.payload?.token) {
      toast.info('Login successfully!');
      await sleep(500);
      window.location.reload();
    } else {
      checkIsReachableMaxAttempts();
    }
  };

  const handleSignUp = () => {
    navigate(PAGE_LINKS.REGISTER.path);
  };

  const handleLoginAsGuest = () => {
    navigate(PAGE_LINKS.HOME.path);
  }

  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.leftSide)}>
        <div className={classnames(styles.leftSideBody)}>
          <div className={classnames(styles.title)}>Login to your account</div>
          {isReachableMaxAttempts ? (
            <div className={classnames(styles.note)}>
              <span>*</span>
              You have exceeded the maximum number of password change attempts.
              Please wait to 15 minutes
            </div>
          ) : null}
          <div className={classnames(styles.form)}>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Username</div>
              <Input
                size="md"
                placeholder="Enter your username"
                classNames={classnames(styles.input)}
                onChange={handleUsername}
              />
            </div>
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>Password</div>
              <Input
                size="md"
                placeholder="Enter your password"
                type={isShowPassword ? '' : 'password'}
                classNames={classnames(styles.input)}
                onChange={handlePassword}
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
          <div>
            <Button
              variant="contained"
              size="md"
              classNames={classnames(styles.buttonLogin)}
              onClick={handleLogin}
              disabled={isReachableMaxAttempts}
            >
              Login
            </Button>
            <Button
              variant="contained"
              size="md"
              classNames={classnames(styles.buttonLogin)}
              onClick={handleLoginAsGuest}
            >
              Login as guest
            </Button>
            <Button
              variant="outlined"
              size="md"
              classNames={classnames(styles.buttonSignUp)}
              onClick={handleSignUp}
            >
              Sign Up
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
      grid('grid-cols-2'),
      sizing('min-h-screen'),
    ),
    leftSide: classnames(),
    rightSide: classnames(backgroundColor('bg-primary-color')),
    leftSideBody: classnames(
      spacing('mt-1/5-screen', 'md:mx-10', 'p-10'),
      backgroundColor('bg-white'),
      borderRadius('rounded-2xl'),
      borders('border-2'),
    ),
    title: classnames(typography('text-tx22'), spacing('mb-4')),
    form: classnames(spacing('mb-4')),
    inputWrap: classnames(spacing('mb-4', 'last:!mb-0')),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(spacing('mb-2')),

    buttonLogin: classnames(sizing('w-full'), spacing('mb-4')),
    buttonSignUp: classnames(sizing('w-full')),
    icon: classnames(sizing('w-5', 'h-5')),
    note: classnames(spacing('mb-5'), typography('text-red-500')),
  };
};
