import { useState, useEffect } from 'react';

import { Button } from '@frontend/components/button';
import { Input } from '@frontend/components/input';
import classnames, {
  backgroundColor,
  display,
  sizing,
  spacing,
  borderRadius,
  borders,
  typography,
  alignItems,
  justifyContent,
  gap,
  fill,
} from '@frontend/tailwindcss-classnames';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import { ChangeUsernameModal } from './changeUsernameModal';
import { ChangePasswordModal } from './changePasswordModal';
import {
  changeAvatarFailed,
  changeAvatarPending,
  changeAvatarSuccess,
  UserController,
} from '@frontend/handlers/user';
import { ChangeAvatarModal } from './changeAvatarModal';
import { getCookie } from '@frontend/helpers/cookie';
import { toast } from 'react-toastify';
import { Icon } from '@frontend/components/icon';

export const Profile = () => {
  const { userState } = useReduxSelector(['userState']);
  const { currentUser, isChangeAvatarPending } = userState;
  const dispatch = useReduxDispatch();
  const userController = UserController.getInstance();

  const styles = useStyles();
  const [isChange, setIsChange] = useState<boolean>(false);
  const [email, setEmail] = useState<string>();
  const [firstName, setFirstName] = useState<string>();
  const [lastName, setLastName] = useState<string>();
  const [username, setUsername] = useState<string>();
  const [phoneNumber, setPhoneNumber] = useState<string>();

  const defaultAvatar = '../../../public/defaultAvatar.jpg';
  const [src, setSrc] = useState('');
  const [avatar, setAvatar] = useState<any>();
  const [isChangeAvatar, setIsChangeAvatar] = useState<boolean>();

  useEffect(() => {
    const { email, firstName, lastName, username, phone } =
      currentUser?.data ?? {
        firstName: 'nam',
        lastName: 'nam',
        username: 'test2',
        phone: '123',
        email: '1234@gmail.com',
      };

    setEmail(email);
    setFirstName(firstName);
    setLastName(lastName);
    setUsername(username);
    setPhoneNumber(phone);
  }, [currentUser]);

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
    }

    if (!isChange) {
      setIsChange(true);
    }
  };

  const handleUpdateProfile = () => {
    console.log({
      email: email,
      firstName: firstName,
      lastName: lastName,
      phoneNumber: phoneNumber,
      username: username,
    });
    dispatch(
      userController.updateUser({
        id: currentUser.data?.id,
        email,
        firstName,
        lastName,
        phoneNumber,
        username,
      }),
    );
  };
  const handleImgChange = (e: any) => {
    setSrc(URL.createObjectURL(e.target.files[0]));
    setAvatar(e.target.files[0]);
    // setModalOpen(true);
  };

  const handleUpdateAvatar = () => {
    const formData = new FormData();

    // Update the formData object
    formData.append('file', avatar);

    setIsChangeAvatar(false)
    
    dispatch(changeAvatarPending());

    fetch(`${import.meta.env.VITE_SERVER_HOST}/api/v1/update/avatar`, {
      method: 'put',
      body: formData,
      headers: {
        Authorization: 'Bearer ' + getCookie('Authentication'),
      },
    }).then(async response => {
      const res = await response.json();
      if (res.success) {
        toast.info(
          `Change avatar for user ${firstName} ${lastName} successfully!`,
        );
        dispatch(changeAvatarSuccess(res.data));
        // closeModal()
      } else {
        dispatch(changeAvatarFailed());
      }
    });
  };

  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.profile)}>
        <div className={classnames(styles.title)}>Your profile</div>
        <div className={classnames(styles.countWrap)}>
          <div
            className={classnames(styles.downloadAmountWrap)}
            title="Number of scroll uploads"
          >
            <Icon
              type="upload"
              classNames={classnames(styles.downloadAmountIcon)}
            />
            <div>{currentUser?.data?.uploadNumber ?? 0}</div>
          </div>
          <div
            className={classnames(styles.downloadAmountWrap)}
            title="Number of scroll downloads"
          >
            <Icon
              type="download"
              classNames={classnames(styles.downloadAmountIcon)}
            />
            <div>{currentUser?.data?.downloadNumber ?? 0}</div>
          </div>
        </div>
        <div className={classnames(styles.form)}>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>First Name</div>
            <Input
              size="lg"
              placeholder="Enter your first name"
              classNames={classnames(styles.input)}
              onChange={handleInput('firstName')}
              value={firstName}
            />
          </div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>Last Name</div>
            <Input
              size="lg"
              placeholder="Enter your last name"
              classNames={classnames(styles.input)}
              onChange={handleInput('lastName')}
              value={lastName}
            />
          </div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>Email</div>
            <Input
              size="lg"
              placeholder="Enter your email"
              type="email"
              classNames={classnames(styles.input)}
              onChange={handleInput('email')}
              value={email}
            />
          </div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>Phone Number</div>
            <Input
              size="lg"
              placeholder="Enter your phone number"
              classNames={classnames(styles.input)}
              onChange={handleInput('phoneNumber')}
              value={phoneNumber}
            />
          </div>
        </div>
        <div className={classnames(styles.changeSecureWrap)}>
          <ChangeUsernameModal />
          <ChangePasswordModal />
        </div>
        <Button
          variant="contained"
          size="lg"
          classNames={classnames(styles.buttonSignUp)}
          onClick={handleUpdateProfile}
          disabled={!isChange}
          color="success"
        >
          Update Profile
        </Button>
      </div>
      <div className={classnames(styles.avatarSide)}>
        <img
          src={
            src
              ? src
              : currentUser.data?.avatarUrl
              ? currentUser.data?.avatarUrl
              : defaultAvatar
          }
          className={classnames(styles.avatar)}
        />

        <ChangeAvatarModal
          src={src}
          handleImgChange={handleImgChange}
          setIsChangeAvatar={setIsChangeAvatar}
        />
        <Button
          variant="text"
          size="lg"
          classNames={classnames(styles.buttonEditAvatar)}
          onClick={handleUpdateAvatar}
          disabled={!isChangeAvatar || isChangeAvatarPending}
        >
          Save
        </Button>
      </div>
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(
      display('flex'),
      justifyContent('justify-center'),
      gap('gap-10'),
      spacing('mt-1/10-screen', 'px-10', 'py-5'),
    ),
    profile: classnames(
      spacing('px-10', 'py-5'),
      backgroundColor('bg-white'),
      borderRadius('rounded-2xl'),
      borders('border-2'),
      sizing('w-2/3', 'md:w-1/2'),
    ),
    avatarSide: classnames(),
    title: classnames(typography('text-tx22'), spacing('mb-4')),
    form: classnames(spacing('mb-4')),
    inputWrap: classnames(spacing('mb-2', 'last:!mb-0')),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(spacing('mb-1'), typography('text-tx16')),

    buttonSignUp: classnames(sizing('w-full'), spacing('mb-2')),
    loginWrap: classnames(
      sizing('w-full'),
      display('flex'),
      alignItems('items-center'),
      justifyContent('justify-start'),
    ),
    changeSecureWrap: classnames(spacing('mb-2')),

    avatar: classnames(
      sizing('h-48', 'w-48'),
      borderRadius('rounded-full'),
      spacing('mb-4', 'mx-auto'),
    ),
    buttonEditAvatar: classnames(spacing('mx-auto')),

    countWrap: classnames(display('flex'), gap('gap-4'), spacing('mb-4')),
    downloadAmountIcon: classnames(
      sizing('h-4', 'w-4'),
      fill('fill-primary-color'),
    ),
    downloadAmountWrap: classnames(
      display('flex'),
      gap('gap-2'),
      alignItems('items-center'),
      typography('text-gray-400'),
    ),
  };
};
