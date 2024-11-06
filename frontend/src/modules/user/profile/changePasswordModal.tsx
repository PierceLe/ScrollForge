import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import { Input } from '@frontend/components/input';
import {
  classnames,
  typography,
  sizing,
  spacing,
} from '@frontend/tailwindcss-classnames';
import { Button } from '@frontend/components/button';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import { UserController } from '@frontend/handlers/user';
import { PAGE_LINKS } from '@frontend/react-routes/permissionLink';
import { Icon } from '@frontend/components/icon';
import { toast } from 'react-toastify';

export const ChangePasswordModal = () => {
  const navigate = useNavigate();
  const dispatch = useReduxDispatch();
  const userController = UserController.getInstance();
  const { userState } = useReduxSelector(['userState']);
  const { currentUser } = userState;

  const [oldPassword, setOldPassword] = useState<string>();
  const [newPassword, setNewPassword] = useState<string>();
  const [repeatNewPassword, setRepeatNewPassword] = useState<string>();
  const [isShowOldPassword, setIsShowOldPassword] = useState<boolean>();
  const [isShowNewPassword, setIsShowNewPassword] = useState<boolean>();
  const [isShowRepeatNewPassword, setIsShowRepeatNewPassword] =
    useState<boolean>();

  const [isReachableMaxAttempts, setIsReachableMaxAttempts] =
    useState<boolean>();

  const [isChange, setIsChange] = useState<boolean>(false);
  const styles = useStyles();

  useEffect(() => {
    checkIsReachableMaxAttempts();
  }, []);

  const checkIsReachableMaxAttempts = () => {
    const numOfAttemptsChangePassword = localStorage.getItem(
      'numOfAttemptsChangePassword',
    );
    const latestAttemptsChangePassword = localStorage.getItem(
      'latestAttemptsChangePassword',
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

  const handleOldPassword = (e: any) => {
    setOldPassword(e.target.value);

    // Check if we should allow to update password
    if (!isChange) {
      setIsChange(true);
    }
  };

  const handleNewPassword = (e: any) => {
    setNewPassword(e.target.value);

    if (!isChange) {
      setIsChange(true);
    }
  };

  const handleRepeatNewPassword = (e: any) => {
    setRepeatNewPassword(e.target.value);

    if (!isChange) {
      setIsChange(true);
    }
  };

  const handleUpdatePassword = async () => {
    if (newPassword !== repeatNewPassword) {
      toast.warn('Repeat password dont match');
      return;
    }

    const data = await dispatch(
      userController.updatePassword({
        id: currentUser.data?.id,
        oldPassword: oldPassword,
        newPassword: newPassword,
      }),
    );

    if (data?.meta?.requestStatus === 'fulfilled') {
      navigate(PAGE_LINKS.HOME.path);
      window.location.reload();
    } else {
      checkIsReachableMaxAttempts();
    }
  };

  const handleShowOldPassword = () => {
    setIsShowOldPassword(!isShowOldPassword);
  };

  const handleShowNewPassword = () => {
    setIsShowNewPassword(!isShowNewPassword);
  };

  const handleShowRepeatNewPassword = () => {
    setIsShowRepeatNewPassword(!isShowRepeatNewPassword);
  };

  return (
    <div>
      <Popup
        key="changePasswordModal"
        trigger={
          <button className="text-primary-color">Change password</button>
        }
        modal={true}
        closeOnDocumentClick
      >
        <div className="modal">
          <div className={classnames(styles.title)}>Enter new password</div>
          <div className={classnames(styles.note)}>
            <span>*</span> After change password successfully, you will be
            redirected to home
          </div>
          {isReachableMaxAttempts ? (
            <div className={classnames(styles.note)}>
              <span>*</span>
              You have exceeded the maximum number of password change attempts.
              Please wait to 15 minutes
            </div>
          ) : null}
          <Input
            size="lg"
            placeholder="Enter your old password"
            type={isShowOldPassword ? '' : 'password'}
            classNames={classnames(styles.input)}
            onChange={handleOldPassword}
            value={oldPassword}
            icon={
              <Icon
                type={isShowOldPassword ? 'eye' : 'eye-slash'}
                classNames={styles.icon}
                onClick={handleShowOldPassword}
              />
            }
            disabled={isReachableMaxAttempts}
          />
          <Input
            size="lg"
            placeholder="Enter your new password"
            type={isShowNewPassword ? '' : 'password'}
            classNames={classnames(styles.input)}
            onChange={handleNewPassword}
            value={newPassword}
            icon={
              <Icon
                type={isShowNewPassword ? 'eye' : 'eye-slash'}
                classNames={styles.icon}
                onClick={handleShowNewPassword}
              />
            }
            disabled={isReachableMaxAttempts}
          />
          <Input
            size="lg"
            placeholder="Re enter your new password"
            type={isShowRepeatNewPassword ? '' : 'password'}
            classNames={classnames(styles.input)}
            onChange={handleRepeatNewPassword}
            value={repeatNewPassword}
            icon={
              <Icon
                type={isShowRepeatNewPassword ? 'eye' : 'eye-slash'}
                classNames={styles.icon}
                onClick={handleShowRepeatNewPassword}
              />
            }
            disabled={isReachableMaxAttempts}
          />
          <div className="actions">
            <Button
              variant="contained"
              size="md"
              color="success"
              onClick={handleUpdatePassword}
              disabled={!isChange || isReachableMaxAttempts}
            >
              Update
            </Button>
          </div>
        </div>
      </Popup>
    </div>
  );
};

const useStyles = () => {
  return {
    title: classnames(typography('text-tx22', 'font-bold'), spacing('mb-5')),
    input: classnames(sizing('w-full'), spacing('mb-5')),
    note: classnames(spacing('mb-5'), typography('text-red-500')),
    icon: classnames(sizing('w-5', 'h-5')),
  };
};
