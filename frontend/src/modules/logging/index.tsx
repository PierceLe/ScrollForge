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
import { useReduxSelector } from '@frontend/redux/hooks';
import { Table } from '@frontend/components/table';
import { createColumnHelper } from '@tanstack/react-table';
import 'reactjs-popup/dist/index.css';
// import { FilterBlock } from './filterBlock';
import { convertDateToString } from '../../utils/date';

export const LoggingManagement = () => {
  const { logState } = useReduxSelector(['logState']);
  const { logs } = logState;

  const styles = useStyles();

  const columnHelper = createColumnHelper<any>();

  const columns = [
    columnHelper.accessor('title', {
      cell: info => info.getValue(),
      header: 'Title',
    }),
    columnHelper.accessor('date', {
      cell: info => convertDateToString(info.getValue(), 'DD/MM/yyyy'),
      header: 'Date',
    }),
  ];
  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.title)}>List logs</div>

      {/* <FilterBlock /> */}

      <Table
        data={logs ?? []}
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
