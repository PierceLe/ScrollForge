/* eslint-disable @typescript-eslint/ban-ts-comment */
import Popup from 'reactjs-popup';
import AvatarEditor from 'react-avatar-editor';
import { useRef } from 'react';
import { Button } from '@frontend/components/button';
import classnames, {
  display,
  gap,
  justifyContent,
  spacing,
} from '@frontend/tailwindcss-classnames';

type ChangeAvatarModalProps = {
  src: string;
  handleImgChange: any;
  setIsChangeAvatar: any
};
export const ChangeAvatarModal = ({
  src,
  handleImgChange,
  setIsChangeAvatar
}: ChangeAvatarModalProps) => {
  const styles = useStyles();
  const cropRef = useRef(null);

  //handle save
  const handleSave = async (close: any) => {
    if (cropRef) {
      setIsChangeAvatar(true);
      close();
    }
  };

  return (
    <div>
      <Popup
        key="changePasswordModal"
        trigger={
          <button>
            {/* <label htmlFor="file">Select file</label> */}
            <input className="mx-auto" id="file" type="file" accept="image/*" onChange={handleImgChange}/>
          </button>
        }
        modal={true}
        closeOnDocumentClick
      >
        {/** @ts-ignore */}
        {close => (
          <div className="modal">
            <AvatarEditor
              ref={cropRef}
              image={src}
              style={{ width: '100%', height: '100%' }}
              border={50}
              borderRadius={150}
              color={[0, 0, 0, 0.72]}
              rotate={0}
            />
            <div className={`actions ${classnames(styles.actions)}`}>
              <Button variant="outlined" onClick={() => close()} size={'sm'}>
                Cancel
              </Button>
              <Button
                variant="contained"
                onClick={() => handleSave(close)}
                size={'sm'}
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
    actions: classnames(
      display('flex'),
      justifyContent('justify-center'),
      gap('gap-2'),
      spacing('mt-4'),
    ),
  };
};
