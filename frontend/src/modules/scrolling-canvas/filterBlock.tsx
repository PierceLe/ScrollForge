import { useState } from 'react';
import classnames, {
  alignItems,
  borders,
  display,
  flexWrap,
  gap,
  sizing,
  spacing,
  typography,
} from '@frontend/tailwindcss-classnames';
import { Input } from '@frontend/components/input';
import { Button } from '@frontend/components/button';
import { useReduxDispatch } from '@frontend/redux/hooks';
import { CreateScrollModal } from './createScrollModal';
import { ScrollingController } from '@frontend/handlers/scrolling';
import { useAuthContext } from '../auth';
import { convertDateToString } from '@frontend/utils/date';
import DatePicker from 'react-datepicker';

import 'react-datepicker/dist/react-datepicker.css';
import { ChangeDefaultScrollModal } from './changeDefaultScrollModal';

export const FilterBlock = () => {
  const scrollController = ScrollingController.getInstance();
  const dispatch = useReduxDispatch();
  const { user } = useAuthContext();

  const [title, setTitle] = useState<string>();
  const [createdBy, setCreatedBy] = useState<string>();
  const [from, setFrom] = useState<any>(null);
  const [to, setTo] = useState<any>(null);
  const styles = useStyles();

  const handleInput = (type: string) => (e: any) => {
    const value: string = e.target.value;

    switch (type) {
      case 'title':
        setTitle(value);
        break;
      case 'createdBy':
        setCreatedBy(value);
        break;
    }
  };

  const handleFrom = (date: any) => {
    setFrom(date);
  };

  const handleTo = (date: any) => {
    setTo(date);
  };

  const handleSearch = () => {
    // Create an object to hold the filter values
    const filters: any = {};

    if (title) {
      filters.title = title;
    }
    if (createdBy) {
      filters.owner = createdBy;
    }
    if (from) {
      filters.From = convertDateToString(new Date(from), 'YYYY-MM-DD HH:mm:ss');
    }
    if (to) {
      filters.To = convertDateToString(new Date(to), 'YYYY-MM-DD HH:mm:ss');
    }

    // Dispatch the search only with non-empty filters
    dispatch(scrollController.getScrollings(filters));
  };

  // New handleClear function to reset all filters
  const handleClear = () => {
    setTitle('');        // Reset title input
    setCreatedBy('');    // Reset createdBy input
    setFrom(null);       // Reset from date picker
    setTo(null);         // Reset to date picker
  };

  return (
      <div className={classnames(styles.root)}>
        <div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>Title</div>
            <Input
                size="md"
                placeholder="Enter title"
                type="text"
                classNames={classnames(styles.input)}
                onChange={handleInput('title')}
                value={title || ''}
            />
          </div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>Owner</div>
            <Input
                size="md"
                placeholder="Enter owner"
                classNames={classnames(styles.input)}
                onChange={handleInput('createdBy')}
                value={createdBy || ''}
            />
          </div>
        </div>
        <div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>From</div>
            <DatePicker
                className={classnames(styles.datepicker)}
                onChange={handleFrom}
                selected={from}
                placeholderText="Enter from"
                dateFormat={'dd/MM/yyyy'}
            />
          </div>
          <div className={classnames(styles.inputWrap)}>
            <div className={classnames(styles.inputLabel)}>To</div>
            <DatePicker
                className={classnames(styles.datepicker)}
                onChange={handleTo}
                selected={to}
                placeholderText="Enter to"
                dateFormat={'dd/MM/yyyy'}
            />
          </div>
        </div>
        <div className={classnames(styles.buttonWrap)}>
          <Button
              variant="contained"
              size="md"
              classNames={classnames(styles.buttonSearch)}
              onClick={handleSearch}
          >
            Search
          </Button>
          <Button
              variant="outlined"
              size="md"
              classNames={classnames(styles.buttonClear)}
              onClick={handleClear}
          >
            Clear
          </Button>
          {user?.id ? <CreateScrollModal /> : null}
          {user?.isAdmin() ? <ChangeDefaultScrollModal /> : null}
        </div>
      </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(spacing('mb-8'), display('flex'), gap('gap-14'), flexWrap('flex-wrap')),
    inputWrap: classnames(
        spacing('mb-2', 'last:!mb-0'),
        display('flex'),
        alignItems('items-center'),
    ),
    input: classnames(sizing('w-full')),
    inputLabel: classnames(
        spacing('mb-1'),
        typography('text-tx14', 'md:text-tx16'),
        sizing('w-1/2'),
    ),
    buttonWrap: classnames(display('flex'), gap('gap-2')),
    buttonSearch: classnames(),
    buttonClear: classnames(borders('border-gray-500', 'border'), spacing('px-4')),
    datepicker: classnames(
        sizing('w-full', 'h-8', 'md:h-10'),
        borders('rounded-xl', 'border-2'),
        spacing('p-2')
    ),
  };
};