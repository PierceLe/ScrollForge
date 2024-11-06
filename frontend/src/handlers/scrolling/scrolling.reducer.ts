import { createSlice } from '@reduxjs/toolkit';
import { ScrollingController } from './scrolling.controller';
import { toast } from 'react-toastify';

const scrollingController = ScrollingController.getInstance();

const initialState: any = {
  scrollings: [],
};

export const scrollSlice = createSlice({
  name: 'userSlice',
  initialState,
  reducers: {
    createScrollPending: state => {
      return {
        ...state,
        isCreateScrollPending: true,
      };
    },
    createScrollSuccess: (state, action) => {
      return {
        ...state,
        isCreateScrollPending: false,
        scrollings: [action.payload, ...state.scrollings],
      };
    },
    createScrollFailed: state => {
      return {
        ...state,
        isCreateScrollPending: false,
      };
    },

    updateScrollPending: state => {
      return {
        ...state,
        isUpdateScrollPending: true,
      };
    },
    updateScrollSuccess: (state, action) => {
      const scrolls = state.scrollings.map((scroll: any) => {
        if (scroll.fileId !== action.payload.fileId) {
          return scroll;
        }

        return {
          ...scroll,
          ...action.payload,
        };
      });
      return {
        ...state,
        scrollings: scrolls,
        isUpdateScrollPending: false,
      };
    },
    updateScrollFailed: state => {
      return {
        ...state,
        isUpdateScrollPending: false,
      };
    },
  },
  extraReducers: builder => {
    // GetScrollingsAPI
    builder.addCase(
      scrollingController.getScrollings.fulfilled,
      (state, action) => {
        return {
          ...state,
          scrollings: action.payload,
        };
      },
    );
    builder.addCase(
      scrollingController.getScrollings.rejected,
      (state, action) => {
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // DeleteScrollAPI
    builder.addCase(
      scrollingController.deleteScrolling.fulfilled,
      (state, action) => {
        toast.info(`Delete scroll with id ${action.payload} successfully!`);
        const scrolls = state.scrollings.filter(
          (scroll: any) => scroll.fileId !== action.payload,
        );

        return {
          ...state,
          scrollings: scrolls,
        };
      },
    );
    builder.addCase(
      scrollingController.deleteScrolling.rejected,
      (state, action) => {
        toast.error(`Delete scroll unsuccessfully!`);
        return {
          ...state,
          error: action.payload,
        };
      },
    );

    // ChangeDefaultTimerScrollingAPI
    builder.addCase(
      scrollingController.changeDefaultTimerScrolling.fulfilled,
      (state) => {
        toast.info(`Update default timer scroll successfully!`);

        return {
          ...state,
        };
      },
    );
    builder.addCase(
      scrollingController.changeDefaultTimerScrolling.rejected,
      (state, action) => {
        toast.error(`Update default timer scroll unsuccessfully!`);
        return {
          ...state,
          error: action.payload,
        };
      },
    );
  },
});

// eslint-disable-next-line no-empty-pattern
export const {
  createScrollSuccess,
  createScrollPending,
  createScrollFailed,
  updateScrollPending,
  updateScrollSuccess,
  updateScrollFailed,
} = scrollSlice.actions;
export const { reducer: scrollReducer } = scrollSlice;
