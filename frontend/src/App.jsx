import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import CustomerDashboard from './pages/customer/CustomerDashboard';
import AdminDashboard from './pages/admin/AdminDashboard';
import FeDashboard from './pages/fe/FeDashboard';

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
          <Route path="/customer" element={<ProtectedRoute role="ROLE_CUSTOMER"><CustomerDashboard /></ProtectedRoute>} />
          <Route path="/admin" element={<ProtectedRoute role="ROLE_ADMIN"><AdminDashboard /></ProtectedRoute>} />
          <Route path="/fe" element={<ProtectedRoute role="ROLE_FE"><FeDashboard /></ProtectedRoute>} />
        </Routes>
      </div>
    </AuthProvider>
  );
}
