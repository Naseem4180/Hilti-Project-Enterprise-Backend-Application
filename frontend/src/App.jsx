import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import CustomerRegistration from './pages/CustomerRegistration';
import CustomerDashboard from './pages/customer/CustomerDashboard';
import Dashboard from './pages/admin/Dashboard';
import RegistrationApproval from './pages/admin/RegistrationApproval';
import FeDashboard from './pages/fe/FeDashboard';
import ManagerDashboard from './pages/manager/ManagerDashboard';

function ProtectedRoute({ children, role }) {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (role && user.role !== role) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <div className="app-shell">
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/register-customer" element={<CustomerRegistration />} />
          <Route path="/customer" element={<ProtectedRoute role="ROLE_CUSTOMER"><CustomerDashboard /></ProtectedRoute>} />
          <Route path="/admin" element={<ProtectedRoute role="ROLE_ADMIN"><Dashboard /></ProtectedRoute>} />
          <Route path="/admin/registrations" element={<ProtectedRoute role="ROLE_ADMIN"><RegistrationApproval /></ProtectedRoute>} />
          <Route path="/fe" element={<ProtectedRoute role="ROLE_FE"><FeDashboard /></ProtectedRoute>} />
          <Route path="/manager" element={<ProtectedRoute role="ROLE_MANAGER"><ManagerDashboard /></ProtectedRoute>} />
        </Routes>
      </div>
    </AuthProvider>
  );
}
