/* eslint-disable @typescript-eslint/ban-ts-comment */
import classnames, {
  backgroundColor,
  spacing,
  borderRadius,
  borders,
  typography,
  sizing,
  display,
  justifyContent,
  fill,
  alignItems,
  gap,
} from '@frontend/tailwindcss-classnames';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import { Table } from '@frontend/components/table';
import { createColumnHelper } from '@tanstack/react-table';
import { Icon } from '@frontend/components/icon';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import { Button } from '@frontend/components/button';
import { UserController } from '@frontend/handlers/user';
import { FilterBlock } from './filterBlock';
import { useAuthContext } from '@frontend/modules/auth';

export const UserManagement = () => {
  const userController = UserController.getInstance();
  const { user } = useAuthContext();
  const { userState } = useReduxSelector(['userState']);
  const dispatch = useReduxDispatch();
  const { users } = userState;

  const styles = useStyles();

  const handleDeleteUser = (username: string) => {
    dispatch(userController.deleteUser(username));
  };

  const columnHelper = createColumnHelper<any>();

  const columns = [
    columnHelper.accessor(
      row => {
        return row.firstName + ' ' + row.lastName;
      },
      {
        id: 'fullName',
        cell: info => info.getValue(),
        header: () => 'Full Name',
      },
    ),
    columnHelper.accessor('email', {
      cell: info => info.getValue(),
      header: 'Email',
    }),
    columnHelper.accessor('username', {
      cell: info => info.getValue(),
      header: 'Username',
    }),
    columnHelper.accessor('phone', {
      cell: info => info.getValue(),
      header: 'Phone',
    }),
    columnHelper.accessor(
      row => {
        return {
          id: row.id,
          username: row.username,
        };
      },
      {
        id: 'action',
        cell: info =>
          info.getValue().id !== user?.id ? (
            <div className={classnames(styles.action)}>
              <Popup
                key="deleteUser"
                trigger={
                  <button>
                    <Icon
                      classNames={classnames(styles.icon, styles.deleteIcon)}
                      type="delete"
                    ></Icon>
                  </button>
                }
                modal={true}
                closeOnDocumentClick
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
                        Do you want to delete this user?
                      </div>
                    </div>

                    <div
                      className={`actions ${classnames(styles.deleteAction)}`}
                    >
                      <Button
                        variant="contained"
                        size="md"
                        color="success"
                        onClick={() =>
                          handleDeleteUser(info.getValue().username)
                        }
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
            </div>
          ) : null,
        header: () => (
          <div className={classnames(styles.actionHeader)}>Action</div>
        ),
      },
    ),
  ];
  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.title)}>List users</div>

      <FilterBlock />

      <Table
        data={users ?? []}
        columns={columns}
        headerClassnames={classnames(
          typography('text-left'),
          borders('border'),
          backgroundColor('bg-gray-200'),
        )}
        rowClassnames={classnames(
          borders('border'),
          backgroundColor('hover:bg-gray-100'),
        )}
      />
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(
      spacing('mt-1/10-screen', 'mx-10', 'md:mx-20', 'px-10', 'py-5'),
      backgroundColor('bg-white'),
      borderRadius('rounded-2xl'),
      borders('border-2'),
    ),
    title: classnames(typography('text-tx26', 'font-bold'), spacing('mb-4')),
    action: classnames(display('flex'), justifyContent('justify-center')),
    actionHeader: classnames(typography('text-center')),
    icon: classnames(sizing('h-4', 'w-4')),
    // @ts-ignore
    deleteIcon: classnames(fill('fill-red-500')),
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
  };
};
