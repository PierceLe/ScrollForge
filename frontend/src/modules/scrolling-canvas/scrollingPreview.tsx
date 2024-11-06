/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useState, useEffect, useRef } from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import {
  classnames,
  typography,
  sizing,
  spacing,
  display,
  gap,
  justifyContent,
  fill,
  alignItems,
  opacity,
  borders,
} from '@frontend/tailwindcss-classnames';
import { Button } from '@frontend/components/button';
import { useAuthContext } from '../auth';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import {
  ScrollingController,
  updateScrollFailed,
  updateScrollPending,
  updateScrollSuccess,
} from '@frontend/handlers/scrolling';
import { Icon } from '@frontend/components/icon';
import { Input } from '@frontend/components/input';
import { getCookie } from '@frontend/helpers/cookie';
import { toast } from 'react-toastify';

type ScrollingPreview = {
  isShowModal: boolean;
  onClose:
    | ((
        event?: React.SyntheticEvent | KeyboardEvent | TouchEvent | MouseEvent,
      ) => void)
    | undefined;
  id?: number;
  title?: string;
  createdBy?: string;
  owner?: any;
  date?: string;
  url?: string;
  timeout?: number;
};

export const ScrollingPreview = (props: ScrollingPreview) => {
  const { isShowModal, onClose, id, title, owner, url, timeout = 999 } = props;
  const { user } = useAuthContext();
  const { scrollState } = useReduxSelector(['scrollState']);

  const dispatch = useReduxDispatch();
  const scrollController = ScrollingController.getInstance();

  const [editTitle, setEditTitle] = useState<string>();
  const [isEdit, setIsEdit] = useState<boolean>();
  const [fileContent, setFileContent] = useState('');
  const [timeoutMinute, setTimeoutMinute] = useState<number>(0);
  const [timeoutSecond, setTimeoutSecond] = useState<number>(0);
  const [previewTimer, setPreviewTimer] = useState<any>();
  const [newFile, setNewFile] = useState<any>();

  const textareaRef = useRef<any>(null);

  const styles = useStyles();

  useEffect(() => {
    const fetchFileContent = async () => {
      try {
        if (url) {
          const response = await fetch(url);
          if (!response.ok) {
            console.error('Network response was not ok');
          }
          const text = await response.text();
          setFileContent(text);
        }
      } catch (err: any) {
        console.error(err.message);
      }
    };

    const minutes = Math.floor(timeout / 60);

    setFileContent('');
    setEditTitle(title ?? '');
    setIsEdit(false);
    fetchFileContent();
    setTimeoutMinute(minutes);
    setTimeoutSecond(timeout - minutes * 60);

    if (timeout && onClose && isShowModal) {
      // Set timeout to close modal preview
      const timer = setTimeout(() => {
        onClose();
      }, timeout * 1000);

      setPreviewTimer(timer);

      // Cleanup function to clear the timeout
      return () => clearTimeout(previewTimer);
    }
  }, [id, isShowModal]);

  useEffect(() => {
    handleResizeTextarea();
  }, [textareaRef.current]);

  const handleResizeTextarea = () => {
    if (textareaRef?.current) {
      // Reset height to auto to shrink the textarea if necessary
      textareaRef.current.style.height = 'auto';
      // Set height to scrollHeight to expand to fit content
      textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
    }
  };

  const handleDeleteScroll = (closeConfirmDelete: any) => {
    dispatch(
      scrollController.deleteScrolling({
        id,
      }),
    );

    closeConfirmDelete();
    if (onClose) onClose();
  };

  const pauseTimeout = () => {
    console.log('Timeout paused', previewTimer);
    clearTimeout(previewTimer);
  };

  const resumeTimeout = () => {
    console.log('Timeout resumed');
    // Start a new timeout after the original delay
    const timer = setTimeout(() => {
      console.log('Timeout completed');
      if (onClose) onClose();
    }, timeout * 1000);
    setPreviewTimer(timer);
  };

  const handleEdit = () => {
    setIsEdit(true);
    pauseTimeout();
  };

  const handleCancel = () => {
    setIsEdit(false);
    resumeTimeout();
  };

  const handleNewFile = (e: any) => {
    setNewFile(e.target.files[0]);
  };

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'title':
        setEditTitle(value);
        break;
      case 'timeoutMinute':
        setTimeoutMinute(Number(value));
        break;
      case 'timeoutSecond':
        setTimeoutSecond(Number(value));
        break;
    }
  };

  const handleUpdateScroll = async () => {
    const formData = new FormData();
    const timeout = timeoutMinute * 60 + timeoutSecond;

    // Update the formData object
    formData.append('id', id?.toString() ?? '');
    formData.append('title', editTitle ?? '');
    formData.append('timer', timeout.toString());

    if (newFile) {
      formData.append('file', newFile);
    } else {
      const blob = convertFileContentToBlob();
      formData.append('file', blob);
    }

    dispatch(updateScrollPending());

    fetch(`${import.meta.env.VITE_SERVER_HOST}/api/v1/scroll/update`, {
      method: 'put',
      body: formData,
      headers: {
        Authorization: 'Bearer ' + getCookie('Authentication'),
      },
    }).then(async response => {
      const res = await response.json();
      if (res.success) {
        toast.info(`Update scroll ${title} successfully!`);
        setIsEdit(false);
        dispatch(updateScrollSuccess(res.data));
        if (onClose) onClose();
      } else {
        toast.error(res.message);
        dispatch(updateScrollFailed());
      }
    });
  };

  const convertFileContentToBlob = () => {
    const binaryData = new Uint8Array(fileContent.length);
    for (let i = 0; i < fileContent.length; i++) {
      binaryData[i] = fileContent.charCodeAt(i);
    }

    const blob = new Blob([binaryData], { type: 'application/octet-stream' });

    return blob;
  };

  const handleDownload = () => {
    const blob = convertFileContentToBlob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${title}.bin`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    dispatch(
      scrollController.downloadScrolling({
        id,
      }),
    );
  };

  return (
    <div>
      <Popup
        key="createUserModal"
        modal={true}
        closeOnDocumentClick
        onClose={onClose}
        open={isShowModal}
        nested={true}
        className="w-900"
      >
        <div className="modal">
          <div className="mb-5">
            {isEdit ? (
              <Input
                size="md"
                value={editTitle}
                classNames={classnames(styles.input)}
                onChange={handleInput('title')}
              />
            ) : (
              <div className={`line-clamp-2 ${classnames(styles.title)}`}>
                {title}
              </div>
            )}
          </div>

          {isEdit && user?.isAdmin() ? (
            <div className="mb-5">
              <div className={classnames(styles.inputWrap)}>
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
              </div>
            </div>
          ) : null}

          {isEdit && (
            <div className={classnames(styles.inputWrap)}>
              <div className={classnames(styles.inputLabel)}>File</div>
              <Input
                size="md"
                classNames={classnames(styles.input)}
                onChange={handleNewFile}
                accept=".bin"
                type="file"
              />
            </div>
          )}

          {!isEdit || (isEdit && !newFile) ? (
            <div className={classnames(styles.pdfPreviewWrap)}>
              {/* <ReactPDF url={url ?? ''} /> */}
              <textarea
                ref={textareaRef}
                value={fileContent}
                disabled={!isEdit}
                className={classnames(styles.fileContent(fileContent))}
                onInput={handleResizeTextarea}
                onChange={e => setFileContent(e.target.value)}
              />
            </div>
          ) : null}

          <div className={`actions ${classnames(styles.action)}`}>
            {/** Save change */}
            {isEdit && (
              <Button
                variant="contained"
                size="md"
                color="success"
                onClick={handleUpdateScroll}
                disabled={scrollState.isUpdateScrollPending}
              >
                Save
              </Button>
            )}
            {/** Cancel change */}
            {isEdit ? (
              <Button
                variant="contained"
                size="md"
                color="default"
                onClick={handleCancel}
              >
                Cancel
              </Button>
            ) : (
              <>
                {/** Edit scroll */}
                {user?.isAdmin() || user?.id === owner?.id ? (
                  <Button
                    variant="contained"
                    size="md"
                    color="success"
                    onClick={handleEdit}
                  >
                    Edit
                  </Button>
                ) : null}
              </>
            )}
            {/** Download file */}
            {user?.id ? (
              <Button variant="contained" size="md" onClick={handleDownload}>
                Download
              </Button>
            ) : null}
            {/** Delete scroll */}
            {(user?.isAdmin() || user?.id === owner?.id) && (
              <Popup
                key="confirmDeleteModal"
                modal={true}
                trigger={
                  <button>
                    <Button variant="contained" size="md" color="danger">
                      Delete
                    </Button>
                  </button>
                }
              >
                {/** @ts-ignore */}
                {close => (
                  <div className="modal">
                    <div className={classnames(styles.deleteWarn)}>
                      <Icon
                        classNames={classnames(styles.warnIcon)}
                        type="warning"
                      ></Icon>
                      <div className={classnames(styles.deleteWarnTitle)}>
                        Do you want to delete this scroll?
                      </div>
                    </div>

                    <div
                      className={`actions ${classnames(styles.deleteAction)}`}
                    >
                      <Button
                        variant="contained"
                        size="md"
                        color="success"
                        onClick={() => handleDeleteScroll(close)}
                      >
                        OK
                      </Button>
                      <Button
                        variant="contained"
                        size="md"
                        color="danger"
                        onClick={() => {
                          close();
                        }}
                      >
                        Cancel
                      </Button>
                    </div>
                  </div>
                )}
              </Popup>
            )}
          </div>
        </div>
      </Popup>
    </div>
  );
};

const useStyles = () => {
  return {
    title: classnames(typography('text-tx22', 'font-bold')),
    form: classnames(spacing('mb-4')),
    inputWrap: classnames(spacing('mb-2', 'last:!mb-0')),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(
      spacing('mb-1'),
      typography('text-tx14', 'md:text-tx16'),
    ),
    action: classnames(
      display('flex'),
      justifyContent('justify-center'),
      gap('gap-4'),
    ),
    icon: classnames(sizing('w-5', 'h-5')),
    pdfPreviewWrap: classnames(spacing('mb-4')),
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    warnIcon: classnames(sizing('h-8', 'w-8'), fill('fill-yellow-500')),
    deleteWarn: classnames(
      display('flex'),
      justifyContent('justify-center'),
      alignItems('items-center'),
      gap('gap-2'),
      spacing('mb-6'),
    ),
    deleteWarnTitle: classnames(typography('text-tx22', 'font-bold')),
    deleteAction: classnames(
      display('flex'),
      justifyContent('justify-center'),
      gap('gap-2'),
    ),
    fileContent: (fileContent: string) =>
      classnames(
        sizing('w-full', 'max-h-48'),
        !fileContent ? opacity('opacity-0') : null,
        borders('border-2', 'rounded-xl'),
        spacing('p-2'),
      ),
    timeoutInputWrap: classnames(
      display('flex'),
      gap('gap-2'),
      alignItems('items-center'),
    ),
    inputTimeout: classnames(sizing('w-16')),
  };
};
