import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Sidebar.css';

export default function Sidebar({ isOpen, toggleSidebar }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, user } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  const getMenuItems = () => {
    const baseItems = [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/bookings', label: 'Bookings', icon: '📅' },
      { path: '/activity-logs', label: 'Activity Logs', icon: '📋' },
      { path: '/settings', label: 'Settings', icon: '⚙️' },
    ];

    if (user?.role === 'ROLE_ADMIN') {
      return [
        { path: '/admin', label: 'Dashboard', icon: '📊' },
        { path: '/admin/registrations', label: 'User Validation', icon: '👥' },
        { path: '/admin/customers', label: 'Customers', icon: '👤' },
        { path: '/admin/reports', label: 'Reports', icon: '📈' },
        { path: '/admin/settings', label: 'Settings', icon: '⚙️' },
      ];
    }

    return baseItems;
  };

  const menuItems = getMenuItems();

  return (
    <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      <div className="sidebar-header">
        <div className="logo">
          <div className="logo-icon">H</div>
          {isOpen && (
            <div className="logo-text">
              <div className="logo-brand">Hilti</div>
              <div className="logo-subtext">Portal</div>
            </div>
          )}
        </div>
        {isOpen && (
          <button className="sidebar-close" onClick={toggleSidebar}>
            ✕
          </button>
        )}
      </div>

      <nav className="sidebar-nav">
        {menuItems.map((item) => (
          <button
            key={item.path}
            className={`nav-item ${isActive(item.path) ? 'active' : ''}`}
            onClick={() => navigate(item.path)}
            title={item.label}
          >
            <span className="nav-icon">{item.icon}</span>
            {isOpen && <span className="nav-label">{item.label}</span>}
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className={`user-info ${!isOpen ? 'collapsed' : ''}`}>
          <div className="user-avatar">{user?.email?.charAt(0).toUpperCase()}</div>
          {isOpen && (
            <div className="user-details">
              <div className="user-email">{user?.email}</div>
              <div className="user-role">{user?.role?.replace('ROLE_', '')}</div>
            </div>
          )}
        </div>
        <button className="logout-btn" onClick={handleLogout} title="Logout">
          {isOpen ? '🚪 Log Out' : '🚪'}
        </button>
      </div>
    </aside>
  );
}
