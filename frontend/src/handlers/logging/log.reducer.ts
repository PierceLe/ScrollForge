import { createSlice } from '@reduxjs/toolkit';
import { LogController } from './log.controller';

const logController = LogController.getInstance();

const initialState = {
  logs: [],
};

export const logSlice = createSlice({
  name: 'logSlice',
  initialState,
  reducers: {
  },
  extraReducers: builder => {
    // GetLogsAPI
    builder.addCase(
      logController.getLogs.fulfilled,
      (state, action) => {
        return {
          ...state,
          logs: action.payload,
        };
      },
    );
    builder.addCase(
      logController.getLogs.rejected,
      (state, action) => {
        return {
          ...state,
          error: action.payload,
        };
      },
    );
  }
});

// eslint-disable-next-line no-empty-pattern
export const { } = logSlice.actions;
export const { reducer: logReducer } = logSlice;
