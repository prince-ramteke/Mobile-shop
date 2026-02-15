import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "../../api/axiosClient";


export default function CustomerLedger() {
  const { id } = useParams();   // 👈 GET ID FROM URL
  const [ledger, setLedger] = useState([]);
  const [payments, setPayments] = useState([]);
const [showPopup, setShowPopup] = useState(false);
const [loadingPayments, setLoadingPayments] = useState(false);

  const totalDue = ledger.reduce(
  (sum, row) => sum + Number(row.balance || 0),
  0
);



 useEffect(() => {
  if (id) fetchLedger();
}, [id]);

const openPayments = async (row) => {
  try {
    setLoadingPayments(true);
    const res = await axios.get(
      `/customers/ledger/${row.type}/${row.reference}/payments`
    );
    setPayments(res.data);
    setShowPopup(true);
  } catch (e) {
    console.error(e);
  } finally {
    setLoadingPayments(false);
  }
};

  const fetchLedger = async () => {
    try {
const res = await axios.get(`/customers/${id}/ledger`);
      setLedger(res.data);
    } catch (err) {
      console.error("Failed to load ledger", err);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Customer Ledger</h2>

      <table className="w-full border">
        <thead>
          <tr className="bg-gray-100">
            <th>Date</th>
            <th>Type</th>
            <th>Reference</th>
            <th>Debit</th>
            <th>Credit</th>
            <th>Pending</th>

          </tr>
        </thead>

        <tbody>
          {ledger.map((row, i) => (
            <tr
  key={i}
  className="border-b text-center cursor-pointer hover:bg-gray-100"
  onClick={() => {
  if (row.type === "SALE") openPayments(row);
}}

>

              <td>{new Date(row.date).toLocaleString("en-IN", { timeZone: "Asia/Kolkata" })}</td>
              <td>{row.type}</td>
              <td>{row.reference}</td>
              <td className="text-red-600">₹{row.debit}</td>
              <td className="text-green-600">₹{row.credit}</td>
              <td className="font-semibold">₹{Number(row.balance || 0)}</td>

            </tr>
          ))}
        </tbody>
      </table>
      <div
  style={{
    marginTop: 20,
    textAlign: "right",
    fontSize: 18,
    fontWeight: 600,
  }}
>
Total Due: ₹{totalDue}
</div>

{/* ================= PAYMENT POPUP ================= */}
{showPopup && (
  <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center">
    <div className="bg-white p-4 rounded shadow-lg w-[500px]">
      <h3 className="text-lg font-bold mb-3">Payment History</h3>

      {loadingPayments ? (
        <p>Loading...</p>
      ) : payments.length === 0 ? (
        <p>No payments found</p>
      ) : (
        <table className="w-full border">
          <thead>
            <tr className="bg-gray-100">
              <th>Date</th>
              <th>Mode</th>
              <th>Amount</th>
            </tr>
          </thead>

          <tbody>
            {payments.map((p, i) => (
              <tr key={i} className="text-center border-b">
                <td>
                  {new Date(p.paymentDate).toLocaleString("en-IN", {
                    timeZone: "Asia/Kolkata",
                  })}
                </td>
                <td>{p.mode}</td>
                <td className="text-green-600">₹{p.amount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <button
        className="mt-3 px-4 py-1 bg-blue-600 text-white rounded"
        onClick={() => setShowPopup(false)}
      >
        Close
      </button>
    </div>
  </div>
)}
{/* ================================================= */}

</div>
);

}
