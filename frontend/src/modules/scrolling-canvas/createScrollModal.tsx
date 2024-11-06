/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useState } from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import { Input } from '@frontend/components/input';
import {
  classnames,
  typography,
  sizing,
  spacing,
  display,
  justifyContent,
  gap,
  alignItems,
} from '@frontend/tailwindcss-classnames';
import { Button } from '@frontend/components/button';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import { toast } from 'react-toastify';
// import { ScrollingController } from '@frontend/handlers/scrolling';
import { getCookie } from '@frontend/helpers/cookie';
import {
  createScrollFailed,
  createScrollPending,
  createScrollSuccess,
} from '@frontend/handlers/scrolling';

export const CreateScrollModal = () => {
  const dispatch = useReduxDispatch();
  const { scrollState } = useReduxSelector(['scrollState']);

  const [title, setTitle] = useState<string>();
  const [file, setFile] = useState<any>();
  const [timeoutMinute, setTimeoutMinute] = useState<number>(0);
  const [timeoutSecond, setTimeoutSecond] = useState<number>(0);

  const styles = useStyles();

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'title':
        setTitle(value);
        break;
      case 'timeoutMinute':
        setTimeoutMinute(Number(value));
        break;
      case 'timeoutSecond':
        setTimeoutSecond(Number(value));
        break;
    }
  };

  const handleFile = (e: any) => {
    setFile(e.target.files[0]);
  };

  const handleCreateScroll = async (closeModal: any) => {
    if (timeoutMinute < 0 || timeoutSecond < 0) {
      toast.warn('The timeout must be positive')
      return;
    }
    if (timeoutSecond >= 60) {
      toast.warn('The timeout seconds must not be greater than or equal to 60')
      return;
    } 

    const formData = new FormData();
    // const timeout = timeoutMinute * 60 + timeoutSecond;

    // Update the formData object
    formData.append('title', title ?? '');
    // formData.append('timer', timeout.toString());
    formData.append('file', file);

    dispatch(createScrollPending());

    fetch(`${import.meta.env.VITE_SERVER_HOST}/api/v1/scroll/upload`, {
      method: 'post',
      body: formData,
      headers: {
        Authorization: 'Bearer ' + getCookie('Authentication'),
      },
    }).then(async response => {
      const res = await response.json();
      if (res.success) {
        toast.info(`Create scroll ${title} successfully!`);
        dispatch(createScrollSuccess(res.data));
        closeModal();
      } else {
        toast.error(res.message);
        dispatch(createScrollFailed());
      }
    });
  };

  return (
    <div>
      <Popup
        key="createScrollModal"
        trigger={
          <button>
            <Button variant="contained" size="md" color="success">
              Create scroll
            </Button>
          </button>
        }
        modal={true}
        closeOnDocumentClick
      >
        {/** @ts-ignore */}
        {(close: any) => (
          <div className="modal">
            <div className={classnames(styles.title)}>Create new scroll</div>
            <div className={classnames(styles.form)}>
              <div className={classnames(styles.inputWrap)}>
                <div className={classnames(styles.inputLabel)}>Title</div>
                <Input
                  size="md"
                  placeholder="Enter title"
                  classNames={classnames(styles.input)}
                  onChange={handleInput('title')}
                />
              </div>
              {/* <div className={classnames(styles.inputWrap)}>
                <div className={classnames(styles.inputLabel)}>
                  Preview timout
                </div>
                <div className={classnames(styles.timeoutInputWrap)}>
                  <Input
                    size="md"
                    onChange={handleInput('timeoutMinute')}
                    type="number"
                    min={0}
                    classNames={classnames(styles.inputTimeout)}
                    value={timeoutMinute}
                  />
                  m<div>:</div>
                  <Input
                    size="md"
                    onChange={handleInput('timeoutSecond')}
                    classNames={classnames(styles.inputTimeout)}
                    type="number"
                    min={0}
                    max={59}
                    value={timeoutSecond}
                  />
                  s
                </div>
              </div> */}
              <div className={classnames(styles.inputWrap)}>
                <div className={classnames(styles.inputLabel)}>File</div>
                <Input
                  size="md"
                  classNames={classnames(styles.input)}
                  onChange={handleFile}
                  accept=".bin"
                  type="file"
                />
              </div>
            </div>
            <div className={`actions ${classnames(styles.action)}`}>
              <Button
                variant="contained"
                size="md"
                color="success"
                onClick={() => handleCreateScroll(close)}
                disabled={scrollState.isCreateScrollPending}
              >
                Create scroll
              </Button>
            </div>
          </div>
        )}
      </Popup>
    </div>
  );
};

const useStyles = () => {
  return {
    title: classnames(typography('text-tx22', 'font-bold'), spacing('mb-5')),
    form: classnames(spacing('mb-4')),
    inputWrap: classnames(spacing('mb-2', 'last:!mb-0')),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(
      spacing('mb-1'),
      typography('text-tx14', 'md:text-tx16'),
    ),
    action: classnames(display('flex'), justifyContent('justify-center')),
    icon: classnames(sizing('w-5', 'h-5')),
    timeoutInputWrap: classnames(
      display('flex'),
      gap('gap-2'),
      alignItems('items-center'),
    ),
    inputTimeout: classnames(sizing('w-16')),
  };
};
