import axiosClient from "./axiosClient";

const reportService = {

// DAILY REPORT
getDailyReport: async (date) => {
  const res = await axiosClient.get(
    `/api/custom-reports/daily?date=${date}`
  );
  return res.data;
},

// MONTHLY REPORT
getMonthlyReport: async (year, month) => {
  const res = await axiosClient.get(
    `/api/custom-reports/monthly?year=${year}&month=${month}`
  );
  return res.data;
},

// DATE RANGE REPORT (if implemented)
getDateRangeReport: async (startDate, endDate) => {
  const res = await axiosClient.get(
    `/api/custom-reports/date-range?startDate=${startDate}&endDate=${endDate}`
  );
  return res.data;
},

  // EXPORT PDF (daily / monthly / date-range)
  exportPdf: async (type, params) => {
    let url = "";

    if (type === "daily") {
      url = `/api/custom-reports/export/daily-pdf?date=${params.date}`;
    }

    if (type === "monthly") {
      url = `/api/custom-reports/export/monthly-pdf?year=${params.year}&month=${params.month}`;
    }

    if (type === "date-range") {
      url = `/api/custom-reports/export/date-range-pdf?startDate=${params.startDate}&endDate=${params.endDate}`;
    }

    const response = await axiosClient.get(url, {
      responseType: "blob",
    });

    return response.data;
  },

  // DOWNLOAD FILE HELPER
  downloadFile: (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
  },
};

export default reportService;