import moment from 'moment';

export const convertDateToString = (date: Date, format: string) => {
  // Create a Moment.js date from the string
  const momentDate = moment(date);

  return momentDate.format(format);
};
