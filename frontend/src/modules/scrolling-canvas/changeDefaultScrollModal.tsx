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
import { useReduxDispatch } from '@frontend/redux/hooks';
import { toast } from 'react-toastify';
import { ScrollingController } from '@frontend/handlers/scrolling';


export const ChangeDefaultScrollModal = () => {
  const dispatch = useReduxDispatch();
  const scrollingController = ScrollingController.getInstance();

  const [timeoutMinute, setTimeoutMinute] = useState<number>(0);
  const [timeoutSecond, setTimeoutSecond] = useState<number>(0);

  const styles = useStyles();

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'timeoutMinute':
        setTimeoutMinute(Number(value));
        break;
      case 'timeoutSecond':
        setTimeoutSecond(Number(value));
        break;
    }
  };

  const handleSave = async (closeModal: any) => {
    if (timeoutMinute < 0 || timeoutSecond < 0) {
      toast.warn('The timeout must be positive')
      return;
    }
    if (timeoutSecond >= 60) {
      toast.warn('The timeout seconds must not be greater than or equal to 60')
      return;
    } 
    const timeout = timeoutMinute * 60 + timeoutSecond

    const res: any = await dispatch(scrollingController.changeDefaultTimerScrolling({
      timer: timeout
    }))

    if (res.payload === "SUCESS") {
      closeModal();
    }
  };

  return (
    <div>
      <Popup
        key="changeDefaultTimerModal"
        trigger={
          <button>
            <Button variant="contained" size="md" color="success">
              Change default timer
            </Button>
          </button>
        }
        modal={true}
        closeOnDocumentClick
      >
        {/** @ts-ignore */}
        {(close: any) => (
          <div className="modal">
            <div className={classnames(styles.title)}>Change default timer</div>
            <div className={classnames(styles.form)}>
              <div className={classnames(styles.inputWrap)}>
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
              </div>
            </div>
            <div className={`actions ${classnames(styles.action)}`}>
              <Button
                variant="contained"
                size="md"
                color="success"
                onClick={() => handleSave(close)}
              >
                Save
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
