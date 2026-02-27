import axiosClient from "./axiosClient";

const reportService = {

  getDaily: async (date) => {
    const res = await axiosClient.get(`/api/reports/daily?date=${date}`);
    return res.data;
  },

  getMonthly: async (year, month) => {
    const res = await axiosClient.get(`/api/reports/monthly?year=${year}&month=${month}`);
    return res.data;
  },
};

export default reportService;
