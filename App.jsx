import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, theme } from 'antd';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider, useTheme } from './context/ThemeContext';


import CustomerLedger from "./pages/customers/CustomerLedger";


// Layout
import AppLayout from './components/layout/AppLayout';
import ProtectedRoute from './components/common/ProtectedRoute';

// Pages
import Login from './pages/auth/Login';
import Dashboard from './pages/dashboard/Dashboard';
import CustomerList from './pages/customers/CustomerList';
import CreateSale from './pages/sales/CreateSale';
import SaleList from './pages/sales/SaleList';
import SaleDetails from './pages/sales/SaleDetails';
import CreateRepair from './pages/repairs/CreateRepair';
import RepairList from './pages/repairs/RepairList';
import DueList from './pages/dues/DueList';
import DailyReport from './pages/reports/DailyReport';
import MonthlyReport from './pages/reports/MonthlyReport';
import DateRangeReport from './pages/reports/DateRangeReport';
import GstReport from './pages/reports/GstReport';
import Settings from './pages/settings/Settings';


import RepairDetails from './pages/repairs/RepairDetails';
import RepairEdit from './pages/repairs/RepairEdit';



// App content with theme-aware Ant Design config
const AppContent = () => {
  const { isDark } = useTheme();

  return (
    <ConfigProvider
      theme={{
        algorithm: isDark ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: {
          colorPrimary: '#00d4ff',
          colorLink: '#00d4ff',
          borderRadius: 8,
          fontFamily: "'Inter', 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
        },
        components: {
          Button: {
            primaryShadow: '0 4px 15px rgba(0, 212, 255, 0.3)',
          },
          Card: {
            borderRadiusLG: 12,
          },
          Table: {
            borderRadiusLG: 12,
          },
        },
      }}
    >
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />

          {/* Protected Routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            {/* Dashboard */}
            <Route index element={<Dashboard />} />

            {/* Customers */}
            <Route path="customers" element={<CustomerList />} />
            <Route path="/customers/:id/ledger" element={<CustomerLedger />} />


            {/* Sales */}
            <Route path="sales" element={<SaleList />} />
            <Route path="sales/create" element={<CreateSale />} />
            <Route path="sales/:id" element={<SaleDetails />} />

{/* Repairs */}
<Route path="repairs" element={<RepairList />} />
<Route path="repairs/create" element={<CreateRepair />} />

{/* ⚠️ EDIT must come BEFORE :id */}
<Route path="repairs/edit/:id" element={<RepairEdit />} />

{/* View Repair */}
<Route path="repairs/:id" element={<RepairDetails />} />




            {/* Dues */}
            <Route path="dues" element={<DueList />} />

            {/* Reports */}
            <Route path="reports/daily" element={<DailyReport />} />
            <Route path="reports/monthly" element={<MonthlyReport />} />
            <Route path="reports/date-range" element={<DateRangeReport />} />
            <Route path="reports/gst" element={<GstReport />} />

            {/* Settings (Admin Only) */}
            <Route
              path="settings"
              element={
                <ProtectedRoute adminOnly>
                  <Settings />
                </ProtectedRoute>
              }
            />

            {/* Catch all - redirect to dashboard */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
};

// Main App with providers
const App = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
