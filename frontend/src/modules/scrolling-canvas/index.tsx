import { useState, useEffect } from 'react';
import classnames, {
  display,
  gap,
  grid,
  spacing,
  typography,
  cursor,
} from '@frontend/tailwindcss-classnames';
import { ScrollingCard } from './scrollingCard';
import { ScrollingPreview } from './scrollingPreview';
import { ScrollingController } from '@frontend/handlers/scrolling';
import { useReduxDispatch, useReduxSelector } from '@frontend/redux/hooks';
import { FilterBlock } from './filterBlock';

export const ScrollingCanvas = () => {
  const scrollingController = ScrollingController.getInstance();
  const dispatch = useReduxDispatch();
  const { scrollState } = useReduxSelector(['scrollState']);
  const { scrollings } = scrollState;

  const [isShowPreview, setIsShowPreview] = useState<boolean>(false);
  const [id, setId] = useState<number>();
  const [title, setTitle] = useState<string>();
  const [owner, setOwner] = useState<string>();
  const [date, setDate] = useState<string>();
  const [url, setUrl] = useState<string>();
  const [timeout, setTimeout] = useState<number>();
  const styles = useStyles();

  useEffect(() => {
    dispatch(scrollingController.getScrollings({}));
  }, []);

  const handleShowPreview = (scrolling: any) => {
    const { fileId, title, owner, uploadDate, filePath, timer } = scrolling;

    setId(fileId);
    setTitle(title);
    setOwner(owner);
    setDate(uploadDate);
    setUrl(filePath);
    setIsShowPreview(true);
    setTimeout(timer ? timer : 5);
  };

  return (
    <div className={classnames(styles.root)}>
      <div className={classnames(styles.title)}>List scrolling cards</div>

      <FilterBlock />

      <div className={classnames(styles.scrollWrap)}>
        {scrollings.map((scroll: any) => {
          return (
            <div
              className={classnames(styles.scrollCard)}
              onClick={() => handleShowPreview(scroll)}
            >
              <ScrollingCard
                title={scroll.title}
                createdBy={scroll.owner?.username}
                date={scroll.uploadDate}
                downloadAmount={scroll.downloadAmount}
              />
            </div>
          );
        })}
      </div>

      <ScrollingPreview
        isShowModal={isShowPreview}
        onClose={() => setIsShowPreview(false)}
        id={id}
        title={title}
        owner={owner}
        url={url}
        date={date}
        timeout={timeout}
      />
    </div>
  );
};

const useStyles = () => {
  return {
    root: classnames(spacing('px-16', 'pb-16')),
    title: classnames(typography('text-tx22', 'font-bold'), spacing('mb-4')),
    scrollWrap: classnames(
      display('grid'),
      grid('grid-cols-1', 'md:grid-cols-2'),
      gap('gap-4'),
    ),
    scrollCard: classnames(cursor('cursor-pointer')),
  };
};
